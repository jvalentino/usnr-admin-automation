package com.blogspot.jvalentino.usnrauto.reportmerger.service

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsELearningMember;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsELearningReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsGMTMember;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsGMTMemberReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.imr.ImrRecord;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.imr.ImrReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.ManualInputReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.ManualInputRules;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.merge.NrowsOrder;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.nrows.NrowsRawEntry;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedMember;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.ruad.RuadReport;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

class MergeServiceTest {

    private ServiceBus bus = ServiceBus.getInstance()
    
    
    private MergeService mergeService
    
    @Before
    void setup() {
        mergeService = bus.getMergeService()
    }
	
	@Test
	void testConvertOrders() {
		NrowsRawEntry input = new NrowsRawEntry()
		input.days = 5
		input.dutyType = "A"
		input.endDate = new Date(1)
		input.fy = 2015
		input.grade = "O1"
		input.line = 1
		input.name = "BAR, FOO"
		input.ruic = "B"
		input.sdn = "C"
		input.ssn = "1111"
		input.startDate = new Date(0)
		input.status = "MOD"
		input.trackingNumber = "D"
		
		NrowsOrder output = mergeService.convertOrders(input)
		
		assertThat( output.sdn, is("C"))
		assertThat( output.trackingNumber, is("D"))
		assertThat( output.dutyType, is("A"))
		assertThat( output.startDate.getTime(), is(0L))
		assertThat( output.endDate.getTime(), is(1L))
		assertThat( output.days, is(5))
		assertThat( output.fy, is(2015))
		assertThat( output.status, is("MOD"))
		
	}
    
	
	
    @Test
    public void testGenerateReport() throws Exception {
                    
        MergedReport report = TestUtils.generateMergedReportUsingTestData(false)
		
		assertDefaultMergedReport(report)
        		
    }
	
	@Test
	public void testGenerateReportWithNrows() throws Exception {
					
		MergedReport report = TestUtils.generateMergedReportUsingTestData(true)
		
		assertDefaultMergedReport(report)
		
		List<MergedMember> members = report.getMembers()
		
		// NROWS stuff
		assertThat(report.getWarnings().get(11), is("NROWS Report Line 6: Line could not be read because the data doesn't contain 12 columns"))
		
		MergedMember bravo = members.get(0)
		assertThat(bravo.daysOfOrdersInCurrentFY, is(87))
		assertThat(bravo.hasEnoughDaysOfOrdersForCurrentFY, is(true))
		assertThat(bravo.orders.size(), is(1))
		assertThat(bravo.orders.get(0).trackingNumber, is("1118479/0"))
		
		// verify eLearning data (CDR HOTEL GOLF)
		MergedMember cdr = members.get(7)
		assertThat(cdr.daysOfOrdersInCurrentFY, is(0))
		assertThat(cdr.hasEnoughDaysOfOrdersForCurrentFY, is(false))
		assertThat(cdr.orders.size(), is(0))
		
		MergedMember charlie = members.get(2)
		assertThat(charlie.daysOfOrdersInCurrentFY, is(49))
		assertThat(charlie.hasEnoughDaysOfOrdersForCurrentFY, is(true))
		assertThat(charlie.orders.size(), is(2))
		assertThat(charlie.orders.get(0).trackingNumber, is("1114253/1"))
		assertThat(charlie.orders.get(1).trackingNumber, is("1113039/0"))
		
		assertThat(report.nrowsFile.getName(), is("MiscSelresReport.txt"))
		
		
	}
	
