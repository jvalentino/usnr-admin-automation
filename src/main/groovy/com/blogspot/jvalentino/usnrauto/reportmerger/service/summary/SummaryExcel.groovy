package com.blogspot.jvalentino.usnrauto.reportmerger.service.summary

import java.io.File;
import java.util.Map;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.blogspot.jvalentino.usnrauto.commons.excel.CopyType;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedMember;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.mas.MasCode;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.merge.NrowsOrder;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.SummaryReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.service.ServiceBus;
import com.blogspot.jvalentino.usnrauto.util.FormatUtil;

import static com.blogspot.jvalentino.usnrauto.util.FormatUtil.*;


class SummaryExcel {

	static def officerRanks = FormatUtil.officerRanks
	
	private static int currentPrimaryHeaderIndex = -1
	
	private static final String[] PRIMARY_HEADERS = [HEADER_BLUE, HEADER_GREEN, HEADER_RED, HEADER_TEAL, HEADER_YELLOW]
	
	private static final String DEFAULT = "DEFAULT"
	private static final String HEADER_BLUE = "HEADER_BLUE"
	private static final String HEADER_GREEN = "HEADER_GREEN"
	private static final String HEADER_RED = "HEADER_RED"
	private static final String HEADER_TEAL = "HEADER_TEAL"
	private static final String HEADER_YELLOW = "HEADER_YELLOW"
	
	private static final String SECONDARY_HEADER = "SECONDARY_HEADER"
	
	private static final String CELL_GOOD = "CELL_GOOD"
	private static final String CELL_WARNING = "CELL_WARNING"
	private static final String CELL_BAD = "CELL_BAD"
	
	
	static Map<String, CellStyle> styles
	
	static void generateSpreadsheet(File file, SummaryReport report) throws Exception {
		Workbook wb = new XSSFWorkbook()
		
		styles = createStyles(wb)
		
		createSummarySheet(wb, styles, report)
		
		createSheetForWarnings(wb, report, "Issues (Output)")
		
		//output the history into this workbook
		
		if (report.manualFile != null) {
			ServiceBus.getInstance().getHistoryService().outputHistoryToWorkbook(
				wb, report.history, "History (Output)")
		}
		
		
		ServiceBus.getInstance().getExcelService().copySheetFromFileIntoWorkbook(
			wb, report.ruadFile, "NRRM RUAD (Input)", CopyType.QUICK)
		
		ServiceBus.getInstance().getExcelService().copySheetFromFileIntoWorkbook(
			wb, report.eLearningFile, "FLTMPS eLearning (Input)", CopyType.FULL)
		
		ServiceBus.getInstance().getExcelService().copySheetFromFileIntoWorkbook(
			wb, report.gmtFile, "FLTMPS GMT (Input)", CopyType.FULL)
		
		ServiceBus.getInstance().getExcelService().copySheetFromFileIntoWorkbook(
			wb, report.iaFile, "FLTMPS IA (Input)", CopyType.FULL)
		
		if (report.manualFile != null) {
			ServiceBus.getInstance().getExcelService().copySheetFromFileIntoWorkbook(
				wb, report.manualFile, "Manual (Input)", CopyType.FULL)
		}
		
		if (report.esamsFile != null) {
			ServiceBus.getInstance().getExcelService().copySheetFromFileIntoWorkbook(
				wb, report.esamsFile, "ESAMS (Input)", CopyType.FULL)
		}
		
		if (report.nrowsFile != null) {
			ServiceBus.getInstance().getExcelService().copySomethingSeparatedFileIntoWorkbook(
				wb, report.nrowsFile, "NROWS (Input)", "\t")
		}
		
		ServiceBus.getInstance().getExcelService().copyCsvFileIntoWorkbook(
			wb, report.imrFile, "NRRM IMR (Input)")
		
		copyMasCodesIntoWorkbook(wb, "MAS Codes (Internal Input)")
		
		FileOutputStream out = new FileOutputStream(file);
		wb.write(out);
		out.close();
	}
	
