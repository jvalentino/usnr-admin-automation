package com.blogspot.jvalentino.usnrauto.reportmerger.service

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.data.time.TimeSeries;
import org.junit.Before;
import org.junit.Test;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.DataCategory;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.History;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.HistoryElement;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.SummaryReport;
import com.blogspot.jvalentino.usnrauto.util.FormatUtil;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

class HistoryServiceTest {

	private ServiceBus bus = ServiceBus.getInstance()
	private HistoryService service
	
	@Before
	void setup() {
		service = bus.getHistoryService()
	}
	
	@Test
	void testUpateHistoryElements() {
		History history = new History()
		Date date = FormatUtil.formatTwoStringToDate("01-Jan-2015")
		String[] courses = [ "Alpha", "Bravo" ]
		double[] values = [ 0.9901, 0.8788]
		String[] secondaryCategories = ["II", "I"]
		
		service.upateHistoryElements(
			DataCategory.ELEARNING, date, courses, values, history, secondaryCategories)
		
		List<HistoryElement> elements = history.getResults(DataCategory.ELEARNING)
		
		assertThat(elements.size(), is(2))
		assertThat(elements.get(0).toString(), is("Alpha (II): [01-Jan-2015, 99.01%], "))
		assertThat(elements.get(1).toString(), is("Bravo (I): [01-Jan-2015, 87.88%], "))
	}
	
	@Test
	void testUpateHistoryElements_ExistingData() {
		History history = new History()
		Date date = FormatUtil.formatTwoStringToDate("02-Jan-2015")
		String[] courses = [ "Alpha", "Bravo" ]
		double[] values = [ 0.9901, 0.8788]
		
		List<HistoryElement> elements = history.getResults(DataCategory.ELEARNING)
		elements.add(new HistoryElement(DataCategory.ELEARNING, "Alpha", FormatUtil.formatTwoStringToDate("01-Jan-2015"), 0.2234))
		
		service.upateHistoryElements(DataCategory.ELEARNING, date, courses, values, history)
		
		assertThat(elements.size(), is(2))
		assertThat(elements.get(0).toString(), is("Alpha: [01-Jan-2015, 22.34%], [02-Jan-2015, 99.01%], "))
		assertThat(elements.get(1).toString(), is("Bravo: [02-Jan-2015, 87.88%], "))
	}
	
	@Test
	void testCollectDates() {
		Set<Date> dates = new HashSet<Date>()
		
		Date one = FormatUtil.formatTwoStringToDate("01-Jan-2015")
		Date two = FormatUtil.formatTwoStringToDate("02-Jan-2015")
		
		List<HistoryElement> elements = new ArrayList<HistoryElement>()
		elements.add(new HistoryElement(DataCategory.ELEARNING, "Alpha", one, 0.2234))
		elements.add(new HistoryElement(DataCategory.ELEARNING, "Bravo", two, 0.4456))
		
		service.collectDates(dates, elements)
		
		assertThat(dates.contains(one), is(true))
		assertThat(dates.contains(two), is(true))
		
	}
	
	@Test
	void testGetAllDates() {
		Date one = FormatUtil.formatTwoStringToDate("01-Jan-2015")
		Date two = FormatUtil.formatTwoStringToDate("02-Jan-2015")
		
		History history = new History()
		history.getResults(DataCategory.ELEARNING).add(new HistoryElement(DataCategory.ELEARNING, "Alpha", one, 0.2234))
		history.getResults(DataCategory.MANUAL_INPUTS).add(new HistoryElement(DataCategory.MANUAL_INPUTS, "Bravo", two, 0.4456))
		
		List<Date> dates = service.getAllDates(history)
		
		assertThat(dates.size(), is(2))
		assertThat(dates.get(0), is(one))
		assertThat(dates.get(1), is(two))
	}
	
