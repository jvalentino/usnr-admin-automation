package com.blogspot.jvalentino.usnrauto.reportmerger.data.merge

import java.util.Date;

class NrowsOrder {

	String sdn
	String trackingNumber
	String dutyType
	Date startDate
	Date endDate
	Integer days
	Integer fy
	String status
	
	boolean isCountableStatus() {
		return this.status.equals("INITIAL") || this.status.equals("MOD")
	}
}
