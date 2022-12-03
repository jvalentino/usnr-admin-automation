package com.blogspot.jvalentino.usnrauto.reportmerger.service

import org.junit.Before;
import org.junit.Test;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.idcreport.IDCReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.SummaryReport;
import com.blogspot.jvalentino.usnrauto.util.FormatUtil;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

class IDCReportServiceTest {

	private ServiceBus bus = ServiceBus.getInstance()
	private IDCReportService service
	
	@Before
	void setup() {
		service = bus.getIDCReportService()
	}
	
	@Test
	void testGenerateText() {
		MergedReport merged = TestUtils.generateMergedReportUsingTestData()
		
		SummaryReport summary = bus.getSummaryReportService().generateSummaryReport(
			merged, FormatUtil.formatTwoStringToDate("03-Jan-2015"))
		
		IDCReport report = service.generateData(summary)
		
		// verify the  data
		assertThat(report.overallMedicalReadiness, is(closeTo(0.6061D, 0.001D)))
		assertThat(report.clearances, is("31/2/?/?"))
		assertThat(report.clearanceIssues.size(), is(1))
		assertThat(report.clearanceIssues.get(0), 
			is("Only 78.79% of Sailors have a current polygraph within 5 years"))
		assertThat(report.manning, 
			is("8 Officers, 26 Enlisted, plus 2 CAI, 2 CAO, 5 IAP"))
		assertThat(report.eidws, is("26/5/16"))
		assertThat(report.idwoQual, is("8/1/7/?"))
		assertThat(report.ipQual, is("0/0/0/?"))
		assertThat(report.intelQual, is("0/0/0/?"))
		assertThat(report.iwQual, is("8/1/7/?"))
		
		// Just print it out to visually verify it
		String text = service.generateText(report)
		println text
		
	}
	
	
}
