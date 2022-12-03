package com.blogspot.jvalentino.usnrauto.component

import javax.swing.*;
import javax.swing.filechooser.*;

import org.apache.commons.io.FilenameUtils;

import com.blogspot.jvalentino.usnrauto.util.FormatUtil;

class CustomFileFilter extends FileFilter {
	
	private String[] extensions
	
	CustomFileFilter(String extension) {
		this.extensions = [extension]
	}
	
	CustomFileFilter(String[] extensions) {
		this.extensions = extensions
	}
	
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		
		for (String extension : extensions) {
		
			String found = FilenameUtils.getExtension(f.getName())
			
			if (found.equalsIgnoreCase(extension)) {
				return true
			}
		}
		
		return false;
	}
 
	//The description of this filter
	public String getDescription() {
		String supported = FormatUtil.arrayToCommaSeparatedString(extensions)
		return supported + " files only";
	}
}
