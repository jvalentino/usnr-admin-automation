package com.blogspot.jvalentino.usnrauto.reportmerger.service

import java.util.List;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.imr.ImrRecord;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.imr.ImrReport;

class IMRServiceTest {

    private ServiceBus bus = ServiceBus.getInstance()
    
    @Test
    void testParse() {
        File file = new File("config/Nrrm System Report Individual Medical Readiness 12-11-2014.csv")
        ImrReport report = bus.getIMRService().parse(file)
        List<ImrRecord> list = report.getMembers()
        
        int i = 0
        
        assertThat(list.get(i++).toString(), is("ROMEO | QUEBEC | Fully Medically Ready"))
        assertThat(list.get(i++).toString(), is("TANGO | SIERRA | Fully Medically Ready"))
        assertThat(list.get(i++).toString(), is("VICTOR | UNIFORM | Fully Medically Ready"))
        assertThat(list.get(i++).toString(), is("X-RAY | WHISKEY | Fully Medically Ready"))
        assertThat(list.get(i++).toString(), is("ZULU | YANKEE | Fully Medically Ready"))
        assertThat(list.get(i++).toString(), is("ALPHA | BRAVO | Fully Medically Ready"))
        assertThat(list.get(i++).toString(), is("CHARLIE | DELTA | Fully Medically Ready"))
        assertThat(list.get(i++).toString(), is("ECHO | FOXTROT | Partially Medically Ready"))
        assertThat(list.get(i++).toString(), is("GOLF | HOTEL | Partially Medically Ready"))
        assertThat(list.get(i++).toString(), is("INDIA | JULIET | Fully Medically Ready"))
        assertThat(list.get(i++).toString(), is("KILO | LIMA | Fully Medically Ready"))
        assertThat(list.get(i++).toString(), is("JULIET | INDIA | Fully Medically Ready"))
        assertThat(list.get(i++).toString(), is("MIKE | NOVEMBER | Fully Medically Ready"))
        assertThat(list.get(i++).toString(), is("OSCAR | PAPA | Fully Medically Ready"))
        assertThat(list.get(i++).toString(), is("PAPA | OSCAR | Fully Medically Ready"))
        assertThat(list.get(i++).toString(), is("BRAVO | ALPHA | Fully Medically Ready"))
        assertThat(list.get(i++).toString(), is("QUEBEC | ROMEO | Partially Medically Ready"))
        assertThat(list.get(i++).toString(), is("SIERRA | TANGO | Fully Medically Ready"))
        assertThat(list.get(i++).toString(), is("UNIFORM | VICTOR | Medical Readiness Indeterminate"))
        assertThat(list.get(i++).toString(), is("WHISKEY | X-RAY | Fully Medically Ready"))
        assertThat(list.get(i++).toString(), is("YANKEE | ZULU | Not Medically Ready"))
        assertThat(list.get(i++).toString(), is("ROBOT | COP | Fully Medically Ready"))
        assertThat(list.get(i++).toString(), is("COREY | HART | Fully Medically Ready"))
        assertThat(list.get(i++).toString(), is("LIMA | KILO | Not Medically Ready"))
        assertThat(list.get(i++).toString(), is("DELTA | CHARLIE | Fully Medically Ready"))
        assertThat(list.get(i++).toString(), is("FOXTROT | ECHO | Not Medically Ready"))
        assertThat(list.get(i++).toString(), is("NOVEMBER | MIKE | Fully Medically Ready"))
        assertThat(list.get(i++).toString(), is("CHRISTOPHER | WALKEN | Partially Medically Ready"))
        assertThat(list.get(i++).toString(), is("DANIEL | RADCLIFF | Not Medically Ready"))
        assertThat(list.get(i++).toString(), is("WILLIAM | RIKER | Medical Readiness Indeterminate"))
        assertThat(list.get(i++).toString(), is("HOTEL | GOLF | Fully Medically Ready"))
        assertThat(list.get(i++).toString(), is("JERRY | SPRINGER | Partially Medically Ready"))
		
		assertThat(report.file, is(file))
    }
}
