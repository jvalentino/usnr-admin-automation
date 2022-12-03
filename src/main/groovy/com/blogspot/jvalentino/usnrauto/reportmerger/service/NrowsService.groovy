package com.blogspot.jvalentino.usnrauto.reportmerger.service

import java.util.Date;

import groovy.util.logging.Log4j;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.nrows.NrowsRawEntry;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.nrows.NrowsRawReport;
import com.blogspot.jvalentino.usnrauto.util.FormatUtil;

@Log4j
class NrowsService {

	
	NrowsRawReport parse(File file) throws Exception {
		NrowsRawReport report = new NrowsRawReport()
		
		report.file = file
		
		int lineCount = 1
		
		file.eachLine { String line ->
			String[] split = line.split("\t")
			
			if (lineCount == 1) {
				// do nothing
			} else if (split.length != 12) {
				String warning = "NROWS Report Line ${lineCount}: Line could not " + 
					"be read because the data doesn't contain 12 columns"
				report.warnings.add(warning)
				log.warn(warning)
			} else {
				try {
					NrowsRawEntry entry = parse(split, lineCount)
					report.entries.add(entry)
				} catch (Exception e) {
					String warning = "NROWS Report Line ${lineCount} Error: " + e.message
					report.warnings.add(warning)
					log.warn(warning)
				}
			}
			
			lineCount++
		}
		
		
		return report
	}
	
	NrowsRawEntry parse(String[] data, int line) throws Exception {
		NrowsRawEntry result = new NrowsRawEntry()
		
		int i = 0
		
		result.ssn = data[i++].trim()
		result.name = data[i++].trim()
		result.sdn = data[i++].trim()
		result.grade = data[i++].trim()
		result.ruic = data[i++].trim()
		result.trackingNumber = data[i++].trim()
		result.dutyType = data[i++].trim()
		result.startDate = FormatUtil.formatMMDDYYYY(data[i++].trim())
		result.endDate = FormatUtil.formatMMDDYYYY(data[i++].trim())
		result.days = Integer.parseInt(data[i++].trim())
		result.fy = Integer.parseInt(data[i++].trim())
		result.status = data[i++].trim()
		result.line = line
		
		return result
	}
}
