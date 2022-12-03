package com.blogspot.jvalentino.usnrauto.reportmerger.service

import java.awt.TexturePaintContext.Int;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dozer.DozerBeanMapper;

import com.blogspot.jvalentino.usnrauto.main.AppState;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.CommonMemberData;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedMember;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.esams.EsamsRecord;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.idcreport.IDCReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.DataCategory;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.History;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.HistoryElement;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.ManualInputReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.ManualMemberRecord;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.mas.MasCode;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.merge.NrowsOrder;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.MemberWithMissingData;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.MissingData;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.SummaryReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.service.summary.SummaryExcel;
import com.blogspot.jvalentino.usnrauto.commons.excel.CopyType;
import com.blogspot.jvalentino.usnrauto.commons.message.EmailMessage;
import com.blogspot.jvalentino.usnrauto.util.FormatUtil;

import static com.blogspot.jvalentino.usnrauto.util.FormatUtil.*;

/**
 * <p>Service used for generating an XLSX report that summarizes all member data</p>
 * 
 * @author jvalentino2
 *
 */
class SummaryReportService {
	
	private DozerBeanMapper dozerBeanMapper = new DozerBeanMapper(["dozerMapping.xml"])
	

	/**
	 * Turns a merged report into a summary report, which calculates percentages,
	 * completion rates, and other items. The intention is to be used to generate
	 * a spreadsheet that correlates data from all of the sources merged.
	 * @param mergedReport
	 * @return
	 */
	SummaryReport generateSummaryReport(MergedReport mergedReport, Date now) {
		
		// map the merged report into the summary report
		SummaryReport report = dozerBeanMapper.map(mergedReport, SummaryReport.class, "mergeToSummary")
		report.history = mergedReport.history
		
		int members = mergedReport.getMembers().size()
		int eLearningCourses = mergedReport.geteLearningCourseNames().length
		int gmtCourses = mergedReport.getGmtCourseNames().length
		int iaCourses = mergedReport.getIaCourseNames().length
		int notExpiredPrdCount = 0
		int fullyMedicallReadyCount = 0
		int goodAtCount = 0
		
		// elearning sums for completion
		int[] completeELearningCounts = new int[eLearningCourses]
		report.eLearningCourseCompletionPercentages = new double[eLearningCourses]
		for (int i = 0; i < eLearningCourses; i++) {
			completeELearningCounts[i] = 0
		}
		
		// gmt sums for completion
		int[] completeGmtCounts = new int[gmtCourses]
		report.gmtCourseCompletionPercentages = new double[gmtCourses]
		for (int i = 0; i < gmtCourses; i++) {
			completeGmtCounts[i] = 0
		}
		
		// ia sums for completion
		int[] completeIaAcounts = new int[iaCourses]
		report.iaCourseCompletionPercentages = new double[iaCourses]
		for (int i = 0; i < iaCourses; i++) {
			completeIaAcounts[i] = 0
		}
				
		// now fill in the details that make up the summary report
		for (int i = 0; i < report.getMembers().size(); i++) {
			
			MergedMember member = report.getMembers().get(i)
			
			// PRD
			if (member.getPrd().after(now)) {
				notExpiredPrdCount++
			} else {
				String warning = member.toKey() + " has an expired PRD of " + FormatUtil.dateToFormatTwoString(member.prd)
				report.commandWarnings.add(warning);
			}
			
			// IMR
			if (member.getImrStatus().equalsIgnoreCase("Fully Medically Ready")) {
				fullyMedicallReadyCount++
			} else if (!member.getImrStatus().contains("not be found")) {
				String warning = member.toKey() + " is " +member.getImrStatus()
				report.commandWarnings.add(warning);
			}
			
			// e-Learning
			if (member.geteLearningCourseCompletions() != null) {
				for (int j = 0; j < member.geteLearningCourseCompletions().length; j++) {
					if (member.geteLearningCourseCompletions()[j]) {
						completeELearningCounts[j]++
					}
				}
			}
			
			// GMT
			if (member.getGmtCourseCompletions() != null) {
				for (int j = 0; j < member.getGmtCourseCompletions().length; j++) {
					if (member.getGmtCourseCompletions()[j]) {
						completeGmtCounts[j]++
					}
				}
			}
			
			// IA
			if (member.getIaCourseCompletions() != null) {
				for (int j = 0; j < member.getIaCourseCompletions().length; j++) {
					if (member.getIaCourseCompletions()[j]) {
						completeIaAcounts[j]++
					}
				}
			}
			
			// NROWS
			if (report.nrowsFile != null) {
				if (!member.hasEnoughDaysOfOrdersForCurrentFY) {
					String warning = member.toKey() + " only has " + member.daysOfOrdersInCurrentFY + 
						" days of orders in current FY"
					report.commandWarnings.add(warning);
				} else {
					goodAtCount++
				}
			}
		}
		
		report.setAtPercentage( goodAtCount / members)
				
		// PRD
		report.setPrdPercentage(   notExpiredPrdCount/ members  )
		
		// IMR
		report.setImrPercentage(  fullyMedicallReadyCount/ members  )
		
		// eLearning
		for (int i = 0; i < eLearningCourses; i++) {
			report.eLearningCourseCompletionPercentages[i] = completeELearningCounts[i] / members
		}
		
		// gmt
		for (int i = 0; i < gmtCourses; i++) {
			report.gmtCourseCompletionPercentages[i] = completeGmtCounts[i] / members
		}
		
		// ia
		for (int i = 0; i < iaCourses; i++) {
			report.iaCourseCompletionPercentages[i] = completeIaAcounts[i] / members
		}
		
		this.integrateEsamsData(report, now)
		
		this.generateManualSummary(report, mergedReport)
		
		// Generate summary information about rank distribution
		report.officerRanks = officerRanks
		report.rankGroup = generateRankDistributionSummary(mergedReport.members)
		
		report.orderGroup = this.generateOrderDistribution(mergedReport.members)
		
		// handle updating the history
		ServiceBus.getInstance().getHistoryService().updateHistory(now, report)
		
		return report
	}
	
	
	/**
	 * Generates a map of rank groups and their counts. For examples YN: 5, CTN: 2, LTJG: 2
	 * @param members
	 * @return
	 */
	protected Map<String, Integer> generateRankDistributionSummary(List<CommonMemberData> members) {
		Map<String, Integer> map = new HashMap<String, Integer>()
				
		for (CommonMemberData member : members) {
			
			String rank = member.getRank()
			String group = this.determineRankGroup(rank)
			
			// does this group exist?
			if (map.containsKey(group)) {
				// increment the count
				int value = map.get(group) + 1
				map.put(group, value)
			} else {
				map.put(group, 1)
			}
			
		}
		
		return map
	}
	
