package com.blogspot.jvalentino.usnrauto.reportmerger.service

import groovy.util.logging.Log4j;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.esams.EsamsRecord;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.esams.EsamsReport;
import com.blogspot.jvalentino.usnrauto.util.FormatUtil;
import com.blogspot.jvalentino.usnrauto.util.PoiUtil;

@Log4j
class EsamsService {
	
	static final String NAME = "Name(Last,First)"
	static final String TITLE = "Course Title"
	static final String REQUIRED_BY_DATE = "Required By Date"
	static final String COURSE_ID = "Course Id"

	EsamsReport parse(File input) throws Exception {
		EsamsReport report = new EsamsReport()
		
		FileInputStream file = new FileInputStream(input)
	
		XSSFWorkbook workbook = new XSSFWorkbook(file)
		XSSFSheet sheet = workbook.getSheetAt(0)
		
		Integer headerRow = PoiUtil.findTheFirstRowContainingThis(COURSE_ID, sheet)
		
		if (headerRow == null) {
			String error = "The row containing the text \"${COURSE_ID}\" could not be found, so we don't know where the data is"
			throw new Exception(error)
		}
		
		Map<String, Integer> headerToColumnMap = PoiUtil.getHeaderTextToColumnMappings(sheet, headerRow)
		
		PoiUtil.throwExceptionIfMapDoesNotHave(headerToColumnMap, [NAME, TITLE, REQUIRED_BY_DATE])
		
		Iterator<Row> rows=sheet.rowIterator()
		while (rows.hasNext()) {
			Row row = (Row) rows.next()
			
			if (row.rowNum <= headerRow) {
				continue
			}
			
			String name = PoiUtil.getColumnValue(row, headerToColumnMap, NAME)
			String title = PoiUtil.getColumnValue(row, headerToColumnMap, TITLE)
			String dateString = PoiUtil.getColumnValue(row, headerToColumnMap, REQUIRED_BY_DATE)
			Date requiredDate = FormatUtil.toDateInAllPossibleFormats(dateString)
			
			EsamsRecord record = new EsamsRecord(name: name, title: title, requiredDate: requiredDate)
			report.records.add(record)
		}
		
		report.file = input
		
		return report
	}
	
	
}
