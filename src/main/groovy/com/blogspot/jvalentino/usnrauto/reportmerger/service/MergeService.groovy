package com.blogspot.jvalentino.usnrauto.reportmerger.service

import groovy.util.logging.Log4j;

import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.dozer.DozerBeanMapper;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.esams.EsamsRecord;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.esams.EsamsReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsELearningMember;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsELearningReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsGMTMember;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsGMTMemberReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.imr.ImrRecord;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.imr.ImrReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.DataCategory;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.History;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.ManualInputReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.ManualInputRules;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.ManualMemberRecord;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.merge.NrowsOrder;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.nrows.NrowsRawEntry;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.nrows.NrowsRawReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.CommonMemberData;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedMember;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.ruad.RuadMemberEntry;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.ruad.RuadReport;
import com.blogspot.jvalentino.usnrauto.util.FormatUtil;

/**
 * <p>The purpose of this service is to handle merging data from different sources into
 * a single set of records and information.</p>
 * 
 * @author jvalentino2
 *
 */
@Log4j
class MergeService {
    
    private DozerBeanMapper dozerBeanMapper = new DozerBeanMapper()

	/**
	 * Utility for removing a member by their full name (RANK FIRST LAST) from a list
	 * 
	 * @param rules
	 * @param members
	 * @param warnings
	 * @param reportName
	 */
	protected void removeMemberFromList(ManualInputRules rules, 
		List<CommonMemberData> members, List<String> warnings, String reportName) {
		for (String fullName : rules.ignorePeople) {
			boolean found = false
			
			for (CommonMemberData entry : members) {
				String memberName = entry.rank + " " + entry.firstName + " " + entry.lastName
				if (fullName.equalsIgnoreCase(memberName)) {
					members.remove(entry)
					found = true
					break
				}
			}

		}
	}
	
	/**
	 * Handles applying rules loaded from manual inputs to the RUAD.
	 * Rules are as follows:
	 * 1. Ability to ignore members by removing them from the RUAD
	 * @param rules
	 * @param ruad
	 * @param warnings
	 */
	protected void handleRulesForRuad(ManualInputRules rules, RuadReport ruad, List<String> warnings) {
		this.removeMemberFromList(rules, ruad.members, warnings, "RUAD")
	}
	
	/**
	 * Handles applying rules loaded from manual inputs to the eLearning report
	 * Rules are as follows:
	 * 1. Ability to ignore members by removing them from the report
	 * 2. Ability to ignore courses by name, which removed them from the list of courses
	 * and removes the member's list of courses and the flag that shows whether or not a course
	 * has been completed
	 * @param rules
	 * @param eLearning
	 * @param warnings
	 */
	protected void handleRulesForELearning(ManualInputRules rules, FltMpsELearningReport eLearning, List<String> warnings) {
		this.handleRulesForFltMpsELearning(rules, eLearning, warnings, "eLearning Report")
	}
	
	/**
	 * Handles applying rules loaded from manual inputs to the GMT report
	 * Rules as follows:
	 * 1. Ability to ignore members by removing them from the report
	 * 2. Ability to ignore courses by name, which removed them from the list of courses
	 * and removes the member's list of courses and the flag that shows whether or not a course
	 * has been completed. This also includes removing the corresponding course category for GMTs.
	 * 
	 * @param rules
	 * @param gmt
	 * @param warnings
	 */
	protected void handleRulesForGmt(ManualInputRules rules, FltMpsGMTMemberReport gmt, List<String> warnings) {
		
		this.removeMemberFromList(rules, gmt.members, warnings, "GMT Report")
		
		int[] indexesToRemove = FormatUtil.getIndexesForElements(
			FormatUtil.listToArray(rules.ignoreCourses), gmt.courseNames)
		
		gmt.courseNames = FormatUtil.removeFromArray(gmt.courseNames, indexesToRemove)
		gmt.courseCategories = FormatUtil.removeFromArray(gmt.courseCategories, indexesToRemove)
		
		for (FltMpsGMTMember member : gmt.members) {
			member.courseCompletions = FormatUtil.removeFromArray(
				member.courseCompletions, indexesToRemove)
			member.courseCategories = FormatUtil.removeFromArray(
				member.courseCategories, indexesToRemove)
			member.courseNames = gmt.courseNames
		}
		
	}
	
