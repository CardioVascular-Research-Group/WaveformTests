package edu.jhu.cvrg.seleniummain;

import java.io.IOException;

import edu.jhu.cvrg.authenticationtests.GlobusLogin;
import edu.jhu.cvrg.waveformtests.WaveformTestProperties;
import edu.jhu.cvrg.waveformtests.analyze.AnalyzeTester;
import edu.jhu.cvrg.waveformtests.download.DownloadTester;
import edu.jhu.cvrg.waveformtests.upload.UploadTester;
import edu.jhu.cvrg.waveformtests.visualize.VisualizeTester;

public class WaveformTestController extends TestController {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if(args.length != 5 || args[0].equals("--help")) {
			System.out.println("Usage:  CVRG_Tests.jar <LOGON | WAVEFORM | ALL> <hostname> <username> <password> <logfile_location>\n");
			System.exit(0);
		}

		String testType = args[0];
		String hostname = args[1];
		String username = args[2];
		String password = args[3];
		String logfilePath = args[4];

		String commonPropsLocation = "./src/testconfig/global_properties.config";
		String waveformPropsLocation = "./src/testconfig/waveform_properties.config";
		
		// initialize the Singleton instance of the global properties
		CommonProperties init = CommonProperties.getInstance();
		init.loadConfiguration(commonPropsLocation);
		
		WaveformTestController mainControl = new WaveformTestController(hostname, logfilePath, username, password);
		
		TestControlTypeEnum testTypeEnum = TestControlTypeEnum.valueOf(testType);
		
		switch(testTypeEnum) {
			case LOGON:
				mainControl.testAuthentication();
				break;
			case WAVEFORM:
				mainControl.testWaveform(waveformPropsLocation);
				break;
			case ALL:
				mainControl.testAuthentication();
				mainControl.testWaveform(waveformPropsLocation);
				break;
			default:
				// Exit
				System.out.println("Invalid test option entered.\n");
				System.out.println("Usage:  CVRG_Tests.jar <LOGON | WAVEFORM | CEP | ALL> <hostname> <username> <password> <logfile_location>\n");
				System.exit(0);
				break;
		}
		
	}
	
	public WaveformTestController(String newHostname, String newLogfilePath, String newUsername, String newPassword) {
		super(newHostname, newLogfilePath, newUsername, newPassword);
	}
	
	public void testAuthentication() {
		
		try {
			setup();
			
			String mainUser = commonProps.getMainUser();
			String mainPassword = commonProps.getMainPassword();
			String newUser = commonProps.getAltUser();
			String newPassword = commonProps.getAltPassword();
			
			GlobusLogin gLogin = new GlobusLogin(hostname, initialWelcomePath, mainUser, mainPassword, true, whichBrowser);
			boolean loginComplete;
			
			loginComplete = gLogin.testGlobus();
			
			if(loginComplete) {
				gLogin.logout();
			}
			gLogin.close();
			
			
			loginComplete = gLogin.testGlobus(newUser, newPassword, true);
			
			if(loginComplete) {
				gLogin.logout();
			}
			gLogin.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void testWaveform(String propertiesFileLocation) {
		
		WaveformTestProperties testProps = WaveformTestProperties.getInstance();
		
		try {
			setup();
			
			testProps.loadConfiguration(propertiesFileLocation);
			
			LogfileManager logger = LogfileManager.getInstance();
			logger.addToLog("Waveform 3 Selenium Test Begin:  " + dateFormat.format(todaysDate.getTime()));
		
			String uploadPath = testProps.getUploadPath();
			String visualizePath = testProps.getVisualizePath();
			String welcomePath = testProps.getWelcomePath();
			String analyzePath = testProps.getAnalyzePath();
			String downloadPath = testProps.getDownloadPath();
			
			UploadTester upload = new UploadTester(hostname, uploadPath, welcomePath, username, password, true);
			
			
			upload.login();
			upload.uploadFile();
			upload.logout();
			
			AnalyzeTester analysis = new AnalyzeTester(hostname, analyzePath, welcomePath, username, password, true);
			
			analysis.login(false);
			analysis.analyzeOneECG();
			analysis.logout();
			
			VisualizeTester visualize = new VisualizeTester(hostname, visualizePath, welcomePath, username, password, true);
			
			visualize.login(false);
			visualize.testVisualizeViews();
			visualize.logout();
			
			DownloadTester download = new DownloadTester(hostname, downloadPath, welcomePath, username, password, true);
			
			download.login(false);
			download.testDownloadPage();
			download.logout();
			download.close();
	
			logger.addToLog("Waveform 3 Selenium Tests Completed");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	

}
