package com.blogspot.jvalentino.usnrauto.reportmerger.data.idcreport

class IDCReport {

	// I.A
	double overallMedicalReadiness
	String overallMedicalReadinessSource
	
	// I.B
	String cdbStats
	String cdbIssues
	String cdbReview
	
	//I.C
	String physicalReadinessNotes
	
	//I.D
	int cleared = 0
	String clearedSource
	int awaitingClearance = 0
	String awaitingClearanceSource
	int awaitingAccess = 0
	String awaitingAccessSource
	int awaitingClearanceMoreThanSixMonths = 0
	String awaitingClearanceMoreThanSixMonthsSource
	String clearances
	List<String> clearanceIssues = new ArrayList<String>()
	
	// I.E
	String manning
	String manningSource
	String retention
	String mobilization
	String awards
	
	// II.A
	String training
	String jqr
	String trainingIsuues
	
	// II.B
	String sourceSailorsInUnit
	String sourceSailorsParticipating
	String sourceSailorsQualified
	int sailorsQualified = 0
	int sailorsParticipating = 0
	String eidws
	String eidwsTraining
	String eidwsIssues
	
	// II.C
	int officerIdcCount = 0 //18xx
	int officerIdcQual = 0
	int officerIwCount = 0 // 181x
	int officerIwQual = 0
	int officerIpCount = 0 // 182x
	int officerIpQual = 0
	int officerIntelCount = 0 // 183x
	int officerIntelQual = 0
	String officerIdcCountSource
	String officerIwCountSource
	String officerIpCountSource
	String officerIntelCountSource
	String officerTraining
	String officerTrainingIssues
	String officerPqsUpdate
	String idwoQual
	String iwQual
	String ipQual
	String intelQual
	
	// II.D
	String unitAT
	String unitCoBullets
	String unitOpsInputs
	
	
	// III.A
	String infrastructure
	// III.B
	String infoTech
	// III.C
	String supplies
	// III.D
	String jric
	
	// IV.A
	String visit
	// IV.B
	String generalComm
	// IV.C
	String goodNews
	String innovation
	
}
