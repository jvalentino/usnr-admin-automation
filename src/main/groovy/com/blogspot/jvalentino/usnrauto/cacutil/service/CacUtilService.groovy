package com.blogspot.jvalentino.usnrauto.cacutil.service

import java.io.File;
import java.util.prefs.Preferences;

import com.blogspot.jvalentino.usnrauto.util.CommandLineUtil;
import com.blogspot.jvalentino.usnrauto.util.OsType;

class CacUtilService {

	private static final CACKEY = "CACKEY"
	private static final PIN = "PIN"
	
	protected Preferences prefs = Preferences.userRoot().node(getClass().getName())
	
	String getLastUsedPin() {
		return prefs.get(PIN, null)
	}
	
	void storePin(String pin) {
		prefs.put(PIN, pin)
	}
	
	File getLastUsedCacKeyLibrary() {
		String result = prefs.get(CACKEY, null)
	
		if (result != null) {
			return new File(result)
		} else {
			return null
		}

	}
	
	void storeLastUsedCacKeyLibrary(File file) {
		prefs.put(CACKEY, file.getAbsolutePath())
	}
	
	File searchForCacKeyLibrary() {
		File pref = this.getLastUsedCacKeyLibrary()
		File found = this.searchForCacKeyLibrary(pref, CommandLineUtil.getOsType())
		
		if (found != null) {
			this.storeLastUsedCacKeyLibrary(found)
		}
		
		return found
	}
	
	
	protected File searchForCacKeyLibrary(File preference, OsType type) throws Exception {
		
		if (preference != null) {
			return preference
		}
		
		switch (type) {
			case OsType.MAC:
				return searchForCacKeyOnMac()
			case OsType.WINDOWS:
				return searchForCackKeyOnWindows()
			default:
				throw new Exception("Unsuppored operating system " + type)
		}
		
		return null
	}
	
	String getCacKeyLibrary(OsType type=CommandLineUtil.getOsType()) {
		switch(type) {
			case OsType.WINDOWS:
				return "libcackey.dll"
			case OsType.MAC:
				return "libcackey.dylib"
		}
		return null
	}
	
	protected File searchForCacKeyOnMac() {
		String[] locations = ["/Library/CACKey"]
		String binary = getCacKeyLibrary(OsType.MAC)
		return this.searchForBinaryInLocations(binary, locations)
	}
	
	protected File searchForCackKeyOnWindows() {
		String[] locations = [
			
		]
		String binary = getCacKeyLibrary(OsType.WINDOWS)
		return this.searchForBinaryInLocations(binary, locations)
	}
	
	/**
	 * Utility for looking for a binary in a list of possible locations
	 * @param binary
	 * @param locations
	 * @return
	 */
	protected File searchForBinaryInLocations(String binary, String[] locations) {
		for (String parent : locations) {
			File file = new File(parent + File.separator + binary)
			
			if (file.exists()) {
				return file
			}
		}
		
		return null
	}
	
	File createCardConfig(File cacKeyLibrary) {
		
	}
}
