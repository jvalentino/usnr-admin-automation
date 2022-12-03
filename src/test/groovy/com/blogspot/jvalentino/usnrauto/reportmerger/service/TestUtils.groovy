package com.blogspot.jvalentino.usnrauto.reportmerger.service

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.esams.EsamsReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsELearningReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsGMTMemberReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.imr.ImrReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.ManualInputReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.nrows.NrowsRawReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.ruad.RuadReport;

class TestUtils {
    
    private static ServiceBus bus = ServiceBus.getInstance()

    static MergedReport generateMergedReportUsingTestData(boolean doNrows=true, boolean doEsams=false) throws Exception {
        RuadReport ruad =
            bus.getRuadService().parseRuad(new File("config/SmartRUAD_RUIC_TEST.xlsx"))
            
        FltMpsELearningReport eLearning =
            bus.getFltMpsService().parseELearning(new File("config/ElearningStatus_120914.xlsx"))
            
        FltMpsGMTMemberReport gmt =
            bus.getFltMpsService().parseGMT(new File("config/GMTCourseCompletionStatus_120914.xlsx"))
            
        FltMpsELearningReport ia =
            bus.getFltMpsService().parseIA(new File("config/IndivAugTrngStat_120914.xlsx"))
            
        ImrReport imr = 
            bus.getIMRService().parse(new File("config/Nrrm System Report Individual Medical Readiness 12-11-2014.csv"))
        
		ManualInputReport manual =
			bus.getManualInputService().parse(new File("config/Manual Unit Inputs.xlsx"))
		
		NrowsRawReport nrows = null
		if (doNrows) {
			nrows = bus.getNrowsService().parse(new File("config/MiscSelresReport.txt"))
		}
		
		EsamsReport esams = null
		if (doEsams) {
			esams = bus.getEsamsService().parse(new File("config/NeededTraining.xlsx"))
		}
				
        MergedReport report = bus.getMergeService().generateReport(ruad, eLearning, gmt, ia, imr, manual, nrows, esams)
        
        return report
        
    }
	
	static MergedReport generateMergedReportUsingTestDataWithNoManualInputs() throws Exception {
		RuadReport ruad =
			bus.getRuadService().parseRuad(new File("config/SmartRUAD_RUIC_TEST.xlsx"))
			
		FltMpsELearningReport eLearning =
			bus.getFltMpsService().parseELearning(new File("config/ElearningStatus_120914.xlsx"))
			
		FltMpsGMTMemberReport gmt =
			bus.getFltMpsService().parseGMT(new File("config/GMTCourseCompletionStatus_120914.xlsx"))
			
		FltMpsELearningReport ia =
			bus.getFltMpsService().parseIA(new File("config/IndivAugTrngStat_120914.xlsx"))
			
		ImrReport imr =
			bus.getIMRService().parse(new File("config/Nrrm System Report Individual Medical Readiness 12-11-2014.csv"))
			
		MergedReport report = bus.getMergeService().generateReport(ruad, eLearning, gmt, ia, imr, null)
		
		return report
		
	}
    
    static void verifyELearningCourses(String[] courses, boolean ignoreIaaV12=true) {
        int j = 0
        if (!ignoreIaaV12) {
			assertThat(courses[j++], is("IAAÊV12"))
        }
        assertThat(courses[j++], is("ICÊIAAÊV12"))
        assertThat(courses[j++], is("CENSECFORÊATÊ010"))
        assertThat(courses[j++], is("PII"))
        assertThat(courses[j++], is("CTIP"))
        assertThat(courses[j++], is("RECORDS MANAGEMENT"))
        assertThat(courses[j++], is("OPSEC"))
        assertThat(courses[j++], is("COUNTERINTELL AWARENESS"))
    }
    
