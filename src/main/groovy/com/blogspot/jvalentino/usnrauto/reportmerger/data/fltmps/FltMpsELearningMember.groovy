package com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps

import com.blogspot.jvalentino.usnrauto.reportmerger.data.CommonMemberData;

class FltMpsELearningMember extends CommonMemberData {

    
    Boolean[] courseCompletions
    String[] courseNames
    
    @Override
    String toString() {
        String value = ""
        value += rank + " | "
        value += firstName + " | "
        value += lastName + " | "
        
        value += this.booleanArrayToString(courseCompletions)
        
        return value
    }
    
   
    
}