	/**
	 * Generates a sheet that contains all MAS codes
	 * @param wb
	 * @param title
	 */
	static void copyMasCodesIntoWorkbook(Workbook wb, String title) {
		List<MasCode> codes = ServiceBus.getInstance().getMasCodeService().getMasCodes()
		
		Sheet sheet = wb.createSheet(title)
		
		int rowNum = 0
		Row row = sheet.createRow(rowNum)
		row.createCell(0).setCellValue("MAS Code")
		row.createCell(1).setCellValue("Definition")
		sheet.setColumnWidth(1, 100 * 256)
		row.createCell(2).setCellValue("Impacts Readiness?")
		
		rowNum++
		
		for (MasCode code : codes) {
			row = sheet.createRow(rowNum)
			row.createCell(0).setCellValue(code.getName())
			row.createCell(1).setCellValue(code.getText())
			row.createCell(2).setCellValue(code.getImpact())
			rowNum++
		}
		row = sheet.createRow(rowNum)
		rowNum++
		
		row = sheet.createRow(rowNum)
		row.createCell(0).setCellValue("* A list of MAS codes is maintained within the software that generated this file")
	}
		
	/**
	 * Creates the unit summary sheet
	 * @param wb
	 * @param styles
	 * @param report
	 * @throws Exception
	 */
	static void createSummarySheet(Workbook wb, Map<String, CellStyle> styles, SummaryReport report) throws Exception {
		Sheet sheet = wb.createSheet("Unit Summary (Output)")
		
		PrintSetup printSetup = sheet.getPrintSetup()
		printSetup.setLandscape(true)
		
		int rownum = createHeaderRows(sheet, styles, report)
		
		for (MergedMember member : report.getMembers()) {
			createMemberRow(sheet, styles, report, rownum, member)
			rownum++
		}
		
		Footer footer = sheet.getFooter();
		footer.setCenter(report.spreadsheetFooter)
		
	}
	