	/**
	 * Handles applying rules loaded from manual inputs to the eLearning report
	 * Rules are as follows:
	 * 1. Ability to ignore members by removing them from the report
	 * 2. Ability to ignore courses by name, which removed them from the list of courses
	 * and removes the member's list of courses and the flag that shows whether or not a course
	 * has been completed
	 * @param rules
	 * @param eLearning
	 * @param warnings
	 */
	protected void handleRulesForIa(ManualInputRules rules, FltMpsELearningReport ia, List<String> warnings) {
		this.handleRulesForFltMpsELearning(rules, ia, warnings, "IA Report")
	}
	
	/**
	 * This is a generic method for applying rules to one of the two FLTMPS reports, meaning
	 * IA and eLearning.
	 * Rules are as follows:
	 * 1. Ability to ignore members by removing them from the report
	 * 2. Ability to ignore courses by name, which removed them from the list of courses
	 * and removes the member's list of courses and the flag that shows whether or not a course
	 * has been completed
	 * 
	 * @param rules
	 * @param report
	 * @param warnings
	 * @param name
	 */
	protected void handleRulesForFltMpsELearning(ManualInputRules rules, 
		FltMpsELearningReport report, List<String> warnings, String name) {
		
		// handling ignoring any members
		this.removeMemberFromList(rules, report.members, warnings, name)
		
		// figure out the indexes for all courses being ignored
		int[] indexesToRemove = FormatUtil.getIndexesForElements(
			FormatUtil.listToArray(rules.ignoreCourses), report.courseNames)
		
		// remove any course names that are being ignored
		report.courseNames = FormatUtil.removeFromArray(report.courseNames, indexesToRemove)
				
		for (FltMpsELearningMember member : report.members) {
			
			// remove any completions for ignored courses
			member.courseCompletions = FormatUtil.removeFromArray(
				member.courseCompletions, indexesToRemove)
			
			// set the course names
			member.courseNames = report.courseNames
		}
	}
	
	/**
	 * Handles applying rules loaded from manual inputs to the Manual Inputs.
	 * Rules are as follows:
	 * 1. Ability to ignore members by removing them from the report
	 * @param rules
	 * @param ruad
	 * @param warnings
	 */
	protected void handleRulesForManualInputs(ManualInputRules rules, ManualInputReport manual, List<String> warnings) {
		this.removeMemberFromList(rules, manual.members, warnings, "Manual Inputs")
	}
	
	/**
	 * Handles applying rules loaded from manual inputs to the IMR.
	 * Rules are as follows:
	 * 1. Ability to ignore members by removing them from the report
	 * @param rules
	 * @param imr
	 * @param warnings
	 */
	protected void handleRulesForIMR(ManualInputRules rules, ImrReport imr, List<String> warnings) {

		for (String fullNameWithRank : rules.ignorePeople) {
			boolean found = false
			
			try {
			
				// have to remove the rank from the full name
				String[] split = fullNameWithRank.split(" ")
				String fullName = split[1] + " " + split[2]
				
				for (ImrRecord entry : imr.members) {
					String memberName = entry.firstName + " " + entry.lastName
					if (fullName.equalsIgnoreCase(memberName)) {
						imr.members.remove(entry)
						found = true
						break
					}
				}
			} catch (Exception e) {
				String error = "Unable to ignore ${fullNameWithRank} in the IMR because that name is in an invalid format"
				log.error(error, e)
				warnings.add(error)
			}
		}
	}
	
	// TODO: Handle rules for ignoring NROWS?
	
	// TODO: Handle rules for ignoring people in ESAMS
	
