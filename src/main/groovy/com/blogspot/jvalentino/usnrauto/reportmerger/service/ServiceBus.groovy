package com.blogspot.jvalentino.usnrauto.reportmerger.service

import org.dozer.DozerBeanMapper;

import com.blogspot.jvalentino.usnrauto.commons.ExcelService;

/**
 * <p>This is a singleton wrapper for all of the various services.</p>
 * 
 * @author jvalentino2
 *
 */
class ServiceBus {

    private static ServiceBus instance;
    
    private static RuadService ruadService = new RuadService()
    private static FltMpsService fltMpsService = new FltMpsService()
    private static MergeService mergeService = new MergeService()
    private static IndividualReportService individualReportService = new IndividualReportService()
    private static MasCodeService masCodeService = new MasCodeService()
    private static IMRService imrService = new IMRService()
    private static SummaryReportService summaryReportService = new SummaryReportService()
	private static ManualInputService manualInputService = new ManualInputService()
	private static CommandLineService commandLineService = new CommandLineService()
	private static ExcelService excelService = new ExcelService()
	private static TrainingNagService trainingNagService = new TrainingNagService()
	private static HistoryService historyService = new HistoryService()
	private static IDCReportService idcReportService = new IDCReportService()
	private static NrowsService nrowsService = new NrowsService()
	private static EsamsService esamsService = new EsamsService()
    
    private ServiceBus() {
        
    }
    
    static ServiceBus getInstance() {
        if (instance == null) {
            instance = new ServiceBus()
        }
        return instance
    }
    
    RuadService getRuadService() {
        return ruadService
    }
    
    FltMpsService getFltMpsService() {
        return fltMpsService
    }
    
    MergeService getMergeService() {
        return mergeService
    }
    
    IndividualReportService getIndividualReportService() {
        return individualReportService
    }
    
    MasCodeService getMasCodeService() {
        return masCodeService
    }
    
    IMRService getIMRService() {
        return imrService
    }
    
    SummaryReportService getSummaryReportService() {
        return summaryReportService
    }
	
	ManualInputService getManualInputService() {
		return manualInputService
	}
	
	CommandLineService getCommandLineService() {
		return commandLineService
	}
	
	ExcelService getExcelService() {
		return excelService
	}
	
	TrainingNagService getTrainingNagService() {
		return trainingNagService
	}
	
	HistoryService getHistoryService() {
		return historyService
	}
	
	IDCReportService getIDCReportService() {
		return idcReportService
	}
	
	NrowsService getNrowsService() {
		return nrowsService
	}
	
	EsamsService getEsamsService() {
		return esamsService
	}
    
}
