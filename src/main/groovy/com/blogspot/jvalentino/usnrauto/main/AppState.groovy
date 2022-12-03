package com.blogspot.jvalentino.usnrauto.main

import java.util.List;

import javax.swing.JFrame;

import com.blogspot.jvalentino.cac.security.CacIdentity;

/**
 * General singleton for keeping track of application-wide information
 * @author jvalentino2
 *
 */
class AppState {

	private static AppState instance
	
	String version = "1.0"
	String buildNumber = "0"
	String binaryName = "usnr-admin-automation"
	String host = "valentino-tech.com"
	
	List<CacIdentity> identities = new ArrayList<CacIdentity>()
	JFrame frame
	
	private AppState() {
		
	}
	
	static AppState getInstance() {
		if (instance == null)
			instance = new AppState()
		return instance
	}
	
	String getWindowsInstallerUrl() {
		return "http://" + host + "/" + binaryName + "-" + version + "-installer.jar"
	}
	
	String getMacInstallerUrl() {
		return "http://" + host + "/" + binaryName + "-" + version + ".zip"
	}
	
	String getJnlpUrl() {
		return "http://" + host + "/" + binaryName + "/" + binaryName + ".jnlp"
	}
}
