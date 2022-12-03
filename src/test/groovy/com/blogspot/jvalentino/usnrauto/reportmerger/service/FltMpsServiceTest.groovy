package com.blogspot.jvalentino.usnrauto.reportmerger.service

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsELearningMember;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsELearningReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsGMTMember;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsGMTMemberReport;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

class FltMpsServiceTest {

    private ServiceBus bus = ServiceBus.getInstance()
    private FltMpsService service
    
    @Before
    void setup() {
        service = bus.getFltMpsService()
    }
    
    
    @Test
    void testParseELearning() throws Exception {
        File file = new File("config/ElearningStatus_120914.xlsx")
        
        FltMpsELearningReport report = service.parseELearning(file)
        
        this.verifyElearning(file, report)
        
    }
    
    @Test
    void testParseELearningWithExtraColumns() throws Exception {
        File file = new File("config/ElearningStatus_120914 extra column.xlsx")
        
        FltMpsELearningReport report = service.parseELearning(file)
        
        this.verifyElearning(file, report)
        
    }
    
    void verifyElearning(File file, FltMpsELearningReport report) {
        List<FltMpsELearningMember> members = report.getMembers()
        
        assertThat(members.size(), is(32));
        
        int i = 0
        
        assertThat(members.get(i++).toString(),
            is("CTT2 | DANIEL | RADCLIFF | false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTNC | ROMEO | QUEBEC | false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTTC | TANGO | SIERRA | true, true, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTN1 | VICTOR | UNIFORM | false, false, false, true, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTN1 | X-RAY | WHISKEY | false, true, true, true, true, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTN1 | ZULU | YANKEE | false, true, true, true, true, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTR2 | ALPHA | BRAVO | false, false, true, true, true, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTT3 | WILLIAM | RIKER | false, true, true, true, true, false, true, false"))
        assertThat(members.get(i++).toString(),
            is("CTN3 | CHARLIE | DELTA | false, true, true, true, true, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTT2 | ECHO | FOXTROT | false, true, true, true, true, true, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTT1 | GOLF | HOTEL | false, true, true, true, true, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTTSN | CHRISTOPHER | WALKEN | false, false, false, false, false, false, true, false"))
        assertThat(members.get(i++).toString(),
            is("YNCS | INDIA | JULIET | false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTRCM | KILO | LIMA | false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("YN1 | MIKE | NOVEMBER | false, false, true, true, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTI1 | OSCAR | PAPA | false, true, true, true, true, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTI2 | QUEBEC | ROMEO | false, true, true, true, true, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTT1 | COREY | HART | false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTTSN | SUPER | MAN | false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTIC | SIERRA | TANGO | true, true, true, true, true, true, true, false"))
        assertThat(members.get(i++).toString(),
            is("CTN1 | UNIFORM | VICTOR | false, true, false, true, true, false, true, false"))
        assertThat(members.get(i++).toString(),
            is("CTTSA | JERRY | SPRINGER | false, true, true, true, true, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTNCS | WHISKEY | X-RAY | false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTR1 | YANKEE | ZULU | false, true, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("LCDR | BRAVO | ALPHA | false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("LTJG | DELTA | CHARLIE | true, true, true, true, true, true, true, false"))
        assertThat(members.get(i++).toString(),
            is("LCDR | FOXTROT | ECHO | false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CDR | HOTEL | GOLF | false, true, true, true, true, false, true, false"))
        assertThat(members.get(i++).toString(),
            is("LT | JULIET | INDIA | false, true, true, true, true, true, true, false"))
        assertThat(members.get(i++).toString(),
            is("LT | LIMA | KILO | false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("LTJG | NOVEMBER | MIKE | false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("LTJG | PAPA | OSCAR | false, false, true, false, false, false, false, false"))
        
        
        TestUtils.verifyELearningCourses(members.get(0).courseNames, false)
        TestUtils.verifyELearningCourses(report.getCourseNames(), false)
        assertThat(report.file, is(file))
    }
    
    
    
    @Test
    public void testParseGMT() throws Exception {
        File file = new File("config/GMTCourseCompletionStatus_120914.xlsx")
        
        FltMpsGMTMemberReport report = service.parseGMT(file)
        
        List<FltMpsGMTMember> members = report.getMembers()
        
        assertThat(members.size(), is(32));
        
        
        int i = 0
        
        assertThat(members.get(i++).toString(), 
            is("CTT2 | DANIEL | RADCLIFF | false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(), 
            is("CTNC | ROMEO | QUEBEC | false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(), 
            is("CTTC | TANGO | SIERRA | false, true, true, false, true, false, false, false, false, true, true, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(), 
            is("CTN1 | VICTOR | UNIFORM | false, true, true, false, true, false, false, false, false, false, false, false, false, false, false, true, false, false"))
        assertThat(members.get(i++).toString(), 
            is("CTN1 | X-RAY | WHISKEY | false, true, true, false, true, false, true, true, false, false, true, false, false, false, false, true, false, false"))
        assertThat(members.get(i++).toString(), 
            is("CTN1 | ZULU | YANKEE | false, false, true, false, false, false, true, true, false, false, true, false, false, false, false, true, false, false"))
        assertThat(members.get(i++).toString(), 
            is("CTR2 | ALPHA | BRAVO | false, true, true, false, true, false, true, true, false, false, false, false, false, false, false, true, false, false"))
        assertThat(members.get(i++).toString(), 
            is("CTT3 | WILLIAM | RIKER | false, true, true, false, true, false, true, true, false, false, true, true, true, true, false, true, false, true"))
        assertThat(members.get(i++).toString(), 
            is("CTN3 | CHARLIE | DELTA | false, true, true, false, true, false, true, true, false, false, true, false, false, false, false, true, false, false"))
        assertThat(members.get(i++).toString(), 
            is("CTT2 | ECHO | FOXTROT | false, true, true, false, true, false, true, true, false, false, true, true, true, false, false, true, true, true"))
        assertThat(members.get(i++).toString(), 
            is("CTT1 | GOLF | HOTEL | false, true, true, false, true, false, true, true, false, false, true, true, false, false, false, true, false, false"))
        assertThat(members.get(i++).toString(), 
            is("CTTSN | CHRISTOPHER | WALKEN | false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false"))
        assertThat(members.get(i++).toString(), 
            is("YNCS | INDIA | JULIET | false, true, true, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(), 
            is("CTRCM | KILO | LIMA | false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(), 
            is("YN1 | MIKE | NOVEMBER | false, true, true, false, true, false, true, false, false, false, false, false, false, false, false, true, false, false"))
        assertThat(members.get(i++).toString(), 
            is("CTI1 | OSCAR | PAPA | false, true, true, false, true, false, true, true, false, false, true, false, false, false, false, true, false, false"))
        assertThat(members.get(i++).toString(), 
            is("CTI2 | QUEBEC | ROMEO | false, true, true, false, true, false, true, true, false, false, true, false, false, false, false, true, false, false"))
        assertThat(members.get(i++).toString(), 
            is("CTT1 | COREY | HART | false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(), 
            is("CTTSN | SUPER | MAN | false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(), 
            is("CTIC | SIERRA | TANGO | false, true, true, false, true, false, true, true, false, true, true, true, true, true, false, true, true, true"))
        assertThat(members.get(i++).toString(), 
            is("CTN1 | UNIFORM | VICTOR | false, true, true, false, true, false, false, true, false, false, true, true, true, true, false, true, false, false"))
        assertThat(members.get(i++).toString(), 
            is("CTTSA | JERRY | SPRINGER | false, true, true, false, true, false, true, true, false, false, true, false, false, false, false, true, false, false"))
        assertThat(members.get(i++).toString(), 
            is("CTNCS | WHISKEY | X-RAY | false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(), 
            is("CTR1 | YANKEE | ZULU | false, true, true, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(), 
            is("LCDR | BRAVO | ALPHA | false, false, true, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(), 
            is("LTJG | DELTA | CHARLIE | false, true, true, false, true, false, true, true, false, true, true, true, true, true, false, true, true, true"))
        assertThat(members.get(i++).toString(), 
            is("LCDR | FOXTROT | ECHO | false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(), 
            is("CDR | HOTEL | GOLF | false, true, true, false, true, false, true, true, false, false, true, true, false, true, false, true, false, true"))
        assertThat(members.get(i++).toString(), 
            is("LT | JULIET | INDIA | false, true, true, false, true, false, true, true, false, false, true, true, true, true, false, true, true, true"))
        assertThat(members.get(i++).toString(), 
            is("LT | LIMA | KILO | false, true, true, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(), 
            is("LTJG | NOVEMBER | MIKE | false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(), 
            is("LTJG | PAPA | OSCAR | false, true, true, false, true, false, true, false, false, false, false, false, false, false, false, false, false, false"))

        

        TestUtils.verifyGMTCategories(members.get(0).getCourseCategories())
        TestUtils.verifyGMTCategories(report.getCourseCategories())
        
        TestUtils.verifyGMTCourses(members.get(0).courseNames)
        TestUtils.verifyGMTCourses(report.courseNames)
		
		assertThat(report.file, is(file))
        
    }
        
    @Test
    public void testParseIA() throws Exception {
        File file = new File("config/IndivAugTrngStat_120914.xlsx")
        FltMpsELearningReport report = service.parseIA(file)
        verifyIA(file, report)
    }
    
    @Test
    public void testParseIAWithExtraColumn() throws Exception {
        File file = new File("config/IndivAugTrngStat_120914 extra column.xlsx")
        FltMpsELearningReport report = service.parseIA(file)
        verifyIA(file, report)
    }
    
    void verifyIA(File file, FltMpsELearningReport report) {
        List<FltMpsELearningMember> members = report.getMembers()
        
        assertThat(members.size(), is(32));
        
        int i = 0
        
        assertThat(members.get(i++).toString(),
            is("CTT2 | DANIEL | RADCLIFF | false, false, true, true, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTNC | ROMEO | QUEBEC | false, false, true, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTTC | TANGO | SIERRA | true, true, true, true, true, true, true, true, false, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTN1 | VICTOR | UNIFORM | false, true, true, true, true, false, false, false, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTN1 | X-RAY | WHISKEY | false, false, true, true, true, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTN1 | ZULU | YANKEE | true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, true, false, false, true, false"))
        assertThat(members.get(i++).toString(),
            is("CTR2 | ALPHA | BRAVO | false, false, false, false, true, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTT3 | WILLIAM | RIKER | false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTN3 | CHARLIE | DELTA | false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTT2 | ECHO | FOXTROT | false, false, true, true, true, true, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, true, false"))
        assertThat(members.get(i++).toString(),
            is("CTT1 | GOLF | HOTEL | true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, true, false, true, true, false"))
        assertThat(members.get(i++).toString(),
            is("CTTSN | CHRISTOPHER | WALKEN | false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("YNCS | INDIA | JULIET | true, true, true, false, true, true, false, false, false, true, true, true, true, false, true, false, false, true, false, true, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTRCM | KILO | LIMA | false, false, true, true, false, false, false, false, false, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("YN1 | MIKE | NOVEMBER | false, false, true, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTI1 | OSCAR | PAPA | false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTI2 | QUEBEC | ROMEO | false, true, true, true, true, false, false, false, false, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTT1 | COREY | HART | false, false, true, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTTSN | SUPER | MAN | false, false, true, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTIC | SIERRA | TANGO | false, false, true, true, true, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTN1 | UNIFORM | VICTOR | false, false, true, true, true, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTTSA | JERRY | SPRINGER | false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTNCS | WHISKEY | X-RAY | false, false, true, true, false, false, false, false, false, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CTR1 | YANKEE | ZULU | false, false, true, true, true, false, false, false, false, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("LCDR | BRAVO | ALPHA | false, true, true, true, true, false, false, false, false, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("LTJG | DELTA | CHARLIE | true, true, true, true, true, true, true, true, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, true, false"))
        assertThat(members.get(i++).toString(),
            is("LCDR | FOXTROT | ECHO | true, true, true, true, true, true, true, true, false, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("CDR | HOTEL | GOLF | false, false, true, true, false, false, false, false, false, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("LT | JULIET | INDIA | false, false, true, true, true, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("LT | LIMA | KILO | false, false, true, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("LTJG | NOVEMBER | MIKE | false, false, true, true, true, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false"))
        assertThat(members.get(i++).toString(),
            is("LTJG | PAPA | OSCAR | false, false, true, true, true, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false"))
        
        TestUtils.verifyIACourses(members.get(0).courseNames)
        TestUtils.verifyIACourses(report.getCourseNames())
        
        assertThat(report.file, is(file))
    }
    
    @Test
    public void testParseGMTwithExtraColumns() throws Exception {
        File file = new File("config/GMTCourseCompletionStatus_111415.xlsx")
        
        FltMpsGMTMemberReport report = service.parseGMT(file)
        
        List<FltMpsGMTMember> members = report.getMembers()
        
        assertThat(members.size(), is(3))
        
        assertThat(report.courseNames.length, is(19))
        assertThat(members.get(0).courseNames.length, is(19))
        
    }
    
    
}
