package com.blogspot.jvalentino.usnrauto.reportmerger.data.nrows

import com.blogspot.jvalentino.usnrauto.util.FormatUtil;

class NrowsRawEntry {

	String ssn	
	String name
	String sdn
	String grade
	String ruic	
	String trackingNumber
	String dutyType
	Date startDate
	Date endDate
	Integer days
	Integer fy
	String status
	int line = 0
	
	String toString() {
		String[] fields = [
			ssn,
			name,
			sdn,
			grade,
			ruic,
			trackingNumber,
			dutyType,
			FormatUtil.toMMDDYYYY(startDate),
			FormatUtil.toMMDDYYYY(endDate),
			days,
			fy,
			status,
			]
		
		String result = ""
		
		for (String field : fields) {
			result += field + " | "
		}
		
		return result
		
	}
		
}
