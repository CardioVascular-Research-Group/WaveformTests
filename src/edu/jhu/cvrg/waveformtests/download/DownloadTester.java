package edu.jhu.cvrg.waveformtests.download;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.jhu.cvrg.seleniummain.BaseFunctions;
import edu.jhu.cvrg.seleniummain.BrowserEnum;
import edu.jhu.cvrg.seleniummain.LogfileManager;
import edu.jhu.cvrg.seleniummain.TestNameEnum;
import edu.jhu.cvrg.waveformtests.CommonWaveformTests;
import edu.jhu.cvrg.waveformtests.UIComponentChecks;

public class DownloadTester extends CommonWaveformTests {

	public DownloadTester(String site, String viewPath, String welcomePath, String userName, String passWord, boolean loginRequired, BrowserEnum whichBrowser) {
		super(site, viewPath, welcomePath, userName, passWord, loginRequired, whichBrowser);
	}
	
	public DownloadTester(String site, String viewPath, String welcomePath,
			String userName, String passWord, boolean loginRequired) {
		super(site, viewPath, welcomePath, userName, passWord, loginRequired);
		// TODO Auto-generated constructor stub
		
	}
	
	public DownloadTester(String site, String viewPath, String welcomePath, String userName, String passWord, WebDriver existingDriver) {
		super(site, viewPath, welcomePath, userName, passWord, existingDriver);
	}
	
	public void testDownloadPage() throws IOException {
		this.goToPage();
		
		portletDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		portletLogMessages.add("Clicking the entries in the raw data table");
		selectData("A9292:formDownload:rawFiles_data");
		
		portletLogMessages.add("Clicking the entries in the analysis data table");
		selectData("A9292:formDownload:analysisResults_data");
		
		logger.addToLog(portletLogMessages, TestNameEnum.WAVEFORMDOWNLOAD);
		if(!seleniumLogMessages.isEmpty()) {
			logger.addToLog(seleniumLogMessages, TestNameEnum.SELENIUM);
		}
		
	}

	private void selectData(String datatableID) {
		List<WebElement> rawDataRows = portletDriver.findElements(By.xpath("//tbody[@id='" + datatableID + "']/tr[@role='row']"));  // only used to get size
		
		// this is needed when the list needs to be refreshed later
		// When a row is clicked, it removes the original DOM entry and replaces it with another with a different xpath on the frontend
		// This will determine the size of the original list while allowing us to refresh the list so that other entries can still be clicked
		
		if(rawDataRows.size() > 0) {
			int size = rawDataRows.size();
			
			for (int i=0; i<size; i++) {
				int xpathElementNum = i + 1;  // indexing begins at 1 for xpath instead of 0				
				
				try {
					// Using xpath, navigate to the next table cell which contains the file name and extract it
					WebElement fileRow = portletDriver.findElement(By.xpath("//tbody[@id='" + datatableID + "']/tr[@role='row' and " + xpathElementNum + "]"));
					
					
					System.out.println("index = " + xpathElementNum);
					List<WebElement> fileColumns = fileRow.findElements(By.xpath("//td[@role='gridcell']"));
					String fileName = fileColumns.get(3).getText();
					
					// now click on that entry in that row
					fileColumns.get(0).click();
					portletDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	
					portletLogMessages.add("The row containing the file \"" + fileName + "\" has been clicked and is working");
					
					fileColumns.clear();
					fileColumns = null;
					fileName = "";
	
				} catch (StaleElementReferenceException se) {
					seleniumLogMessages.add("A row was not able to be clicked.  This was due to the corresponding element being altered or removed and then replaced with a new one.  This means that element being accessed is no longer valid.\n  Here is more information:  \n" + LogfileManager.extractStackTrace(se));
				} catch (NoSuchElementException nse) {
					seleniumLogMessages.add("An element was not found while validating the folder tree, here is more information:  \n" + LogfileManager.extractStackTrace(nse));
				} finally {
					// again, since new tags are created, the List of checkBox elements must be refreshed
//					rawDataRows = portletDriver.findElements(By.xpath("//tbody[@id='" + datatableID + "']/tr[@role='row']"));
//					portletDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				}
			}
		}
		else {
			portletLogMessages.add("Records were not able to be found");
		}
		
		portletLogMessages.add("\n");
	}
}
