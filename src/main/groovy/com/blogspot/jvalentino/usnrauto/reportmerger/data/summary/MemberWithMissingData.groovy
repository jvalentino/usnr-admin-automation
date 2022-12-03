package com.blogspot.jvalentino.usnrauto.reportmerger.data.summary

import java.util.List;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedMember;

class MemberWithMissingData  {
	MergedMember member
	List<MissingData> missing
	boolean selected = true
	String cell
	List<String> emails = new ArrayList<String>()
	
	MemberWithMissingData(MergedMember member, List<MissingData> missing) {
		this.member = member
		this.missing = missing
	}
	
}
