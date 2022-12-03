package com.blogspot.jvalentino.usnrauto.reportmerger.service

import java.awt.Font;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.ShapeUtilities;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.DataCategory;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.History;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.HistoryElement;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.SummaryReport;
import com.blogspot.jvalentino.usnrauto.util.FormatUtil;
import com.blogspot.jvalentino.usnrauto.util.PoiUtil;
import com.sun.org.apache.xerces.internal.impl.dv.xs.DayDV;

class HistoryService {

	private static final String HISTORICAL = "Historical"
	
	/**
	 * Updates a Summary Report's history with the current state of that report.
	 * The current state gets added to the history as entries on the given date.
	 * 
	 * @param date
	 * @param report
	 */
	void updateHistory(Date date, SummaryReport report) {
		// membership fields are static, like PRD and IMR
		List<String> membershipNames = [
			"PRD",
			"Fully Medically Ready"
		]
		
		List<Double> membershipValues = [
			report.getPrdPercentage(),
			report.getImrPercentage()
		]
		
		if (report.nrowsFile != null) {
			membershipNames.push("Orders in FY")
			membershipValues.push(report.getAtPercentage())
		}
		
		if (report.esamsFile != null) {
			this.upateHistoryElements(
				DataCategory.ESAMS,
				date,
				report.esamsCourseNames,
				report.esamsCourseCompletionPercentages,
				report.history)
		}

		this.upateHistoryElements(
				DataCategory.MEMBERSHIP,
				date,
				membershipNames.toArray() as String[],
				membershipValues.toArray() as double[],
				report.history)

		// eLearning
		this.upateHistoryElements(
				DataCategory.ELEARNING,
				date,
				report.eLearningCourseNames,
				report.eLearningCourseCompletionPercentages,
				report.history)

		// ia
		this.upateHistoryElements(
				DataCategory.INDIVIDUAL_AUGMENTEE, 
				date,
				report.iaCourseNames,
				report.iaCourseCompletionPercentages,
				report.history)

		// gmt
		this.upateHistoryElements(
				DataCategory.GMT,
				date,
				report.gmtCourseNames,
				report.gmtCourseCompletionPercentages,
				report.history,
				report.gmtCourseCategories)

		// manual
		this.upateHistoryElements(
				DataCategory.MANUAL_INPUTS,
				date,
				FormatUtil.listToArray(report.secondaryColumnHeaders),
				FormatUtil.doubleUnwrap(report.manualPercentages),
				report.history,
				FormatUtil.listToArray(report.primaryColumnHeaders))

		// Rectify missing dates
		this.rectifyMissingDates(report.history)

	}

	/**
	 * Looks at all of the date/value pairs within all history elements,
	 * collections all dates, and makes sure there is a date/value pair for every date.
	 * When there is not, the date/value pair is created with a value of 0.
	 *
	 * @param history
	 */
	protected void rectifyMissingDates(History history) {
		List<Date> dates = this.getAllDates(history)

		for (DataCategory category : DataCategory.values()) {
			this.addMissingDateData(dates, history.getResults(category))
		}

		history.dates = dates
	}

	/**
	 * Adds the date to any of the elements when that element does not have a vluae for
	 * any of the given dates
	 *
	 * @param dates
	 * @param elements
	 */
	protected void addMissingDateData(List<Date> dates, List<HistoryElement> elements) {

		// for each element...
		for (HistoryElement element : elements) {

			// does this element contain all of the given dates?
			for (Date date : dates) {
				Double value = element.values.get(date)

				// if not, add the date with the value of 0
				if (value == null) {
					element.values.put(date, 0.00)
				}
			}

		}
	}

	/**
	 * Looks at all of the values in history and creates a List of all unique dates
	 * @param history
	 * @return
	 */
	protected List<Date> getAllDates(History history) {
		Set<Date> dates = new HashSet<Date>()

		for (DataCategory category : DataCategory.values()) {
			this.collectDates(dates, history.getResults(category))
		}

		List<Date> list = dates.toList()
		list.sort()

		return list
	}


	/**
	 * Iterates through the given list of element to look at all values, and places
	 * the value dates in the given date set.
	 *
	 * @param dates
	 * @param elements
	 */
	protected void collectDates(Set<Date> dates, List<HistoryElement> elements) {
		for (HistoryElement element : elements) {
			Set<Date> elementDates = element.values.keySet()
			for (Date date : elementDates) {
				dates.add(date)
			}
		}
	}

