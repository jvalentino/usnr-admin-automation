package com.blogspot.jvalentino.usnrauto.reportmerger.service

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.ruad.RuadMemberEntry;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.ruad.RuadReport;

class RuadServiceTest {
    
    private ServiceBus bus = ServiceBus.getInstance()
    private RuadService ruadService
    
    @Before
    void setup() {
        ruadService = bus.getRuadService()
    }
	
	
	
	@Test
	void testParseRuadNewFormat() throws Exception {
		File ruadFile = new File("config/SmartRUAD_RUIC_NEW_FORMAT.xlsx")
		RuadReport report = ruadService.parseRuad(ruadFile)
		List<RuadMemberEntry> ruad = report.getMembers()
		
		int column = ruadService.getColumnThatContainsOfficerRank(report.sheet)
		assertThat(column, is(2))
		
		assertThat(ruad.size(), is(10))
		
		
		int i = 0
        assertThat(ruad.get(i++).toString(), is("LCDR | ALPHA | W | BRAVO"))
		assertThat(ruad.get(i++).toString(), is("LT | CHARLIE | null | DELTA"))
		assertThat(ruad.get(i++).toString(), is("LTJG | ECHO | K | FOXTROT"))
		assertThat(ruad.get(i++).toString(), is("CTNC | GOLF | D | HOTEL"))
		assertThat(ruad.get(i++).toString(), is("CTR1 | INDIA | A | JULIET"))
		assertThat(ruad.get(i++).toString(), is("CTRCM | KILO | B | LIMA"))
		assertThat(ruad.get(i++).toString(), is("CTTC | MIKE | M | NOVEMBER"))
		assertThat(ruad.get(i++).toString(), is("CTRSN | OSCAR | L | PAPA"))
		assertThat(ruad.get(i++).toString(), is("CTT1 | QUEBEC | W | ROMEO"))
		assertThat(ruad.get(i++).toString(), is("YN3 | SIERRA | C | TANGO"))
		
		assertThat(report.file, is(ruadFile));
		
		assertThat(report.totalCAI, is(3))
		assertThat(report.totalCAO, is(2))
		assertThat(report.totalIAP, is(6))
		assertThat(report.totalOfficer, is(3))
		assertThat(report.totalEnlisted, is(7))
		
		assertThat(ruad.get(0).getIms(), is("RD2"))
		assertThat(ruad.get(0).getPrdAsString(), is("20161101"))
		assertThat(ruad.get(1).getMasCodeT(), is("VOL"))
		assertThat(ruad.get(3).getMasCodeM(), is("MPQ"))
		assertThat(ruad.get(5).getMasCodeA(), is("AS3"))
		
	}
    
    @Test
    void testParseRuad() throws Exception {
        File ruadFile = new File("config/SmartRUAD_RUIC_TEST.xlsx")
        RuadReport report = ruadService.parseRuad(ruadFile)
        List<RuadMemberEntry> ruad = report.getMembers()
		
		
		int column = ruadService.getColumnThatContainsOfficerRank(report.sheet)
		assertThat(column, is(1))
        
        assertThat(ruad.size(), is(34));
        
        int i = 0
        assertThat(ruad.get(i++).toString(), is("LCDR | BRAVO | W | ALPHA"))
        assertThat(ruad.get(i++).toString(), is("LTJG | DELTA | H | CHARLIE"))
        assertThat(ruad.get(i++).toString(), is("LCDR | FOXTROT | A | ECHO"))
        assertThat(ruad.get(i++).toString(), is("CDR | HOTEL | N | GOLF"))
        assertThat(ruad.get(i++).toString(), is("LT | JULIET | W | INDIA"))
        assertThat(ruad.get(i++).toString(), is("LT | LIMA | M | KILO"))
        assertThat(ruad.get(i++).toString(), is("LTJG | NOVEMBER | null | MIKE"))
        assertThat(ruad.get(i++).toString(), is("LTJG | PAPA | K | OSCAR"))
        assertThat(ruad.get(i++).toString(), is("CTNC | ROMEO | D | QUEBEC"))
        assertThat(ruad.get(i++).toString(), is("CTTC | TANGO | Y | SIERRA"))
        assertThat(ruad.get(i++).toString(), is("CTN1 | VICTOR | J | UNIFORM"))
        assertThat(ruad.get(i++).toString(), is("CTN1 | X-RAY | J | WHISKEY"))
        assertThat(ruad.get(i++).toString(), is("CTN1 | ZULU | S | YANKEE"))
        assertThat(ruad.get(i++).toString(), is("CTR2 | ALPHA | M | BRAVO"))
        assertThat(ruad.get(i++).toString(), is("CTN3 | CHARLIE | C | DELTA"))
        assertThat(ruad.get(i++).toString(), is("CTT2 | ECHO | A | FOXTROT"))
        assertThat(ruad.get(i++).toString(), is("CTT1 | GOLF | J | HOTEL"))
        assertThat(ruad.get(i++).toString(), is("YNCS | INDIA | L | JULIET"))
        assertThat(ruad.get(i++).toString(), is("CTRCM | KILO | B | LIMA"))
        assertThat(ruad.get(i++).toString(), is("YN1 | MIKE | W | NOVEMBER"))
        assertThat(ruad.get(i++).toString(), is("CTI1 | OSCAR | K | PAPA"))
        assertThat(ruad.get(i++).toString(), is("CTI2 | QUEBEC | null | ROMEO"))
        assertThat(ruad.get(i++).toString(), is("CTIC | SIERRA | H | TANGO"))
        assertThat(ruad.get(i++).toString(), is("CTN1 | UNIFORM | A | VICTOR"))
        assertThat(ruad.get(i++).toString(), is("CTNCS | WHISKEY | L | X-RAY"))
        assertThat(ruad.get(i++).toString(), is("CTR1 | YANKEE | A | ZULU"))
        assertThat(ruad.get(i++).toString(), is("CTNC | ROBO | A | COP"))
        assertThat(ruad.get(i++).toString(), is("CTTC | AMANDA | M | HUGINKISS"))
        assertThat(ruad.get(i++).toString(), is("CTT1 | COREY | W | HART"))
        assertThat(ruad.get(i++).toString(), is("CTTSN | CHRISTOPHER | J | WALKEN"))
        assertThat(ruad.get(i++).toString(), is("CTT2 | DANIEL | K | RADCLIFF"))
        assertThat(ruad.get(i++).toString(), is("CTT3 | WILLIAM | T | RIKER"))
        assertThat(ruad.get(i++).toString(), is("CTTSN | SUPER | D | MAN"))
        assertThat(ruad.get(i++).toString(), is("CTTSA | JERRY | W | SPRINGER"))
        
        assertThat(ruad.get(0).getIms(), is("RD2"))
        assertThat(ruad.get(0).getPrdAsString(), is("20161101"))
        
        assertThat(ruad.get(1).getMasCodeT(), is("VOL"))
        
        assertThat(ruad.get(2).getMasCodeM(), is("MS1"))
        
        assertThat(ruad.get(11).getMasCodeA(), is("AS3"))
		
		assertThat(report.file, is(ruadFile));
		
		assertThat(report.totalCAI, is(2))
		assertThat(report.totalCAO, is(2))
		assertThat(report.totalIAP, is(5))
		assertThat(report.totalOfficer, is(8))
		assertThat(report.totalEnlisted, is(26))
         
    }
}