	/**
	 * Combines all given reports into a single "merged" report
	 * @param ruad
	 * @param eLearning
	 * @param gmt
	 * @param ia
	 * @param imr
	 * @param manual
	 * @return
	 */
    MergedReport generateReport(RuadReport ruad, FltMpsELearningReport eLearning,
            FltMpsGMTMemberReport gmt, FltMpsELearningReport ia, ImrReport imr,
			ManualInputReport manual, NrowsRawReport nrows=null, EsamsReport esams=null) {
            
        MergedReport report = new MergedReport()
		
		// handle rules, which will ignore certain members from everything
		if (manual != null) {
			handleRulesForRuad(manual.rules, ruad, report.warnings)
			handleRulesForELearning(manual.rules, eLearning, report.warnings)
			handleRulesForGmt(manual.rules, gmt, report.warnings)
			handleRulesForIa(manual.rules, ia, report.warnings)
			handleRulesForIMR(manual.rules, imr, report.warnings)
			handleRulesForManualInputs(manual.rules, manual, report.warnings)
			
			// warn for anyone being ignored
			for (String person : manual.rules.ignorePeople) {
				report.warnings.add(person + " is being ignored in all reporting")
			}
			
			// warning for any course being ignored
			for (String course : manual.rules.ignoreCourses) {
				report.warnings.add(course + " is being ignored in all reporting")
			}
			
			report.manualFile = manual.file
		}
				
		// handle keeping track of where everything came from
		report.ruadFile = ruad.file
		report.eLearningFile = eLearning.file
		report.gmtFile = gmt.file
		report.iaFile = ia.file
		report.imrFile = imr.file
		
        
        // The RUAD is the record of source, so get the people from that
        List<MergedMember> members = this.generateMemberListFromRUAD(ruad)
        report.setTotalMembersFromRuad(ruad.getMembers().size())
		report.totalIAP = ruad.totalIAP
		report.totalCAO = ruad.totalCAO
		report.totalCAI = ruad.totalCAI
		report.totalEnlisted = ruad.totalEnlisted
		report.totalOfficer = ruad.totalOfficer
        
        // scrub the list for duplicates and generate warnings
        List<MergedMember> map = this.scrubMembersForDuplicates(members, report.getWarnings())
        
        // deal with this member eLearning data
        this.mergeELearning(map, eLearning, report.getWarnings())
        report.seteLearningCourseNames(eLearning.getCourseNames())
        report.setTotalMembersFromELearning(eLearning.getMembers().size())
        
        // deal with member GMT data
        this.mergeGMT(map, gmt, report.getWarnings())
        report.setGmtCourseCategories(gmt.getCourseCategories())
        report.setGmtCourseNames(gmt.getCourseNames())
        report.setTotalMembersFromGMT(gmt.getMembers().size())
        
        // deal with IA data
        this.mergeIA(map, ia, report.getWarnings())
        report.setIaCourseNames(ia.getCourseNames())
        report.setTotalMembersFromIA(ia.getMembers().size())
        
        // deal with mapping medical data
        if (imr == null) {
            report.getWarnings().add("No IMR report was given from NRRM")
        } else {
            List<ImrRecord> imrRecords = this.scrubImr(imr.getMembers(), report.getWarnings())
            this.mergeImr(imrRecords, map, report.getWarnings())
			report.setTotalMembersFromIMR(imrRecords.size())
        }
		
		if (manual == null) {
			report.getWarnings().add("No Manual inputs were given")
			report.spreadsheetFooter = ""
			report.pdfFooter = ""
		} else {
			this.mergeManualInputs(map, manual, report.getWarnings())
			report.setTotalMembersFromManualInputs(manual.getMembers().size())
			report.primaryColumnHeaders = manual.primaryColumnHeaders
			report.secondaryColumnHeaders = manual.secondaryColumnHeaders
			report.spreadsheetFooter = manual.spreadsheetFooter
			report.pdfFooter = manual.pdfFooter
			report.trainingEmailHeader = manual.trainingEmailHeader
			report.trainingSmsHeader = manual.trainingSmsHeader
			report.emailRequiresAuthorization = manual.emailRequiresAuthorization
			report.emailTlsEnabled = manual.emailTlsEnabled
			report.emailHost = manual.emailHost
			report.emailPort = manual.emailPort
			report.emailUsername = manual.emailUsername
			report.history = manual.history
		}
		
		if (nrows != null) {
			try {
				this.mergeNrows(map, nrows, report.getWarnings())
				report.nrowsFile = nrows.file
			} catch (Exception e) {
				report.warnings.add("Unable to process NROWS Report: " + e.message)
				log.error("Unable to process NROWS", e)
			}
		}
		
		if (esams != null) {
			try {
				this.mergeEsams(report, map, esams, report.getWarnings())
				report.esamsFile = esams.file
			} catch (Exception e) {
				report.warnings.add("Unable to process ESAMS Report: " + e.message)
				log.error("Unable to process ESAMS", e)
			}
		}
        
        // sort the list by last name
        map = map.sort { it.lastName }
        
        report.members = map
        
        return report;
    }
    