	static int createHeaderRows(Sheet sheet, Map<String, CellStyle> styles, SummaryReport report) {
		
		Row headerRow1 = sheet.createRow(0)
		Row headerRow2 = sheet.createRow(1)
		Row headerRow3 = sheet.createRow(2)
		
		int column = 0
		
		// general info
		String generalInfoPrimaryHeader = nextHeader()
		
		createCell(headerRow1, column, "General Info (Source: NRRM RUAD)", generalInfoPrimaryHeader)
		createCell(headerRow2, column, "Last Name", SECONDARY_HEADER)
		createCell(headerRow3, column, "-")
		sheet.setColumnWidth(column, 15 * 256)
		column++
		
		createCell(headerRow1, column, "", generalInfoPrimaryHeader)
		createCell(headerRow2, column, "First Name", SECONDARY_HEADER)
		createCell(headerRow3, column, "-")
		sheet.setColumnWidth(column, 15 * 256)
		column++
		
		createCell(headerRow1, column, "", generalInfoPrimaryHeader)
		createCell(headerRow2, column, "Rank/Rate", SECONDARY_HEADER)
		createCell(headerRow3, column, "-")
		sheet.addMergedRegion(new CellRangeAddress(headerRow1.rowNum,headerRow1.rowNum,column - 2,column))
		column++
						
		// general readiness
		String readinessPrimaryHeader = nextHeader()
		createCell(headerRow1, column, "General Readiness (Source: NRRM RUAD)", readinessPrimaryHeader)
		createCell(headerRow2, column, "PRD", SECONDARY_HEADER)
		createCell(headerRow3, column, fractionToPercentage(report.getPrdPercentage()), styleForValue(report.getPrdPercentage()))
		sheet.setColumnWidth(column, 12 * 256)
		column++
		
		createCell(headerRow1, column, "", readinessPrimaryHeader)
		createCell(headerRow2, column, "MAS-A", SECONDARY_HEADER)
		createCell(headerRow3, column, "-")
		column++
		
		createCell(headerRow1, column, "", readinessPrimaryHeader)
		createCell(headerRow2, column, "MAS-M", SECONDARY_HEADER)
		createCell(headerRow3, column, "-")
		column++
		
		createCell(headerRow1, column, "", readinessPrimaryHeader)
		createCell(headerRow2, column, "MAS-T", SECONDARY_HEADER)
		createCell(headerRow3, column, "-")
		sheet.addMergedRegion(new CellRangeAddress(headerRow1.rowNum,headerRow1.rowNum,column - 3,column))
		column++
		
		// medical
		String medicalPrimaryHeader = nextHeader()
		createCell(headerRow1, column, "Medical (Source: NRRM IMR)", medicalPrimaryHeader)
		createCell(headerRow2, column, "IMR", SECONDARY_HEADER)
		createCell(headerRow3, column, fractionToPercentage(report.getImrPercentage()), styleForValue(report.getImrPercentage()))
		sheet.setColumnWidth(column, 25 * 256)
		column++
		
		// General eLearning
		String eLearningPrimaryHeader = nextHeader()
		for (int i = 0; i < report.geteLearningCourseNames().length; i++) {
			
			if (i == 0) {
				createCell(headerRow1, column, "General e-Learning (Source: FLTMPS e-Learning)", eLearningPrimaryHeader)
			} else {
				createCell(headerRow1, column, "", eLearningPrimaryHeader)
			}
			
			sheet.setColumnWidth(column, 15 * 256)
			
			createCell(headerRow2, column, report.geteLearningCourseNames()[i], SECONDARY_HEADER)
			
			createCell(headerRow3, column,
				fractionToPercentage(report.geteLearningCourseCompletionPercentages()[i]),
				styleForValue(report.geteLearningCourseCompletionPercentages()[i]))
			
			column++
		}
		sheet.addMergedRegion(new CellRangeAddress(
			headerRow1.rowNum,
			headerRow1.rowNum,
			column - report.geteLearningCourseNames().length,
			column - 1))
		
		// GMT
		String gmtPrimaryHeader = nextHeader()
		for (int i = 0; i < report.getGmtCourseNames().length; i++) {
			String name = report.getGmtCourseNames()[i]
			String category = report.getGmtCourseCategories()[i]
			
			String categoryLabel = "(" + category + ")"
			
			if (category.equalsIgnoreCase("CATEGORY ONE")) {
				categoryLabel = "(I)"
			} else if (category.equalsIgnoreCase("CATEGORY TWO")) {
				categoryLabel = "(II)"
			}
			
			sheet.setColumnWidth(column, 15 * 256)
			
			String formattedName = name + " " + categoryLabel
			 
			if (i == 0) {
				createCell(headerRow1, column, "GMT (Source: FLTMPS GMT Report)", gmtPrimaryHeader)
			} else {
				createCell(headerRow1, column, "", gmtPrimaryHeader)
			}
			
			createCell(headerRow2, column, formattedName, SECONDARY_HEADER)
			
			createCell(headerRow3, column,
				fractionToPercentage(report.gmtCourseCompletionPercentages[i]),
				styleForValue(report.gmtCourseCompletionPercentages[i]))
			
			column++
		}
		sheet.addMergedRegion(new CellRangeAddress(
			headerRow1.rowNum,
			headerRow1.rowNum,
			column - report.getGmtCourseNames().length,
			column - 1))
		
		// IA eLearning
		String iaPrimaryHeader = nextHeader()
		for (int i = 0; i < report.getIaCourseNames().length; i++) {
			
			if (i == 0) {
				createCell(headerRow1, column, "IA e-Learning (Source: FLTMPS IA Report)", iaPrimaryHeader)
			} else {
				createCell(headerRow1, column, "", iaPrimaryHeader)
			}
			
			sheet.setColumnWidth(column, 15 * 256)
			
			createCell(headerRow2, column, report.getIaCourseNames()[i], SECONDARY_HEADER)
			
			createCell(headerRow3, column,
				fractionToPercentage(report.iaCourseCompletionPercentages[i]),
				styleForValue(report.iaCourseCompletionPercentages[i]))
			
			column++
		}
		sheet.addMergedRegion(new CellRangeAddress(
			headerRow1.rowNum,
			headerRow1.rowNum,
			column - report.getIaCourseNames().length,
			column - 1))
		
		// ESAMS
		if (report.esamsFile != null) {
			String esamsPrimaryHeader = nextHeader()
			for (int i = 0; i < report.esamsCourseNames.length; i++) {
				
				if (i == 0) {
					createCell(headerRow1, column, "ESAMS (Source: ESAMS Courses Report)", esamsPrimaryHeader)
				} else {
					createCell(headerRow1, column, "", esamsPrimaryHeader)
				}
				
				sheet.setColumnWidth(column, 15 * 256)
				
				createCell(headerRow2, column, report.esamsCourseNames[i], SECONDARY_HEADER)
				
				createCell(headerRow3, column,
					fractionToPercentage(report.esamsCourseCompletionPercentages[i]),
					styleForValue(report.esamsCourseCompletionPercentages[i]))
				
				column++
			}
			sheet.addMergedRegion(new CellRangeAddress(
				headerRow1.rowNum,
				headerRow1.rowNum,
				column - report.esamsCourseNames.length,
				column - 1))
		}
		
		if (report.nrowsFile != null) {
			String nrowsPrimaryHeader = nextHeader()
			
			createCell(headerRow1, column, "Orders (Source: NROWS)", nrowsPrimaryHeader)
			createCell(headerRow2, column, "SDN", SECONDARY_HEADER)
			createCell(headerRow3, column, "-")
			sheet.setColumnWidth(column, 15 * 256)
			column++
			
			createCell(headerRow1, column, "", nrowsPrimaryHeader)
			createCell(headerRow2, column, "Tracking #", SECONDARY_HEADER)
			createCell(headerRow3, column, "-")
			sheet.setColumnWidth(column, 15 * 256)
			column++
			
			createCell(headerRow1, column, "", nrowsPrimaryHeader)
			createCell(headerRow2, column, "Duty Type", SECONDARY_HEADER)
			createCell(headerRow3, column, "-")
			sheet.setColumnWidth(column, 15 * 256)
			column++
			
			createCell(headerRow1, column, "", nrowsPrimaryHeader)
			createCell(headerRow2, column, "Start Date", SECONDARY_HEADER)
			createCell(headerRow3, column, "-")
			sheet.setColumnWidth(column, 15 * 256)
			column++
			
			createCell(headerRow1, column, "", nrowsPrimaryHeader)
			createCell(headerRow2, column, "End Date", SECONDARY_HEADER)
			createCell(headerRow3, column, "-")
			sheet.setColumnWidth(column, 15 * 256)
			column++
			
			createCell(headerRow1, column, "", nrowsPrimaryHeader)
			createCell(headerRow2, column, "Days", SECONDARY_HEADER)
			createCell(headerRow3, column, "-")
			sheet.setColumnWidth(column, 15 * 256)
			column++
			
			createCell(headerRow1, column, "", nrowsPrimaryHeader)
			createCell(headerRow2, column, "FY", SECONDARY_HEADER)
			createCell(headerRow3, column, "-")
			sheet.setColumnWidth(column, 15 * 256)
			column++
			
			createCell(headerRow1, column, "", nrowsPrimaryHeader)
			createCell(headerRow2, column, "Status", SECONDARY_HEADER)
			createCell(headerRow3, column, "-")
			sheet.setColumnWidth(column, 15 * 256)
			column++
		}
		
		// if there are no manual inputs, skip this
		if (report.manualFile == null) {
			return 3
		}
		
		// manual columns
		String lastHeader = null
		int columnOfLastHeader = -1
		String currentPrimaryHeader = nextHeader()
		for (int i = 0; i < report.primaryColumnHeaders.size(); i++) {
			
			String primary = report.primaryColumnHeaders.get(i)
			String secondary = report.secondaryColumnHeaders.get(i)
			String percentage = fractionToPercentage(report.manualPercentages[i])
			
			// if the primary header was already used
			if (lastHeader.equals(primary)) {
				// just use a blank
				createCell(headerRow1, column, "", currentPrimaryHeader)
			} else {
			
				if (columnOfLastHeader != -1) {
					sheet.addMergedRegion(new CellRangeAddress(
						headerRow1.rowNum,
						headerRow1.rowNum,
						columnOfLastHeader,
						column - 1))
				}
			
				createCell(headerRow1, column, primary + " (Source: Manual Inputs)", currentPrimaryHeader)
				
				columnOfLastHeader = column
				currentPrimaryHeader = nextHeader()
			}
			
			sheet.setColumnWidth(column, 20 * 256)
			
			
			
			createCell(headerRow2, column, secondary, SECONDARY_HEADER)
			
			createCell(headerRow3, column, percentage, styleForValue(report.manualPercentages[i]))
			
			lastHeader = primary
			column++
			
		}
		
		sheet.addMergedRegion(new CellRangeAddress(
			headerRow1.rowNum,
			headerRow1.rowNum,
			columnOfLastHeader,
			column - 1))
		
		
		return 3
		
		
	}
	
