package com.blogspot.jvalentino.usnrauto.component.generaltable

import org.junit.Test;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.ruad.RuadReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.service.ServiceBus;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

class GeneralTableModelTest {

	private ServiceBus bus = ServiceBus.getInstance()
	
	@Test
	void testRuad() throws Exception {
		
		RuadReport ruad =
			bus.getRuadService().parseRuad(new File("config/SmartRUAD_RUIC_TEST.xlsx"))
			
		GeneralTableModel model = new GeneralTableModel(ruad.members)
		
		assertThat(model.columnCount, is(10));
		assertThat(model.rowCount, is(34));
		
		assertThat(model.getFriendlyColumnNames().toString(), 
			is("[MI, PRD, IMS, MAS-A, MAS-M, MAS-T, DESG, First Name, Last Name, Rank/Rate]"))
		
		assertThat(model.getValueAt(0, 0), is("W"))
		assertThat(model.getValueAt(0, 1), is("01-Nov-2016"))
		assertThat(model.getValueAt(0, 2), is("RD2"))
		assertThat(model.getValueAt(0, 3), is(""))
		assertThat(model.getValueAt(0, 4), is(""))
		assertThat(model.getValueAt(0, 5), is(""))
		assertThat(model.getValueAt(0, 6), is("1815I"))
		assertThat(model.getValueAt(0, 7), is("BRAVO"))
		assertThat(model.getValueAt(0, 8), is("ALPHA"))
		assertThat(model.getValueAt(0, 9), is("LCDR"))
		
		
	}
}