    static void verifyGMTCategories(String[] categories) {
        int j = 0
        assertThat(categories[j++], is("CATEGORY ONE"))
        assertThat(categories[j++], is("CATEGORY ONE"))
        assertThat(categories[j++], is("CATEGORY ONE"))
        assertThat(categories[j++], is("CATEGORY ONE"))
        assertThat(categories[j++], is("CATEGORY ONE"))
        assertThat(categories[j++], is("CATEGORY ONE"))
        assertThat(categories[j++], is("CATEGORY TWO"))
        assertThat(categories[j++], is("CATEGORY TWO"))
        assertThat(categories[j++], is("CATEGORY TWO"))
        assertThat(categories[j++], is("CATEGORY TWO"))
        assertThat(categories[j++], is("CATEGORY TWO"))
        assertThat(categories[j++], is("CATEGORY TWO"))
        assertThat(categories[j++], is("CATEGORY TWO"))
        assertThat(categories[j++], is("CATEGORY TWO"))
        assertThat(categories[j++], is("CATEGORY TWO"))
        assertThat(categories[j++], is("CATEGORY TWO"))
        assertThat(categories[j++], is("CATEGORY TWO"))
        assertThat(categories[j++], is("CATEGORY TWO"))
    }
    
    static void verifyGMTCourses(String[] courses) {
        int k = 0
        assertThat(courses[k++], is("ALCOH DRUG TOBACCO AWARE AVAIL APR"))
        assertThat(courses[k++], is("EO SEX HARR GRIEVANCE"))
        assertThat(courses[k++], is("PERS FINANCIAL MGMT"))
        assertThat(courses[k++], is("SEX ASSAULT PREV RESP AWARE AVAIL APR"))
        assertThat(courses[k++], is("STRESS MGMT AVAILIBLE AUG 2015"))
        assertThat(courses[k++], is("SUICIDE PREVENT AWARE AVAIL DEC"))
        assertThat(courses[k++], is("ANTITERRORISM LEVEL I AWARENESS"))
        assertThat(courses[k++], is("COMBATING TRAFFICKING IN PERSONS"))
        assertThat(courses[k++], is("COUNTERINTELL AWARENESS"))
        assertThat(courses[k++], is("DOD CYBER AWARENESS CHALLENGE V2"))
        assertThat(courses[k++], is("IC IAA V12 (INTEL COMMUNITY ONLY)"))
        assertThat(courses[k++], is("DOMESTIC VIOLENCE"))
        assertThat(courses[k++], is("OPERATIONAL RISK MANAGEMENT"))
        assertThat(courses[k++], is("OPERATIONAL SECURITY"))
        assertThat(courses[k++], is("PHYSICAL READINESS"))
        assertThat(courses[k++], is("PRIV AND PERS IDENTIFIABLE INFO"))
        assertThat(courses[k++], is("RECORDS MANAGEMENT"))
        assertThat(courses[k++], is("SEXUAL HEALTH AND RESPONSIBILITY"))
    }
    
    static void verifyIACourses(String[] courses) {
        int j = 0
        
        assertThat(courses[j++], is("CANS M16WS 1.0"))
        assertThat(courses[j++], is("OSTNG"))
        assertThat(courses[j++], is("ATFP CONUS"))
        assertThat(courses[j++], is("ATFP OCONUS"))
        assertThat(courses[j++], is("INFP"))
        assertThat(courses[j++], is("INTRO TO EQUAL OPPORTUNITY"))
        assertThat(courses[j++], is("MR1 PART1"))
        assertThat(courses[j++], is("MR1 PART2"))
        assertThat(courses[j++], is("COC LEVELA"))
        assertThat(courses[j++], is("NPDC CWI 1.0"))
        assertThat(courses[j++], is("NPDC HWI 1.0"))
        assertThat(courses[j++], is("NPDC SAEDA 1.0"))
        assertThat(courses[j++], is("NPDC USAV 1.0"))
        assertThat(courses[j++], is("JKDDC TIP 2.0"))
        assertThat(courses[j++], is("PRE DEPLOY SUICIDE AWARE"))
        assertThat(courses[j++], is("PRE DEPLOY SEXUAL ASSAULT"))
        assertThat(courses[j++], is("VIRTUAL MISSION PREP INTEL"))
        assertThat(courses[j++], is("VIRTUAL MISSION PREP MEDIA"))
        assertThat(courses[j++], is("M9 PISTOL"))
        assertThat(courses[j++], is("INTRO BIOMETRICS"))
        assertThat(courses[j++], is("COIN"))
        assertThat(courses[j++], is("MALARIA PREVENTION AND CONTROL"))
        assertThat(courses[j++], is("COIN OPERATIONS"))
        assertThat(courses[j++], is("AFGAN IN PROSPECTIVE"))
        assertThat(courses[j++], is("ACTIVE SHOOTER"))
        assertThat(courses[j++], is("USFFC ISAF BASIC"))
    }
    
}