    /**
     * Since we only have first name name, we cannot handle members with the same first and last
     * name. We need to remove their medical info from the IMR Report
     *    
     * @param members
     * @param warnings
     * @return
     */
    protected List<ImrRecord> scrubImr(List<ImrRecord> members, List<String> warnings) {
        LinkedHashMap<String, ImrRecord> map = new LinkedHashMap<String, ImrRecord>()
        
        List<String> memberKeysToRemove = new ArrayList<String>()
        
        for (ImrRecord member : members) {
            if (map.containsKey(member.toKey())) {
                // uh oh, there is already a member with this name
                memberKeysToRemove.add(member.toKey())
                String message = member.toKey() + " exists muliple times in the list, which means we can't handle them."
                message += " The only way to reliably identify members in all these files is by First Name, and Last Name."
                warnings.add(message)
            } else {
                map.put(member.toKey(), member)
            }
        }
        
        // remove duplicates from the map for which we generated warnings
        for (String remove : memberKeysToRemove) {
            map.remove(remove);
        }
                
        List<ImrRecord> newList = new ArrayList<ImrRecord>()
        for (ImrRecord member : map.values()) {
            newList.add(member)
        }
        
        return newList
    }
            
    private void mergeImr(List<ImrRecord> records, List<MergedMember> members, List<String> warnings) {
           
       for (MergedMember member : members) {
           
           // try and find this person by first and last name
           ImrRecord found = null
           
           for (ImrRecord record : records) {
               if (record.getFirstName().equals(member.getFirstName())
                   && record.getLastName().equals(member.getLastName())) {
                   found = record
                   break
               }
           }
           
           if (found == null) {
               String message = "The IMR information for " + member.toKey() + " could not be found."
               warnings.add(message)
               member.setImrStatus(message)
           } else {
               member.setImrStatus(found.getStatus())
           }
           
       }
        
    }
    
    /**
     * Generate the list of members initially based on the RUAD, so it will start out
     * with RUAD related data populated.             
     * @param ruad
     * @return
     */
    private List<MergedMember> generateMemberListFromRUAD(RuadReport ruad) {
        List<MergedMember> members = new ArrayList<MergedMember>()
        
        for (RuadMemberEntry ruadMember : ruad.getMembers()) {
            MergedMember member = dozerBeanMapper.map(ruadMember, MergedMember.class)
            members.add(member)
        }
        
        return members
    }
    
