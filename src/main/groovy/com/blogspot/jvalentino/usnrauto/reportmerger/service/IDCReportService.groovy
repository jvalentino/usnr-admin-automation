package com.blogspot.jvalentino.usnrauto.reportmerger.service

import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedMember;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.idcreport.IDCReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.SummaryReport;
import com.blogspot.jvalentino.usnrauto.util.FormatUtil;

class IDCReportService {

	private static final DATA_NOT_AVAILABLE = "DATA NOT AVAILABLE"
	
	IDCReport generateData(SummaryReport summary) {
		IDCReport report = new IDCReport()
		
		report.overallMedicalReadiness = summary.imrPercentage
		report.overallMedicalReadinessSource = "Source: NRRM IMR Report"
		
		report.cdbStats = DATA_NOT_AVAILABLE + " (See Career Counselor)"
		report.cdbIssues = DATA_NOT_AVAILABLE + " (See Career Counselor)"
		report.cdbReview =  DATA_NOT_AVAILABLE + " (See Career Counselor)"
		
		report.physicalReadinessNotes = DATA_NOT_AVAILABLE + " (See Career Counselor)"
		
		this.handleClearances(report, summary)
		
		this.handleManning(report, summary)
		
		this.handleTraining(report, summary)
		
		this.handleEidws(report, summary)
		
		this.handleOfficerQual(report, summary)
		
		this.handleOps(report, summary)
		
		report.infrastructure = DATA_NOT_AVAILABLE + " (See N6)"
		report.infoTech = DATA_NOT_AVAILABLE + " (See N6)"
		report.supplies = DATA_NOT_AVAILABLE + " (See N1)"
		report.jric = DATA_NOT_AVAILABLE + " (See POM, Manual Unit Inputs.xlsx AT Plans)"
		
		report.visit = DATA_NOT_AVAILABLE + " (See POM)"
		report.generalComm = DATA_NOT_AVAILABLE + " (See POM)"
		report.goodNews = DATA_NOT_AVAILABLE + " (See POM)"
		report.innovation = DATA_NOT_AVAILABLE + " (See Ongoing projects)"
		
		
		return report
	}
	
	protected void handleClearances(IDCReport report, SummaryReport summary) {
		int clearanceColumn = -1
		int polyColumn = -1
		int nationalColumn = -1
		int jointColumn = -1
		
		
		for (MergedMember member : summary.members) {
			for (int i = 0; i < summary.secondaryColumnHeaders.size(); i++) {
				String column = summary.secondaryColumnHeaders.get(i)
				
				if (column.equalsIgnoreCase("clearance")) {
					clearanceColumn = i
					
					if (member.valueValid == null) {
						continue
					}
					
					if (member.valueValid[i]) {
						report.cleared++
					} else {
						report.awaitingClearance++
					}
				} else if (column.equalsIgnoreCase("poly")) {
					polyColumn = i
				} else if (column.equalsIgnoreCase("national")) {
					nationalColumn = i
				} else if (column.equalsIgnoreCase("joint")) {
					jointColumn = i
				}
				
			}
		}
		
		if (clearanceColumn != -1) {
			double clearance = summary.manualPercentages[clearanceColumn]
			if (clearance < 0.80D) {
				report.clearanceIssues.add("Only " + FormatUtil.fractionToPercentage(clearance) + " of Sailors have a the appropriate required clearance")
			}
		}
		
		if (polyColumn != -1) {
			double poly = summary.manualPercentages[polyColumn]
			if (poly < 0.80D) {
				report.clearanceIssues.add("Only " + FormatUtil.fractionToPercentage(poly) + " of Sailors have a current polygraph within 5 years")
			}
		}
		
		if (nationalColumn != -1) {
			double national = summary.manualPercentages[nationalColumn]
			if (national < 0.80D) {
				report.clearanceIssues.add("Only " + FormatUtil.fractionToPercentage(national) + " of Sailors have an active National highside account")
			}
		}
		
		if (jointColumn != -1) {
			double joint = summary.manualPercentages[jointColumn]
			if (joint < 0.80D) {
				report.clearanceIssues.add("Only " + FormatUtil.fractionToPercentage(joint) + " of Sailors have an active Joint highside account")
			}
		}
		
		report.clearedSource = "Source Cleared: Manual Unit Inputs.xslx, everyone with Clearance = Full"
		report.awaitingClearanceSource = "Source Awaiting Clearance: Manual Unit Inputs.xslx, everyone with Clearance NOT Full"
		report.awaitingAccessSource = "Source Awaiting Access: Access is not indepedently tracked"
		report.awaitingClearanceMoreThanSixMonthsSource =
			"Source Awaiting Clearance > 6 months: Clearance process start dates not tracked"
		report.clearances = report.cleared + "/" + report.awaitingClearance + "/?/?"
		
	}
	
