package com.blogspot.jvalentino.usnrauto.reportmerger.data.manual

import com.blogspot.jvalentino.usnrauto.util.FormatUtil;

class HistoryElement {

	DataCategory category
	String name
	String secondaryCategory = "NONE"
	Map<Date, Double> values = new TreeMap<Date, Double>()
	
	HistoryElement() {
		
	}
	
	HistoryElement(DataCategory category, String name, Date date, Double value) {
		this.name = name
		this.category = category
		values.put(date, value)
	}
	
	HistoryElement(DataCategory category, String name, Date date, Double value, String secondaryCategory) {
		this.name = name
		this.category = category
		this.secondaryCategory = secondaryCategory
		values.put(date, value)
	}
	
	/**
	 * IA V12 (II): [01-Jan-2015, 99.87%], [02-Jan-2015, 88.99%], 
	 */
	@Override
	String toString() {
		String result = name
		
		if (!secondaryCategory.equals("NONE")) {
			result += " (" + secondaryCategory + ")"
		}
		
		result += ": "
		
		for (Map.Entry<Date, Double> entry : values.entrySet()) {
			Date key = entry.getKey();
			Double value = entry.getValue();
			
			result += "[" + FormatUtil.dateToFormatTwoString(key) + ", "
			result += FormatUtil.fractionToPercentage(value) + "], "
		}
		
		return result
	}
	
	
}
