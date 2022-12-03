package com.blogspot.jvalentino.usnrauto.reportmerger.data.summary

class MissingData {

	String primary
	String secondary
	Set<String> values = new LinkedHashSet<String>()
	String type
	boolean pii = false
	
	String toString() {
		String typeValue = ""
		if (type) {
			typeValue = " (" + type + ")"
		}
		String piiValue = ""
		if (pii) {
			piiValue = " [Warning: Potential PII]"
		}
		return primary + ": " + secondary + typeValue + piiValue
	}
}