	static void createSheetForWarnings(Workbook wb, SummaryReport report, String title) {
		Sheet sheet = wb.createSheet(title)
		
		int rowNum = 0
		Row row = sheet.createRow(rowNum)
		row.createCell(0).setCellValue("Issues")
		sheet.setColumnWidth(0, 120 * 256)
		rowNum++
		
		for (String value : report.commandWarnings) {
			
			row = sheet.createRow(rowNum)
			row.createCell(0).setCellValue(value)
			rowNum++
		}
		
	}
	
	static void createMemberRow(Sheet sheet, Map<String, CellStyle> styles, SummaryReport report,
		int rownum, MergedMember member) {
		
		Row row = sheet.createRow(rownum)
		
		int column = 0
		
		// general info
		this.createCell(row, column++, member.getLastName())
		this.createCell(row, column++, member.getFirstName())
		this.createCell(row, column++, member.getRank())
		
		// general readiness
		this.createCell(row, column++, member.getPrd().format("MM/dd/yyyy"))
		this.createCell(row, column++, member.getMasCodeA())
		this.createCell(row, column++, member.getMasCodeM())
		this.createCell(row, column++, member.getMasCodeT())
		
		// medical
		this.createCell(row, column++, member.getImrStatus())
		
		// General eLearning
		if (member.existsInELearning) {
			for (boolean state : member.geteLearningCourseCompletions()) {
				this.createCell(row, column++, booleanToCompletionStatus(state))
			}
		} else {
			column += report.geteLearningCourseNames().length
		}
		
		// GMT
		if (member.existsInGMT) {
			for (boolean state : member.getGmtCourseCompletions()) {
				this.createCell(row, column++, booleanToCompletionStatus(state))
			}
		} else {
			column += report.getGmtCourseNames().length
		}
		
		// IA
		if (member.existsInIA) {
			for (boolean state : member.getIaCourseCompletions()) {
				this.createCell(row, column++, booleanToCompletionStatus(state))
			}
		} else {
			column += report.getIaCourseNames().length
		}
		
		// ESAMS
		if (report.esamsFile != null) {
			for (boolean state : member.esamsCourseCompletions) {
				this.createCell(row, column++, booleanToCompletionStatus(state))
			}
		}
		
		if (report.nrowsFile != null) {
			if (member.orders.size() == 0) {
				for (int i = 0; i < 8; i++) {
					createCell(row, column++, "")
				}
			} else {
				String sdn = ""
				String tracking = ""
				String duty = ""
				String start = ""
				String end = ""
				String days = ""
				String fy = ""
				String status = ""
				
				int index = 0
				for (NrowsOrder order : member.orders) {
					
					String endLine = "\n"
					if (index == member.orders.size() - 1) {
						endLine = ""
					}
					
					sdn += order.sdn + endLine
					tracking += order.trackingNumber + endLine
					duty += order.dutyType + endLine
					start += FormatUtil.toMMDDYYYY(order.startDate) + endLine
					end += FormatUtil.toMMDDYYYY(order.endDate) + endLine
					days += order.days + endLine
					fy += order.fy + endLine
					status += order.status + endLine
					
					index++
				}
				
				createCell(row, column++, sdn)
				createCell(row, column++, tracking)
				createCell(row, column++, duty)
				createCell(row, column++, start)
				createCell(row, column++, end)
				createCell(row, column++, days)
				createCell(row, column++, fy)
				createCell(row, column++, status)
			}
		}
		
		// handle manual columns
		for (String value : member.values) {
			this.createCell(row, column++, value)
		}
		
	}
		
