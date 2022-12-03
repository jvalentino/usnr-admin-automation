package com.blogspot.jvalentino.usnrauto.commons

import java.io.File;
import java.util.prefs.Preferences;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.blogspot.jvalentino.usnrauto.commons.excel.CopySheets;
import com.blogspot.jvalentino.usnrauto.commons.excel.CopyType;
import com.blogspot.jvalentino.usnrauto.commons.excel.MergeExcel;
import com.blogspot.jvalentino.usnrauto.util.CommandLineUtil;
import com.blogspot.jvalentino.usnrauto.util.OsType;

class ExcelService {
	
	private static final LIBRE_OFFICE = "LIBRE_OFFICE"
	
	protected Preferences prefs = Preferences.userRoot().node(getClass().getName())
	
	/**
	 * Provides two methods for copying a sheet from another spreadsheet into another workbook.
	 * Found two different methods online, but will the FULL method copies all details it won't
	 * work on larger and more complicated workbooks.
	 * 
	 * @param wb
	 * @param input
	 * @param title
	 * @param type
	 */
	void copySheetFromFileIntoWorkbook(Workbook wb, File input, String title, CopyType type) {
		
		FileInputStream file = new FileInputStream(input)
		XSSFWorkbook workbook = new XSSFWorkbook(file)
		Sheet original = workbook.getSheetAt(0)
		Sheet newSheet = wb.createSheet(title)
		
		if (type == CopyType.FULL)
			CopySheets.copySheets(newSheet, original, true)
		else
			MergeExcel.addSheet(newSheet, original)
			
		file.close()
	}
	
	/**
	 * Utility for loading a CSV file, and adding its content to an existing XLS
	 * workboot as a new sheet
	 * 
	 * @param wb
	 * @param input
	 * @param title
	 */
	void copyCsvFileIntoWorkbook(Workbook wb, File input, String title) {
		InputStream inputStream = new FileInputStream(input)
		Reader reader = new InputStreamReader(new BOMInputStream(inputStream), "UTF-8")
		CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader())
		Map<String,Integer> header = parser.getHeaderMap()
		
		try {
			
			Sheet sheet = wb.createSheet(title)
			
			int rowNum = 0
			Row row = sheet.createRow(rowNum)
			
			int column = 0
			for (String key : header.keySet()) {
				Cell cell = row.createCell(column)
				cell.setCellValue(key)
				column++
			}
			
			rowNum++
			
			for (CSVRecord record : parser) {
				
				row = sheet.createRow(rowNum)
				
				column = 0
				for (String value : record) {
					Cell cell = row.createCell(column)
					cell.setCellValue(value)
					column++
				}
				
				rowNum++
			}
		} finally {
			parser.close()
			reader.close()
		}
	}
	
	/**
	 * Utility to open a file where columns are separated by some character, and copy the
	 * data into a spreadsheet
	 * @param wb
	 * @param file
	 * @param title
	 * @param separator
	 */
	void copySomethingSeparatedFileIntoWorkbook(Workbook wb, File file, String title, String separator) {
		
		Sheet sheet = wb.createSheet(title)
		int rowNum = 0
		
		file.eachLine { String line ->
			Row row = sheet.createRow(rowNum)
			
			String[] split = line.split(separator)
			
			for (int j = 0; j < split.length; j++) {
				Cell cell = row.createCell(j)
				cell.setCellValue(split[j])
			}
			
			rowNum++
			
		}
	}
	
	File getLastUsedLibreOfficeBinary() {
		String result = prefs.get(LIBRE_OFFICE, null)
	
		if (result != null) {
			return new File(result)
		} else {
			return null
		}

	}
	
	void storeLastUserLibreOfficeBinary(File file) {
		prefs.put(LIBRE_OFFICE, file.getAbsolutePath())
	}
	
	File searchForLibreOfficeBinary() {
		File pref = this.getLastUsedLibreOfficeBinary()
		File found = this.searchForLibreOfficeBinary(pref, CommandLineUtil.getOsType())
		
		if (found != null) {
			this.storeLastUserLibreOfficeBinary(found)
		}
		
		return found
	}
	
	/**
	 * If a preferred file exists, it is returned. Otherwise common LibreOffice installation
	 * locations are searched depending on operating system.
	 * 
	 * @param preference
	 * @param type
	 * @return
	 * @throws Exception
	 */
	protected File searchForLibreOfficeBinary(File preference, OsType type) throws Exception {
		
		if (preference != null) {
			return preference
		}
		
		switch (type) {
			case OsType.MAC:
				return searchForLibreOfficeOnMac()			
			case OsType.WINDOWS:
				return searchForLibreOfficeOnWindows()			
			default:
				throw new Exception("Unsuppored operating system " + type)
		}
		
		return null
	}
	
	String getLibreOfficeBinary(OsType type=CommandLineUtil.getOsType()) {
		switch(type) {
			case OsType.WINDOWS:
				return "soffice.exe"
			case OsType.MAC:
				return "soffice"
		}
		return null
	}
	
	protected File searchForLibreOfficeOnMac() {
		String[] locations = ["/Applications/LibreOffice.app/Contents/MacOS"]
		String binary = getLibreOfficeBinary(OsType.MAC)
		return this.searchForBinaryInLocations(binary, locations)
	}
	
	protected File searchForLibreOfficeOnWindows() {
		String[] locations = [
			"C:\\Program Files (x86)\\LibreOffice 4\\program",
			"C:\\Program Files\\LibreOffice 4\\program"
		]
		String binary = getLibreOfficeBinary(OsType.WINDOWS)
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
	
	String executeLibreOfficeCommand(File binary, String text) {
		return CommandLineUtil.executeBinary(binary, text)
	}
	
	File convertXlsToXlsx(File libreOfficeBinary, File input) throws Exception {
		
		String outputDir = input.getParentFile().getAbsolutePath()
		
		String extension = FilenameUtils.getExtension(input.getName())
		
		File output = null
		
		if (extension.equalsIgnoreCase("xlsx")) {
			output = new File(input.getAbsolutePath() + ".xlsx")
			String newName = input.getAbsolutePath() + ".xls"
			input.renameTo(newName)
			input = new File(newName)
		} else {
			output = new File(input.getAbsolutePath() + "x")
		}
		
		// delete the output file if it already exists
		output.delete()
		
		String command = "--headless --convert-to xlsx \"" + input.getAbsolutePath() + "\"" +
			" --outdir \"" + outputDir + "\""
		String result =  this.executeLibreOfficeCommand(libreOfficeBinary, command)
		
		// verify that this file was created with the xlsx extension		
		if (!output.exists()) {
			throw new Exception("Conversion to XLSX did not occur: \n" + result)	
		}
		
		return output
	}
	
	
}
