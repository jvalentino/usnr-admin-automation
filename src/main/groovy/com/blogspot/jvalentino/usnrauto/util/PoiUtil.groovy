package com.blogspot.jvalentino.usnrauto.util

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

class PoiUtil {

	static String[] rowToStrings(Row row) {
		List<String> results = new ArrayList<String>()
		
		Iterator cells = row.cellIterator()
		while (cells.hasNext()) {
			Cell cell = (Cell) cells.next()
			String cellValue =  cell.toString()
			results.add(cellValue)
		}
		
		return FormatUtil.listToArray(results)
		
	}
	
	/**
	 * Utility for looking up the index in the given map using the given name, and returning
	 * the value in the row using that index as a column
	 * @param row
	 * @param map
	 * @param name
	 * @return
	 */
	static String getColumnValue(Row row, Map<String, Integer> map, String name) {
		return row.getCell(map.get(name)).toString().trim()
	}
	
	/**
	 * Utility for finding the first row in a spreadsheet that contains the given text
	 * @param text
	 * @param sheet
	 * @return
	 */
	static Integer findTheFirstRowContainingThis(String text, XSSFSheet sheet) {
		Iterator<Row> rows=sheet.rowIterator()
		while (rows.hasNext()) {
			Row row = (Row) rows.next()
			
			Iterator cells = row.cellIterator()
			
			while (cells.hasNext()) {
				Cell cell = (Cell) cells.next()
				
				if (cell.toString().contains(text)) {
					return row.rowNum
				}
			}
		}
		
		return null
	}
	
	/**
	 * Utility for turning a row into a map of columm text to column number
	 * @param sheet
	 * @param rowNum
	 * @return
	 */
	static Map<String, Integer> getHeaderTextToColumnMappings(XSSFSheet sheet, int rowNum) {
		
		Row row = sheet.getRow(rowNum)
		
		Map map = new LinkedHashMap<String, Integer>()
		Iterator cells = row.cellIterator()
		
		while (cells.hasNext()) {
			Cell cell = (Cell) cells.next()
			
			map.put(cell.toString(), cell.columnIndex)
			
		}
		
		return map
	}

	/**
	 * Utility for throwing an exception if a column does not exist in the given map
	 * @param map
	 * @param columns
	 * @throws Exception
	 */
	static void throwExceptionIfMapDoesNotHave(Map<String, Integer> map, List<String> columns) throws Exception {
		for (String column : columns) {
			if (!map.containsKey(column)) {
				throw new Exception("Spreadsheet does not contain the column \"" + column + "\"")
			}
		}
	}
}
