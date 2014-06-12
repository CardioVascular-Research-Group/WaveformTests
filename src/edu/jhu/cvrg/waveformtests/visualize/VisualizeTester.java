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
		
		currentPage = DisplayPanelEnum.INITIAL;
		testProps = WaveformTestProperties.getInstance();
	}
	
	public VisualizeTester(String site, String viewPath, String welcomePath,
			String userName, String passWord, boolean loginRequired) {
		super(site, viewPath, welcomePath, userName, passWord, loginRequired);
		
		currentPage = DisplayPanelEnum.INITIAL;
		testProps = WaveformTestProperties.getInstance();
		
	}
	
	public VisualizeTester(String site, String viewPath, String welcomePath, String userName, String passWord, WebDriver existingDriver) {
		super(site, viewPath, welcomePath, userName, passWord, existingDriver);
		
		currentPage = DisplayPanelEnum.INITIAL;
		testProps = WaveformTestProperties.getInstance();
	}
	
	public void testVisualizeViews() throws IOException {
		goToPage();
		selectECGFromTree();
		validateButtons();
		
		pickLead();
		
		logger.addToLog(portletLogMessages, TestNameEnum.WAVEFORMVISUALIZE);
		if(!seleniumLogMessages.isEmpty()) {
			logger.addToLog(seleniumLogMessages, TestNameEnum.SELENIUM);
		}
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
			portletLogMessages.add("Clicked on a record with the name ");
			portletDriver.findElement(By.id("A1576:formVisualize:btnView12LeadECGTest")).click();
			
			portletDriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
			
			currentPage = DisplayPanelEnum.MULTILEAD;
		}
		else {
			throw new NoSuchElementException("Visualize.java, line " + new Throwable().getStackTrace()[0].getLineNumber() +  " - pickECGForDisplay():  Unable to select records in the selection table on Visualize Portlet.  Either they do not exist or Selenium was unable to locate them");  // the new Throwable object was the easiest way to get the line number dynamically
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
			portletDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			ArrayList<String> navButtons = testProps.getMultiLeadNavButtons();
			
			portletLogMessages.add("Checking navigation buttons");
			for (String buttonID : navButtons) {
				try {
					WebElement currentButton = portletDriver.findElement(By.id(buttonID));
					String buttonName = currentButton.findElement(By.xpath("//span[@class='ui-button-text ui-c']")).getText();
					portletLogMessages.add("Clicking on the " + buttonName + " button");
					
					portletDriver.findElement(By.xpath("//button[@id='" + buttonID + "']/span[@class='ui-button-text ui-c']")).click();
					portletDriver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
				} catch (NoSuchElementException nse) {
					seleniumLogMessages.add("An element was not found while validating the folder tree, here is more information:  \n" + LogfileManager.extractStackTrace(nse));
				} catch (StaleElementReferenceException ser) {
					seleniumLogMessages.add("An element in the folder tree was present at some point in the DOM but is no longer valid, here is more information:  \n" + LogfileManager.extractStackTrace(ser));
				} catch (Exception e) {
					seleniumLogMessages.add("A different error has cropped up in the folder tree, here is more information:  \n" + LogfileManager.extractStackTrace(e));
				}
			}
			
			String textInputID = testProps.getJumpToInputBox();
			String jumpButtonID = testProps.getJumpToButton();
			
			WebElement textInput = portletDriver.findElement(By.id(textInputID));
			textInput.click();
			textInput.sendKeys("5");
			portletLogMessages.add("Entered 5 seconds for the jump to time input field");
			
			portletDriver.findElement(By.id(jumpButtonID)).click();
			portletLogMessages.add("Clicked the Jump To Time (Sec) button");
			
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
