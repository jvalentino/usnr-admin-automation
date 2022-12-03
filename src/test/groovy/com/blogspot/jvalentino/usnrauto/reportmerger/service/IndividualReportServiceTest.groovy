package com.blogspot.jvalentino.usnrauto.reportmerger.service

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.individual.IndividualSummary;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.SummaryReport;
import com.blogspot.jvalentino.usnrauto.util.FormatUtil;
import com.blogspot.jvalentino.usnrauto.util.PdfUtil;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

class IndividualReportServiceTest {

    private ServiceBus bus = ServiceBus.getInstance()
    private IndividualReportService service
    
    @Before
    void setup() {
        service = bus.getIndividualReportService()
    }
    
    @Test
    void testGenerateIndividualActionItems() throws Exception {
        MergedReport merged = TestUtils.generateMergedReportUsingTestData()
		SummaryReport report = bus.getSummaryReportService().generateSummaryReport(
			merged, FormatUtil.formatTwoStringToDate("03-Jan-2015"))
		
        List<IndividualSummary> list = service.generateIndividualActionItems(bus.getMasCodeService(), report)
        
        assertThat(list.size(), is(33))
        
        IndividualSummary one = list.get(0)
        assertThat(one.toKey(), is("ALPHA, BRAVO LCDR"))
        
        int i = 0
        
        // assertThat(one.geteLearningCoursesToDo().get(i++), is("IAAÊV12")) Now ignored with rules
        assertThat(one.geteLearningCoursesToDo().get(i++), is("ICÊIAAÊV12"))
        assertThat(one.geteLearningCoursesToDo().get(i++), is("CENSECFORÊATÊ010"))
        assertThat(one.geteLearningCoursesToDo().get(i++), is("PII"))
        assertThat(one.geteLearningCoursesToDo().get(i++), is("CTIP"))
        assertThat(one.geteLearningCoursesToDo().get(i++), is("RECORDS MANAGEMENT"))
        assertThat(one.geteLearningCoursesToDo().get(i++), is("OPSEC"))
        assertThat(one.geteLearningCoursesToDo().get(i++), is("COUNTERINTELL AWARENESS"))
        
        i = 0
        assertThat(one.getIaCoursesToDo().get(i++), is("CANS M16WS 1.0"))
        assertThat(one.getIaCoursesToDo().get(i++), is("INTRO TO EQUAL OPPORTUNITY"))
        assertThat(one.getIaCoursesToDo().get(i++), is("MR1 PART1"))
        assertThat(one.getIaCoursesToDo().get(i++), is("MR1 PART2"))
        assertThat(one.getIaCoursesToDo().get(i++), is("COC LEVELA"))
        assertThat(one.getIaCoursesToDo().get(i++), is("VIRTUAL MISSION PREP INTEL"))
        assertThat(one.getIaCoursesToDo().get(i++), is("VIRTUAL MISSION PREP MEDIA"))
        assertThat(one.getIaCoursesToDo().get(i++), is("M9 PISTOL"))
        assertThat(one.getIaCoursesToDo().get(i++), is("INTRO BIOMETRICS"))
        assertThat(one.getIaCoursesToDo().get(i++), is("COIN"))
        assertThat(one.getIaCoursesToDo().get(i++), is("MALARIA PREVENTION AND CONTROL"))
        assertThat(one.getIaCoursesToDo().get(i++), is("COIN OPERATIONS"))
        assertThat(one.getIaCoursesToDo().get(i++), is("AFGAN IN PROSPECTIVE"))
        assertThat(one.getIaCoursesToDo().get(i++), is("ACTIVE SHOOTER"))
        assertThat(one.getIaCoursesToDo().get(i++), is("USFFC ISAF BASIC"))
        
        i = 0
        assertThat(one.getGmtCoursesToDo().get(i++), is("ALCOH DRUG TOBACCO AWARE AVAIL APR"))
        assertThat(one.getGmtCoursesToDo().get(i++), is("EO SEX HARR GRIEVANCE"))
        assertThat(one.getGmtCoursesToDo().get(i++), is("SEX ASSAULT PREV RESP AWARE AVAIL APR"))
        assertThat(one.getGmtCoursesToDo().get(i++), is("SUICIDE PREVENT AWARE AVAIL DEC"))
        assertThat(one.getGmtCoursesToDo().get(i++), is("ANTITERRORISM LEVEL I AWARENESS"))
        assertThat(one.getGmtCoursesToDo().get(i++), is("COMBATING TRAFFICKING IN PERSONS"))
        assertThat(one.getGmtCoursesToDo().get(i++), is("COUNTERINTELL AWARENESS"))
        assertThat(one.getGmtCoursesToDo().get(i++), is("DOD CYBER AWARENESS CHALLENGE V2"))
        assertThat(one.getGmtCoursesToDo().get(i++), is("IC IAA V12 (INTEL COMMUNITY ONLY)"))
        assertThat(one.getGmtCoursesToDo().get(i++), is("DOMESTIC VIOLENCE"))
        assertThat(one.getGmtCoursesToDo().get(i++), is("OPERATIONAL RISK MANAGEMENT"))
        assertThat(one.getGmtCoursesToDo().get(i++), is("OPERATIONAL SECURITY"))
        assertThat(one.getGmtCoursesToDo().get(i++), is("PHYSICAL READINESS"))
        assertThat(one.getGmtCoursesToDo().get(i++), is("PRIV AND PERS IDENTIFIABLE INFO"))
        assertThat(one.getGmtCoursesToDo().get(i++), is("RECORDS MANAGEMENT"))
        assertThat(one.getGmtCoursesToDo().get(i++), is("SEXUAL HEALTH AND RESPONSIBILITY"))
        
        i = 0
        assertThat(one.getCategoryOneGMTsToDo().get(i++), is("ALCOH DRUG TOBACCO AWARE AVAIL APR"))
        assertThat(one.getCategoryOneGMTsToDo().get(i++), is("EO SEX HARR GRIEVANCE"))
        assertThat(one.getCategoryOneGMTsToDo().get(i++), is("SEX ASSAULT PREV RESP AWARE AVAIL APR"))
        assertThat(one.getCategoryOneGMTsToDo().get(i++), is("SUICIDE PREVENT AWARE AVAIL DEC"))
        
        i = 0
        assertThat(one.getCategoryTwoGMTsToDo().get(i++), is("ANTITERRORISM LEVEL I AWARENESS"))
        assertThat(one.getCategoryTwoGMTsToDo().get(i++), is("COMBATING TRAFFICKING IN PERSONS"))
        assertThat(one.getCategoryTwoGMTsToDo().get(i++), is("COUNTERINTELL AWARENESS"))
        assertThat(one.getCategoryTwoGMTsToDo().get(i++), is("DOD CYBER AWARENESS CHALLENGE V2"))
        assertThat(one.getCategoryTwoGMTsToDo().get(i++), is("IC IAA V12 (INTEL COMMUNITY ONLY)"))
        assertThat(one.getCategoryTwoGMTsToDo().get(i++), is("DOMESTIC VIOLENCE"))
        assertThat(one.getCategoryTwoGMTsToDo().get(i++), is("OPERATIONAL RISK MANAGEMENT"))
        assertThat(one.getCategoryTwoGMTsToDo().get(i++), is("OPERATIONAL SECURITY"))
        assertThat(one.getCategoryTwoGMTsToDo().get(i++), is("PHYSICAL READINESS"))
        assertThat(one.getCategoryTwoGMTsToDo().get(i++), is("PRIV AND PERS IDENTIFIABLE INFO"))
        assertThat(one.getCategoryTwoGMTsToDo().get(i++), is("RECORDS MANAGEMENT"))
        assertThat(one.getCategoryTwoGMTsToDo().get(i++), is("SEXUAL HEALTH AND RESPONSIBILITY"))
        
        IndividualSummary three = list.get(2)
        assertThat(three.toKey(), is("CHARLIE, DELTA LTJG"))
        
        assertThat(three.getMasCodes().get(0).toString(), is("VOL: Volunteer for mobilization/recall."))
        
    }
    
