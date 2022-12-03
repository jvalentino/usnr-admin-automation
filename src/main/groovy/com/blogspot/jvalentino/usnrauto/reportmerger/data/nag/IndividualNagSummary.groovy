package com.blogspot.jvalentino.usnrauto.reportmerger.data.nag

import com.blogspot.jvalentino.usnrauto.reportmerger.data.individual.IndividualSummary;

class IndividualNagSummary extends IndividualSummary {

	boolean selected = true
	String cell
	List<String> emails = new ArrayList<String>()
}
