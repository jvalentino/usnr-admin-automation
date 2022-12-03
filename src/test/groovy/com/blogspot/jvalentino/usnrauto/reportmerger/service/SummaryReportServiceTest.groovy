package com.blogspot.jvalentino.usnrauto.reportmerger.service

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.blogspot.jvalentino.usnrauto.commons.message.EmailMessage;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedMember;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.esams.EsamsRecord;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.DataCategory;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.History;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.HistoryElement;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.ruad.RuadReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.MemberWithMissingData;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.MissingData;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.SummaryReport;
import com.blogspot.jvalentino.usnrauto.util.FormatUtil;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

class SummaryReportServiceTest {

	private ServiceBus bus = ServiceBus.getInstance()
	private SummaryReportService service
	
	@Before
	public void setup() {
		service = bus.getSummaryReportService()
	}
	
	@Test
	void testGenerateSummaryReport() throws Exception {
		MergedReport merged = TestUtils.generateMergedReportUsingTestData(false)
		
		SummaryReport report = service.generateSummaryReport(
			merged, FormatUtil.formatTwoStringToDate("03-Jan-2015"))
		
		assertDefaultReport(report, false)
		
		// generate the PDF
		File output = new File("build/my-test-summary-no-nrows.xls")
		output.getParentFile().mkdirs()
		service.generateSpreadsheet(output, report)
		
	}
	
	@Test
	void testGenerateSummaryReportWithEsams() throws Exception {
		MergedReport merged = TestUtils.generateMergedReportUsingTestData(false, true)
		
		SummaryReport report = service.generateSummaryReport(
			merged, FormatUtil.formatTwoStringToDate("03-Jan-2015"))
		
		assertDefaultReport(report, false)
		
		// generate the PDF
		File output = new File("build/my-test-summary-with-esams.xls")
		output.getParentFile().mkdirs()
		service.generateSpreadsheet(output, report)
		
	}
	
	@Test
	void testGenerateSummaryReportWithNrows() throws Exception {
		MergedReport merged = TestUtils.generateMergedReportUsingTestData(true)
		
		SummaryReport report = service.generateSummaryReport(
			merged, FormatUtil.formatTwoStringToDate("03-Jan-2015"))
		
		assertDefaultReport(report, true)
		
		// verify the nrows data
		MergedMember bravo = report.members.get(0)
		assertThat(bravo.daysOfOrdersInCurrentFY, is(87))
		assertThat(bravo.hasEnoughDaysOfOrdersForCurrentFY, is(true))
		assertThat(bravo.orders.size(), is(1))
		assertThat(bravo.orders.get(0).trackingNumber, is("1118479/0"))
		
		// verify the order distribution
		assertThat(report.orderGroup.get("AT"), is(5))
		assertThat(report.orderGroup.get("ADT"), is(87))
		assertThat(report.orderGroup.get("IDTT"), is(49))
		assertThat(report.orderGroup.get("ALL"), is(141))
		assertThat(FormatUtil.fractionToPercentage(report.atPercentage), is("6.06%"))
		
		// generate the PDF
		File output = new File("build/my-test-summary-with-nrows.xls")
		output.getParentFile().mkdirs()
		service.generateSpreadsheet(output, report)
		
	}
	
