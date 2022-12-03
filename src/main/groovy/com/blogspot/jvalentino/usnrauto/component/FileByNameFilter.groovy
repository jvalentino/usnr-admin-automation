package com.blogspot.jvalentino.usnrauto.component

import java.io.File;

import javax.swing.filechooser.FileFilter;


class FileByNameFilter extends FileFilter {

	private String fileName
	
	FileByNameFilter(String fileName) {
		this.fileName = fileName
	}
	
	@Override
	boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		
		if (f.getName().equalsIgnoreCase(fileName)) {
			return true
		}
		
		return false;
	}

	@Override
	String getDescription() {
		return this.fileName + " only"
	}
	
	

}