	protected void handleManning(IDCReport report, SummaryReport summary) {
		report.manning = summary.totalOfficer + " Officers, " + summary.totalEnlisted + " Enlisted, plus " +
		summary.totalCAI + " CAI, " + summary.totalCAO + " CAO, " + summary.totalIAP + " IAP"
		report.manningSource = "Source: NRRM Smart RUAD/RUIC Report"
		
		report.retention =  DATA_NOT_AVAILABLE + " (See N1)"
		report.mobilization =  DATA_NOT_AVAILABLE + " (See last month's report or AT activity in Manual Unit Inputs.xlsx)"
		report.awards = DATA_NOT_AVAILABLE + " (See POM)"
	}
	
	protected void handleTraining(IDCReport report, SummaryReport summary) {
		report.training = DATA_NOT_AVAILABLE + " (See POM)"
		report.jqr = DATA_NOT_AVAILABLE + " (See POM)"
		report.trainingIsuues = DATA_NOT_AVAILABLE + " (See N7)"
	}
	
	protected void handleEidws(IDCReport report, SummaryReport summary) {
		for (MergedMember member : summary.members) {
			
			if (FormatUtil.isOfficer(member.rank)) {
				continue
			}
			
			if (member.values == null) {
				continue
			}
			
			for (int i = 0; i < summary.secondaryColumnHeaders.size(); i++) {
								
				String column = summary.secondaryColumnHeaders.get(i)
				String primary = summary.primaryColumnHeaders.get(i)
								
				String value = member.values[i]
												
				if (column.equalsIgnoreCase("Status") && primary.equals("Qualification")) {
										
					if (value.equalsIgnoreCase("Complete")) {
						report.sailorsQualified++
					} else if (value.equalsIgnoreCase("In Progress")) {
						report.sailorsParticipating++
					}
				}
			}
		}
		report.sourceSailorsInUnit = "Source Sailors in Unit: NRRM Smart RUAD/RUIC Report"
		report.sourceSailorsParticipating = "Source Sailors Participating: Manual Unit Inputs.xlsx, Qualification Source of In Progress"
		report.sourceSailorsQualified = "Source Sailors Qualified: Manual Unit Inputs.xlsx, Qualification Source of Complete"
		report.eidws = summary.totalEnlisted + "/" + report.sailorsParticipating + "/" + report.sailorsQualified
		report.eidwsTraining  = DATA_NOT_AVAILABLE + " (See POM or EIDWS Coordinator)"
		report.eidwsIssues = DATA_NOT_AVAILABLE + " (See EIDWS Coordinator)"
	}
	