	private void verifyHistory(History history, boolean nrows) {
		int i = 0
		List<HistoryElement> membership = history.getResults(DataCategory.MEMBERSHIP)
		
		assertThat(membership.get(i++).toString(),
			is("PRD: [02-Jan-2015, 84.85%], [03-Jan-2015, 84.85%], "))
		assertThat(membership.get(i++).toString(),
			is("Fully Medically Ready: [02-Jan-2015, 60.61%], [03-Jan-2015, 60.61%], "))
		
		if (!nrows) {
			assertThat(membership.size(), is(2))
			
		} else {
			assertThat(membership.size(), is(3))
			assertThat(membership.get(i++).toString(),
				is("Orders in FY: [02-Jan-2015, 0%], [03-Jan-2015, 6.06%], "))
		}
		
		i = 0
		List<HistoryElement> gmt = history.getResults(DataCategory.GMT)
		assertThat(gmt.size(), is(18))
		assertThat(gmt.get(i++).toString(),
			is("ALCOH DRUG TOBACCO AWARE AVAIL APR (CATEGORY ONE): [02-Jan-2015, 0%], [03-Jan-2015, 0%], "))
		assertThat(gmt.get(i++).toString(),
			is("EO SEX HARR GRIEVANCE (CATEGORY ONE): [02-Jan-2015, 63.64%], [03-Jan-2015, 63.64%], "))
		assertThat(gmt.get(i++).toString(),
			is("PERS FINANCIAL MGMT (CATEGORY ONE): [02-Jan-2015, 66.67%], [03-Jan-2015, 66.67%], "))
		assertThat(gmt.get(i++).toString(),
			is("SEX ASSAULT PREV RESP AWARE AVAIL APR (CATEGORY ONE): [02-Jan-2015, 0%], [03-Jan-2015, 0%], "))
		assertThat(gmt.get(i++).toString(),
			is("STRESS MGMT AVAILIBLE AUG 2015 (CATEGORY ONE): [02-Jan-2015, 63.64%], [03-Jan-2015, 63.64%], "))
		assertThat(gmt.get(i++).toString(),
			is("SUICIDE PREVENT AWARE AVAIL DEC (CATEGORY ONE): [02-Jan-2015, 0%], [03-Jan-2015, 0%], "))
		assertThat(gmt.get(i++).toString(),
			is("ANTITERRORISM LEVEL I AWARENESS (CATEGORY TWO): [02-Jan-2015, 48.48%], [03-Jan-2015, 48.48%], "))
		assertThat(gmt.get(i++).toString(),
			is("COMBATING TRAFFICKING IN PERSONS (CATEGORY TWO): [02-Jan-2015, 45.45%], [03-Jan-2015, 45.45%], "))
		assertThat(gmt.get(i++).toString(),
			is("COUNTERINTELL AWARENESS (CATEGORY TWO): [02-Jan-2015, 0%], [03-Jan-2015, 0%], "))
		assertThat(gmt.get(i++).toString(),
			is("DOD CYBER AWARENESS CHALLENGE V2 (CATEGORY TWO): [02-Jan-2015, 9.09%], [03-Jan-2015, 9.09%], "))
		assertThat(gmt.get(i++).toString(),
			is("IC IAA V12 (INTEL COMMUNITY ONLY) (CATEGORY TWO): [02-Jan-2015, 45.45%], [03-Jan-2015, 45.45%], "))
		assertThat(gmt.get(i++).toString(),
			is("DOMESTIC VIOLENCE (CATEGORY TWO): [02-Jan-2015, 24.24%], [03-Jan-2015, 24.24%], "))
		assertThat(gmt.get(i++).toString(),
			is("OPERATIONAL RISK MANAGEMENT (CATEGORY TWO): [02-Jan-2015, 18.18%], [03-Jan-2015, 18.18%], "))
		assertThat(gmt.get(i++).toString(),
			is("OPERATIONAL SECURITY (CATEGORY TWO): [02-Jan-2015, 21.21%], [03-Jan-2015, 21.21%], "))
		assertThat(gmt.get(i++).toString(),
			is("PHYSICAL READINESS (CATEGORY TWO): [02-Jan-2015, 0%], [03-Jan-2015, 0%], "))
		assertThat(gmt.get(i++).toString(),
			is("PRIV AND PERS IDENTIFIABLE INFO (CATEGORY TWO): [02-Jan-2015, 51.52%], [03-Jan-2015, 51.52%], "))
		assertThat(gmt.get(i++).toString(),
			is("RECORDS MANAGEMENT (CATEGORY TWO): [02-Jan-2015, 12.12%], [03-Jan-2015, 12.12%], "))
		assertThat(gmt.get(i++).toString(),
			is("SEXUAL HEALTH AND RESPONSIBILITY (CATEGORY TWO): [02-Jan-2015, 18.18%], [03-Jan-2015, 18.18%], "))
		
		i = 0
		List<HistoryElement> ia = history.getResults(DataCategory.INDIVIDUAL_AUGMENTEE)
		assertThat(ia.size(), is(26))
		assertThat(ia.get(i++).toString(),
			is("CANS M16WS 1.0: [02-Jan-2015, 18.18%], [03-Jan-2015, 18.18%], "))
		assertThat(ia.get(i++).toString(),
			is("OSTNG: [02-Jan-2015, 27.27%], [03-Jan-2015, 27.27%], "))
		assertThat(ia.get(i++).toString(),
			is("ATFP CONUS: [02-Jan-2015, 81.82%], [03-Jan-2015, 81.82%], "))
		assertThat(ia.get(i++).toString(),
			is("ATFP OCONUS: [02-Jan-2015, 66.67%], [03-Jan-2015, 66.67%], "))
		assertThat(ia.get(i++).toString(),
			is("INFP: [02-Jan-2015, 60.61%], [03-Jan-2015, 60.61%], "))
		assertThat(ia.get(i++).toString(),
			is("INTRO TO EQUAL OPPORTUNITY: [02-Jan-2015, 24.24%], [03-Jan-2015, 24.24%], "))
		assertThat(ia.get(i++).toString(),
			is("MR1 PART1: [02-Jan-2015, 15.15%], [03-Jan-2015, 15.15%], "))
		assertThat(ia.get(i++).toString(),
			is("MR1 PART2: [02-Jan-2015, 15.15%], [03-Jan-2015, 15.15%], "))
		assertThat(ia.get(i++).toString(),
			is("COC LEVELA: [02-Jan-2015, 9.09%], [03-Jan-2015, 9.09%], "))
		assertThat(ia.get(i++).toString(),
			is("NPDC CWI 1.0: [02-Jan-2015, 36.36%], [03-Jan-2015, 36.36%], "))
		assertThat(ia.get(i++).toString(),
			is("NPDC HWI 1.0: [02-Jan-2015, 36.36%], [03-Jan-2015, 36.36%], "))
		assertThat(ia.get(i++).toString(),
			is("NPDC SAEDA 1.0: [02-Jan-2015, 36.36%], [03-Jan-2015, 36.36%], "))
		assertThat(ia.get(i++).toString(),
			is("NPDC USAV 1.0: [02-Jan-2015, 36.36%], [03-Jan-2015, 36.36%], "))
		assertThat(ia.get(i++).toString(),
			is("JKDDC TIP 2.0: [02-Jan-2015, 72.73%], [03-Jan-2015, 72.73%], "))
		assertThat(ia.get(i++).toString(),
			is("PRE DEPLOY SUICIDE AWARE: [02-Jan-2015, 27.27%], [03-Jan-2015, 27.27%], "))
		assertThat(ia.get(i++).toString(),
			is("PRE DEPLOY SEXUAL ASSAULT: [02-Jan-2015, 24.24%], [03-Jan-2015, 24.24%], "))
		assertThat(ia.get(i++).toString(),
			is("VIRTUAL MISSION PREP INTEL: [02-Jan-2015, 15.15%], [03-Jan-2015, 15.15%], "))
		assertThat(ia.get(i++).toString(),
			is("VIRTUAL MISSION PREP MEDIA: [02-Jan-2015, 18.18%], [03-Jan-2015, 18.18%], "))
		assertThat(ia.get(i++).toString(),
			is("M9 PISTOL: [02-Jan-2015, 15.15%], [03-Jan-2015, 15.15%], "))
		assertThat(ia.get(i++).toString(),
			is("INTRO BIOMETRICS: [02-Jan-2015, 18.18%], [03-Jan-2015, 18.18%], "))
		assertThat(ia.get(i++).toString(),
			is("COIN: [02-Jan-2015, 6.06%], [03-Jan-2015, 6.06%], "))
		assertThat(ia.get(i++).toString(),
			is("MALARIA PREVENTION AND CONTROL: [02-Jan-2015, 9.09%], [03-Jan-2015, 9.09%], "))
		assertThat(ia.get(i++).toString(),
			is("COIN OPERATIONS: [02-Jan-2015, 3.03%], [03-Jan-2015, 3.03%], "))
		assertThat(ia.get(i++).toString(),
			is("AFGAN IN PROSPECTIVE: [02-Jan-2015, 3.03%], [03-Jan-2015, 3.03%], "))
		assertThat(ia.get(i++).toString(),
			is("ACTIVE SHOOTER: [02-Jan-2015, 12.12%], [03-Jan-2015, 12.12%], "))
		assertThat(ia.get(i++).toString(),
			is("USFFC ISAF BASIC: [02-Jan-2015, 0%], [03-Jan-2015, 0%], "))
		
		i = 0
		List<HistoryElement> manaul = history.getResults(DataCategory.MANUAL_INPUTS)
		assertThat(manaul.size(), is(17))
		assertThat(manaul.get(i++).toString(),
			is("Status (Qualification): [02-Jan-2015, 69.7%], [03-Jan-2015, 69.7%], "))
		assertThat(manaul.get(i++).toString(),
			is("Notes (Qualification): [02-Jan-2015, 100%], [03-Jan-2015, 100%], "))
		assertThat(manaul.get(i++).toString(),
			is("Poly (Security, Accounts, and Access): [02-Jan-2015, 78.79%], [03-Jan-2015, 78.79%], "))
		assertThat(manaul.get(i++).toString(),
			is("Clearance (Security, Accounts, and Access): [02-Jan-2015, 93.94%], [03-Jan-2015, 93.94%], "))
		assertThat(manaul.get(i++).toString(),
			is("National (Security, Accounts, and Access): [02-Jan-2015, 93.94%], [03-Jan-2015, 93.94%], "))
		assertThat(manaul.get(i++).toString(),
			is("Joint (Security, Accounts, and Access): [02-Jan-2015, 93.94%], [03-Jan-2015, 93.94%], "))
		assertThat(manaul.get(i++).toString(),
			is("NMCI (Security, Accounts, and Access): [02-Jan-2015, 93.94%], [03-Jan-2015, 93.94%], "))
		assertThat(manaul.get(i++).toString(),
			is("NMCI Home Access (Security, Accounts, and Access): [02-Jan-2015, 87.88%], [03-Jan-2015, 87.88%], "))
		assertThat(manaul.get(i++).toString(),
			is("FY 2015 Preference (Mission Support): [02-Jan-2015, 93.94%], [03-Jan-2015, 93.94%], "))
		assertThat(manaul.get(i++).toString(),
			is("FY 2015 Target (Mission Support): [02-Jan-2015, 93.94%], [03-Jan-2015, 93.94%], "))
		assertThat(manaul.get(i++).toString(),
			is("FY 2014 Activity (Mission Support): [02-Jan-2015, 84.85%], [03-Jan-2015, 84.85%], "))
		assertThat(manaul.get(i++).toString(),
			is("Cell # (Contact Information): [02-Jan-2015, 9.09%], [03-Jan-2015, 9.09%], "))
		assertThat(manaul.get(i++).toString(),
			is("Home # (Contact Information): [02-Jan-2015, 0%], [03-Jan-2015, 0%], "))
		assertThat(manaul.get(i++).toString(),
			is("Work # (Contact Information): [02-Jan-2015, 0%], [03-Jan-2015, 0%], "))
		assertThat(manaul.get(i++).toString(),
			is("Navy Email (Contact Information): [02-Jan-2015, 9.09%], [03-Jan-2015, 9.09%], "))
		assertThat(manaul.get(i++).toString(),
			is("Personal Email (Contact Information): [02-Jan-2015, 3.03%], [03-Jan-2015, 3.03%], "))
		assertThat(manaul.get(i++).toString(),
			is("Work Email (Contact Information): [02-Jan-2015, 3.03%], [03-Jan-2015, 3.03%], "))
		
		i = 0
		List<HistoryElement> elearning = history.getResults(DataCategory.ELEARNING)
		assertThat(elearning.size(), is(7))
		//assertThat(elearning.get(i++).toString(),
			//is("IAAÊV12: [02-Jan-2015, 9.09%], [03-Jan-2015, 9.09%], "))
		assertThat(elearning.get(i++).toString(),
			is("ICÊIAAÊV12: [02-Jan-2015, 45.45%], [03-Jan-2015, 45.45%], "))
		assertThat(elearning.get(i++).toString(),
			is("CENSECFORÊATÊ010: [02-Jan-2015, 48.48%], [03-Jan-2015, 48.48%], "))
		assertThat(elearning.get(i++).toString(),
			is("PII: [02-Jan-2015, 51.52%], [03-Jan-2015, 51.52%], "))
		assertThat(elearning.get(i++).toString(),
			is("CTIP: [02-Jan-2015, 45.45%], [03-Jan-2015, 45.45%], "))
		assertThat(elearning.get(i++).toString(),
			is("RECORDS MANAGEMENT: [02-Jan-2015, 12.12%], [03-Jan-2015, 12.12%], "))
		assertThat(elearning.get(i++).toString(),
			is("OPSEC: [02-Jan-2015, 21.21%], [03-Jan-2015, 21.21%], "))
		assertThat(elearning.get(i++).toString(),
			is("COUNTERINTELL AWARENESS: [02-Jan-2015, 0%], [03-Jan-2015, 0%], "))
	}
	