	protected Map<String, Integer> generateOrderDistribution(List<MergedMember> list) {
		
		Map<String, Integer> map = new LinkedHashMap<String, Integer>()
		
		int total = 0
		
		for (MergedMember member : list) {
			for (NrowsOrder orders : member.orders) {
				if (orders.isCountableStatus()) {
					
					String status = orders.dutyType
					
					total += orders.days
					
					// does this group exist?
					if (map.containsKey(status)) {
						// increment the count
						int value = map.get(status) + orders.days
						map.put(status, value)
					} else {
						map.put(status, orders.days)
					}
					
				}
			}
		}
		
		map.put("ALL", total)
		
		return map
	}
	
	def officerRanks = FormatUtil.officerRanks
	
	/**
	 * Takes an officer or enlisted rank and returns the rank group.
	 * For officers the rank group is just the rank, while for enlisted it is the rate
	 * without the rank. So YNCS becomes YN, and CTN3 becomes CTN.
	 * 
	 * @param rank
	 * @return
	 */
	public String determineRankGroup(String rank) {		
		
		String group = null
		
		// is this member an officer?
		if (officerRanks.contains(rank)) {
			group = rank
		} else if (rank.endsWith("1") || rank.endsWith("2") || rank.endsWith("3")) {
			// determine if a petty officer
			group = rank.substring(0, rank.length() - 1)
		} else if (rank.endsWith("C")) {
			// the member is a chief
			group = rank.substring(0, rank.length() - 1)
		} else if (rank.endsWith("CS") || rank.endsWith("CM")) {
			group = rank.substring(0, rank.length() - 2)
		} else {
			// a seaman/airman/fireman of some sort, always a two character abbreviation on the send
			group = rank.substring(0, rank.length() - 2)
		}
		
		return group
		
	}
	