	protected void handleOfficerQual(IDCReport report, SummaryReport summary) {
		for (MergedMember member : summary.members) {
			
			if (!FormatUtil.isOfficer(member.rank)) {
				continue	
			}
			
			String desg = member.designator
			
			for (int i = 0; i < summary.secondaryColumnHeaders.size(); i++) {
				
				String column = summary.secondaryColumnHeaders.get(i)
				String primary = summary.primaryColumnHeaders.get(i)
				String value = member.values[i]
				
				if (column.equalsIgnoreCase("Status") && primary.equals("Qualification")) {
					
					if (value.equalsIgnoreCase("Complete")) {
						if (desg.startsWith("181")) {
							report.officerIwQual++
						} else if (desg.startsWith("182")) {
							report.officerIpQual++
						} else if (desg.startsWith("183")) {
							report.officerIntelQual++
						}
						
						if (desg.startsWith("18")) {
							report.officerIdcQual++
						}
					}
				}
			}
			
			if (desg.startsWith("181")) {
				report.officerIwCount++
			} else if (desg.startsWith("182")) {
				report.officerIpCount++
			} else if (desg.startsWith("183")) {
				report.officerIntelCount++
			}
			
			if (desg.startsWith("18")) {
				report.officerIdcCount++
			}
			
		}
				
		report.officerIdcCountSource = "IDC Officer Source: NRRM Smart RUAD/RUIC Report, 18XX DESG"
		report.officerIwCountSource = "IW Officer Source: NRRM Smart RUAD/RUIC Report, 181X DESG"
		report.officerIpCountSource = "IP Officer Source: NRRM Smart RUAD/RUIC Report, 182X DESG"
		report.officerIntelCountSource = "INTEL Officer Source: NRRM Smart RUAD/RUIC Report, 183X DESG"
		report.officerTraining = DATA_NOT_AVAILABLE + " (See N7 or Manual Unit Inputs.xslx for AT Plans)"
		report.officerTrainingIssues = DATA_NOT_AVAILABLE + " (See N7)"
		report.officerPqsUpdate = DATA_NOT_AVAILABLE + " (See N7)"
		
		report.idwoQual = report.officerIdcCount + "/" + (report.officerIdcCount - report.officerIdcQual) +
			"/" + report.officerIdcQual + "/?"
		report.iwQual = report.officerIwCount + "/" + (report.officerIwCount - report.officerIwQual) +
			"/" + report.officerIwQual + "/?"
		report.ipQual = report.officerIpCount + "/" + (report.officerIpCount - report.officerIpQual) +
			"/" + report.officerIpQual + "/?"
		report.intelQual = report.officerIntelCount + "/" + (report.officerIntelCount - report.officerIntelQual) +
			"/" + report.officerIntelQual + "/?"
	}
	
	protected void handleOps(IDCReport report, SummaryReport summary) {
		report.unitAT = DATA_NOT_AVAILABLE + " (See Manual Unit Inputs.xslx AT plans)"
		report.unitCoBullets = DATA_NOT_AVAILABLE + " (See Manual Unit Inputs.xslx AT plans)"
		report.unitOpsInputs = DATA_NOT_AVAILABLE + " (N3 will provide UNCLASS OPS Bullets)"
	}
	
	static final String N = "\n"
	static final String T = "\t"
	