	@Test
	void testAddMissingDateData() {
		Date one = FormatUtil.formatTwoStringToDate("01-Jan-2015")
		Date two = FormatUtil.formatTwoStringToDate("02-Jan-2015")
		List<Date> dates = new ArrayList<Date>()
		dates.add(one)
		dates.add(two)
		
		List<HistoryElement> elements = new ArrayList<HistoryElement>()
		elements.add(new HistoryElement(DataCategory.ELEARNING, "Alpha", one, 0.2234))
		elements.add(new HistoryElement(DataCategory.ELEARNING, "Bravo", two, 0.4456))
		
		service.addMissingDateData(dates, elements)
		
		assertThat(elements.size(), is(2))
		assertThat(elements.get(0).toString(),
			is("Alpha: [01-Jan-2015, 22.34%], [02-Jan-2015, 0%], "))
		assertThat(elements.get(1).toString(),
			is("Bravo: [01-Jan-2015, 0%], [02-Jan-2015, 44.56%], "))
		
	}
	
	@Test
	void testRectifyMissingDates() {
		Date one = FormatUtil.formatTwoStringToDate("01-Jan-2015")
		Date two = FormatUtil.formatTwoStringToDate("02-Jan-2015")
		
		History history = new History()
		history.getResults(DataCategory.ELEARNING).add(
			new HistoryElement(DataCategory.ELEARNING, "Alpha", one, 0.2234))
		history.getResults(DataCategory.MANUAL_INPUTS).add(
				new HistoryElement(DataCategory.MANUAL_INPUTS, "Bravo", two, 0.4456))
		
		service.rectifyMissingDates(history)
		
		assertThat(history.getResults(DataCategory.ELEARNING).size(), is(1))
		assertThat(history.getResults(DataCategory.ELEARNING).get(0).toString(),
			is("Alpha: [01-Jan-2015, 22.34%], [02-Jan-2015, 0%], "))
		
		assertThat(history.getResults(DataCategory.MANUAL_INPUTS).size(), is(1))
		assertThat(history.getResults(DataCategory.MANUAL_INPUTS).get(0).toString(),
			is("Bravo: [01-Jan-2015, 0%], [02-Jan-2015, 44.56%], "))
		
		assertThat(history.dates.get(0), is(one))
		assertThat(history.dates.get(1), is(two))
	}
	
	@Test
	void testGetDatesFromHistoryRow() {
		String[] values = ["category", "secondary", "course", "01-Jan-2015", "02-Jan-2015"]
		List<Date> results = service.getDatesFromHistoryRow(values)
		assertThat(results.size(), is(2))
		assertThat(results.get(0), is(FormatUtil.formatTwoStringToDate("01-Jan-2015")))
		assertThat(results.get(1), is(FormatUtil.formatTwoStringToDate("02-Jan-2015")))
	}
	
	@Test
	void testGetDatesFromHistoryRow_Invalid() {
		String[] values = ["category", "secondary", "course", "FOOBAR"]
		try {
			service.getDatesFromHistoryRow(values)
			fail("This should have failed because FOOBAR is not a valid date")
		} catch (Exception e) {
			assertThat(e.message, 
				is("In the first row the date \"FOOBAR\" could not be parsed. It needs to be in 01-Jan-2015 format."))
		}
		
	}
	
	@Test
	void testGetHistoryElementFromRow() {
		Date one = FormatUtil.formatTwoStringToDate("01-Jan-2015")
		Date two = FormatUtil.formatTwoStringToDate("02-Jan-2015")
		
		List<Date> dates = new ArrayList<Date>()
		dates.add(one)
		dates.add(two)
		
		String[] values = ["MANUAL INPUTS", "Qualification", "Status", "87.65%", "0.5432"]
		
		HistoryElement element = service.getHistoryElementFromRow(dates, values)
		
		assertThat(element.toString(), 
			is("Status (Qualification): [01-Jan-2015, 87.65%], [02-Jan-2015, 54.32%], "))
		assertThat(element.category, is(DataCategory.MANUAL_INPUTS))
	}
	
	@Test
	void testGetHistoryElementFromRow_Invalid() {
		List<Date> dates = new ArrayList<Date>()
		dates.add(FormatUtil.formatTwoStringToDate("01-Jan-2015"))
		
		String[] values = ["FOO", "Qualification", "Status", "87.65%"]
		
		try {
			service.getHistoryElementFromRow(dates, values)
			fail("This should have failed because FOO isn't a valid data category")
		} catch (Exception e) {
			assertThat(e.message, 
				is("Column 0: FOO is not a valid data category. valid values are MEMBERSHIP, ELEARNING, INDIVIDUAL AUGMENTEE, GMT, MANUAL INPUTS, ESAMS."))
		}
		
	}
	