	/**
	 * Finds the given history element by its unqiue name in the given list if it exists
	 * @param name
	 * @param list
	 * @return
	 */
	protected HistoryElement findHistoryElement(String name, List<HistoryElement> list) {
		for (HistoryElement element : list) {
			if (element.getName().equals(name)) {
				return element
			}
		}
		return null
	}

	/**
	 * For a given list of courses and their summary percentages, searches the history
	 * for a matching course. If that course can be found it gets a new date/value record,
	 * otherwise a new history entry is created with the date and value.
	 *
	 * @param date
	 * @param courses
	 * @param values
	 * @param elements
	 */
	protected void upateHistoryElements(DataCategory category, 
			Date date, String[] courses, double[] values,
			History history, String[] secondaryCategories = null) {

		List<HistoryElement> elements = history.getResults(category)
			
		for (int i = 0; i < courses.length; i++) {
			String course = courses[i];
			double value = values[i]

			// find the history element of the given name, for eLearning
			HistoryElement element = this.findHistoryElement(course, elements)

			// if this element doesn't exist
			if (element == null) {
				element = new HistoryElement(category, course, date, value)
				
				// this is use to handle secondary categories like "Category II" for GMTs
				if (secondaryCategories != null) {
					element.secondaryCategory = secondaryCategories[i]
				}
				
				elements.add(element)
			} else {
				// add a new date and value
				element.values.put(date, value)
			}
		}
	}

	/**
	 * Opens the given file as a workbook (or creates it if it doesn't exist),
	 * and outputs the given History to a worksheet named Historical
	 * 
	 * @param input
	 * @param history
	 */
	void outputHistoryToSpreadsheet(File input, History history) {
		XSSFWorkbook workbook = null
		
		if (input.exists()) {
			FileInputStream file = new FileInputStream(input)
			workbook = new XSSFWorkbook(file)
			file.close()
		} else {
			workbook = new XSSFWorkbook()
		}
		
		this.outputHistoryToWorkbook(workbook, history)

		// output the changes
		FileOutputStream output = new FileOutputStream(input);
		workbook.write(output);
		output.close();

	}
	
	void outputHistoryToWorkbook(Workbook workbook, History history, String name=HISTORICAL) {
		Sheet sheet = workbook.getSheet(name)
		
		// if the sheet exists, delete it
		if(sheet != null)   {
			int index = workbook.getSheetIndex(sheet)
			workbook.removeSheetAt(index)
		}

		// create a new history sheet
		sheet = workbook.createSheet(name)

		this.addHeadersForHistory(sheet, history.dates)
		this.addRowsForHistory(sheet, history)
	}

	private void addHeadersForHistory(Sheet sheet, List<Date> dates) {
		Row row = sheet.createRow(0)

		int column = 0

		row.createCell(column++).setCellValue("Category")
		row.createCell(column++).setCellValue("Secondary Category")
		row.createCell(column++).setCellValue("Item")
		
		// add a column for each date
		for (Date date : dates) {
			row.createCell(column).setCellValue(FormatUtil.dateToFormatTwoString(date))
			column++
		}
		
		sheet.setColumnWidth(0, 25 * 256)
		sheet.setColumnWidth(1, 25 * 256)
		sheet.setColumnWidth(2, 40 * 256)
	}

	private void addRowsForHistory(Sheet sheet, History history) {
		int rowNum = 1

		// For each data category (manual, eLearning, etc...)
		for (DataCategory category : DataCategory.values()) {
			List<HistoryElement> elements = history.getResults(category)

			// for each element (IAA V12, Cyber, etc...)
			for (HistoryElement element : elements) {
				int column = 0

				Row row = sheet.createRow(rowNum++)
				row.createCell(column++).setCellValue(category.text)
				row.createCell(column++).setCellValue(element.secondaryCategory)
				row.createCell(column++).setCellValue(element.name)

				// for each result on a date...
				for (Double value : element.values.values()) {
					String format = FormatUtil.fractionToPercentage(value)
					row.createCell(column++).setCellValue(format)
				}

			}
		}
	}
	