    /**
     * we need to scrub the list for members with the name rank/rate, first, and last names 
     * This is because this is only only method for reliable correlation between all of
     * the different spreadsheets
     * 
     * @param members
     * @param warnings
     * @return
     */
    private List<MergedMember> scrubMembersForDuplicates(
        List<MergedMember> members, List<String> warnings) {
        
        LinkedHashMap<String, MergedMember> map = new LinkedHashMap<String, MergedMember>()
        
        List<String> memberKeysToRemove = new ArrayList<String>()
        
        for (MergedMember member : members) {
            if (map.containsKey(member.toKey())) {
                // uh oh, there is already a member with this name
                memberKeysToRemove.add(member.toKey())
                String message = member.toKey() + " exists muliple times in the list, which means we can't handle them."
                message += " The only way to reliably identify members in all these files is by Rank/Rate, First Name, and Last Name."
                warnings.add(message)
            } else {
                map.put(member.toKey(), member)
            }
        }
        
        // remove duplicates from the map for which we generated warnings
        for (String remove : memberKeysToRemove) {
            map.remove(remove);
        }
                
        List<MergedMember> newList = new ArrayList<MergedMember>()
        for (MergedMember member : map.values()) {
            newList.add(member)
        } 
        
        return newList
    }
    
    /**
     * Merges all eLearning related data into the record for each member
     *    
     * @param map
     * @param eLearning
     * @param warnings
     */
    private void mergeELearning(List<MergedMember> map, FltMpsELearningReport eLearning,
        List<String> warnings) {
        
        for (MergedMember member : map) {
            
            FltMpsELearningMember found = this.findMemberInList(member.toKey(), eLearning.getMembers())
            
            if (found == null) {
                String message = member.toKey() + " does not have any data in eLearning"
                warnings.add(message)
            } else {
                member.setExistsInELearning(true)
                member.seteLearningCourseCompletions(found.getCourseCompletions())
            }
        }
     
    }
    
    private CommonMemberData findMemberInList(String key, List<CommonMemberData> list) {
        for (CommonMemberData current : list) {
            if (current.toKey().equals(key)) {
                return current
            }
        }
        return null
    }
        
        
    private void mergeGMT(List<MergedMember> map, FltMpsGMTMemberReport gmt,
        List<String> warnings) {
        
        for (MergedMember member : map) {
            
            FltMpsGMTMember found = this.findMemberInList(member.toKey(), gmt.getMembers())
            
            if (found == null) {
                String message = member.toKey() + " does not have any data in the GMT report"
                warnings.add(message)
            } else {
                member.setExistsInGMT(true)
                member.setGmtCourseCompletions(found.getCourseCompletions())
            }
        }
        
    }
        
    private void mergeIA(List<MergedMember> map, FltMpsELearningReport ia,
        List<String> warnings) {
        
        for (MergedMember member : map) {
            
            FltMpsELearningMember found = this.findMemberInList(member.toKey(), ia.getMembers())
            
            if (found == null) {
                String message = member.toKey() + " does not have any data in the IA report"
                warnings.add(message)
            } else {
                member.setExistsInIA(true)
                member.setIaCourseCompletions(found.getCourseCompletions())
            }
        }
                
    }
		
	private void mergeManualInputs(List<MergedMember> map, ManualInputReport manual,
		List<String> warnings) {
		
		for (MergedMember member : map) {
			
			ManualMemberRecord found = this.findMemberInList(member.toKey(), manual.getMembers())
			
			if (found == null) {
				String message = member.toKey() + " does not have any data in the Manual Input report"
				warnings.add(message)
			} else {
				member.setExistsInManualInputs(true)
				
				// map all of the data user dozer
				dozerBeanMapper.map(found, member)
				
			}
		}
	}
		
	/**
	 * Takes a name in the format of "LASTNAME, FIRSTNAME M" and returns [FIRSTNAME, LASTNAME]
	 * @param name
	 * @return
	 */
	String[] getFirstNameLastNameFromCommaSeparatedName(String name) {
		// determine the first and last name
		String[] split = name.split(",")
		String lastName = split[0].trim()
		String firstName = split[1].trim().split(" ")[0]
		
		String[] result = [firstName, lastName]
		
		return result
	}
	