	private History loadHistoryFromFile(File file) {
		FileInputStream fis = new FileInputStream(file)
		Workbook workbook = new XSSFWorkbook(fis)
		fis.close()
		
		History history = service.loadHistoryFromWorkbook(workbook)
		
		return history
	}
	
	@Test
	void testLoadHistoryFromWorkbook() {
		History history = loadHistoryFromFile(new File("config/Manual Unit Inputs.xlsx"))
		verifyHistory(history)
	}
	
	@Test
	void testOutputHistoryToSpreadsheet() {
		// Get the history out of the manual inputs
		History history = loadHistoryFromFile(new File("config/Manual Unit Inputs.xlsx"))
		
		// Write that history to a new file
		File output = new File("build/history-output.xlsx")
		output.getParentFile().mkdirs()
		service.outputHistoryToSpreadsheet(output, history)
		
		// load it from the place we just wrote to and verify it is the same
		history = loadHistoryFromFile(output)
		
		verifyHistory(history)
		
	}
	
	private void verifyHistory(History history) {
		assertThat(history.dates.size(), is(1))
		assertThat(history.dates.get(0), is(FormatUtil.formatTwoStringToDate("02-Jan-2015")))
		
		int i = 0
		List<HistoryElement> membership = history.getResults(DataCategory.MEMBERSHIP)
		assertThat(membership.size(), is(2))
		assertThat(membership.get(i++).toString(),
			is("PRD: [02-Jan-2015, 84.85%], "))
		assertThat(membership.get(i++).toString(),
			is("Fully Medically Ready: [02-Jan-2015, 60.61%], "))
		
		i = 0
		List<HistoryElement> gmt = history.getResults(DataCategory.GMT)
		assertThat(gmt.size(), is(18))
		assertThat(gmt.get(i++).toString(),
			is("ALCOH DRUG TOBACCO AWARE AVAIL APR (CATEGORY ONE): [02-Jan-2015, 0%], "))
		assertThat(gmt.get(i++).toString(),
			is("EO SEX HARR GRIEVANCE (CATEGORY ONE): [02-Jan-2015, 63.64%], "))
		assertThat(gmt.get(i++).toString(),
			is("PERS FINANCIAL MGMT (CATEGORY ONE): [02-Jan-2015, 66.67%], "))
		assertThat(gmt.get(i++).toString(),
			is("SEX ASSAULT PREV RESP AWARE AVAIL APR (CATEGORY ONE): [02-Jan-2015, 0%], "))
		assertThat(gmt.get(i++).toString(),
			is("STRESS MGMT AVAILIBLE AUG 2015 (CATEGORY ONE): [02-Jan-2015, 63.64%], "))
		assertThat(gmt.get(i++).toString(),
			is("SUICIDE PREVENT AWARE AVAIL DEC (CATEGORY ONE): [02-Jan-2015, 0%], "))
		assertThat(gmt.get(i++).toString(),
			is("ANTITERRORISM LEVEL I AWARENESS (CATEGORY TWO): [02-Jan-2015, 48.48%], "))
		assertThat(gmt.get(i++).toString(),
			is("COMBATING TRAFFICKING IN PERSONS (CATEGORY TWO): [02-Jan-2015, 45.45%], "))
		assertThat(gmt.get(i++).toString(),
			is("COUNTERINTELL AWARENESS (CATEGORY TWO): [02-Jan-2015, 0%], "))
		assertThat(gmt.get(i++).toString(),
			is("DOD CYBER AWARENESS CHALLENGE V2 (CATEGORY TWO): [02-Jan-2015, 9.09%], "))
		assertThat(gmt.get(i++).toString(),
			is("IC IAA V12 (INTEL COMMUNITY ONLY) (CATEGORY TWO): [02-Jan-2015, 45.45%], "))
		assertThat(gmt.get(i++).toString(),
			is("DOMESTIC VIOLENCE (CATEGORY TWO): [02-Jan-2015, 24.24%], "))
		assertThat(gmt.get(i++).toString(),
			is("OPERATIONAL RISK MANAGEMENT (CATEGORY TWO): [02-Jan-2015, 18.18%], "))
		assertThat(gmt.get(i++).toString(),
			is("OPERATIONAL SECURITY (CATEGORY TWO): [02-Jan-2015, 21.21%], "))
		assertThat(gmt.get(i++).toString(),
			is("PHYSICAL READINESS (CATEGORY TWO): [02-Jan-2015, 0%], "))
		assertThat(gmt.get(i++).toString(),
			is("PRIV AND PERS IDENTIFIABLE INFO (CATEGORY TWO): [02-Jan-2015, 51.52%], "))
		assertThat(gmt.get(i++).toString(),
			is("RECORDS MANAGEMENT (CATEGORY TWO): [02-Jan-2015, 12.12%], "))
		assertThat(gmt.get(i++).toString(),
			is("SEXUAL HEALTH AND RESPONSIBILITY (CATEGORY TWO): [02-Jan-2015, 18.18%], "))
		
		i = 0
		List<HistoryElement> ia = history.getResults(DataCategory.INDIVIDUAL_AUGMENTEE)
		assertThat(ia.size(), is(26))
		assertThat(ia.get(i++).toString(),
			is("CANS M16WS 1.0: [02-Jan-2015, 18.18%], "))
		assertThat(ia.get(i++).toString(),
			is("OSTNG: [02-Jan-2015, 27.27%], "))
		assertThat(ia.get(i++).toString(),
			is("ATFP CONUS: [02-Jan-2015, 81.82%], "))
		assertThat(ia.get(i++).toString(),
			is("ATFP OCONUS: [02-Jan-2015, 66.67%], "))
		assertThat(ia.get(i++).toString(),
			is("INFP: [02-Jan-2015, 60.61%], "))
		assertThat(ia.get(i++).toString(),
			is("INTRO TO EQUAL OPPORTUNITY: [02-Jan-2015, 24.24%], "))
		assertThat(ia.get(i++).toString(),
			is("MR1 PART1: [02-Jan-2015, 15.15%], "))
		assertThat(ia.get(i++).toString(),
			is("MR1 PART2: [02-Jan-2015, 15.15%], "))
		assertThat(ia.get(i++).toString(),
			is("COC LEVELA: [02-Jan-2015, 9.09%], "))
		assertThat(ia.get(i++).toString(),
			is("NPDC CWI 1.0: [02-Jan-2015, 36.36%], "))
		assertThat(ia.get(i++).toString(),
			is("NPDC HWI 1.0: [02-Jan-2015, 36.36%], "))
		assertThat(ia.get(i++).toString(),
			is("NPDC SAEDA 1.0: [02-Jan-2015, 36.36%], "))
		assertThat(ia.get(i++).toString(),
			is("NPDC USAV 1.0: [02-Jan-2015, 36.36%], "))
		assertThat(ia.get(i++).toString(),
			is("JKDDC TIP 2.0: [02-Jan-2015, 72.73%], "))
		assertThat(ia.get(i++).toString(),
			is("PRE DEPLOY SUICIDE AWARE: [02-Jan-2015, 27.27%], "))
		assertThat(ia.get(i++).toString(),
			is("PRE DEPLOY SEXUAL ASSAULT: [02-Jan-2015, 24.24%], "))
		assertThat(ia.get(i++).toString(),
			is("VIRTUAL MISSION PREP INTEL: [02-Jan-2015, 15.15%], "))
		assertThat(ia.get(i++).toString(),
			is("VIRTUAL MISSION PREP MEDIA: [02-Jan-2015, 18.18%], "))
		assertThat(ia.get(i++).toString(),
			is("M9 PISTOL: [02-Jan-2015, 15.15%], "))
		assertThat(ia.get(i++).toString(),
			is("INTRO BIOMETRICS: [02-Jan-2015, 18.18%], "))
		assertThat(ia.get(i++).toString(),
			is("COIN: [02-Jan-2015, 6.06%], "))
		assertThat(ia.get(i++).toString(),
			is("MALARIA PREVENTION AND CONTROL: [02-Jan-2015, 9.09%], "))
		assertThat(ia.get(i++).toString(),
			is("COIN OPERATIONS: [02-Jan-2015, 3.03%], "))
		assertThat(ia.get(i++).toString(),
			is("AFGAN IN PROSPECTIVE: [02-Jan-2015, 3.03%], "))
		assertThat(ia.get(i++).toString(),
			is("ACTIVE SHOOTER: [02-Jan-2015, 12.12%], "))
		assertThat(ia.get(i++).toString(),
			is("USFFC ISAF BASIC: [02-Jan-2015, 0%], "))
		
		i = 0
		List<HistoryElement> manaul = history.getResults(DataCategory.MANUAL_INPUTS)
		assertThat(manaul.size(), is(17))
		assertThat(manaul.get(i++).toString(),
			is("Status (Qualification): [02-Jan-2015, 69.7%], "))
		assertThat(manaul.get(i++).toString(),
			is("Notes (Qualification): [02-Jan-2015, 100%], "))
		assertThat(manaul.get(i++).toString(),
			is("Poly (Security, Accounts, and Access): [02-Jan-2015, 78.79%], "))
		assertThat(manaul.get(i++).toString(),
			is("Clearance (Security, Accounts, and Access): [02-Jan-2015, 93.94%], "))
		assertThat(manaul.get(i++).toString(),
			is("National (Security, Accounts, and Access): [02-Jan-2015, 93.94%], "))
		assertThat(manaul.get(i++).toString(),
			is("Joint (Security, Accounts, and Access): [02-Jan-2015, 93.94%], "))
		assertThat(manaul.get(i++).toString(),
			is("NMCI (Security, Accounts, and Access): [02-Jan-2015, 93.94%], "))
		assertThat(manaul.get(i++).toString(),
			is("NMCI Home Access (Security, Accounts, and Access): [02-Jan-2015, 87.88%], "))
		assertThat(manaul.get(i++).toString(),
			is("FY 2015 Preference (Mission Support): [02-Jan-2015, 93.94%], "))
		assertThat(manaul.get(i++).toString(),
			is("FY 2015 Target (Mission Support): [02-Jan-2015, 93.94%], "))
		assertThat(manaul.get(i++).toString(),
			is("FY 2014 Activity (Mission Support): [02-Jan-2015, 84.85%], "))
		assertThat(manaul.get(i++).toString(),
			is("Cell # (Contact Information): [02-Jan-2015, 9.09%], "))
		assertThat(manaul.get(i++).toString(),
			is("Home # (Contact Information): [02-Jan-2015, 0%], "))
		assertThat(manaul.get(i++).toString(),
			is("Work # (Contact Information): [02-Jan-2015, 0%], "))
		assertThat(manaul.get(i++).toString(),
			is("Navy Email (Contact Information): [02-Jan-2015, 9.09%], "))
		assertThat(manaul.get(i++).toString(),
			is("Personal Email (Contact Information): [02-Jan-2015, 3.03%], "))
		assertThat(manaul.get(i++).toString(),
			is("Work Email (Contact Information): [02-Jan-2015, 3.03%], "))
		
		i = 0
		List<HistoryElement> elearning = history.getResults(DataCategory.ELEARNING)
		assertThat(elearning.size(), is(7))
		//assertThat(elearning.get(i++).toString(),
		//	is("IAAÊV12: [02-Jan-2015, 9.09%], "))
		assertThat(elearning.get(i++).toString(),
			is("ICÊIAAÊV12: [02-Jan-2015, 45.45%], "))
		assertThat(elearning.get(i++).toString(),
			is("CENSECFORÊATÊ010: [02-Jan-2015, 48.48%], "))
		assertThat(elearning.get(i++).toString(),
			is("PII: [02-Jan-2015, 51.52%], "))
		assertThat(elearning.get(i++).toString(),
			is("CTIP: [02-Jan-2015, 45.45%], "))
		assertThat(elearning.get(i++).toString(),
			is("RECORDS MANAGEMENT: [02-Jan-2015, 12.12%], "))
		assertThat(elearning.get(i++).toString(),
			is("OPSEC: [02-Jan-2015, 21.21%], "))
		assertThat(elearning.get(i++).toString(),
			is("COUNTERINTELL AWARENESS: [02-Jan-2015, 0%], "))
	}
	
