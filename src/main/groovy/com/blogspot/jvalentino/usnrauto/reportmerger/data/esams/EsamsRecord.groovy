package com.blogspot.jvalentino.usnrauto.reportmerger.data.esams

import com.blogspot.jvalentino.usnrauto.util.FormatUtil;

class EsamsRecord {
	String name
	String title
	Date requiredDate
	
	String toString() {
		return "name: ${name}, title: ${title}, requiredDate: " + FormatUtil.toMMDDYYYY(requiredDate)
	}
}
