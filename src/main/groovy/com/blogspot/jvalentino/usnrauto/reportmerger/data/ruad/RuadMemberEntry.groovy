package com.blogspot.jvalentino.usnrauto.reportmerger.data.ruad

import com.blogspot.jvalentino.usnrauto.component.generaltable.FriendlyName;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.CommonMemberData;

class RuadMemberEntry extends CommonMemberData {

    
	@FriendlyName(column="MI")
    String middleInitial
	@FriendlyName(column="PRD")
    Date prd
	@FriendlyName(column="IMS")
    String ims
	@FriendlyName(column="MAS-A")
    String masCodeA
	@FriendlyName(column="MAS-M")
    String masCodeM
	@FriendlyName(column="MAS-T")
    String masCodeT
	@FriendlyName(column="DESG")
	String designator
    
    @Override
    String toString() {
        return rank + " | " + firstName + " | " + middleInitial + " | " + lastName
    }
    
    String getPrdAsString() {
        return dateToString(prd)
    }
}