	History generateTestHistory() {
		History history = new History()
		Date one = FormatUtil.formatTwoStringToDate("01-Jan-2015")
		Date two = FormatUtil.formatTwoStringToDate("02-Jan-2015")
		
		List<HistoryElement> gmt = history.getResults(DataCategory.GMT)
		
		HistoryElement iaa = new HistoryElement(
			DataCategory.GMT, 
			"IAA V12", 
			one, 
			0.1234,
			"CATEGORY ONE")
		iaa.values.put(two, 0.2345)
		
		gmt.add(iaa)
		
		HistoryElement cyber = new HistoryElement(
			DataCategory.GMT, 
			"CYBER", 
			one, 
			0.1234,
			"CATEGORY TWO")
		cyber.values.put(two, 0.3456)
		
		gmt.add(cyber)
		
		List<HistoryElement> members = history.getResults(DataCategory.MEMBERSHIP)
		
		HistoryElement prd = new HistoryElement(
			DataCategory.MEMBERSHIP,
			"PRD",
			one,
			0.1111,
			"NONE")
		prd.values.put(two, 0.2222)
		
		members.add(prd)
		
		HistoryElement med = new HistoryElement(
			DataCategory.MEMBERSHIP,
			"Medical",
			one,
			0.3333,
			"NONE")
		med.values.put(two, 0.4444)
		
		members.add(med)

		history.dates.add(one)
		history.dates.add(two)
		
		return history		
		
	}
	
