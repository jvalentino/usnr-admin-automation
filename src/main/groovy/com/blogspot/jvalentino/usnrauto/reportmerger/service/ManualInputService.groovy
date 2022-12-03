package com.blogspot.jvalentino.usnrauto.reportmerger.service

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.DataCategory;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.History;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.HistoryElement;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.ManualInputReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.ManualInputRules;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.ManualMemberRecord;
import com.blogspot.jvalentino.usnrauto.util.FormatUtil;

class ManualInputService {

	
	ManualInputReport parse(File input) throws Exception {
		ManualInputReport report = new ManualInputReport()
		report.members = new ArrayList<ManualMemberRecord>()
		report.file = input
		
		FileInputStream file = new FileInputStream(input)

		//Create Workbook instance holding reference to .xlsx file
		XSSFWorkbook workbook = new XSSFWorkbook(file)

		//Get first/desired sheet from the workbook
		XSSFSheet sheet = workbook.getSheetAt(0)
		handleMembership(sheet, report)
		
		// attempt to get rules
		XSSFSheet rules = workbook.getSheet("Rules")
		report.rules = handleRules(rules, report)
		
		// attempt to get settings
		XSSFSheet settings = workbook.getSheet("Settings")
		handleSettings(settings, report)
		
		// attempt to get history
		report.history = ServiceBus.getInstance().getHistoryService().loadHistoryFromWorkbook(workbook)
		
		file.close()
		
		return report
	}
	
	private void handleMembership(XSSFSheet sheet, ManualInputReport report) {
		Iterator<Row> rows=sheet.rowIterator()
		while (rows.hasNext()) {
			Row row = (Row) rows.next()

			if (row.getRowNum() == 1) {
				// secondary header
			
				Iterator cells = row.cellIterator()
				while (cells.hasNext()) {
					Cell cell = (Cell) cells.next()
					
					String cellValue = cell.toString()
					int columnIndex = cell.columnIndex
					
					// the first three columns are non dynamic
					if (columnIndex > 2) {
						
						// look for the primary header above this row
						Cell headerCell = sheet.getRow(0).getCell(cell.columnIndex)
						String headerCellValue = headerCell.toString()
												
						report.secondaryColumnHeaders.add(cellValue)
						
						if (headerCell != null) {
							report.primaryColumnHeaders.add(headerCell.toString())
						} else {
							report.primaryColumnHeaders.add(report.primaryColumnHeaders.get(columnIndex - 1 - 3))
						}
					}
				}
			
			} else if (row.getRowNum() > 1){
				//member row
				ManualMemberRecord record = this.createMemberRecord(row, report.secondaryColumnHeaders.size())
				report.getMembers().add(record)
			
			}
			
		}
	}
	
	private ManualMemberRecord createMemberRecord(Row row, int columns) {
		ManualMemberRecord record = new ManualMemberRecord()
		record.values = new String[columns]
		
		Iterator cells = row.cellIterator()
		
		while (cells.hasNext()) {
			Cell cell = (Cell) cells.next()
			
			int column = cell.getColumnIndex()
			
			String value = cell.toString()
			
			switch (column) {
				case 0:
					record.setLastName(value.toUpperCase())
				break
				
				case 1:
					record.setFirstName(value.toUpperCase())
				break
				
				case 2:
					record.setRank(value.toUpperCase())
				break
				
				default:
					record.values[column - 3] = value
				break
			}
			
		}
		
		// replace nulls with blanks
		for (int i = 0; i < record.values.length; i++) {
			if (record.values[i] == null) {
				record.values[i] = ""
			}
		}
		
		
		return record
	}
	
	private ManualInputRules handleRules(XSSFSheet sheet, ManualInputReport report) {
		ManualInputRules rules = new ManualInputRules()
		
		if (sheet == null) {
			return rules
		}
		
		Iterator<Row> rows=sheet.rowIterator()
		while (rows.hasNext()) {
			Row row = (Row) rows.next()
			
			// ignore the first row
			if (row.rowNum == 0)
				continue
			
			String action = row.getCell(0).toString()
			String value = row.getCell(1).toString()
			
			if (action.equalsIgnoreCase("ignore")) {
				rules.ignorePeople.add(value)
			} else if (action.equalsIgnoreCase("ignore course")) {
				rules.ignoreCourses.add(value)
			}
		}
		
		return rules
		
	}
	
	private handleSettings(XSSFSheet sheet, ManualInputReport report) {
		if (sheet == null) {
			return
		}
		
		Iterator<Row> rows=sheet.rowIterator()
		while (rows.hasNext()) {
			Row row = (Row) rows.next()
			
			String setting = row.getCell(0).toString()
			String value = row.getCell(1).toString()
			
			if (setting.equalsIgnoreCase("Spreadsheet Footer")) {
				report.spreadsheetFooter = value
			} else if (setting.equalsIgnoreCase("PDF Footer")) {
				report.pdfFooter = value
			} else if (setting.equalsIgnoreCase("Training Email Header")) {
				report.trainingEmailHeader = value
			} else if (setting.equalsIgnoreCase("Training SMS Header")) {
				report.trainingSmsHeader = value
			} else if (setting.equalsIgnoreCase("Email Requires Authorization")) {
				report.emailRequiresAuthorization = value
			} else if (setting.equalsIgnoreCase("TLS Enabled")) {
				report.emailTlsEnabled = value
			} else if (setting.equalsIgnoreCase("Email Host")) {
				report.emailHost = value
			} else if (setting.equalsIgnoreCase("Email Port")) {
				report.emailPort = Double.parseDouble(value).intValue()
			} else if (setting.equalsIgnoreCase("Email Username")) {
				report.emailUsername = value
			}
			
		}
		
	}
	
	

}
