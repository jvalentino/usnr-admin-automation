package com.blogspot.jvalentino.usnrauto.reportmerger.data.manual

class ManualInputReport {
	
	ManualInputRules rules
	
	List<String> primaryColumnHeaders = new ArrayList<String>()
	List<String> secondaryColumnHeaders = new ArrayList<String>()
	
	List<ManualMemberRecord> members = new ArrayList<ManualMemberRecord>()
	
	String spreadsheetFooter = ""
	String pdfFooter = ""
	String trainingEmailHeader = ""
	String trainingSmsHeader = ""
	boolean emailRequiresAuthorization = false
	boolean emailTlsEnabled = false
	String emailHost
	int emailPort = 0
	String emailUsername
	
	File file
	
	History history = new History()
}
