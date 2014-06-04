/**
 * 
 */
package edu.jhu.cvrg.waveformtests.visualize;

import java.io.IOException;
import java.util.ArrayList;
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
import edu.jhu.cvrg.waveformtests.WaveformTestProperties;
import edu.jhu.cvrg.waveformtests.UIComponentChecks;

/**
 * @author bbenite1
 *
 */
public class VisualizeTester extends CommonWaveformTests implements UIComponentChecks{
	
	private DisplayPanelEnum currentPage;
	private WaveformTestProperties testProps;
	
	public VisualizeTester(String site, String viewPath, String welcomePath, String userName, String passWord, boolean loginRequired, BrowserEnum whichBrowser) {
		super(site, viewPath, welcomePath, userName, passWord, loginRequired, whichBrowser);
	}
	
	public VisualizeTester(String site, String viewPath, String welcomePath,
			String userName, String passWord, boolean loginRequired) {
		super(site, viewPath, welcomePath, userName, passWord, loginRequired);
		
		currentPage = DisplayPanelEnum.INITIAL;
		testProps = WaveformTestProperties.getInstance();
		
	}
	
	public VisualizeTester(String site, String viewPath, String welcomePath, String userName, String passWord, WebDriver existingDriver) {
		super(site, viewPath, welcomePath, userName, passWord, existingDriver);
	}
	
	public void testVisualizeViews() throws IOException {
		goToPage();		
		selectECGFromTree();
		validateButtons();
		
		pickLead();
		
		logger.addToLog(portletLogMessages, TestNameEnum.WAVEFORMVISUALIZE);
		logger.addToLog(seleniumLogMessages, TestNameEnum.SELENIUM);
	}
	
	public void pickLead() {
		// TODO:  Select a lead graph from the dygraph screen.  Figure out some way to get the 
		//			dygraphs to work in Selenium
	}

	public void selectECGFromTree() {
		// expand the folder tree to expose the node for selection
		try {
			this.validateFolderTree();
		} catch (NoSuchElementException nse) {
			seleniumLogMessages.add("An element was not found while validating the folder tree, here is more information:  \n" + LogfileManager.extractStackTrace(nse));
		} catch (StaleElementReferenceException ser) {
			seleniumLogMessages.add("An element in the folder tree was present at some point in the DOM but is no longer valid, here is more information:  \n" + LogfileManager.extractStackTrace(ser));
		} catch (Exception e) {
			seleniumLogMessages.add("A different error has cropped up in the folder tree, here is more information:  \n" + LogfileManager.extractStackTrace(e));
		}
		
		try {
			this.simulateDragAndDrop("xpath=//span[@class='ui-treenode-icon ui-icon ui-icon ui-icon-note' and 1]", "id=A1576:formVisualize:availableStudy_data");
		} catch (Exception e) {
			seleniumLogMessages.add("Something went wrong while simulating the drag and drop, here is more information:  \n" + LogfileManager.extractStackTrace(e));
		}
		
		try {
			pickECGForDisplay();
		} catch (NoSuchElementException nse) {
			seleniumLogMessages.add("An element was not found while selecting an ECG, here is more information:  \n" + LogfileManager.extractStackTrace(nse));
		} catch (StaleElementReferenceException ser) {
			seleniumLogMessages.add("An element in the ECG Selection table was present at some point in the DOM but is no longer valid, here is more information:  \n" + LogfileManager.extractStackTrace(ser));
		} catch (Exception e) {
			seleniumLogMessages.add("A different error has cropped up in the ECG Selection Table, here is more information:  \n" + LogfileManager.extractStackTrace(e));
		}
				
	}

	public void pickECGForDisplay() {
		portletDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		
		List<WebElement> ecgRows = portletDriver.findElements(By.xpath("//td[@role='gridcell']"));
		if(!(ecgRows.isEmpty())) {
		ecgRows.get(0).click();
		portletDriver.findElement(By.id("A1576:formVisualize:btnView12LeadECGTest")).click();
		portletDriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		
		currentPage = DisplayPanelEnum.MULTILEAD;
		}
		else {
			throw new NoSuchElementException("Visualize.java, line " + new Throwable().getStackTrace()[0].getLineNumber() +  " - pickECGForDisplay():  Unable to select records in the selection table on Visualize Portlet");  // the new Throwable object was the easiest way to get the line number dynamically
		}
	}

	@Override
	public void validateButtons() {
		// TODO Auto-generated method stub
		switch(currentPage) {
		case INITIAL:
			// Testing buttons on initial view
			break;
		case MULTILEAD:
			// primarily testing the navigation buttons
			ArrayList<String> navButtons = testProps.getMultiLeadNavButtons();
			
			for (String buttonID : navButtons) {
				portletDriver.findElement(By.id(buttonID)).click();
				portletDriver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			}
			
			String textInputID = testProps.getJumpToInputBox();
			String jumpButtonID = testProps.getJumpToButton();
			
			WebElement textInput = portletDriver.findElement(By.id(textInputID));
			textInput.click();
			textInput.sendKeys("5");
			portletDriver.findElement(By.id(jumpButtonID)).click();
			
			portletDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			
			break;
		case SINGLELEAD:
			// testing the navigation buttons again
			break;
		case ANNOTATION:
			// buttons on annotation screen
			break;
		default:
			break;	
		}
	}

	@Override
	public void selectSingleECG() {

			this.simulateDragAndDrop("xpath=//span[@class='ui-treenode-label ui-corner-all' and 1]", "id=A1576:formVisualize:availableStudy_data");

	}

	@Override
	public void selectMultipleECGs() {
		// TODO Auto-generated method stub
		
	}

}
