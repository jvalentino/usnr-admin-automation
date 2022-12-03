package com.blogspot.jvalentino.usnrauto.reportmerger.data.summary

import java.util.List;
import java.util.Map;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedMember;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedReport;

class SummaryReport extends MergedReport {

	double prdPercentage
	double imrPercentage
	double atPercentage
	
	double[] eLearningCourseCompletionPercentages
	double[] iaCourseCompletionPercentages
	double[] gmtCourseCompletionPercentages
	double[] esamsCourseCompletionPercentages
	
	// from manual inputs
	Double[] manualPercentages
	
	// for rank summary
	Map<String, Integer> rankGroup
	def officerRanks
	
	// for orders summary
	Map<String, Integer> orderGroup
	
	List<String> commandWarnings = new ArrayList<String>()
	
	int getMaxSetsOfOrders() {
		int max = 0
		for (MergedMember member : members) {
			int orders = member.orders.size()
			if (orders > 0) {
				max = orders
			}
		}
		return max
	}
	
}
