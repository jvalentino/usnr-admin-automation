package com.blogspot.jvalentino.usnrauto.reportmerger.data.ruad

import org.apache.poi.xssf.usermodel.XSSFSheet;

class RuadReport {
	File file
    List<RuadMemberEntry> members
	int totalIAP = 0
	int totalCAO = 0
	int totalCAI = 0
	int totalEnlisted = 0
	int totalOfficer = 0
	
	XSSFSheet sheet
}