	List<MergedMember> findMembers(String firstName, String lastName, List<MergedMember> map) {
		List<MergedMember> result = new ArrayList<MergedMember>()
		
		for (MergedMember member : map) {
			if (member.firstName.equals(firstName) && member.lastName.equals(lastName)) {
				result.add(member)
			}
		}
		
		return result
	}
		
	protected void mergeNrows(List<MergedMember> map, NrowsRawReport nrows, List<String> warnings) throws Exception {
			
		// for each nrows entry..
		for (NrowsRawEntry entry : nrows.entries) {
			// determine the first and last name
			String[] result = getFirstNameLastNameFromCommaSeparatedName(entry.name)
			String lastName = result[1]
			String firstName = result[0]
						
			List<MergedMember> founds = this.findMembers(firstName, lastName, map)
			int matches = founds.size()
			
			if (matches == 0) {
				String warning = entry.name + " from NROWS could not be found in the RUAD"
				log.warn(warning)
				warnings.add(warning)
			} else if (matches > 1) {
				String warning = entry.name + " from NROWS could not be matched because someone else has the same name"
				log.warn(warning)
				warnings.add(warning)
			} else {
				NrowsOrder order = convertOrders(entry)
				founds.get(0).orders.add(order)
			}
			
		}
		
		// for each member, total up the days of INITIAL and MOD
		for (MergedMember member : map) {
			for (NrowsOrder orders : member.orders) {
				if (orders.isCountableStatus()) {
					member.daysOfOrdersInCurrentFY += orders.days
					//log.info("orders found for " + member.toKey() + " of " + orders.days + " days")
				}
			}
			
			if (member.daysOfOrdersInCurrentFY >= 12) {
				member.hasEnoughDaysOfOrdersForCurrentFY = true
			} else {
				member.hasEnoughDaysOfOrdersForCurrentFY = false
			}
		}
		
		// add all the warnings from the original parsing
		for (String warning : nrows.warnings) {
			warnings.add(warning)
		}
	}
	
	NrowsOrder convertOrders(NrowsRawEntry input) {
		NrowsOrder output = dozerBeanMapper.map(input, NrowsOrder.class)
		return output
	}
	
	/**
	 * ESAMS is weird in that we only get a list of training that members need to do, or need to due in the near future
	 * We assume that if a course is listed for John but not Jeff, then Jeff must have completed that course
	 * The meaning of these states is that for negative completion. If 30% require training than 70% don't, but
	 * we really are only certain of the specifics of the 30%. While Jeff may not even need training A it doesn't
	 * matter since that course name doesn't show in their report. It only shows in the summary that he doesn't
	 * need to take it as a part of the entire unit percentage for "Training A".
	 * @param report
	 * @param members
	 * @param esams
	 * @param warnings
	 */
	protected void mergeEsams(MergedReport report, List<MergedMember> members, EsamsReport esams, List<String> warnings) {
		
		// determine unique course names across all records
		Set<String> courseSet = new LinkedHashSet<String>()
		for (EsamsRecord record : esams.records) {
			courseSet.add(record.title)
		}
		
		report.esamsCourseNames = new String[courseSet.size()]
		int index = 0
		for (String name : courseSet) {
			report.esamsCourseNames[index] = name
			index++
		}
		
		
		
		// for each entry
		for (EsamsRecord record : esams.records) {
			String[] result = getFirstNameLastNameFromCommaSeparatedName(record.name)
			String lastName = result[1]
			String firstName = result[0]
			
			List<MergedMember> founds = this.findMembers(firstName, lastName, members)
			int matches = founds.size()
			
			if (matches == 0) {
				String warning = record.name + " from ESAMS could not be found in the RUAD"
				log.warn(warning)
				if (!warnings.contains(warning)) {
					warnings.add(warning)
				}
			} else if (matches > 1) {
				String warning = record.name + " from record could not be matched because someone else has the same name"
				log.warn(warning)
				if (!warnings.contains(warning)) {
					warnings.add(warning)
				}
			} else {
				// add this training to the member
				founds.get(0).esamsRecords.add(record)
			}
		}
		
	}
		

}