	History loadHistoryFromWorkbook(Workbook workbook) throws Exception {
		
		History history = new History()
		
		Sheet sheet = workbook.getSheet(HISTORICAL)
	
		if (sheet == null) {
			return history
		}
		
		List<Date> dates = null
		
		Iterator<Row> rows = sheet.rowIterator()
		while (rows.hasNext()) {
			Row row = (Row) rows.next()
			
			String[] values = PoiUtil.rowToStrings(row)
			
			// the first row is going to contain labels and dates
			if (row.rowNum == 0) {
				dates = this.getDatesFromHistoryRow(values)
			} else {
				// these are historical values for each column
				HistoryElement element = this.getHistoryElementFromRow(dates, values)
				
				if (element.category != null) {
					history.getResults(element.category).add(element)
				}
				
			}
		}
		
		// handle missing data, populate history.dates
		rectifyMissingDates(history)
		
		return history
	}
	
	/**
	 * Gets a list of dates from the given row. If any of the values
	 * after the first three columns are not valid and exception is thrown.
	 * 
	 * @param row
	 * @return
	 * @throws Exception
	 */
	protected List<Date> getDatesFromHistoryRow(String[] values) throws Exception {
		List<Date> dates = new ArrayList<Date>()
		
		// the first three columns are category, secondary, and item
		for (int i = 3; i < values.length; i++) {
			String cellValue = values[i]
			Date date = FormatUtil.formatTwoStringToDate(cellValue)
			
			if (date == null) {
				throw new Exception("In the first row the date \"" + cellValue +
					"\" could not be parsed. It needs to be in 01-Jan-2015 format.")
			}
			
			dates.add(date)
		}
		
		return dates
	}
	
	/**
	 * Generates a history element based on the given row values
	 * 
	 * @param dates
	 * @param values
	 * @return
	 * @throws Exception
	 */
	protected HistoryElement getHistoryElementFromRow(List<Date> dates, String[] values) throws Exception {
		HistoryElement element = new HistoryElement()
				
		for (int i = 0; i < values.length; i++) {
			
			String cellValue =  values[i].toString()
			
			// category
			if (i == 0) {
				 DataCategory category = DataCategory.getForText(cellValue)
				
				if (category == null) {
					
					String message = "Column " + i + ": "
					message += cellValue + " is not a valid data category. "
					message += "valid values are " + DataCategory.listValues() + "."
					throw new Exception(message)
				}
				
				element.category = category
					
			} else if (i == 1) {
				// Secondary category
				element.secondaryCategory = cellValue
			} else if (i == 2) {
				element.name = cellValue
			} else {
				// this is a value that corresponds to a date taking into account the first
				// three columns are category, secondary, and name
				element.values.put(dates.get(i - 3), FormatUtil.percentageToDouble(cellValue))
			}
			
		}
		
		return element
	}
	
	TimeSeries createSeriesAverage(String title, History history, DataCategory category,
		String secondaryCategory=null, String item=null) {
		
		List<HistoryElement> elements = this.filter(history, category, secondaryCategory, item)
		
		TimeSeries series = new TimeSeries(title)
		
		// average all these things together based on date
		for (Date date : history.dates) {
			
			double sum = 0
			
			for (HistoryElement element : elements) {
				Double value = element.values.get(date)
				sum += value.doubleValue()
			}
			
			double average = sum / elements.size().doubleValue()
			
			try {
				Day day = new Day(date)
				series.add(day, average)
			} catch (Exception e) {
			
			}
		}
		
		return series
		
	}
		
	boolean seriesAllNaNs(TimeSeries series) {
		boolean numberFound = false
		
		for (TimeSeriesDataItem item : series.data) {
			if (!item.getValue().toString().equals("NaN")) {
				numberFound = true
				break
			}
		}
		
		return !numberFound
		
	}
	
	/**
	 * Filters history based on the given criteria
	 * 	
	 * @param history
	 * @param category
	 * @return
	 */
	List<HistoryElement> filter(History history, DataCategory category,
		String secondaryCategory=null, String item=null) {
		
		List<HistoryElement> results = new ArrayList<HistoryElement>()
		
		// get the data series
		List<HistoryElement> elements = history.getResults(category)
		
		for (HistoryElement element : elements) {
			if (secondaryCategory != null 
				&& item != null) {
				
				if (secondaryCategory.equals(element.secondaryCategory)
					&& item.equals(element.name)) {
					results.add(element)
				}
				
			} else if (secondaryCategory != null ) {
			
				if (secondaryCategory.equals(element.secondaryCategory)) {
					results.add(element)
				}
				
			} else if (item != null) {
				
				if (item.equals(element.name)) {
					results.add(element)
				}
				
			} else {
				results.add(element)
			}
		}
		
		return results
		
	}
		