	/**
	 * Handles populating the manual portion of the summary
	 * @param summary
	 * @param mergedReport
	 */
	private void generateManualSummary(SummaryReport summary, MergedReport mergedReport) {
		
		int members = mergedReport.getMembers().size()
		
		// there contains percentages of "Good" status for each dynamic column
		summary.manualPercentages = new Double[mergedReport.primaryColumnHeaders.size()]
		
		// this is the running sum for each dynamic column
		Integer[] manualCounts = new Integer[mergedReport.primaryColumnHeaders.size()]
		for (int i = 0; i < mergedReport.primaryColumnHeaders.size(); i++) {
			manualCounts[i] = 0
		}
		
		for (MergedMember member : summary.getMembers()) {
			
			// if this member doesn't have any manual data
			if (member.values == null) {
				
				continue
			}
			
			// we will be establishing what is valid and what is not
			member.valueValid = new boolean[member.values.length]
			for (int i = 0; i < member.valueValid.length; i++) {
				member.valueValid[i] = false
			}
			
			// for each dynamic column
			for (int i = 0; i < mergedReport.primaryColumnHeaders.size(); i++) {
				
				String columnGroup = mergedReport.primaryColumnHeaders.get(i)
				String columnName =  mergedReport.secondaryColumnHeaders.get(i)				
				
				String columnValue = member.values[i]
				
				// polygraph is a special logic case
				if (columnName.equalsIgnoreCase("Poly")) {
					
					if (isStringADateWithin5Years(new Date(), columnValue)) {
						manualCounts[i] =  manualCounts[i] + 1
						member.valueValid[i] = true
					} else {
						String warning = member.toKey() + " needs a poly (" + columnValue + ")"
						summary.commandWarnings.add(warning)
					}
				} else if (columnValue != null && columnGroup.equalsIgnoreCase("Mission Support")) {
					// mission support is a special case where it just needs to not be blank
					if (columnValue.length() != 0 && !columnValue.equalsIgnoreCase("?")) {
						manualCounts[i] =  manualCounts[i] + 1
						member.valueValid[i] = true
					}
				} else if (columnValue != null && (columnGroup.equalsIgnoreCase("Contact Information") || columnGroup.equalsIgnoreCase("Skills") )) {
					
					if (columnValue.length() != 0 && !columnValue.equalsIgnoreCase("?")) {
						manualCounts[i] =  manualCounts[i] + 1
						member.valueValid[i] = true
					}
								
				} else if (columnName.equalsIgnoreCase("Notes")) {
				
					// just ignore anything involving notes
					manualCounts[i] =  manualCounts[i] + 1
					member.valueValid[i] = true
				
				} else if (columnName.contains("ID")) {
				
					// must be non-blank and not ?
					if (columnValue.length() != 0 
						&& !columnValue.equalsIgnoreCase("?") 
						&& !columnValue.equalsIgnoreCase("no")
						&& !columnValue.equalsIgnoreCase("in progress")) {
						manualCounts[i] =  manualCounts[i] + 1
						member.valueValid[i] = true
					}
				
				}else if (columnValue != null) {
					
					// everything else just looks for a positive status
				
					// if the value is a positive status
					if (columnValue.equalsIgnoreCase("complete") || 
						columnValue.equalsIgnoreCase("full") || 
						columnValue.equalsIgnoreCase("no need") || 
						columnValue.equalsIgnoreCase("good") ||
						columnValue.equalsIgnoreCase("yes") ||
						isStringADate(columnValue)) {
						
						manualCounts[i] =  manualCounts[i] + 1
						member.valueValid[i] = true
					}
				}
			} // end for each column
			
		} // end for each member
		
		// make all the percentages
		for (int i = 0; i < mergedReport.primaryColumnHeaders.size(); i++) {
			
			summary.manualPercentages[i] = manualCounts[i] / members
			
		}
		
		
	}
	
