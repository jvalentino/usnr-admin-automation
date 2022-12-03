package com.blogspot.jvalentino.usnrauto.reportmerger.service

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.ManualInputReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.ManualInputRules;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.ManualMemberRecord;

class ManualInputServiceTest {

	private ServiceBus bus = ServiceBus.getInstance()
	private ManualInputService service
	
	@Before
	public void setup() {
		service = bus.getManualInputService()
	}
	
	@Test
	void testParse() throws Exception {
		File file = new File("config/Manual Unit Inputs.xlsx")
		
		ManualInputReport report = service.parse(file)
			
		int i = 0
		
		assertThat(report.primaryColumnHeaders.get(i++), is("Qualification"))
		assertThat(report.primaryColumnHeaders.get(i++), is("Qualification"))
		assertThat(report.primaryColumnHeaders.get(i++), is("Security, Accounts, and Access"))
		assertThat(report.primaryColumnHeaders.get(i++), is("Security, Accounts, and Access"))
		assertThat(report.primaryColumnHeaders.get(i++), is("Security, Accounts, and Access"))
		assertThat(report.primaryColumnHeaders.get(i++), is("Security, Accounts, and Access"))
		assertThat(report.primaryColumnHeaders.get(i++), is("Security, Accounts, and Access"))
		assertThat(report.primaryColumnHeaders.get(i++), is("Security, Accounts, and Access"))
		assertThat(report.primaryColumnHeaders.get(i++), is("Mission Support"))
		assertThat(report.primaryColumnHeaders.get(i++), is("Mission Support"))
		assertThat(report.primaryColumnHeaders.get(i++), is("Mission Support"))
		
		i = 0
		
		assertThat(report.secondaryColumnHeaders.get(i++), is("Status"))
		assertThat(report.secondaryColumnHeaders.get(i++), is("Notes"))
		assertThat(report.secondaryColumnHeaders.get(i++), is("Poly"))
		assertThat(report.secondaryColumnHeaders.get(i++), is("Clearance"))
		assertThat(report.secondaryColumnHeaders.get(i++), is("National"))
		assertThat(report.secondaryColumnHeaders.get(i++), is("Joint"))
		assertThat(report.secondaryColumnHeaders.get(i++), is("NMCI"))
		assertThat(report.secondaryColumnHeaders.get(i++), is("NMCI Home Access"))
		assertThat(report.secondaryColumnHeaders.get(i++), is("FY 2015 Preference"))
		assertThat(report.secondaryColumnHeaders.get(i++), is("FY 2015 Target"))
		assertThat(report.secondaryColumnHeaders.get(i++), is("FY 2014 Activity"))
		
		List<ManualMemberRecord> members = report.getMembers()
		assertThat(members.size, is(34))
		
		i = 0
		assertThat(members.get(i++).toString(), 
			is("ALPHA | BRAVO | LCDR | Complete | None | 01-Jan-2014 | Full | Good | Good | Yes | Yes | ? | ? | ? | 555-555-0000 | ? | ? | alpha.navy@none.com | alpha.home@none.com | alpha.work@none.com"))
		assertThat(members.get(i++).toString(), 
			is("BRAVO | ALPHA | CTR2 | In Progress |  | ? | Interim | Locked | Good | No | No | ? | ? | ? | 555-555-0001 | ? | ? | bravo.navy@none.com | ? | ?"))
		assertThat(members.get(i++).toString(), 
			is("CHARLIE | DELTA | LTJG | Complete |  | ? | In Progress | No Need | Locked | No | No | 14D AT @ FIOC N for DNI | Depends on clearance | 365D MOB @ PACFLT | 555-555-0002 | ? | ? | ? | ? | ?"))
	
		ManualInputRules rules = report.rules
		assertThat(rules.ignorePeople.size(), is(1))
		assertThat(rules.ignorePeople.get(0), is("CTR1 Yankee Zulu"))
		
		assertThat(rules.ignoreCourses.size(), is(1))
		assertThat(rules.ignoreCourses.get(0), is("IAAÊV12"))
		
		
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
		
		assertThat(report.file, is(file))
	}
	
}