	@Test
	public void testGenerateReportWithEsams() throws Exception {
					
		MergedReport report = TestUtils.generateMergedReportUsingTestData(false, true)
		
		assertDefaultMergedReport(report)
		
		List<MergedMember> members = report.getMembers()
		
		assertThat(report.esamsCourseNames.length, is(4))
		assertThat(report.esamsCourseNames[0], is("ESAMS Training for Supervisors (Web or Classroom)"))
		assertThat(report.esamsCourseNames[1], is("Fire Prevention and Portable Fire Extinguisher Training and Education"))
		assertThat(report.esamsCourseNames[2], is("HAZCOM Training Job/Chemical Specific (OJT by Supervisor)"))
		assertThat(report.esamsCourseNames[3], is("Monthly Safety Talks - Given"))
		
		assertThat(members.get(1).esamsRecords.size(), is(0))
		
		MergedMember bravo = members.get(0)
		assertThat(bravo.esamsRecords.size(), is(4))
		assertThat(bravo.esamsRecords.get(0).toString(), 
			is('name: ALPHA, BRAVO W, title: ESAMS Training for Supervisors (Web or Classroom), requiredDate: 01/01/2015'))
		assertThat(bravo.esamsRecords.get(1).toString(), 
			is('name: ALPHA, BRAVO W, title: Fire Prevention and Portable Fire Extinguisher Training and Education, requiredDate: 06/09/2016'))
		assertThat(bravo.esamsRecords.get(2).toString(),
			is('name: ALPHA, BRAVO W, title: HAZCOM Training Job/Chemical Specific (OJT by Supervisor), requiredDate: 12/12/2014'))
		assertThat(bravo.esamsRecords.get(3).toString(),
			is('name: ALPHA, BRAVO W, title: Monthly Safety Talks - Given, requiredDate: 12/12/2014'))
		
		MergedMember charlie = members.get(2)
		assertThat(charlie.esamsRecords.size(), is(3))
		assertThat(charlie.esamsRecords.get(0).toString(),
			is('name: CHARLIE, DELTA H, title: Fire Prevention and Portable Fire Extinguisher Training and Education, requiredDate: 06/29/2016'))
		assertThat(charlie.esamsRecords.get(1).toString(), 
			is('name: CHARLIE, DELTA H, title: HAZCOM Training Job/Chemical Specific (OJT by Supervisor), requiredDate: 12/13/2013'))
		assertThat(charlie.esamsRecords.get(2).toString(), 
			is('name: CHARLIE, DELTA H, title: Monthly Safety Talks - Given, requiredDate: 12/12/2014'))
		
				
	}
    
    @Test
    void testScrubImr() {
        List<ImrRecord> members = new ArrayList<ImrRecord>()
        List<String> warnings = new ArrayList<String>()
        
        ImrRecord one = new ImrRecord()
        one.setFirstName("John")
        one.setLastName("Smith")
        
        ImrRecord two = new ImrRecord()
        two.setFirstName("Foo")
        two.setLastName("Bar")
        
        ImrRecord three = new ImrRecord()
        three.setFirstName("John")
        three.setLastName("Smith")
        
        members.add(one)
        members.add(two)
        members.add(three)
        
        List<ImrRecord> results = bus.getMergeService().scrubImr(members, warnings)
        
        assertThat(results.size(), is(1))
        assertThat(results.get(0).toKey(), is("Foo Bar"))
        
    }
	
	private ManualInputRules generateRules() {
		ManualInputRules rules = new ManualInputRules()
		rules.ignorePeople.add("CTR1 Yankee Zulu")
		rules.ignorePeople.add("CTR1 Foo Foo")
		return rules
	}
	
	@Test
	void testHandleRulesForRuad() {
		RuadReport ruad = bus.getRuadService().parseRuad(new File("config/SmartRUAD_RUIC_TEST.xlsx"))
		
		ManualInputRules rules = generateRules()
		List<String> warnings = new ArrayList<String>()
		
		assertThat(ruad.members.size(), is(34))
		
		mergeService.handleRulesForRuad(rules, ruad, warnings)
		
		assertThat(ruad.members.size(), is(33))
		assertThat(warnings.size(), is(0))
		
	}
	
	@Test
	void testHandleRulesForELearning() {
		FltMpsELearningReport eLearning =
			bus.getFltMpsService().parseELearning(new File("config/ElearningStatus_120914.xlsx"))
			
		ManualInputRules rules = generateRules()
		List<String> warnings = new ArrayList<String>()
		
		assertThat(eLearning.members.size(), is(32))
		
		mergeService.handleRulesForELearning(rules, eLearning, warnings)
		
		assertThat(eLearning.members.size(), is(31))
		assertThat(warnings.size(), is(0))
	}
	
	@Test
	void testHandleRulesForGmt() {
		FltMpsGMTMemberReport gmt = 
			bus.getFltMpsService().parseGMT(new File("config/GMTCourseCompletionStatus_120914.xlsx"))
			
		ManualInputRules rules = generateRules()
		List<String> warnings = new ArrayList<String>()
		
		assertThat(gmt.members.size(), is(32))
		
		mergeService.handleRulesForGmt(rules, gmt, warnings)
		
		assertThat(gmt.members.size(), is(31))
		assertThat(warnings.size(), is(0))
	}
	
	@Test
	void testhandleRulesForIa() {
		FltMpsELearningReport ia =
			bus.getFltMpsService().parseIA(new File("config/IndivAugTrngStat_120914.xlsx"))
		
		ManualInputRules rules = generateRules()
		List<String> warnings = new ArrayList<String>()
		
		assertThat(ia.members.size(), is(32))
		
		mergeService.handleRulesForIa(rules, ia, warnings)
		
		assertThat(ia.members.size(), is(31))
		assertThat(warnings.size(), is(0))
	}
	