	protected boolean isStringADateWithin5Years(Date current, String dateString) {
		
		if (dateString.startsWith("N/A")) {
			return true
		}
		
		Date parsedDate = FormatUtil.formatTwoStringToDate(dateString)
		
		// if the date could not be parsed, we can't process it
		if (parsedDate == null) {
			return false
		}
		
		int days = FormatUtil.daysBetweenDates(current, parsedDate)
		
		if (days < 365 * -5) {
			return false
		} else {
			return true
		}
		
	}
	
	protected boolean isStringADate(String dateString) {
		Date parsedDate = FormatUtil.formatTwoStringToDate(dateString)
		
		// if the date could not be parsed, we can't process it
		if (parsedDate == null) {
			return false
		}
		
		return true
	}
	
	/**
	 * Handles the generate of the spreadsheet using the summary report as the inputs.
	 * This will also contain the raw input sources from which the data came.
	 * 
	 * @param file
	 * @param report
	 * @throws Exception
	 */
	void generateSpreadsheet(File file, SummaryReport report) throws Exception {
		SummaryExcel.generateSpreadsheet(file, report)
	}
	
	
	
	
	
	/**
	 * Returns true if the given value is somethign which we consider to be blank or missing
	 * @param value
	 * @return
	 */
	protected boolean isValueMissing(String value) {
		if (value == null)
			return true
		
		if (value.equals("?"))
			return true
			
		if (value.equals(""))
			return true
			
		return false
	}
	
	List<MemberWithMissingData> assembleMissingInformation(SummaryReport report) {
		List<MemberWithMissingData> result = new ArrayList<MemberWithMissingData>()
		
		for (MergedMember member : report.members) {
			List<MissingData> list = this.determineMissingInformationWithNoPII(report, member)
			
			MemberWithMissingData data = new MemberWithMissingData(member, list)
			
			// figure out their cell phone and email addresses
			data.emails = this.getMemberEmailAddresses(report, member)
			data.cell = this.getMemberCell(report, member)
			
			result.add(data)
		}
		
		return result
	}
	
	List<String> getMemberEmailAddresses(SummaryReport report, MergedMember member ) {
		List<String> result = new ArrayList<String>()
		
		if (member == null ) {
			return result
		}
		
		if (member.values == null) {
			return result
		}
		
		for (int i = 0; i < report.secondaryColumnHeaders.size(); i++) {
			String value =  report.secondaryColumnHeaders.get(i).toLowerCase()
			
			if (value.contains("email")) {
				if (member.values[i].contains("@")) {
					result.add(member.values[i])
				}
			}
		}
		
		return result
		
	}
	
	String getMemberCell(SummaryReport report, MergedMember member ) {
		
		if (member == null) {
			return null
		}
		
		if (member.values == null) {
			return null
		}
		
		for (int i = 0; i < report.secondaryColumnHeaders.size(); i++) {
			String value =  report.secondaryColumnHeaders.get(i).toLowerCase()
			
			if (value.contains("cell #")) {
				
				if (FormatUtil.isValidPhone(member.values[i])) {
					return member.values[i]
				}
			}
		}
		
		return null
		
	}
	
	/**
	 * Gets a list of the NON-PII missing information for the given member
	 * @param report
	 * @param member
	 * @return
	 */
	List<MissingData> determineMissingInformationWithNoPII(SummaryReport report, MergedMember member) {
		List<MissingData> all = this.determineMissingInformation(report, member)
		List<MissingData> filtered = new ArrayList<MissingData>()
		
		for (MissingData data : all) {
			if (!data.pii) {
				filtered.add(data)
			}
		}
		
		return filtered
	}
	
	
	/**
	 * Determines what information a member has from manual inputs based on the summary report
	 * @param report
	 * @param member
	 * @return
	 */
	List<MissingData> determineMissingInformation(SummaryReport report, MergedMember member) {
		List<MissingData> list = new ArrayList<MissingData>()
		
		if (member.values == null) {
			return list
		}
		
		// for all the values...
		for (int i = 0; i < member.values.length; i++) {
			
			// if this value was not valid...
			boolean valid = member.valueValid[i]
			
			if (valid) {
				continue
			}
			
			// just because it is not valid doesn't mean it is missing
			String memberValue = member.values[i]
			
			if (!this.isValueMissing(memberValue)) {
				continue
			}
			
			MissingData data = new MissingData()
			
			// get the category and column names
			data.primary = report.primaryColumnHeaders.get(i)
			data.secondary =  report.secondaryColumnHeaders.get(i)
			
			// label this if it is potentially PII
			String secondary = data.secondary.toLowerCase()
			String[] pii = [
				"personal", "birth", "dob", "home #", "last 4", 
				"social security", "medical"
			]
			
			for (String piiValue : pii) {
				if (secondary.contains(piiValue)) {
					data.pii = true
				}
			}
			
			// get all the possible unique values for this column
			for (MergedMember other : report.members) {
				
				if (other.values == null) {
					continue
				}
				
				String value = other.values[i]
				
				// don't count empty values
				if (!isValueMissing(value))
					data.values.add(value)
			}
			
			// determine the data type based on the values
			if (!data.pii) {
				data.type = this.determineTypeBasedOnValues(secondary, data.values)
			}
			
			list.add(data)
		}
		
		
		return list
	}
	