	static String booleanToCompletionStatus(boolean state) {
		if (state) {
			return "C"
		} else {
			return ""
		}
	}
		
	static String nextHeader() {
		currentPrimaryHeaderIndex = (currentPrimaryHeaderIndex + 1) % PRIMARY_HEADERS.length
		return PRIMARY_HEADERS[currentPrimaryHeaderIndex]
	}
	
	static String styleForValue(Double value) {
		if (value < 0.51D) {
			return CELL_BAD
		}
		
		if (value < 0.81D) {
			return CELL_WARNING
		}
		
		return CELL_GOOD
	}
	
	static Map<String, CellStyle> createStyles(Workbook wb){
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>()
		
		styles.put(DEFAULT, createDefaultStyle(wb))
		styles.put(HEADER_BLUE, createHeaderStyle(wb, IndexedColors.DARK_BLUE.getIndex()))
		styles.put(HEADER_GREEN, createHeaderStyle(wb, IndexedColors.DARK_GREEN.getIndex()))
		styles.put(HEADER_RED, createHeaderStyle(wb, IndexedColors.DARK_RED.getIndex()))
		styles.put(HEADER_TEAL, createHeaderStyle(wb, IndexedColors.DARK_TEAL.getIndex()))
		styles.put(HEADER_YELLOW, createHeaderStyle(wb, IndexedColors.DARK_YELLOW.getIndex()))
		
		styles.put(SECONDARY_HEADER, createSecondaryHeader(wb))
		styles.put(CELL_GOOD, createColorCell(wb, IndexedColors.GREEN.index))
		styles.put(CELL_WARNING, createColorCell(wb, IndexedColors.ORANGE.index))
		styles.put(CELL_BAD, createColorCell(wb, IndexedColors.RED.index))
		
		return styles
	}
	
