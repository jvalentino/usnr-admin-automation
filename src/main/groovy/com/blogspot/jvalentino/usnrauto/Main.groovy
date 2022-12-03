package com.blogspot.jvalentino.usnrauto

import java.awt.Color;

import javax.swing.JFrame;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsELearningReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsGMTMemberReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.imr.ImrReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.individual.IndividualSummary;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.ManualInputReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.ruad.RuadMemberEntry;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.ruad.RuadReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.SummaryReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.service.FltMpsService;
import com.blogspot.jvalentino.usnrauto.reportmerger.service.RuadService;
import com.blogspot.jvalentino.usnrauto.reportmerger.service.ServiceBus;
import com.blogspot.jvalentino.usnrauto.component.SplashScreen;
import com.blogspot.jvalentino.usnrauto.main.AppState;
import com.blogspot.jvalentino.usnrauto.main.view.MainView;

class Main {

	public static final String APP_TITLE = "USNR ADMIN AUTOMATION PROJECT"
	
    private ServiceBus bus = ServiceBus.getInstance()
	private AppState appState = AppState.getInstance()
	
	private SplashScreen splash
    
    static main(args) {
        new Main(args)
    }
	
	private loadConfiguration() {
		// load dynamic properties
		final Properties prop = new Properties();
		InputStream is = getClass().getResourceAsStream("/application.properties");
		prop.load(is);
		is.close();
		
		appState.version = prop.get("version")
		appState.buildNumber = prop.get("buildNumber")
		appState.host = prop.get("host")
		appState.binaryName = prop.get("binaryName")
	}
	
	private showSplashScreen() {
		splash = new SplashScreen("/Splashscreen-512x512.png");
		splash.setUndecorated(true);
		splash.setSize(512, 512);
		splash.setLocationRelativeTo(null);
		splash.setVisible(true);
		splash.setBackground(new Color(1.0f,1.0f,1.0f,0.5f));
		
		Thread.sleep(1000L)
	}
    
    Main(String[] args) {
        
		if (args.length > 0) {
			
			bus.getCommandLineService().launchCommandLine(args)
			
		} else {
			println "No command-line arguments provided, launching graphical user interface"
			println " "
			println "If you want to use this program at the command-line, here is how:"
			println " "
			bus.getCommandLineService().printUsage()
			
			loadConfiguration()
			
			showSplashScreen()
			
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					MainView view = new MainView()
					view.setTitle(APP_TITLE + " " + appState.version + "." +  appState.buildNumber)
					view.setSize(1000 + 320, 1020)
					view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
					view.setLocationRelativeTo(null)
					view.setVisible(true)
					splash.setVisible(false)
				}
			});
						
		}
		       
    }
	
	
}