	@Test
	void testIsStringADateWithin5Years() {

		boolean oneYearAgo =
			service.isStringADateWithin5Years(FormatUtil.formatTwoStringToDate("01-Jan-2014"), "01-Jan-2013")
		assertThat(oneYearAgo, is(true))
		
		boolean twoYearsAgo =
			service.isStringADateWithin5Years(FormatUtil.formatTwoStringToDate("01-Jan-2014"), "13-Feb-2011")
		assertThat(twoYearsAgo, is(true))
		
		boolean fiveYearsAgo =
			service.isStringADateWithin5Years(FormatUtil.formatTwoStringToDate("01-Jan-2014"), "05-Jan-2009")
		assertThat(fiveYearsAgo, is(true))
		
		boolean fiveYearsAgoAndOneDay =
			service.isStringADateWithin5Years(FormatUtil.formatTwoStringToDate("02-Jan-2014"), "01-Jan-2009")
		assertThat(fiveYearsAgoAndOneDay, is(false))
	}
	
	@Test
	public void testDetermineRankGroup() {
		assertThat(service.determineRankGroup("LTJG"), is ("LTJG"))
		assertThat(service.determineRankGroup("ENS"), is ("ENS"))
		assertThat(service.determineRankGroup("CAPT"), is ("CAPT"))
		assertThat(service.determineRankGroup("CTT1"), is ("CTT"))
		assertThat(service.determineRankGroup("CTN3"), is ("CTN"))
		assertThat(service.determineRankGroup("CTNCS"), is ("CTN"))
		assertThat(service.determineRankGroup("CTNC"), is ("CTN"))
		assertThat(service.determineRankGroup("CTNCM"), is ("CTN"))
		assertThat(service.determineRankGroup("CTNSN"), is ("CTN"))
		assertThat(service.determineRankGroup("CTNSA"), is ("CTN"))
		assertThat(service.determineRankGroup("CTNSR"), is ("CTN"))
		
	}
	
