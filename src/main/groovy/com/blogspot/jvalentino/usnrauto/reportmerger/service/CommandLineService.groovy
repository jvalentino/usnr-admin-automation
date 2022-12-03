package com.blogspot.jvalentino.usnrauto.reportmerger.service

import java.io.File;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsELearningReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsGMTMemberReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.imr.ImrReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.individual.IndividualSummary;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.ManualInputReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.ruad.RuadReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.SummaryReport;

class CommandLineService {

	private ServiceBus bus = ServiceBus.getInstance()
	
	public void launchCommandLine(String[] args) {
		
		String lastItem = ""
		File ruadFile = null
		File imrFile = null
		File gmtFile = null
		File elearnFile = null
		File iaFile = null
		File manualFile = null
		
		for (int i = 0; i < args.length; i++) {
		
			if (lastItem.equalsIgnoreCase("-ruad")) {
				ruadFile = new File(args[i])
			} else if (lastItem.equalsIgnoreCase("-imr")) {
				imrFile = new File(args[i])
			} else if (lastItem.equalsIgnoreCase("-gmt")) {
				gmtFile = new File(args[i])
			} else if (lastItem.equalsIgnoreCase("-elearn")) {
				elearnFile = new File(args[i])
			} else if (lastItem.equalsIgnoreCase("-ia")) {
				iaFile = new File(args[i])
			} else if (lastItem.equalsIgnoreCase("-manual")) {
				manualFile = new File(args[i])
			}
			
			lastItem = args[i]
		}
		
		try {
			this.runSystem(ruadFile, imrFile, gmtFile, elearnFile, iaFile, manualFile)
		} catch (Exception e) {
			printUsage()
			println "Unable to do the report for the following reasons:"
			println " "
			e.printStackTrace()
		}
		
	}
	
	private void runSystem(File ruadFile, File imrFile, File gmtFile, File elearnFile,
		File iaFile, File manualFile) throws Exception {
		
		println "Parsing RUAD: " + ruadFile.path
		RuadReport ruad =
			bus.getRuadService().parseRuad(ruadFile)
		
			
		println "Parsing e-Learning: " + elearnFile.path
		FltMpsELearningReport eLearning =
			bus.getFltMpsService().parseELearning(elearnFile)
			
		println "Parsing GMT: " + gmtFile.path
		FltMpsGMTMemberReport gmt =
			bus.getFltMpsService().parseGMT(gmtFile)
		
		println "Parsing IA: " + iaFile.path
		FltMpsELearningReport ia =
			bus.getFltMpsService().parseIA(iaFile)
		
		println "Parsing IMR: " + imrFile.path
		ImrReport imr =
			bus.getIMRService().parse(imrFile)
		
		println "Parsing manual inputs: " + manualFile.path
		ManualInputReport manual =
			bus.getManualInputService().parse(manualFile)
		
		println "Merging all the reports together"
		MergedReport merged = bus.getMergeService().generateReport(ruad, eLearning, gmt, ia, imr, manual)
		
		println "Generating summary data"
		SummaryReport report = bus.getSummaryReportService().generateSummaryReport(merged, new Date())
		
		File output = new File("Generated Unit Tracker.xls")
		println "Geneating the unit summary at " + output.path
		bus.getSummaryReportService().generateSpreadsheet(output, report)
		
		File output2 = new File("Generated Individual Member Action Plan.pdf")
		println "Generating the individual member action plan at " + output2.path
		List<IndividualSummary> list = bus.getIndividualReportService().generateIndividualActionItems(
			bus.getMasCodeService(), report)
		bus.getIndividualReportService().generateMemberActionPdf(output2, list, report)
	}
	
	
	public void printUsage() {
		println "Correct Usage:"
		
		String message = "java -jar usnr-admin-automation.jar"
		message += " -ruad \"SmartRUAD_RUIC 88888.xlsx\""
		message += " -imr \"Nrrm System Report Individual Medical Readiness 12-11-2014.csv\""
		message += " -gmt \"GMTCourseCompletionStatus_120914.xlsx\""
		message += " -elearn \"ElearningStatus_120914.xlsx\""
		message += " -ia \"IndivAugTrngStat_120914.xlsx\""
		message += "- manual \"Manual Unit Inputs.xlsx\""
		
		println message
		
		println " "
		println "This will result in two things:"
		println "1. Generated Individual Member Action Plan.pdf"
		println "2. Generated Unit Tracker.xls"
		println " "
		
		
		println "ruad: Get an XLS file from exporting the NRRM SmartRaud, open it in Excel, and save it in XSLX format."
		println "imr: Get the CSV file from exporting the Indivudal Medical Readiness for your unit on NRRM."
		println "gmt: Get the XLS file from export GMT status on FLTMPS, open it in Excel, and save it in XSLX format"
		println "elearn: Get the XLS file from export e-Learning status on FLTMPS, open it in Excel, and save it in XSLX format"
		println "ia: Get the XLS file from export IA status on FLTMPS, open it in Excel, and save it in XSLX format"
		println "manaul: An XLSX file containing manual unit inputs, kept in the PII section of SharePoint"
		
	}
}