	JFreeChart createHistoryChart(History history, boolean nrows, boolean esams) {
		XYDataset dataset = this.createDataset(history, nrows, esams)
		
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
           "Readiness History",  // title
            "Date",             // x-axis label
            "Completion",   // y-axis label
            dataset,            // data
            true,               // create legend?
            true,               // generate tooltips?
            false               // generate URLs?
		)  
		
		chart.setTitle(
			new TextTitle("Readiness History",
				new java.awt.Font("SansSerif", java.awt.Font.BOLD, 8)
			)
		 );
	 
		LegendTitle legend = chart.getLegend();
		Font labelFont = new Font("SansSerif", Font.BOLD, 8);
		legend.setItemFont(labelFont);
		
		
		
		XYPlot plot = (XYPlot) chart.getPlot()
		DateAxis axis = (DateAxis) plot.getDomainAxis()
		axis.setDateFormatOverride(new SimpleDateFormat("dd-MMM-yyyy"))
		
		plot.getDomainAxis().setLabelFont(labelFont);
		plot.getRangeAxis().setLabelFont(labelFont);
		
		plot.getDomainAxis().setTickLabelFont(labelFont)
		plot.getRangeAxis().setTickLabelFont(labelFont)
		
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		for (int i = 0; i < dataset.seriesCount; i++) {
			r.setSeriesShapesVisible(i, true);
		}
		
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		DecimalFormat pctFormat = new DecimalFormat("#.0%");
		rangeAxis.setNumberFormatOverride(pctFormat);
		
		
		return chart
	}
	
	XYDataset createDataset(History history, boolean nrows, boolean esams) {
		TimeSeries prd = this.createSeriesAverage(
			"PRD", history, DataCategory.MEMBERSHIP, null, "PRD")
		
		TimeSeries medical = this.createSeriesAverage(
			"Fully Medically Ready", history, DataCategory.MEMBERSHIP, null, "Fully Medically Ready")
		
		TimeSeries one = this.createSeriesAverage(
			"GMT (Category I)", history, DataCategory.GMT, "CATEGORY ONE")
		
		TimeSeries two = this.createSeriesAverage(
			"GMT (Category II)", history, DataCategory.GMT, "CATEGORY TWO")
		
		TimeSeries elearning = this.createSeriesAverage(
			"eLearning", history, DataCategory.ELEARNING)
		
		TimeSeries poly = this.createSeriesAverage(
			"Current Poly", history, DataCategory.MANUAL_INPUTS, null, "Poly")
		
		TimeSeries clearance = this.createSeriesAverage(
			"Clearance", history, DataCategory.MANUAL_INPUTS, null, "Clearance")
		
		TimeSeries national = this.createSeriesAverage(
			"National Account", history, DataCategory.MANUAL_INPUTS, null, "National")
		
		TimeSeries orders = this.createSeriesAverage(
			"Orders in FY", history, DataCategory.MEMBERSHIP, null, "Orders in FY")
		
		TimeSeries esamsSeries = this.createSeriesAverage(
			"ESAMS", history, DataCategory.ESAMS)
		
		TimeSeriesCollection dataset = new TimeSeriesCollection()
		dataset.addSeries(prd)
		dataset.addSeries(medical)
		dataset.addSeries(one)
		dataset.addSeries(two)
		dataset.addSeries(elearning)
		
		if (!seriesAllNaNs(poly)) {
			dataset.addSeries(poly)
		}
		
		if (!seriesAllNaNs(clearance)) {
			dataset.addSeries(clearance)
		}
		
		if (!seriesAllNaNs(national)) {
			dataset.addSeries(national)
		}
		
		if (!seriesAllNaNs(orders) && nrows) {
			dataset.addSeries(orders)
		}
		
		if (!seriesAllNaNs(esamsSeries) && esams) {
			dataset.addSeries(esamsSeries)
		}
		
		return dataset
	}
}