	@Test
	void testFilter_category() {
		History history = this.generateTestHistory()
		List<HistoryElement> results = service.filter(history, DataCategory.GMT)
		
		assertThat(results.size(), is(2)) 
		
		assertThat(results.get(0).toString(),
			is("IAA V12 (CATEGORY ONE): [01-Jan-2015, 12.34%], [02-Jan-2015, 23.45%], "))
		
		assertThat(results.get(1).toString(),
			is("CYBER (CATEGORY TWO): [01-Jan-2015, 12.34%], [02-Jan-2015, 34.56%], "))
		
	}
	
	@Test
	void testFilter_item() {
		History history = this.generateTestHistory()
		List<HistoryElement> results = service.filter(history, DataCategory.GMT, null, "IAA V12")
		
		assertThat(results.size(), is(1))
		
		assertThat(results.get(0).toString(),
			is("IAA V12 (CATEGORY ONE): [01-Jan-2015, 12.34%], [02-Jan-2015, 23.45%], "))
		
	}
	
	@Test
	void testFilter_secondary() {
		History history = this.generateTestHistory()
		List<HistoryElement> results = service.filter(history, DataCategory.GMT, "CATEGORY ONE")
		
		assertThat(results.size(), is(1))
		
		assertThat(results.get(0).toString(),
			is("IAA V12 (CATEGORY ONE): [01-Jan-2015, 12.34%], [02-Jan-2015, 23.45%], "))
	}
	
	@Test
	void testFilter_secondaryAndItem() {
		History history = this.generateTestHistory()
		List<HistoryElement> results = service.filter(history, DataCategory.GMT, "CATEGORY ONE", "IAA V12")
		
		assertThat(results.size(), is(1))
		
		assertThat(results.get(0).toString(),
			is("IAA V12 (CATEGORY ONE): [01-Jan-2015, 12.34%], [02-Jan-2015, 23.45%], "))
	}
	
	@Test
	void testCreateSeriesAverage() {
		History history = this.generateTestHistory()
		TimeSeries series = service.createSeriesAverage("GMTs", history, DataCategory.GMT)
		
		assertThat(series.getItemCount(), is(2))
		
		assertThat(series.getDataItem(0).period.toString(), is("1-January-2015"))
		assertThat(series.getDataItem(0).value, is(0.1234D))
		
		assertThat(series.getDataItem(1).period.toString(), is("2-January-2015"))
		assertThat(series.getDataItem(1).value, is(closeTo(0.2900D, 0.0001D)))
		
	}
	
}
