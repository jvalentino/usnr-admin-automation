package com.blogspot.jvalentino.usnrauto.reportmerger.service

import com.blogspot.jvalentino.usnrauto.reportmerger.data.nrows.NrowsRawEntry;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.nrows.NrowsRawReport;

import spock.lang.Specification;

class NrowsServiceTestSpec extends Specification {
	
	NrowsService service
	
	def setup() {
		service = new NrowsService()
	}
	
	void "Test Parsing the NROWS Text File"() {
		
		when:
		NrowsRawReport report = service.parse(new File("./config/MiscSelresReport.txt"))
		String one =  report.entries.get(0).toString()
		String two =  report.entries.get(1).toString()
		String three =  report.entries.get(2).toString()
		String four =  report.entries.get(3).toString()
		String five =  report.entries.get(4).toString()
		String six =  report.entries.get(5).toString()
		
		then:
		report.entries.size() == 6
		report.warnings.size() == 1
		
		one == "1111 | ALPHA, BRAVO W | N9998885 | O4 | 84080 | 1118479/0 | ADT | 07/06/2015 | 09/30/2015 | 87 | 2015 | INITIAL | "
		two == "2222 | CHARLIE, DELTA H | N9998885 | O2 | 84080 | 1114253/1 | ADT | 07/05/2015 | 08/07/2015 | 34 | 2015 | CANCEL | "
		three == "2222 | CHARLIE, DELTA H | N9998885 | O2 | 84080 | 1113039/0 | IDTT | 08/13/2015 | 09/30/2015 | 49 | 2015 | INITIAL | "
		four == "3333 | ECHO, FOXTROT A | N9998885 | O4 | 84080 | 1116518/0 | AT | 02/02/2015 | 02/06/2015 | 5 | 2015 | MOD | "
   		five == "5555 | QUEBEC, ROMEO D | N9998885 | E7 | 84080 | 1113134/0 | ADT | 02/18/2015 | 03/31/2015 | 42 | 2015 | FOOBAR | "
		six == "5555 | QUEBEC, ROMEO D | N9998885 | E7 | 84080 | 1118923/1 | ADT | 10/06/2014 | 10/08/2014 | 3 | 2015 | CANCEL | "
		
		report.warnings.get(0) == "NROWS Report Line 6: Line could not be read because the data doesn't contain 12 columns"
	}

}