	String generateText(IDCReport report) {
		String text = ""
		
		text += "I. MAN" + N
				
		text += T + "A. Overall Medical Readiness percentage within IDC Region: " + FormatUtil.fractionToPercentage(report.overallMedicalReadiness) + N
		text += T + report.overallMedicalReadinessSource + N
				
		text += T + "B. CDBs" + N
		text += T + T + "a. # Required This Month/Number Completed/Incomplete (Note)" + N
		text += T + T + T + report.cdbStats + N
		text += T + T + "b. Any Issues (note on incompletes)" + N
		text += T + T + T + report.cdbIssues + N
		text += T + T + "c. Date CCC/CDB Website was Reviewed and Updated" + N
		text += T + T + T + report.cdbReview + N
				
		text += T + "C. Physical Readiness Fitness Status" + N
		text += T + T + "a. Notes/Issues" + N
		text += T + T + T + report.physicalReadinessNotes + N
				
		text += T + "D. Clearances" + N
		text += T + T + "a. # Cleared/# Awaiting Clearance/# Awaiting Access/# Awaiting Clearance or Access > 6 month" + N
		text += T + T + T + report.clearances + N
		text += T + T + T + report.clearedSource + N
		text += T + T + T + report.awaitingClearanceSource + N
		text += T + T + T + report.awaitingAccessSource + N
		text += T + T + T + report.awaitingClearanceMoreThanSixMonthsSource + N
		text += T + T + "b. Issues (> 6 months) (Send Personnel Related Information SEPCOR)" + N
		for (String issue : report.clearanceIssues) {
			text += T + T + T + issue + N
		}
		text += T + T + T + T + "There can be more issues listed here which are automatically tracked, see the SSO/SSR, N6, and last month's report" + N
				
		text += T + "E. General Communications Related to Manning: Concisely identify issues and successes related to:" + N
		text += T + T + "a. Billet/Manning: " + report.manning + N
		text += T + T + report.manningSource + N
		text += T + T + "b. Recruiting/Retention: Provide general situational awareness (not metrics) for recruiting/retention issues." + N
		text += T + T + T + report.retention + N
		text += T + T + "c. Mobilization/Long Term Order>90 days" + N
		text += T + T + T + report.mobilization + N
		text += T + T + "d. Awards/Recognition" + N
		text += T + T + T + report.awards + N
				
		text += "II. TRAIN" + N
				
		text += T + "A. General Communications Related to Training" + N
		text += T + T + "a.GMTs, ESAMS, and other Training Accomplished" + N
		text += T + T + T + report.training + N
		text += T + T + "b. JQRs and Supported Command Training Accomplished" + N
		text += T + T + T + report.jqr + N
		text += T + T + "c. Issues/Needs/Shortfall" + N
		text += T + T + T + report.trainingIsuues + N
				
		text += T + "B. EIDWS" + N
		text += T + T + "a. # Sailors in Unit/# Participating (pg 13)/# Qualified" + N
		text += T + T + T + report.eidws + N
		text += T + T + T + report.sourceSailorsInUnit + N
		text += T + T + T + report.sourceSailorsParticipating + N
		text += T + T + T + report.sourceSailorsQualified + N
		text += T + T + "b. What EIDWS Training did you do this month? Collaboration with other Units or AC?" + N
		text += T + T + T + report.eidwsTraining + N
		text += T + T + "c. Any issues?" + N
		text += T + T + T + report.eidwsIssues + N
				
		text += T + "C. Officer PQS" + N
		text += T + T + "a. IDWO - # IDC Officers in Unit/# in Training/# Qualified (in Record)/ # Delinquent (>5 Years)" + N
		text += T + T + T + report.idwoQual + N
		text += T + T + T + report.officerIdcCountSource + N
		text += T + T + "b. IP - # IP Officers in Unit/# in Training/# Qualified (in Record)/ # Delinquent (>3 Years)" + N
		text += T + T + T + report.ipQual + N
		text += T + T + T + report.officerIpCountSource + N
		text += T + T + "c. INTEL - # INTEL Officers in Unit/# in Training/# Qualified (in Record)/ # Delinquent (>3 Years)" + N
		text += T + T + T + report.intelQual + N
		text += T + T + T + report.officerIntelCountSource + N
		text += T + T + "d. #IW - IW Officers in Unit/# in Training/# Qualified (in Record)/ # Delinquent (>3 Years" + N
		text += T + T + T + report.iwQual + N
		text += T + T + T + report.officerIwCountSource + N
		text += T + T + "e. Officer Training this month? Collaboration with other Units or AC?" + N
		text += T + T + T + report.officerTraining + N
		text += T + T + "f. Any Issues (note on qualification delinquents)" + N
		text += T + T + T + report.officerTrainingIssues + N
		text += T + T + "g. Date last Officer PQS Website Reviewed and Updated: " + report.officerPqsUpdate + N
				
		text += T + "D. Unit Operations Accomplishments" + N
		text += T + T + "a. Include IDT/AT/ADT/ADWS Bullets" + N
		text += T + T + T + report.unitAT + N
		text += T + T + "b. Unit (CO) - Two Liner Bullet in order of IDT, AT, and Long-Term Orders" + N
		text += T + T + T + report.unitCoBullets + N
		text += T + T + "c. Supported Command or Operationally Related Issues" + N
		text += T + T + T + report.unitOpsInputs + N
				
		text += "III. EQUIP" + N
				
		text += T + "A. General Communications Related to Infrastructure" + N
		text += T + T + report.infrastructure + N
		text += T + "B. General Communications Related to Information Technology (IT)" + N
		text += T + T + report.infoTech + N
		text += T + "C. Supplies/Resources" + N
		text += T + T + report.supplies + N
		text += T + "D. JRIC or Classified Site Utilization Numbers (Monthly" + N
		text += T + T + report.jric + N
				
		text += "IV. OTHER" + N
				
		text += T + "A. DV/VIP visits" + N
		text += T + T + report.visit + N
		text += T + "B. General Communications" + N
		text += T + T + report.generalComm + N
		text += T + "C. Individual Good News Stories and other Items " + N
		text += T + T + report.goodNews + N
		text += T + "D. Best Practices/Innovation: " + N
		text += T + T + report.innovation + N
		
		return text
	}
}
