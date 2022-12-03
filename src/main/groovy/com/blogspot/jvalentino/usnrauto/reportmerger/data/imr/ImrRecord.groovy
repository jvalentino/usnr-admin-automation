package com.blogspot.jvalentino.usnrauto.reportmerger.data.imr

import com.blogspot.jvalentino.usnrauto.component.generaltable.FriendlyName;

class ImrRecord {

	@FriendlyName(column="First Name")
    String firstName
	@FriendlyName(column="Last Name")
    String lastName
	@FriendlyName(column="Status")
    String status
    
    @Override
    String toString() {
        return firstName + " | " + lastName + " | " + status
    }
    
    String toKey() {
        return firstName + " " + lastName
    }
}
