package com.blogspot.jvalentino.usnrauto.sms.service

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.ManualInputReport;
import com.blogspot.jvalentino.usnrauto.sms.data.RecipientVO;

class ManualInputsServiceTest {

	private ManualInputsService service = new ManualInputsService()
	
	@Test
	void testLoad() {
		File file = new File("config/Manual Unit Inputs.xlsx")
		ManualInputReport manual = service.loadManualInputs(file)
		
		List<RecipientVO> results = service.load(manual)
		int i = 0
		assertThat(results.get(i++).toString(), 
			is("ALPHA | BRAVO | LCDR | KHAKI | 555-555-0000 | alpha.navy@none.com; alpha.home@none.com; alpha.work@none.com; "))
		assertThat(results.get(i++).toString(), 
			is("BRAVO | ALPHA | CTR2 | WHITEHAT | 555-555-0001 | bravo.navy@none.com; "))
		assertThat(results.get(i++).toString(), 
			is("CHARLIE | DELTA | LTJG | KHAKI | 555-555-0002 | "))
		assertThat(results.get(i++).toString(), 
			is("COP | ROBO | CTNC | KHAKI | null | cop.navy@none.com; "))
	}
	
}
