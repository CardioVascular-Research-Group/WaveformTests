package edu.jhu.cvrg.waveformtests.download;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.jhu.cvrg.seleniummain.BaseFunctions;
import edu.jhu.cvrg.seleniummain.BrowserEnum;
import edu.jhu.cvrg.seleniummain.LogfileManager;
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
	
	public void testDownloadPage() {
		selectData("A9292:formDownload:rawFiles_data");
		selectData("A9292:formDownload:analysisResults_data");
		
	}

	private void selectData(String datatableID) {
		List<WebElement> rawDataRows = portletDriver.findElements(By.xpath("//tbody[@id='" + datatableID + "']/tr[@class='ui-widget-content ui-datatable-even ui-datatable-selectable ui-state-highlight']"));
		
		// this is needed when the list needs to be refreshed later
		// When a row is clicked, it removes the original DOM entry and replaces it with another with a different xpath on the frontend
		// This will determine the size of the original list while allowing us to refresh the list so that other entries can still be clicked
		int size = rawDataRows.size() - 1;
		
		for (int i=0; i<size; i++) {
			try {
				// Using xpath, navigate to the next table cell which contains the file name and extract it
				System.out.println("i = " + i);
				List<WebElement> fileColumn = rawDataRows.get(i).findElements(By.xpath("//td[role='gridcell']"));
				String fileName = fileColumn.get(3).getText();
				
				// now click on that entry in that row
				rawDataRows.get(i).click();
				portletDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

				
				portletLogMessages.add("The row containing the file \"" + fileName + "\" is working");	

			} catch (StaleElementReferenceException se) {
				seleniumLogMessages.add("A row was not able to be clicked.  This was due to the corresponding element being altered or removed and then replaced with a new one.  This means that element being accessed is no longer valid.\n  Here is more information:  \n" + LogfileManager.extractStackTrace(se));
				logger.incrementAnalyzeFails();
			} finally {
				// again, since new tags are created, the List of checkBox elements must be refreshed
				rawDataRows = portletDriver.findElements(By.xpath("//tbody[@id='" + datatableID + "']/tr[@class='ui-widget-content ui-datatable-even ui-datatable-selectable ui-state-highlight']"));
				portletDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			}
		}
	}
}