	@Test
	void testHandleRulesForIMR() {
		ImrReport imr =
			bus.getIMRService().parse(new File("config/Nrrm System Report Individual Medical Readiness 12-11-2014.csv"))
		
		ManualInputRules rules = generateRules()
		List<String> warnings = new ArrayList<String>()
		
		assertThat(imr.members.size(), is(32))
		
		mergeService.handleRulesForIMR(rules, imr, warnings)
		
		assertThat(imr.members.size(), is(31))
		assertThat(warnings.size(), is(0))
	}
	
	@Test
	void testHandleRulesForManualInputs() {
		ManualInputReport manual =
			bus.getManualInputService().parse(new File("config/Manual Unit Inputs.xlsx"))
		
		ManualInputRules rules = generateRules()
		List<String> warnings = new ArrayList<String>()
		
		assertThat(manual.members.size(), is(34))
		
		mergeService.handleRulesForManualInputs(rules, manual, warnings)
		
		assertThat(manual.members.size(), is(33))
		assertThat(warnings.size(), is(0))
		
	}
	
	@Test
	void testHandleRulesForELearning_courses() {
		List<String> warnings = new ArrayList<String>()
		
		ManualInputRules rules = new ManualInputRules()
		rules.ignoreCourses.add("bravo")
		
		FltMpsELearningReport eLearning = new FltMpsELearningReport()
		eLearning.courseNames = ["alpha", "bravo", "charlie"]
		
		eLearning.members = new ArrayList<FltMpsELearningMember>()
		FltMpsELearningMember member = new FltMpsELearningMember()
		member.courseCompletions = [true, false, true]
		member.courseNames = eLearning.courseNames.clone()
		member.firstName = "First"
		member.lastName = "Last"
		member.rank = "CTN1"
		eLearning.members.add(member)
		
		mergeService.handleRulesForELearning(rules, eLearning, warnings)
		
		assertThat(warnings.size(), is(0))
		assertThat(eLearning.courseNames.toString(), is("[alpha, charlie]"))
		
		FltMpsELearningMember found = eLearning.members.get(0)
		assertThat(found.courseNames.toString(), is("[alpha, charlie]"))
		assertThat(found.courseCompletions.toString(), is("[true, true]"))
		
	}
	
	@Test
	void testHandleRulesForIa_courses() {
		List<String> warnings = new ArrayList<String>()
		
		ManualInputRules rules = new ManualInputRules()
		rules.ignoreCourses.add("bravo")
		
		FltMpsELearningReport eLearning = new FltMpsELearningReport()
		eLearning.courseNames = ["alpha", "bravo", "charlie"]
		
		eLearning.members = new ArrayList<FltMpsELearningMember>()
		FltMpsELearningMember member = new FltMpsELearningMember()
		member.courseCompletions = [true, false, true]
		member.courseNames = eLearning.courseNames.clone()
		member.firstName = "First"
		member.lastName = "Last"
		member.rank = "CTN1"
		eLearning.members.add(member)
		
		mergeService.handleRulesForIa(rules, eLearning, warnings)
		
		assertThat(warnings.size(), is(0))
		assertThat(eLearning.courseNames.toString(), is("[alpha, charlie]"))
		
		FltMpsELearningMember found = eLearning.members.get(0)
		assertThat(found.courseNames.toString(), is("[alpha, charlie]"))
		assertThat(found.courseCompletions.toString(), is("[true, true]"))
	}
	
	@Test
	void testHandleRulesForGmt_courses() {
		List<String> warnings = new ArrayList<String>()
		
		ManualInputRules rules = new ManualInputRules()
		rules.ignoreCourses.add("bravo")
		
		FltMpsGMTMemberReport gmt = new FltMpsGMTMemberReport()
		gmt.courseNames = ["alpha", "bravo", "charlie"]
		gmt.courseCategories = ["I", "II", "III"]
		
		gmt.members = new ArrayList<FltMpsGMTMemberReport>()
		FltMpsGMTMember member = new FltMpsGMTMember()
		member.courseCompletions = [true, false, true]
		member.courseNames = gmt.courseNames.clone()
		member.courseCategories = gmt.courseCategories.clone()
		member.firstName = "First"
		member.lastName = "Last"
		member.rank = "CTN1"
		gmt.members.add(member)
		
		mergeService.handleRulesForGmt(rules, gmt, warnings)
		
		assertThat(warnings.size(), is(0))
		assertThat(gmt.courseNames.toString(), is("[alpha, charlie]"))
		assertThat(gmt.courseCategories.toString(), is("[I, III]"))
		
		FltMpsGMTMember found = gmt.members.get(0)
		assertThat(found.courseNames.toString(), is("[alpha, charlie]"))
		assertThat(found.courseCompletions.toString(), is("[true, true]"))
		assertThat(found.courseCategories.toString(), is("[I, III]"))
		
	}
	