	@Test
	public void testGenerateRankDistributionSummary() {
		RuadReport ruad =
			bus.getRuadService().parseRuad(new File("config/SmartRUAD_RUIC_TEST.xlsx"))
			
		Map<String, Integer> map = service.generateRankDistributionSummary(ruad.members)
		
		assertThat(map.get("CDR"), is(1))
		assertThat(map.get("LCDR"), is(2))
		assertThat(map.get("LT"), is(2))
		assertThat(map.get("LTJG"), is(3))
		assertThat(map.get("CTN"), is(8))
		assertThat(map.get("CTT"), is(10))
		assertThat(map.get("CTR"), is(3))
		assertThat(map.get("CTI"), is(3))
		assertThat(map.get("YN"), is(2))
		
	}
	
	
	/**
	 * Utility for converin an array of doubles to string percentages
	 * @param values
	 * @return
	 */
	private String[] doublesToPercents(double[] values) {
		String[] result = new String[values.length]
		
		for (int i = 0; i < values.length; i++) {
			result[i] = FormatUtil.fractionToPercentage(values[i])
		}
		
		return result
	}
	
	@Test
	void testIsStringADate() {
		assertThat(service.isStringADate("02-Jan-2015"), is(true))
		assertThat(service.isStringADate("02-2015"), is(false))
		assertThat(service.isStringADate(""), is(false))
		assertThat(service.isStringADate(null), is(false))
		
	}
	