	static void setBorder(CellStyle style) {
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
	}
	
	static CellStyle createDefaultStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		this.setBorder(style)
		style.setWrapText(true);
		return style
	}
	
	static CellStyle createHeaderStyle(Workbook wb, short color) {
		CellStyle style = wb.createCellStyle();
		this.setBorder(style)
				
		style.setFillForegroundColor(color)
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		
		Font font = wb.createFont();
		font.setColor(HSSFColor.WHITE.index);
		style.setFont(font);
		
		return style
	}
	
	static CellStyle createColorCell(Workbook wb, short color) {
		CellStyle style = wb.createCellStyle();
		setBorder(style)
		
		Font font = wb.createFont();
		font.setColor(color);
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style.setFont(font);
		
		return style
	}
	
	static CellStyle createSecondaryHeader(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		setBorder(style)
		Font font = wb.createFont();
		font.setFontHeightInPoints((short)8);
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style.setFont(font);
		
		style.setWrapText(true);
		return style
	}
	
	static createCell(Row row, int column, String value, String styleName = null) {
		Cell cell = row.createCell(column)
		cell.setCellValue(value)
		if (styleName != null) {
			cell.setCellStyle(styles.get(styleName))
		} else {
			cell.setCellStyle(styles.get(DEFAULT))
		}
	}
	
	
	
}
