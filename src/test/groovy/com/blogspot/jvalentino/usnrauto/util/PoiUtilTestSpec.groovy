package com.blogspot.jvalentino.usnrauto.util

import spock.lang.Specification;

class PoiUtilTestSpec extends Specification {

	void "Test validating columns"() {
		when:
		String message = null
		try {
			PoiUtil.throwExceptionIfMapDoesNotHave(map, list)
		} catch (Exception e) {
			message = e.message
		}
		
		then:
		message == result
		
		where:
		map						| list				|| result
		["name": 1, "foo": 2]	| ["name", "foo"]	|| null
		["name": 1, "foo": 2]	| ["bar"]			|| 'Spreadsheet does not contain the column "bar"'
		["name": 1, "foo": 2]	| ["name", "bar"]	|| 'Spreadsheet does not contain the column "bar"'
				
	}
}