	@Test
	void testGenerateSummaryReport_NoManualInputs() throws Exception {
		MergedReport merged = TestUtils.generateMergedReportUsingTestDataWithNoManualInputs()
		
		SummaryReport report = service.generateSummaryReport(
			merged, FormatUtil.formatTwoStringToDate("03-Jan-2015"))
		
		// verify the mapping between merged and summary worked
		assertThat(report.getTotalMembersFromRuad(), is(34))
		assertThat(report.getTotalMembersFromELearning(), is(32))
		assertThat(report.getTotalMembersFromGMT(), is(32))
		assertThat(report.getTotalMembersFromIA(), is(32))
		assertThat(report.getTotalMembersFromManualInputs(), is(0))
		
		File output = new File("build/my-test-summary-no-manual.xls")
		output.getParentFile().mkdirs()
		service.generateSpreadsheet(output, report)
		
	}
	
	@Test
	void testDetermineTypeBasedOnValues_List() {
		Set<String> values = new LinkedHashSet<String>()
		values.add("Foo")
		values.add("Bar")
		
		String result = service.determineTypeBasedOnValues("foo", values)
		assertThat(result, is("Foo/Bar"))
	}
	
	@Test
	void testDetermineTypeBasedOnValues_Date() {
		Set<String> values = new LinkedHashSet<String>()
		values.add("N/A")
		values.add("03-Jan-2015")
		
		String result = service.determineTypeBasedOnValues("foo", values)
		assertThat(result, is("Date"))
	}
	
	@Test
	void testDetermineTypeBasedOnValues_Differet() {
		Set<String> values = new LinkedHashSet<String>()
		values.add("Foo")
		values.add("Bar")
		values.add("Alpha")
		values.add("Bravo")
		values.add("Charlie")
		values.add("Delta")
		
		String result = service.determineTypeBasedOnValues("foo", values)
		assertThat(result, nullValue())
	}
	
