package com.blogspot.jvalentino.usnrauto.reportmerger.service

import com.blogspot.jvalentino.usnrauto.reportmerger.data.esams.EsamsRecord;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.esams.EsamsReport;
import com.blogspot.jvalentino.usnrauto.util.PoiUtil;

import spock.lang.Specification;

class EsamsServiceTestSpec extends Specification {
	
	private EsamsService service
	
	def setup() {
		service = new EsamsService()
	}
	
	void "Test Parsing"() {
		when:
		File file = new File("config/NeededTraining.xlsx")
		EsamsReport report = service.parse(file)
		List<EsamsRecord> records = report.records
		int i = 0
		
		then:
		records.get(i++).toString() == 'name: ALPHA, BRAVO W, title: ESAMS Training for Supervisors (Web or Classroom), requiredDate: 01/01/2015'
		records.get(i++).toString() == 'name: ALPHA, BRAVO W, title: Fire Prevention and Portable Fire Extinguisher Training and Education, requiredDate: 06/09/2016'
		records.get(i++).toString() == 'name: ALPHA, BRAVO W, title: HAZCOM Training Job/Chemical Specific (OJT by Supervisor), requiredDate: 12/12/2014'
		records.get(i++).toString() == 'name: ALPHA, BRAVO W, title: Monthly Safety Talks - Given, requiredDate: 12/12/2014'
		records.get(i++).toString() == 'name: CHARLIE, DELTA H, title: Fire Prevention and Portable Fire Extinguisher Training and Education, requiredDate: 06/29/2016'
		records.get(i++).toString() == 'name: CHARLIE, DELTA H, title: HAZCOM Training Job/Chemical Specific (OJT by Supervisor), requiredDate: 12/13/2013'
		records.get(i++).toString() == 'name: CHARLIE, DELTA H, title: Monthly Safety Talks - Given, requiredDate: 12/12/2014'
	}
	
	

}
