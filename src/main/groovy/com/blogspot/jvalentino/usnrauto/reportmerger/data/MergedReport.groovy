package com.blogspot.jvalentino.usnrauto.reportmerger.data

import java.util.List;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.History;

class MergedReport {

    String[] eLearningCourseNames
    String[] iaCourseNames
    String[] gmtCourseNames
    String[] gmtCourseCategories
	String[] esamsCourseNames
    
    List<String> warnings = new ArrayList<String>()
    
    int totalMembersFromRuad
    int totalMembersFromELearning
    int totalMembersFromGMT
    int totalMembersFromIA
	int totalMembersFromManualInputs
	int totalMembersFromIMR
    
    List<MergedMember> members
	
	// Header info from MAnual inputs
	List<String> primaryColumnHeaders = new ArrayList<String>()
	List<String> secondaryColumnHeaders = new ArrayList<String>()
	
	String spreadsheetFooter = ""
	String pdfFooter = ""
	String trainingEmailHeader = ""
	String trainingSmsHeader = ""
	boolean emailRequiresAuthorization = false
	boolean emailTlsEnabled = false
	String emailHost
	int emailPort = 0
	String emailUsername
	
	// The file from which the RUAD was loaded
    File ruadFile
	// The file from which the eLearning data was loaded
	File eLearningFile
	// The file from which the GMT data was loaded
	File gmtFile
	// The file from which the IA data was loaded
	File iaFile
	// The file from which the IMR data was loaded
	File imrFile
	// The file from which the manual inputs were loaded
	File manualFile
	File nrowsFile
	File esamsFile
	
	History history = new History()
	
	int totalIAP = 0
	int totalCAO = 0
	int totalCAI = 0
	int totalEnlisted = 0
	int totalOfficer = 0
}