	@Test
	void testDetermineMissingInformation() {
		MergedReport merged = TestUtils.generateMergedReportUsingTestData()
		SummaryReport report = service.generateSummaryReport(
			merged, FormatUtil.formatTwoStringToDate("03-Jan-2015"))
		
		List<MissingData> list = service.determineMissingInformation(report, report.members.get(0))
		
		int i = 0
		assertThat(list.get(i++).toString(), 
			is("Mission Support: FY 2015 Preference (14D AT @ FIOC N for DNI/Waiver/28D qual training)"))
		assertThat(list.get(i++).toString(),
			is("Mission Support: FY 2015 Target (Depends on clearance/14D AT @ FIOC N for DNI)"))
		assertThat(list.get(i++).toString(),
			is("Mission Support: FY 2014 Activity"))
		assertThat(list.get(i++).toString(),
			is("Contact Information: Home # [Warning: Potential PII]"))
		assertThat(list.get(i++).toString(),
			is("Contact Information: Work #"))
		
		list = service.determineMissingInformationWithNoPII(report, report.members.get(0))
		
		i = 0
		assertThat(list.get(i++).toString(),
			is("Mission Support: FY 2015 Preference (14D AT @ FIOC N for DNI/Waiver/28D qual training)"))
		assertThat(list.get(i++).toString(),
			is("Mission Support: FY 2015 Target (Depends on clearance/14D AT @ FIOC N for DNI)"))
		assertThat(list.get(i++).toString(),
			is("Mission Support: FY 2014 Activity"))
		assertThat(list.get(i++).toString(),
			is("Contact Information: Work #"))
		
		list = service.determineMissingInformation(report, report.members.get(1))
		
		i = 0
		
		assertThat(list.get(i++).toString(), 
			is("Security, Accounts, and Access: Poly (Date)"))
		assertThat(list.get(i++).toString(), 
			is("Mission Support: FY 2015 Preference (14D AT @ FIOC N for DNI/Waiver/28D qual training)"))
		assertThat(list.get(i++).toString(), 
			is("Mission Support: FY 2015 Target (Depends on clearance/14D AT @ FIOC N for DNI)"))
		assertThat(list.get(i++).toString(), 
			is("Mission Support: FY 2014 Activity"))
		assertThat(list.get(i++).toString(), 
			is("Contact Information: Home # [Warning: Potential PII]"))
		assertThat(list.get(i++).toString(), 
			is("Contact Information: Work #"))
		assertThat(list.get(i++).toString(), 
			is("Contact Information: Personal Email [Warning: Potential PII]"))
		assertThat(list.get(i++).toString(), 
			is("Contact Information: Work Email"))
		
		list = service.determineMissingInformationWithNoPII(report, report.members.get(1))
		
		i = 0
		
		assertThat(list.get(i++).toString(),
			is("Security, Accounts, and Access: Poly (Date)"))
		assertThat(list.get(i++).toString(),
			is("Mission Support: FY 2015 Preference (14D AT @ FIOC N for DNI/Waiver/28D qual training)"))
		assertThat(list.get(i++).toString(),
			is("Mission Support: FY 2015 Target (Depends on clearance/14D AT @ FIOC N for DNI)"))
		assertThat(list.get(i++).toString(),
			is("Mission Support: FY 2014 Activity"))
		assertThat(list.get(i++).toString(),
			is("Contact Information: Work #"))
		assertThat(list.get(i++).toString(),
			is("Contact Information: Work Email"))
	}
	
	@Test
	void testIsValueMissing() {
		assertThat(service.isValueMissing(null), is(true))
		assertThat(service.isValueMissing("?"), is(true))
		assertThat(service.isValueMissing(""), is(true))
		assertThat(service.isValueMissing("Foobar"), is(false))
	}
	
	@Test
	void testGenerateEmailForMissingData_NoEmails() {
		String header = "Header"
		String from = "foo@bar.com"
		MemberWithMissingData data = new MemberWithMissingData(null, null)
		
		EmailMessage result = service.generateEmailForMissingData(header, from, data)
		assertThat(result, nullValue())
	}
	
	@Test
	void testGenerateEmailForMissingData_NoMissingData() {
		String header = "Header"
		String from = "foo@bar.com"
		MemberWithMissingData data = new MemberWithMissingData(null, new ArrayList<MissingData>())
		data.emails.add("alpha@bravo.com")
		
		EmailMessage result = service.generateEmailForMissingData(header, from, data)
		assertThat(result, nullValue())
	}
	
	@Test
	void testGenerateEmailForMissingData() {
		String header = "Header"
		String from = "foo@bar.com"
		
		MergedMember member = new MergedMember()
		member.firstName = "Alpha"
		member.lastName = "Bravo"
		member.rank = "CTT2"
		
		MissingData missing = new MissingData()
		missing.primary = "Security"
		missing.secondary = "Access"
		missing.type = "Date"
		
		MemberWithMissingData data = new MemberWithMissingData(member, new ArrayList<MissingData>())
		data.missing.add(missing)
		data.emails.add("alpha@bravo.com")
		
		EmailMessage result = service.generateEmailForMissingData(header, from, data)
		
		assertThat(result.to.get(0), is("alpha@bravo.com"))
		assertThat(result.from, is(from))
		assertThat(result.title, is("USNR Admin Automation: Missing Data Notification"))
		
		String[] lines = result.content.split("\n")
		int i = 0
		assertThat(lines[i++], is("CTT2 Bravo,"))
		assertThat(lines[i++], is(""))
		assertThat(lines[i++], is("Header"))
		assertThat(lines[i++], is(""))
		assertThat(lines[i++], is("Security: Access (Date)"))
		assertThat(lines[i++], is(""))
		assertThat(lines[i++], is("This message was generated and sent by the USNR Admin Automation Project 1.0.0"))
		
	}
	