	/**
	 * Used to look at a list of values and determine what the "type" is
	 * Anything with a date in it is Date, anything with 4 or less values is
	 * a comma separated list of those values, otherwise it is null for None
	 * 
	 * @param values
	 * @return
	 */
	protected String determineTypeBasedOnValues(String category, Set<String> values) {
		String result = null
		
		if (category.toLowerCase().contains("email")) {
			return null
		}
		
		// if any value is a date, the type is Date
		for (String value : values) {
			if (isStringADate(value)) {
				return "Date"
			}
		}
		
		// if there are 4 or less unique values
		// return a comma separated list
		if (values.size() <= 4) {
			return FormatUtil.arrayToSlashSeparatedString(values as String[])
		}
		
		return null
	}
	
	EmailMessage generateEmailForMissingData(String header, String from, MemberWithMissingData data) {
		
		// if this member doens't have an email
		if (data.emails.size() == 0) {
			return null
		}
		
		// if this member doesn't have any missing data
		if (data.missing.size() == 0) {
			return null
		}
		
		EmailMessage message = new EmailMessage()
		
		message.from = from
		message.to = data.emails
		message.title = "USNR Admin Automation: Missing Data Notification"
		message.content = ""
		
		message.content += data.member.rank + " " + data.member.lastName + ",\n"
		message.content += "\n"
		message.content += header + "\n"
		message.content += "\n"
		
		for (MissingData missing : data.missing) {
			message.content += missing.toString() + "\n"
		}
		
		message.content += "\n"
		
		message.content += "This message was generated and sent by the USNR Admin Automation Project "
		message.content += AppState.getInstance().version + "." + AppState.getInstance().buildNumber
		
		return message
	}
	
	
	protected void integrateEsamsData(SummaryReport report, Date date) {
		
		if (report.esamsFile == null) {
			return
		}
		
		int courses = report.esamsCourseNames.length
		int members = report.members.size()
		
		// mark every member as having completed these courses
		for (MergedMember member: report.members) {
			member.esamsCourseCompletions = new boolean[courses]
			for (int i = 0; i < courses; i++) {
				member.esamsCourseCompletions[i] = true
			}
		}
		
		// now look through the members that have courses that need to be done
		for (MergedMember member: report.members) {
			for (EsamsRecord record: member.esamsRecords) {
				boolean due = date.after(record.requiredDate)
				
				// if this training is now due, mark the completion as false
				if (due) {
					int index = ArrayUtils.indexOf(report.esamsCourseNames, record.title)
					member.esamsCourseCompletions[index] = false
					member.esamsCoursesToDo.add(record.title)
				}
			}
		}
		
		// sum the completions
		int[] completionCounts = new int[courses]
		report.esamsCourseCompletionPercentages = new double[courses]
		for (int i = 0; i < courses; i++) {
			completionCounts[i] = 0
		}
		
		for (MergedMember member: report.members) {
			
			if (member.esamsCourseCompletions == null) {
				continue	
			}
			
			for (int j = 0; j < member.esamsCourseCompletions.length; j++) {
				if (member.esamsCourseCompletions[j]) {
					completionCounts[j]++
				}
			}
		}
		
		// generate percentages of completions for each course
		for (int i = 0; i < courses; i++) {
			report.esamsCourseCompletionPercentages[i] = completionCounts[i] / members
		}
		
	}
}
