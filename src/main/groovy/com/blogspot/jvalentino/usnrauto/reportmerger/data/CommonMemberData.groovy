package com.blogspot.jvalentino.usnrauto.reportmerger.data

import com.blogspot.jvalentino.usnrauto.component.generaltable.FriendlyName;

abstract class CommonMemberData {

	@FriendlyName(column="First Name")
    String firstName
	@FriendlyName(column="Last Name")
    String lastName
	@FriendlyName(column="Rank/Rate")
    String rank
    
    /**
     * This is the information that we use to uniquely identify people
     */
    String toKey() {
        return lastName + ", " + firstName + " " + rank
    }
    
    protected String booleanArrayToString(boolean[] list) {
        String value = ""
        for (int i = 0; i < list.length; i++) {
            if ( i != list.length -1) {
                 value += list[i].toString() + ", "
            } else {
                value += list[i].toString()
            }
        }
        return value
    }
    
    String dateToString(Date date) {
        return date.format("yyyyMMdd")
    }
}
