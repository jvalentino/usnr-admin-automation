package com.blogspot.jvalentino.usnrauto.reportmerger.model

import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.esams.EsamsReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsELearningReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsGMTMemberReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.imr.ImrReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.individual.IndividualSummary;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.ManualInputReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.nrows.NrowsRawReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.ruad.RuadReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.SummaryReport;

class ReportMergerModel {
	/** The location of the user home directory on this computer */
	File userHome
	/** The specified location to output the summary XLS file */
	File summaryOutput
	/** The specified location to output the action plan PDF */
	File individualOutput
	/** The selected location of the RUAD XLSX */
	File ruadFile
	/** The report that was generated from the RUAD XLSX */
	RuadReport ruadReport
	/** The selected location of the IMR CSV */
	File imrFile
	/** The report generated from the selected IMR CSV */
	ImrReport imrReport
	/** The selected location of the GMT XLSX file */
	File gmtFile
	/** The report generated from the GMT file */
	FltMpsGMTMemberReport gmtReport
	/** The selected file for the FLTMPS eLearn data */
	File eLearnFile
	/** The report generated from the FLTMPS eLearn file */
	FltMpsELearningReport eLearnReport
	/** The selected location of the IA file */
	File iaFile
	/** Report generated from the IA file */
	FltMpsELearningReport iaReport
	/** The selected file for the manual unit inputs */
	File manualFile
	/** Report generated from the manual inputs */
	ManualInputReport manualReport
	File nrowsFile
	NrowsRawReport nrowsReport
	File esamsFile
	EsamsReport esamsReport
	
	MergedReport mergedReport
	SummaryReport summaryReport
	List<IndividualSummary> individualSummaries
	
	boolean libreOffice = false
}
