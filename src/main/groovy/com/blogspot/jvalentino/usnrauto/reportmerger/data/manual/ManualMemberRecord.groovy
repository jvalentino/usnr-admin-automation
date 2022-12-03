package com.blogspot.jvalentino.usnrauto.reportmerger.data.manual

import com.blogspot.jvalentino.usnrauto.reportmerger.data.CommonMemberData;

class ManualMemberRecord extends CommonMemberData {
	String[] values
	
	@Override
	String toString() {
		String result = lastName + " | " + firstName + " | " + rank + " | "
		for (int i = 0; i < values.length; i++) {
			String value = values[i]
			if (i != values.length - 1) {
				result += value + " | "
			} else {
				result += value
			}
		}
		
		return result
		
	}
	
}
