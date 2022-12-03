package com.blogspot.jvalentino.usnrauto.reportmerger.data.manual

enum DataCategory {

	MEMBERSHIP("MEMBERSHIP"),
	ELEARNING("ELEARNING"),
	INDIVIDUAL_AUGMENTEE("INDIVIDUAL AUGMENTEE"),
	GMT("GMT"),
	MANUAL_INPUTS("MANUAL INPUTS"),
	ESAMS("ESAMS")
	
	private String text
	
	DataCategory(String text) {
		this.text = text
	}
	
	static DataCategory getForText(String value) {
		DataCategory result = null
		
		for (DataCategory current : DataCategory.values()) {
			if (current.text.equalsIgnoreCase(value)) {
				return current
			}
		}
		
		return result
	}
	
	static String listValues() {
		String result = ""
		int count = DataCategory.values().length
		
		for (int i = 0; i < count; i++) {
			String value = DataCategory.values()[i].text
			if (i != count - 1) {
				result += value + ", "
			} else {
				result += value
			}
		}
		
		return result
	}
}
