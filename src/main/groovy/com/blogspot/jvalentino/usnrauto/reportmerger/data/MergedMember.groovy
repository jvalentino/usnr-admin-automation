package com.blogspot.jvalentino.usnrauto.reportmerger.data

import java.util.Date;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.esams.EsamsRecord;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.merge.NrowsOrder;

class MergedMember extends CommonMemberData {
    
	// RUAD
    Date prd
    String ims
    String masCodeA
    String masCodeM
    String masCodeT
	String designator
    
    boolean existsInRuad = true
    boolean existsInELearning = false
    boolean existsInGMT = false
    boolean existsInIA = false
	boolean existsInManualInputs = false
    
    boolean[] eLearningCourseCompletions
    boolean[] gmtCourseCompletions
    boolean[] iaCourseCompletions
	boolean[] esamsCourseCompletions
	List<String> esamsCoursesToDo = new ArrayList<String>()
	
	List<EsamsRecord> esamsRecords = new ArrayList<EsamsRecord>()
    
	// IMR
    String imrStatus = "Unknown"
	
	// MANUAL INPUTS
	String[] values
	
	// Summary info, but used to indicate whether the corresponding manual inputs
	// value if valid or not
	boolean[] valueValid
	
	List<NrowsOrder> orders = new ArrayList<NrowsOrder>()
	int daysOfOrdersInCurrentFY = 0
	boolean hasEnoughDaysOfOrdersForCurrentFY = false
        
    String eLearningCompletionsToString() {
        return this.booleanArrayToString(eLearningCourseCompletions)
    }
    
    String gmtCompletionsToString() {
        return this.booleanArrayToString(gmtCourseCompletions)
    }
    
    String iaCourseCompletionsToString() {
        return this.booleanArrayToString(iaCourseCompletions)
    }
    
}