	private void assertDefaultReport(SummaryReport report, boolean nrows) {
		// verify the mapping between merged and summary worked
		assertThat(report.getTotalMembersFromRuad(), is(33))
		assertThat(report.getTotalMembersFromELearning(), is(31))
		assertThat(report.getTotalMembersFromGMT(), is(31))
		assertThat(report.getTotalMembersFromIA(), is(31))
		
		// verify PRD percent
		assertThat(FormatUtil.fractionToPercentage(report.getPrdPercentage()), is("84.85%"))
		
		// verify IMR percent
		assertThat(FormatUtil.fractionToPercentage(report.getImrPercentage()), is("60.61%"))
		
		// verify e-Learning percents
		String[] eLearning = this.doublesToPercents(report.eLearningCourseCompletionPercentages)
		assertThat(eLearning.toString(), is("[45.45%, 48.48%, 51.52%, 45.45%, 12.12%, 21.21%, 0%]"))
			
		// verify GMT percents
		String[] gmt = this.doublesToPercents(report.gmtCourseCompletionPercentages)
		assertThat(gmt.toString(),
			is("[0%, 63.64%, 66.67%, 0%, 63.64%, 0%, 48.48%, 45.45%, 0%, 9.09%, 45.45%, 24.24%, 18.18%, 21.21%, 0%, 51.52%, 12.12%, 18.18%]"))
		
		// verify IA percents
		String[] ia = this.doublesToPercents(report.iaCourseCompletionPercentages)
		assertThat(ia.toString(),
			is("[18.18%, 27.27%, 81.82%, 66.67%, 60.61%, 24.24%, 15.15%, 15.15%, 9.09%, 36.36%, 36.36%, 36.36%, 36.36%, 72.73%, 27.27%, 24.24%, 15.15%, 18.18%, 15.15%, 18.18%, 6.06%, 9.09%, 3.03%, 3.03%, 12.12%, 0%]"))
		
		// manual percents
		double qualPercentage = report.manualPercentages[0]
		double polyPercentage = report.manualPercentages[2]
		double clearedPercentage = report.manualPercentages[3]
		double nationalPercentage = report.manualPercentages[4]
		double jointPercentage = report.manualPercentages[5]
		double nmciPercentage = report.manualPercentages[6]
		double nmciAtHomePercentage = report.manualPercentages[7]
		double currentYearAtPrefPercentage = report.manualPercentages[8]
		double currentYearAtTargerPercentage = report.manualPercentages[9]
		double previousYearActivityPercentage = report.manualPercentages[10]
		
		assertThat(FormatUtil.fractionToPercentage(qualPercentage), is("69.7%"))
		assertThat( FormatUtil.fractionToPercentage(polyPercentage), is("78.79%"))
		assertThat( FormatUtil.fractionToPercentage(clearedPercentage), is("93.94%"))
		assertThat( FormatUtil.fractionToPercentage(nationalPercentage), is("93.94%"))
		assertThat( FormatUtil.fractionToPercentage(jointPercentage), is("93.94%"))
		assertThat( FormatUtil.fractionToPercentage(nmciPercentage), is("93.94%"))
		assertThat( FormatUtil.fractionToPercentage(nmciAtHomePercentage), is("87.88%"))
		assertThat( FormatUtil.fractionToPercentage(currentYearAtPrefPercentage), is("93.94%"))
		assertThat( FormatUtil.fractionToPercentage(currentYearAtTargerPercentage), is("93.94%"))
		assertThat( FormatUtil.fractionToPercentage(previousYearActivityPercentage), is("84.85%"))
		
		Map<String, Integer> map = report.rankGroup
		
		assertThat(map.get("CDR"), is(1))
		assertThat(map.get("LCDR"), is(2))
		assertThat(map.get("LT"), is(2))
		assertThat(map.get("LTJG"), is(3))
		assertThat(map.get("CTN"), is(8))
		assertThat(map.get("CTT"), is(10))
		assertThat(map.get("CTR"), is(2))
		assertThat(map.get("CTI"), is(3))
		assertThat(map.get("YN"), is(2))
		
		// verify the source files
		assertThat(report.ruadFile.getName(), is("SmartRUAD_RUIC_TEST.xlsx"))
		assertThat(report.eLearningFile.getName(), is("ElearningStatus_120914.xlsx"))
		assertThat(report.gmtFile.getName(), is("GMTCourseCompletionStatus_120914.xlsx"))
		assertThat(report.iaFile.getName(), is("IndivAugTrngStat_120914.xlsx"))
		assertThat(report.imrFile.getName(), is("Nrrm System Report Individual Medical Readiness 12-11-2014.csv"))
		assertThat(report.manualFile.getName(), is("Manual Unit Inputs.xlsx"))
		
		assertThat(report.spreadsheetFooter,
			is("https://private.navyreserve.navy.mil/ALPHA/BRAVO/00000/00000PII/Generated%20Unit%20Tracker.xls"))
		assertThat(report.pdfFooter,
			is("https://private.navyreserve.navy.mil/ALPHA/BRAVO/00000/00000PII/Generated%20Individual%20Member%20Action%20Plan.pdf"))
		assertThat(report.trainingEmailHeader,
			is("You are required to complete the following courses on eLearning:"))
		assertThat(report.trainingSmsHeader,
			is("You need to complete the following courses on eLearning, see email for details:"))
		assertThat(report.emailRequiresAuthorization, is(true))
		assertThat(report.emailTlsEnabled, is(true))
		assertThat(report.emailHost, is("smtp.gmail.com"))
		assertThat(report.emailPort, is(587))
		assertThat(report.emailUsername, is("foo@bar.com"))
		
		// verify the history
		this.verifyHistory(report.history, nrows)
		
		// verify valid information
		MergedMember bravo = report.members.get(0)
		assertThat(bravo.firstName, is("BRAVO"))
		assertThat(bravo.lastName, is("ALPHA"))
		assertThat(bravo.rank, is("LCDR"))
		assertThat(bravo.valueValid.toString(),
			is("[true, true, true, true, true, true, true, true, false, false, false, true, false, false, true, true, true]"))
		
		MergedMember alpha = report.members.get(1)
		assertThat(alpha.firstName, is("ALPHA"))
		assertThat(alpha.lastName, is("BRAVO"))
		assertThat(alpha.rank, is("CTR2"))
		assertThat(alpha.valueValid.toString(),
			is("[false, true, false, false, false, true, false, false, false, false, false, true, false, false, true, false, false]"))
		
		assertThat(report.totalCAI, is(2))
		assertThat(report.totalCAO, is(2))
		assertThat(report.totalIAP, is(5))
		assertThat(report.totalOfficer, is(8))
		assertThat(report.totalEnlisted, is(26))
	}
	
