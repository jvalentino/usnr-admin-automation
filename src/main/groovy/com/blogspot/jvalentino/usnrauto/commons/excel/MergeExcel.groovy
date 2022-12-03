package com.blogspot.jvalentino.usnrauto.commons.excel

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * http://blog.sodhanalibrary.com/2014/11/merge-excel-files-using-java.html#.VO-LQ8Zkt9k
 * 
 * @author jvalentino2
 *
 */
class MergeExcel {
	
	
	/**
	 * Copies sheet into mergedSheet, that's a lot of sheet.
	 *  
	 * @param mergedSheet
	 * @param sheet
	 */
	public static void addSheet(XSSFSheet mergedSheet, XSSFSheet sheet) {
		// map for cell styles
		Map<Integer, XSSFCellStyle> styleMap = new HashMap<Integer, XSSFCellStyle>();

		// This parameter is for appending sheet rows to mergedSheet in the end
		int len = mergedSheet.getLastRowNum();
		for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {

			XSSFRow row = sheet.getRow(j);
			XSSFRow mrow = mergedSheet.createRow(len + j);

			if (row == null) {
				println "WARNING: Row at " + j + " is null"
				continue
			}
			
			for (int k = row.getFirstCellNum(); k < row.getLastCellNum(); k++) {
				XSSFCell cell = row.getCell(k);
				XSSFCell mcell = mrow.createCell(k);

				if (cell.getSheet().getWorkbook() == mcell.getSheet()
				.getWorkbook()) {
					mcell.setCellStyle(cell.getCellStyle());
				} else {
					int stHashCode = cell.getCellStyle().hashCode();
					XSSFCellStyle newCellStyle = styleMap.get(stHashCode);
					if (newCellStyle == null) {
						newCellStyle = mcell.getSheet().getWorkbook()
								.createCellStyle();
						newCellStyle.cloneStyleFrom(cell.getCellStyle());
						styleMap.put(stHashCode, newCellStyle);
					}
					mcell.setCellStyle(newCellStyle);
				}

				switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_FORMULA:
						mcell.setCellFormula(cell.getCellFormula());
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						mcell.setCellValue(cell.getNumericCellValue());
						break;
					case HSSFCell.CELL_TYPE_STRING:
						mcell.setCellValue(cell.getStringCellValue());
						break;
					case HSSFCell.CELL_TYPE_BLANK:
						mcell.setCellType(HSSFCell.CELL_TYPE_BLANK);
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN:
						mcell.setCellValue(cell.getBooleanCellValue());
						break;
					case HSSFCell.CELL_TYPE_ERROR:
						mcell.setCellErrorValue(cell.getErrorCellValue());
						break;
					default:
						mcell.setCellValue(cell.getStringCellValue());
						break;
				}
			}
		}
	}
}