    @Test
    void testGenerateMemberActionPdf() throws Exception {
        
        MergedReport report = TestUtils.generateMergedReportUsingTestData(false)
		SummaryReport summary = bus.getSummaryReportService().generateSummaryReport(
			report, FormatUtil.formatTwoStringToDate("03-Jan-2015"))
		
        List<IndividualSummary> list = service.generateIndividualActionItems(
            bus.getMasCodeService(), summary)
        
        File output = new File("build/my-test-report.pdf")
        output.getParentFile().mkdirs()
        		
        service.generateMemberActionPdf(output, list, summary)
        
    }
	
	@Test
	void testGenerateMemberActionPdfWithEsamsAndNrows() throws Exception {
		
		MergedReport report = TestUtils.generateMergedReportUsingTestData(true, true)
		SummaryReport summary = bus.getSummaryReportService().generateSummaryReport(
			report, FormatUtil.formatTwoStringToDate("03-Jan-2015"))
		
		List<IndividualSummary> list = service.generateIndividualActionItems(
			bus.getMasCodeService(), summary)
		
		File output = new File("build/my-test-report-nrows-esams.pdf")
		output.getParentFile().mkdirs()
				
		service.generateMemberActionPdf(output, list, summary)
		
	}
	
	@Test
	void testGenerateMemberActionPdf_WithNrows() throws Exception {
		
		MergedReport report = TestUtils.generateMergedReportUsingTestData()
		SummaryReport summary = bus.getSummaryReportService().generateSummaryReport(
			report, FormatUtil.formatTwoStringToDate("03-Jan-2015"))
		
		List<IndividualSummary> list = service.generateIndividualActionItems(
			bus.getMasCodeService(), summary)
		
		File output = new File("build/my-test-report-nrows.pdf")
		output.getParentFile().mkdirs()
				
		service.generateMemberActionPdf(output, list, summary)
		
	}
	
	@Test
	void testGenerateMemberActionPdf_NoManualInputs() throws Exception {
		
		MergedReport merged = TestUtils.generateMergedReportUsingTestData()
		SummaryReport summary = bus.getSummaryReportService().generateSummaryReport(
			merged, FormatUtil.formatTwoStringToDate("03-Jan-2015"))
		
		List<IndividualSummary> list = service.generateIndividualActionItems(
			bus.getMasCodeService(), summary)
		
		File output = new File("build/my-test-report-no-manual.pdf")
		output.getParentFile().mkdirs()
				
		service.generateMemberActionPdf(output, list, summary)
		
	}
    
    @Test
    void testGenerateTrainingTableData() {
        List<String> todo = ["a", "c", "d"]
        String[][] content = PdfUtil.generateTrainingTableData(todo, 2)
        assertThat(content.toString(), is("[[a, c], [d, ]]"))
        
       todo = ["a"]
        content = PdfUtil.generateTrainingTableData(todo, 2)
        assertThat(content.toString(), is("[[a, ]]"))
        
        todo = ["a", "b", "e", "f"]
        content = PdfUtil.generateTrainingTableData(todo, 2)
        assertThat(content.toString(), is("[[a, b], [e, f]]"))
    }
}