	@Test
	void testIntegrateEsamsData() {
		SummaryReport report = new SummaryReport()
		Date date = FormatUtil.formatTwoStringToDate("03-Jan-2015")
		
		report.esamsFile = new File(".tmp")
		report.esamsCourseNames = ["Foo", "Bar", "Zoo"]
		
		MergedMember alpha = new MergedMember(firstName: "John", lastName: "Alpha")
		MergedMember bravo = new MergedMember(firstName: "Tony", lastName: "Bravo")
		MergedMember charlie = new MergedMember(firstName: "Sarah", lastName: "Charlie")
		report.members = [alpha, bravo, charlie]
		
		alpha.esamsRecords.add(new EsamsRecord(title: "Foo", requiredDate: FormatUtil.formatTwoStringToDate("02-Jan-2015")))
		alpha.esamsRecords.add(new EsamsRecord(title: "Bar", requiredDate: FormatUtil.formatTwoStringToDate("10-Jan-2015")))
		alpha.esamsRecords.add(new EsamsRecord(title: "Zoo", requiredDate: FormatUtil.formatTwoStringToDate("02-Jan-2014")))
		
		
		service.integrateEsamsData(report, date)
		
		assertThat(alpha.esamsCourseCompletions.toString(), is("[false, true, false]"))
		assertThat(bravo.esamsCourseCompletions.toString(), is("[true, true, true]"))
		assertThat(charlie.esamsCourseCompletions.toString(), is("[true, true, true]"))
		
		assertThat(FormatUtil.fractionToPercentage(report.esamsCourseCompletionPercentages[0]), is("66.67%"))
		assertThat(FormatUtil.fractionToPercentage(report.esamsCourseCompletionPercentages[1]), is("100%"))
		assertThat(FormatUtil.fractionToPercentage(report.esamsCourseCompletionPercentages[2]), is("66.67%"))
		
		assertThat(alpha.esamsCoursesToDo.toString(), is("[Foo, Zoo]"))
		
	}
	
}
