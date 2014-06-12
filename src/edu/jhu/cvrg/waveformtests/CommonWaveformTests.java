package edu.jhu.cvrg.waveformtests;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebElement;

import com.thoughtworks.selenium.Selenium;

import edu.jhu.cvrg.seleniummain.BaseFunctions;
import edu.jhu.cvrg.seleniummain.BrowserEnum;

public class CommonWaveformTests extends BaseFunctions {

	protected CommonWaveformTests(String site, String viewPath,
			String welcomePath, String userName, String passWord,
			boolean newWindowRequired) {
		super(site, viewPath, welcomePath, userName, passWord,
				newWindowRequired);
		// TODO Auto-generated constructor stub
	}

	protected CommonWaveformTests(String site, String viewPath,
			String welcomePath, String userName, String passWord,
			WebDriver existingDriver) {
		super(site, viewPath, welcomePath, userName, passWord, existingDriver);
		// TODO Auto-generated constructor stub
	}

	protected CommonWaveformTests(String site, String viewPath,
			String welcomePath, String userName, String passWord,
			boolean newWindowRequired, BrowserEnum whichBrowser) {
		super(site, viewPath, welcomePath, userName, passWord,
				newWindowRequired, whichBrowser);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Checks selection and expansion/condensing capabilities in the folder tree.  Note:  This does not check the validity
	 * of anything stored in the tree
	 */
	public void validateFolderTree() {
		try {
			List<WebElement> folderArrows = portletDriver.findElements(By.className("ui-icon-triangle-1-e"));
			
			while(!(folderArrows.isEmpty())) { 
				
				for(WebElement folderArrow : folderArrows) {
					folderArrow.click();
					portletDriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
				}
				
				folderArrows = portletDriver.findElements(By.className("ui-icon-triangle-1-e"));
			}
			
			portletDriver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
			
		} catch(StaleElementReferenceException ser) {
			ser.printStackTrace();
			return;
		} catch(IndexOutOfBoundsException iob) {
			iob.printStackTrace();
			return;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
				
	}
	
	protected void simulateDragAndDrop(String treeNode, String tableElement) {		
		String fullHostName = host;		
		
		if(!(host.contains("http://"))) {
			fullHostName = "http://" + host + "/" + portletPage;
		}

		WebDriverBackedSelenium selenium = new WebDriverBackedSelenium(portletDriver, fullHostName + "/" + portletPage);
		
		selenium.dragAndDropToObject(treeNode, tableElement);
		portletLogMessages.add("Succesfully dragged the ECG entry into the datatable");
	}

}