	@Test
	public void testGenerateReport_NoManualInputs() throws Exception {
		
		MergedReport report = TestUtils.generateMergedReportUsingTestDataWithNoManualInputs()
		
		assertThat(report.getTotalMembersFromRuad(), is(34))
		assertThat(report.getTotalMembersFromELearning(), is(32))
		assertThat(report.getTotalMembersFromGMT(), is(32))
		assertThat(report.getTotalMembersFromIA(), is(32))
		assertThat(report.getTotalMembersFromIMR(), is(32))
		assertThat(report.getTotalMembersFromManualInputs(), is(0))
		
		TestUtils.verifyELearningCourses(report.geteLearningCourseNames(), false)
		TestUtils.verifyGMTCourses(report.getGmtCourseNames())
		TestUtils.verifyGMTCategories(report.getGmtCourseCategories())
		TestUtils.verifyIACourses(report.getIaCourseNames())
	
	}
	
	private void assertDefaultMergedReport(MergedReport report) {
		assertThat(report.getTotalMembersFromRuad(), is(33))
		assertThat(report.getTotalMembersFromELearning(), is(31))
		assertThat(report.getTotalMembersFromGMT(), is(31))
		assertThat(report.getTotalMembersFromIA(), is(31))
		assertThat(report.getTotalMembersFromIMR(), is(31))
		assertThat(report.getTotalMembersFromManualInputs(), is(33))
		
		int i = 0
		
		assertThat(report.getWarnings().get(i++), is("CTR1 Yankee Zulu is being ignored in all reporting"))
		assertThat(report.getWarnings().get(i++), is("IAAÊV12 is being ignored in all reporting"))
		assertThat(report.getWarnings().get(i++), is("COP, ROBO CTNC does not have any data in eLearning"))
		assertThat(report.getWarnings().get(i++), is("HUGINKISS, AMANDA CTTC does not have any data in eLearning"))
		assertThat(report.getWarnings().get(i++), is("COP, ROBO CTNC does not have any data in the GMT report"))
		assertThat(report.getWarnings().get(i++), is("HUGINKISS, AMANDA CTTC does not have any data in the GMT report"))
		assertThat(report.getWarnings().get(i++), is("COP, ROBO CTNC does not have any data in the IA report"))
		assertThat(report.getWarnings().get(i++), is("HUGINKISS, AMANDA CTTC does not have any data in the IA report"))
		assertThat(report.getWarnings().get(i++), is("The IMR information for COP, ROBO CTNC could not be found."))
		assertThat(report.getWarnings().get(i++), is("The IMR information for HUGINKISS, AMANDA CTTC could not be found."))
		assertThat(report.getWarnings().get(i++), is("The IMR information for MAN, SUPER CTTSN could not be found."))
		
		TestUtils.verifyELearningCourses(report.geteLearningCourseNames())
		TestUtils.verifyGMTCourses(report.getGmtCourseNames())
		TestUtils.verifyGMTCategories(report.getGmtCourseCategories())
		TestUtils.verifyIACourses(report.getIaCourseNames())
		
		List<MergedMember> members = report.getMembers()
		
		assertThat(members.get(0).designator, is("1815I"))
		
		// verify that members were organized by last name
		i = 0
		assertThat(members.get(i++).toKey(), is("ALPHA, BRAVO LCDR"))
		assertThat(members.get(i++).toKey(), is("BRAVO, ALPHA CTR2"))
		assertThat(members.get(i++).toKey(), is("CHARLIE, DELTA LTJG"))
		assertThat(members.get(i++).toKey(), is("COP, ROBO CTNC"))
		assertThat(members.get(i++).toKey(), is("DELTA, CHARLIE CTN3"))
		assertThat(members.get(i++).toKey(), is("ECHO, FOXTROT LCDR"))
		assertThat(members.get(i++).toKey(), is("FOXTROT, ECHO CTT2"))
		assertThat(members.get(i++).toKey(), is("GOLF, HOTEL CDR"))
		assertThat(members.get(i++).toKey(), is("HART, COREY CTT1"))
		assertThat(members.get(i++).toKey(), is("HOTEL, GOLF CTT1"))
		assertThat(members.get(i++).toKey(), is("HUGINKISS, AMANDA CTTC"))
		assertThat(members.get(i++).toKey(), is("INDIA, JULIET LT"))
		assertThat(members.get(i++).toKey(), is("JULIET, INDIA YNCS"))
		assertThat(members.get(i++).toKey(), is("KILO, LIMA LT"))
		assertThat(members.get(i++).toKey(), is("LIMA, KILO CTRCM"))
		assertThat(members.get(i++).toKey(), is("MAN, SUPER CTTSN"))
		assertThat(members.get(i++).toKey(), is("MIKE, NOVEMBER LTJG"))
		assertThat(members.get(i++).toKey(), is("NOVEMBER, MIKE YN1"))
		assertThat(members.get(i++).toKey(), is("OSCAR, PAPA LTJG"))
		assertThat(members.get(i++).toKey(), is("PAPA, OSCAR CTI1"))
		assertThat(members.get(i++).toKey(), is("QUEBEC, ROMEO CTNC"))
		assertThat(members.get(i++).toKey(), is("RADCLIFF, DANIEL CTT2"))
		assertThat(members.get(i++).toKey(), is("RIKER, WILLIAM CTT3"))
		assertThat(members.get(i++).toKey(), is("ROMEO, QUEBEC CTI2"))
		assertThat(members.get(i++).toKey(), is("SIERRA, TANGO CTTC"))
		assertThat(members.get(i++).toKey(), is("SPRINGER, JERRY CTTSA"))
		assertThat(members.get(i++).toKey(), is("TANGO, SIERRA CTIC"))
		assertThat(members.get(i++).toKey(), is("UNIFORM, VICTOR CTN1"))
		assertThat(members.get(i++).toKey(), is("VICTOR, UNIFORM CTN1"))
		assertThat(members.get(i++).toKey(), is("WALKEN, CHRISTOPHER CTTSN"))
		assertThat(members.get(i++).toKey(), is("WHISKEY, X-RAY CTN1"))
		assertThat(members.get(i++).toKey(), is("X-RAY, WHISKEY CTNCS"))
		assertThat(members.get(i++).toKey(), is("YANKEE, ZULU CTN1"))
		// assertThat(members.get(i++).toKey(), is("ZULU, YANKEE CTR1")) this guy is ignored now
				
		// verify eLearning data (CDR HOTEL GOLF)
		MergedMember cdr = members.get(7)
		assertThat(cdr.toKey(), is("GOLF, HOTEL CDR"))
		assertThat(cdr.eLearningCompletionsToString(),
			is("true, true, true, true, false, true, false"))
		
		// verify GMT data
		assertThat(cdr.gmtCompletionsToString(),
			is("false, true, true, false, true, false, true, true, false, false, true, true, false, true, false, true, false, true"))
		
		// verify IA data
		assertThat(cdr.iaCourseCompletionsToString(),
			is("false, false, true, true, false, false, false, false, false, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false"))
		
		// verify PRD
		assertThat(cdr.dateToString(cdr.getPrd()), is("20151101"))
		
		assertThat(cdr.getImrStatus(), is("Fully Medically Ready"))
		assertThat(members.get(9).getImrStatus(), is("Partially Medically Ready"))
		
		// verify manual inputs
		assertThat(report.primaryColumnHeaders.toString(),
			is("[Qualification, Qualification, Security, Accounts, and Access, Security, Accounts, and Access, Security, Accounts, and Access, Security, Accounts, and Access, Security, Accounts, and Access, Security, Accounts, and Access, Mission Support, Mission Support, Mission Support, Contact Information, Contact Information, Contact Information, Contact Information, Contact Information, Contact Information]"))
		assertThat(report.secondaryColumnHeaders.toString(),
			is("[Status, Notes, Poly, Clearance, National, Joint, NMCI, NMCI Home Access, FY 2015 Preference, FY 2015 Target, FY 2014 Activity, Cell #, Home #, Work #, Navy Email, Personal Email, Work Email]"))
		
		// verify the commander's manual inputs
		assertThat(cdr.values.toString(),
			is("[Complete, , 01-Jan-2014, Full, Good, Good, Yes, Yes, Waiver, 14D AT @ FIOC N for DNI, 14D AT Unit Admin, ?, ?, ?, ?, ?, ?]"))
		
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
		
		assertThat(report.totalCAI, is(2))
		assertThat(report.totalCAO, is(2))
		assertThat(report.totalIAP, is(5))
		assertThat(report.totalOfficer, is(8))
		assertThat(report.totalEnlisted, is(26))
	}

	
}
