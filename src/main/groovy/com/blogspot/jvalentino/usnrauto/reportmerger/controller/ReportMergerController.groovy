package com.blogspot.jvalentino.usnrauto.reportmerger.controller

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SystemUtils;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.esams.EsamsReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsELearningReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsGMTMemberReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.idcreport.IDCReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.imr.ImrReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.individual.IndividualSummary;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.ManualInputReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.nrows.NrowsRawReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.ruad.RuadReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.MemberWithMissingData;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.SummaryReport;
import com.blogspot.jvalentino.usnrauto.main.controller.BaseController;
import com.blogspot.jvalentino.usnrauto.reportmerger.service.ServiceBus;
import com.blogspot.jvalentino.usnrauto.component.CustomFileFilter;
import com.blogspot.jvalentino.usnrauto.component.FileByNameFilter;
import com.blogspot.jvalentino.usnrauto.component.JProgressDialog;
import com.blogspot.jvalentino.usnrauto.component.generaltable.GeneralTableView;
import com.blogspot.jvalentino.usnrauto.reportmerger.model.ReportMergerModel;
import com.blogspot.jvalentino.usnrauto.reportmerger.view.MissingInputsView;
import com.blogspot.jvalentino.usnrauto.reportmerger.view.ReportMergerView;
import com.blogspot.jvalentino.usnrauto.reportmerger.view.TrainingNagView;
import com.blogspot.jvalentino.usnrauto.util.CommandLineUtil;
import com.blogspot.jvalentino.usnrauto.util.OsType;


class ReportMergerController extends BaseController {
	private ServiceBus bus = ServiceBus.getInstance()
	private ReportMergerView view
	private ReportMergerModel model
	
	private static final LAST_RUAD_FILE = "LAST_RUAD_FILE"
	private static final LAST_IMR_FILE = "LAST_IMR_FILE"
	private static final LAST_GMT_FILE = "LAST_GMT_FILE"
	private static final LAST_ELEARN_FILE = "LAST_ELEARN_FILE"
	private static final LAST_IA_FILE = "LAST_IA_FILE"
	private static final LAST_MANUAL_FILE = "LAST_MANUAL_FILE"
	private static final LAST_IDCRSE_PREF = "LAST_IDCRSE_PREF"
	private static final LAST_NROWS_FILE = "LAST_NROWS_FILE"
	private static final LAST_ESAMS_FILE = "LAST_ESAMS_FILE"
	
	private boolean loadLastInputs = false
	private int totalInputs = 8
	private int inputLoadCount = 0
	
	ReportMergerController(ReportMergerView view) {
		this.view = view
		this.model = new ReportMergerModel()
	}
	
	void viewConstructed() {
		
		// disable all of the text fields
		view.getRuadField().setEnabled(false)
		view.getGmtField().setEnabled(false)
		view.getImrField().setEnabled(false)
		view.getIaField().setEnabled(false)
		view.getManualField().setEnabled(false)
		view.geteLearnField().setEnabled(false)
		view.getIndividualField().setEnabled(false)
		view.getSummaryField().setEnabled(false)
		view.getNrowsField().setEnabled(false)
		view.getEsamsField().setEnabled(false)
		
		view.getGenerateButton().setEnabled(false)
		view.openSummary.setEnabled(false)
		view.openAction.setEnabled(false)
		view.notifyForTrainingButton.setEnabled(false)
		
		view.ruadViewButton.setEnabled(false)
		view.gmtViewButton.setEnabled(false)
		view.imrViewButton.setEnabled(false)
		view.iaViewButton.setEnabled(false)
		view.manualViewButton.setEnabled(false)
		view.eLearnViewButton.setEnabled(false)
		view.updateHistory.setEnabled(false)
		view.historyButton.setEnabled(false)
		view.notifyForMissingInputsButton.setEnabled(false)
		view.nrowsViewButton.setEnabled(false)
		view.esamsViewButton.setEnabled(false)
		
		// add listeners to button
		final ReportMergerController me = this
		
		view.getRuadBrowseButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.browseForRuad()
			}
		});
	
		view.getImrBrowseButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.browseForImr()
			}
		});
	
		view.getGmtBrowseButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.browseForGmt()
			}
		});
	
		view.eLearnBrowseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.browseForELearn()
			}
		});
	
		view.getIaBrowseButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.browseForIa()
			}
		});
	
		view.getManualBrowseButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.browseForManual()
			}
		});
	
		view.nrowsBrowseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.browseForNrows()
			}
		});
	
		view.esamsBrowseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.browseForEsams()
			}
		});
		
		view.getSummaryBrowseButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.browseForSummary()
			}
		});
	
		view.getIndividualBrowseButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.browseForIndividual()
			}
		});
	
		view.getGenerateButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.generateReports()
			}
		});
	
		view.openSummary.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.openSummary()
			}
		});
	
		view.openAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.openAction()
			}
		});
	
		// deal with viewing data
		view.ruadViewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.viewTableData("Loaded RUAD Data", model.ruadReport.members)
			}
		});
	
		view.gmtViewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.viewTableData("Loaded GMT Data", model.gmtReport.members)
			}
		});
	
		view.imrViewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.viewTableData("Loaded IMR Data", model.imrReport.members)
			}
		});
	
		view.iaViewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.viewTableData("Loaded IA Data", model.iaReport.members)
			}
		});
	
		view.eLearnViewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.viewTableData("Loaded e-Learning Data", model.eLearnReport.members)
			}
		});
	
		view.manualViewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.viewTableData("Loaded Manual Data", model.manualReport.members)
			}
		});
	
		view.nrowsViewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.viewTableData("Loaded NROWS Data", model.nrowsReport.entries)
			}
		});
	
		view.esamsViewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.viewTableData("Loaded ESAMS Data", model.esamsReport.records)
			}
		});
	
		view.loadPreviousButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.loadPreviousInputs()
			}
		});
	
		view.notifyForTrainingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.displayNotifyForTraining()
			}
		});
	
		view.libreBrowseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.browseForLibre()
			}
		});
	
		view.historyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.viewHistory()
			}
		});
	
		view.notifyForMissingInputsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.notifyForMissingInputs()
			}
		});
		
		// Default the location of the outputs
		model.userHome = SystemUtils.getUserHome()
		
		File individual = new File(model.userHome.path +
			File.separator + "Generated Individual Member Action Plan.pdf")
		this.setIndividualFile(individual)
		
		File summary = new File(model.userHome.path + 
			File.separator + "Generated Unit Tracker.xls")
		this.setSummaryFile(summary)
		
		this.handleInitialLibreSettings();
		
		this.handleIdcReportChecked()
		
		view.idcrseReport.setEnabled(false)
				
	}
	
	private void handleIdcReportChecked() {
		boolean checked = this.getPreferenceAsBoolean(LAST_IDCRSE_PREF)
		view.idcrseReport.setSelected(checked)
	}
	
	/**
	 * Handles finding libre office settings and validating them
	 */
	private void handleInitialLibreSettings() {
		view.libreField.setEnabled(false)
		this.validateLibreSettings()
	}
	
	private void validateLibreSettings() {
		File found = bus.getExcelService().searchForLibreOfficeBinary()
		
		if (found != null) {
			view.libreField.setText(found.getAbsolutePath())
			model.libreOffice = true
		} else {
			model.libreOffice = false
		}
	}
	
	private void browseForLibre() {
				
		String fileName 
		
		if (CommandLineUtil.getOsType() == OsType.WINDOWS) {
			fileName = bus.getExcelService().getLibreOfficeBinary()
		} else {
			fileName = "LibreOffice.app"
		}
		
		final JFileChooser fc = new JFileChooser(getLastUserFolder())
		fc.setDialogTitle("Select the binary for Libre Office called " + fileName)
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileByNameFilter(fileName));
		
		int returnVal = fc.showOpenDialog(null)
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file =  fc.getSelectedFile()
			
			// weird case for a mac, because we can't browse into an app
			if (CommandLineUtil.getOsType() == OsType.MAC &&
				file.getName().equalsIgnoreCase("LibreOffice.app")) {
				
				file = new File(file.getAbsolutePath() + "/Contents/MacOS/soffice")
			}
			
			bus.getExcelService().storeLastUserLibreOfficeBinary(file)
			validateLibreSettings()
		} 
	}
	
	private void setIndividualFile(File file) {
		model.individualOutput = file
		view.getIndividualField().setText(model.individualOutput.path)
	}
	
	private void setSummaryFile(File file) {
		model.summaryOutput = file
		view.getSummaryField().setText(model.summaryOutput.path)
	}
	
	/**
	 * Returns the available spreadsheet extensions that can be loaded,
	 * which depends on whether or not Libre Office is installed and available
	 * @return
	 */
	private String[] getAvailableSpreadsheetExtensions() {
		String[] result = null
		if (model.libreOffice) {
			 result = ["xls", "xlsx"]
		} else {
			result = ["xlsx"]
		}
		return result
	}
	
	private void convertUsingLibreOffice(String title, Closure callback, file) {
		ReportMergerController me = this
		
		me.showModalInThread("Converting XLS to XLSX using LibreOffice")
		
		(new Thread() {
			public void run() {
				try {
					
					File libreOffice = bus.getExcelService().getLastUsedLibreOfficeBinary()
					File result = bus.getExcelService().convertXlsToXlsx(libreOffice, file)
					me.hideModalFromThread()
					callback(result)
				} catch (Exception e) {
					me.hideModalFromThread()
					me.showExceptionInDialog(e)
					callback(null)
				}
							
			}
		}).start()
	}
	
	/**
	 * 	In a thread this method will prompt the user to browser for available
	 * spreadsheet types based on whether or not Libre Office is available. If libre 
	 * office is available the user can browse for the XLS type. If an XLS type is selected
	 * Libre Office is used to convert that XLS file to XLSX.
	 * Whatever the result, the callback is called with the appropriate file.
	 * Case 1: If no libre office the XLSX file selected
	 * Case 2: If libre office then the XLS is converted to XLSX and the XLSX is returned
	 * Case 3: Use cancels, null is returnd
	 */
	protected void browseForExcelAndUpgradeIfAvailable(String title, Closure callback) {
		ReportMergerController me = this
				
		File file = me.browseForFileType(me.getAvailableSpreadsheetExtensions(), title)
		
		if (file == null) {
			callback(null)
			return
		}
		
		String extension = FilenameUtils.getExtension(file.getName())
		
		// if the extension is old excel, convert it
		if (extension.equalsIgnoreCase("xls")) {
	
			convertUsingLibreOffice(title, callback, file)
			
		} else if (extension.equalsIgnoreCase("xlsx")) {
			// the file is xlsx, we have to make sure it is valid
			ZipFile zipFile = new ZipFile(file);
			
			Enumeration zipEntries = zipFile.entries();
			
			boolean docProps = false
			
			while (zipEntries.hasMoreElements()) {
				
				//Process the name, here we just print it out
				String entryName = ((ZipEntry)zipEntries.nextElement()).getName()
				
				if (entryName.startsWith("docProps")) {
					docProps = true
					break
				}
				
			}
			
			// if this is a valid XLSX file
			if (docProps) {
				callback(file)
			} else {
				// you have to convert it
				convertUsingLibreOffice(title, callback, file)
			}
			
			
		} else {
			callback(file)
		}
				
	}
	
	/**
	 * Handles when the button is pressed to select a RUAD
	 */
	private void browseForRuad() {
		browseForExcelAndUpgradeIfAvailable("Select a Smart RUAD", this.&loadRuadFromFile)		
	}
	
	private void loadRuadFromFile(File file) {
		if (file == null) {
			loadNextInput()
			return
		}
		
		this.storeFileAsPreference(LAST_RUAD_FILE, file)
		
		ReportMergerController me = this
		
		this.showModalInThread("Loading RUAD")
		
		(new Thread() {
			public void run() {
				try {
					RuadReport ruad = bus.getRuadService().parseRuad(file)
					me.setRuadData(file, ruad)
				} catch (Exception e) {
					me.showExceptionInDialog(e)
					me.setRuadData(null, null)
				} finally {
					me.hideModalFromThread()
					me.loadNextInput()
				}
							
			}
		}).start()
	}
	
	/**
	 * Handles when RUAD data is selected or fails to be selected due to an error
	 * 
	 * @param file
	 * @param ruad
	 */
	private void setRuadData(File file, RuadReport ruad) {
		model.ruadFile = file
		model.ruadReport = ruad
		if (file != null) {
			view.getRuadField().setText(file.path)
			view.getRuadLabel().setForeground(Color.green.darker())
			view.getRuadLabel().setText("RUAD contains " + ruad.members.size() + " members")
			view.ruadViewButton.setEnabled(true)
		} else {
			view.getRuadField().setText("")
			view.getRuadLabel().setForeground(Color.red.darker())
			view.getRuadLabel().setText("There was an error loading the RUAD")
			view.ruadViewButton.setEnabled(false)
		}
		
		enableGenerateButtonIfPossible()
	}
	
	// IMR
	
	/**
	 * Handles when the browser button is pressed to select the IMR CSV file
	 */
	private void browseForImr() {
		File file = this.browseForFileType("csv", "Select an IMR report in CSV format")
		loadImrFromFile(file)
	}
	
	private void loadImrFromFile(File file) {
		if (file == null) {
			loadNextInput()
			return
		}
		
		this.storeFileAsPreference(LAST_IMR_FILE, file)
		
		ReportMergerController me = this
				
		this.showModalInThread("Loading IMR Data")
		
		(new Thread() {
			public void run() {
				try {
					ImrReport imr = bus.getIMRService().parse(file)
					me.setImrData(file, imr)
				} catch (Exception e) {
					me.showExceptionInDialog(e)
					me.setImrData(null, null)
				} finally {
					me.hideModalFromThread()
					me.loadNextInput()
				}
							
			}
		}).start()
	}
	
	/**
	 * Handles setting the data for the IMR report
	 * 
	 * @param file
	 * @param imr
	 */
	private void setImrData(File file, ImrReport imr) {
		model.imrFile = file
		model.imrReport = imr
		if (file != null) {
			view.getImrField().setText(file.path)
			view.getImrLabel().setForeground(Color.green.darker())
			view.getImrLabel().setText("IMR contains " + imr.members.size() + " members")
			view.imrViewButton.setEnabled(true)
		} else {
			view.getImrField().setText("")
			view.getImrLabel().setForeground(Color.red.darker())
			view.getImrLabel().setText("There was an error loading the IMR")
			view.imrViewButton.setEnabled(false)
		}
		
		enableGenerateButtonIfPossible()
	}
	
	// GMT
	
	/**
	 * Called when the browse button is pressed to select a GMT XLSX file
	 */
	private void browseForGmt() {
		browseForExcelAndUpgradeIfAvailable("Select the FLTMPS GMT report", this.&loadGmtFromFile)
	}
	
	private void loadGmtFromFile(File file) {
		if (file == null) {
			loadNextInput()
			return
		}
		
		this.storeFileAsPreference(LAST_GMT_FILE, file)
		
		ReportMergerController me = this
				
		this.showModalInThread("Loading FLTMPS GMT Data")
		
		(new Thread() {
			public void run() {
				try {
					FltMpsGMTMemberReport gmt = bus.getFltMpsService().parseGMT(file)
					me.setGmtData(file, gmt)
				} catch (Exception e) {
					me.showExceptionInDialog(e)
					me.setGmtData(null, null)
				} finally {
					me.hideModalFromThread()
					me.loadNextInput()
				}
							
			}
		}).start()
	}
	
	/**
	 * Handles setting the GMT data once the file is loaded
	 * @param file
	 * @param gmt
	 */
	private void setGmtData(File file, FltMpsGMTMemberReport gmt) {
		model.gmtFile = file
		model.gmtReport = gmt
		if (file != null) {
			view.getGmtField().setText(file.path)
			view.getGmtLabel().setForeground(Color.green.darker())
			view.getGmtLabel().setText("GMT data contains " + gmt.members.size() + " members")
			view.gmtViewButton.setEnabled(true)
		} else {
			view.getGmtField().setText("")
			view.getGmtLabel().setForeground(Color.red.darker())
			view.getGmtLabel().setText("There was an error loading the GMT Data")
			view.gmtViewButton.setEnabled(false)
		}
		
		enableGenerateButtonIfPossible()
	}
	
	// E learing
	
	/**
	 * Handles when the browse button is pressed for selecting the FLTMPS elearning data
	 */
	private void browseForELearn() {
		browseForExcelAndUpgradeIfAvailable("Select the FLTMPS e-Learning report", this.&loadELearnFromFile)
	}
	
	private void loadELearnFromFile(File file) {
		if (file == null) {
			loadNextInput()
			return
		}
		
		this.storeFileAsPreference(LAST_ELEARN_FILE, file)
		
		ReportMergerController me = this
				
		this.showModalInThread("Loading FLTMPS e-Learning Data")
		
		(new Thread() {
			public void run() {
				try {
					FltMpsELearningReport elearn = bus.getFltMpsService().parseELearning(file)
					me.setELearnData(file, elearn)
				} catch (Exception e) {
					me.showExceptionInDialog(e)
					me.setELearnData(null, null)
				} finally {
					me.hideModalFromThread()
					me.loadNextInput()
				}
							
			}
		}).start()
	}
	
	/**
	 * Handles loading the FLTMPS e-learning data
	 * 
	 * @param file
	 * @param elearn
	 */
	private void setELearnData(File file, FltMpsELearningReport elearn) {
		model.eLearnFile = file
		model.eLearnReport = elearn
		if (file != null) {
			view.geteLearnField().setText(file.path)
			view.geteLeanLabel().setForeground(Color.green.darker())
			view.geteLeanLabel().setText("e-Learning data contains " + elearn.members.size() + " members")
			view.eLearnViewButton.setEnabled(true)
		} else {
			view.geteLearnField().setText("")
			view.geteLeanLabel().setForeground(Color.red.darker())
			view.geteLeanLabel().setText("There was an error loading the e-Learning Data")
			view.eLearnViewButton.setEnabled(true)
		}
		
		enableGenerateButtonIfPossible()
	}
	
	// IA
	
	/**
	 * Handles when the browse button is pressed to select the FLTMPS XLSX file
	 */
	private void browseForIa() {
		browseForExcelAndUpgradeIfAvailable("Select the FLTMPS IA report", this.&loadIaFromFile)
	}
	
	private void loadIaFromFile(File file) {
		if (file == null) {
			loadNextInput()
			return
		}
		
		this.storeFileAsPreference(LAST_IA_FILE, file)
		
		ReportMergerController me = this
				
		this.showModalInThread("Loading FLTMPS IA Data")
		
		(new Thread() {
			public void run() {
				try {
					FltMpsELearningReport ia = bus.getFltMpsService().parseIA(file)
					me.setIaData(file, ia)
				} catch (Exception e) {
					me.showExceptionInDialog(e)
					me.setIaData(null, null)
				} finally {
					me.hideModalFromThread()
					me.loadNextInput()
				}
							
			}
		}).start()
	}
	
	/**
	 * Handles loading FLTMPS IA data
	 * 
	 * @param file
	 * @param ia
	 */
	private void setIaData(File file, FltMpsELearningReport ia) {
		model.iaFile = file
		model.iaReport = ia
		if (file != null) {
			view.getIaField().setText(file.path)
			view.getIaLabel().setForeground(Color.green.darker())
			view.getIaLabel().setText("IA data contains " + ia.members.size() + " members")
			view.iaViewButton.setEnabled(true)
		} else {
			view.getIaField().setText("")
			view.getIaLabel().setForeground(Color.red.darker())
			view.getIaLabel().setText("There was an error loading the IA Data")
			view.iaViewButton.setEnabled(false)
		}
		
		enableGenerateButtonIfPossible()
	}
	
	// MANUAL INPUTS
	
	private void browseForManual() {
		File file = this.browseForFileType("xlsx", "Select the Manual Unit Inputs")
		loadManualFromFile(file)
	}
	
	private void loadManualFromFile(File file) {
		if (file == null) {
			loadNextInput()
			return
		}
		
		this.storeFileAsPreference(LAST_MANUAL_FILE, file)
		
		ReportMergerController me = this
				
		this.showModalInThread("Loading Manual Inputs")
		
		(new Thread() {
			public void run() {
				try {
					ManualInputReport manual = bus.getManualInputService().parse(file)
					me.setManualData(file, manual)
				} catch (Exception e) {
					me.showExceptionInDialog(e)
					me.setManualData(null, null)
				} finally {
					me.hideModalFromThread()
					me.loadNextInput()
				}
							
			}
		}).start()
	}
	
	private void setManualData(File file, ManualInputReport manual) {
		model.manualFile = file
		model.manualReport = manual
		if (file != null) {
			view.getManualField().setText(file.path)
			view.getManualLabel().setForeground(Color.green.darker())
			view.getManualLabel().setText("Manual data contains " + manual.members.size() + " members")
			view.manualViewButton.setEnabled(true)
		} else {
			view.getManualField().setText("")
			view.getManualLabel().setForeground(Color.red.darker())
			view.getManualLabel().setText("There was an error loading the Manual Data")
			view.manualViewButton.setEnabled(false)
		}
		
		enableGenerateButtonIfPossible()
	}
	
	// NROWS
	
	private void browseForNrows() {
		File file = this.browseForFileType("txt", "Select the NROWS Text File")
		loadNrowsFromFile(file)
	}
	
	private void loadNrowsFromFile(File file) {
		if (file == null) {
			loadNextInput()
			return
		}
		
		this.storeFileAsPreference(LAST_NROWS_FILE, file)
		
		ReportMergerController me = this
				
		this.showModalInThread("Loading NROWS Data")
		
		(new Thread() {
			public void run() {
				try {
					NrowsRawReport report = bus.getNrowsService().parse(file)
					me.setNrowsRawReport(file, report)
				} catch (Exception e) {
					me.showExceptionInDialog(e)
					me.setNrowsRawReport(null, null)
				} finally {
					me.hideModalFromThread()
					me.loadNextInput()
				}
							
			}
		}).start()
	}
	
	private void setNrowsRawReport(File file, NrowsRawReport report) {
		model.nrowsFile = file
		model.nrowsReport = report
		if (file != null) {
			view.getNrowsField().setText(file.path)
			view.getNrowsLabel().setForeground(Color.green.darker())
			view.getNrowsLabel().setText("NROWS data contains " + report.entries.size() + " orders")
			view.nrowsViewButton.setEnabled(true)
		} else {
			view.getNrowsField().setText("")
			view.getNrowsLabel().setForeground(Color.red.darker())
			view.getNrowsLabel().setText("There was an error loading the NROWS Data")
			view.nrowsViewButton.setEnabled(false)
		}
		
		enableGenerateButtonIfPossible()
	}
	
	// ESAMS
	
	private void browseForEsams() {
		browseForExcelAndUpgradeIfAvailable("Select the ESAMS report", this.&loadEsamsFromFile)
	}
	
	private void loadEsamsFromFile(File file) {
		if (file == null) {
			loadNextInput()
			return
		}
		
		this.storeFileAsPreference(LAST_ESAMS_FILE, file)
		
		ReportMergerController me = this
				
		this.showModalInThread("Loading ESAMS Data")
		
		(new Thread() {
			public void run() {
				try {
					EsamsReport ia = bus.getEsamsService().parse(file)
					me.setEsamsData(file, ia)
				} catch (Exception e) {
					me.showExceptionInDialog(e)
					me.setEsamsData(null, null)
				} finally {
					me.hideModalFromThread()
					me.loadNextInput()
				}
							
			}
		}).start()
	}
	
	private void setEsamsData(File file, EsamsReport ia) {
		model.esamsFile = file
		model.esamsReport = ia
		if (file != null) {
			view.getEsamsField().setText(file.path)
			view.getEsamsLabel().setForeground(Color.green.darker())
			view.getEsamsLabel().setText("ESAMS data contains " + ia.records.size() + " records")
			view.esamsViewButton.setEnabled(true)
		} else {
			view.getEsamsField().setText("")
			view.getEsamsLabel().setForeground(Color.red.darker())
			view.getEsamsLabel().setText("There was an error loading the ESAMS Data")
			view.esamsViewButton.setEnabled(false)
		}
		
		enableGenerateButtonIfPossible()
	}
	
	// SUMMARY
	
	private void browseForSummary() {
		File file = this.browseForFileSave(
			"Generated Unit Tracker.xls", 
			"Select a location for the Summary report output")
		this.setSummaryFile(file)
	}
	
	private void browseForIndividual() {
		File file = this.browseForFileSave(
			"Generated Individual Member Action Plan.pdf",
			"Select a location for the individual action plan output")
		this.setIndividualFile(file)
	}
	
	private void enableGenerateButtonIfPossible() {
		if (model.eLearnReport == null) {
			return
		}
		
		if (model.gmtReport == null) {
			return
		}
		
		if (model.iaReport == null) {
			return
		}
		
		if (model.imrReport == null) {
			return
		}
		
		if (model.ruadReport == null) {
			return
		}
		
		view.getGenerateButton().setEnabled(true)
		
		view.idcrseReport.setEnabled(true)
		
		if (model.manualReport != null) {
			view.updateHistory.setEnabled(true)
		} else {
			view.updateHistory.setEnabled(false)
		}
			
	}
	
	private void generateReports() {
		ReportMergerController me = this
		
		final boolean updateHistory = view.updateHistory.isSelected()
		final boolean generateIdcrseReport = view.idcrseReport.isSelected()
		
		this.showModalInThread("Generating Reports")
		
		(new Thread() {
			public void run() {
				boolean success = true
				try {
					MergedReport merged = bus.getMergeService().generateReport(
						model.ruadReport, 
						model.eLearnReport, 
						model.gmtReport, 
						model.iaReport, 
						model.imrReport,
						model.manualReport,
						model.nrowsReport,
						model.esamsReport)
					
					model.mergedReport = merged
					
					SummaryReport summary = bus.getSummaryReportService().generateSummaryReport(
						merged, new Date())
					model.summaryReport = summary
					
					bus.getSummaryReportService().generateSpreadsheet(
						model.summaryOutput, summary)
					
					List<IndividualSummary> list = bus.getIndividualReportService().generateIndividualActionItems(
						bus.getMasCodeService(), summary)
					model.individualSummaries = list
					
					String idcRseReportText = null
					
					if (generateIdcrseReport) {
						IDCReport idc = ServiceBus.getInstance().getIDCReportService().generateData(summary)
						idcRseReportText = ServiceBus.getInstance().getIDCReportService().generateText(idc)
					}
					
					bus.getIndividualReportService().generateMemberActionPdf(
						model.individualOutput, list, summary, idcRseReportText)
					
					if (updateHistory) {
						println "Updating history in manual inputs"
						// add the history to the manual inputs
						bus.getHistoryService().outputHistoryToSpreadsheet(
							model.manualFile, summary.history)
					} else {
						println "Not updating history in manual inputs"
					}
					
				} catch (Exception e) {
					success = false
					me.showExceptionInDialog(e)
				} finally {
					me.hideModalFromThread()
				}
				
				if (success) {
					JOptionPane.showMessageDialog(view,
						"The following reports were generated:\n\n" +
						model.summaryOutput.path + "\n" +
						model.individualOutput.path
					);
					
					view.notifyForTrainingButton.setEnabled(model.manualReport != null)
					view.openSummary.setEnabled(true)
					view.openAction.setEnabled(true)
					view.historyButton.setEnabled(model.manualReport != null)
					view.notifyForMissingInputsButton.setEnabled(model.manualReport != null)
					
					// store the state of idcrse report generation
					me.storeBooleanAsPreference(LAST_IDCRSE_PREF, generateIdcrseReport) 
					
				} else {
					view.openSummary.setEnabled(false)
					view.openAction.setEnabled(false)
					view.notifyForTrainingButton.setEnabled(false)
					view.historyButton.setEnabled(false)
					view.notifyForMissingInputsButton.setEnabled(false)
				}
							
			}
		}).start()
	}
	
	private void openSummary() {
		try {
			CommandLineUtil.open(model.summaryOutput)
		} catch (Exception e) {
			this.showExceptionInDialog(e)
		}
	}
	
	private void openAction() {
		try {
			CommandLineUtil.open(model.individualOutput)
		} catch (Exception e) {
			this.showExceptionInDialog(e)
		}
	}
	
	private void viewTableData(String title, List<Object> list) {
		GeneralTableView dialog = new GeneralTableView(null, title, list)
		dialog.setVisible(true)
	}
	
	private void loadPreviousInputs() {
		loadLastInputs = true
		
		model.ruadFile = null
		model.eLearnFile = null
		model.gmtFile = null
		model.iaFile = null
		model.imrFile = null
		model.manualFile = null
		model.nrowsFile = null
		
		loadNextInput()
	}
	
	private void loadNextInput() {
		if (!loadLastInputs) {
			return
		}
		
		inputLoadCount++
		
		// if all 6 have been loaded, stop
		if (inputLoadCount == totalInputs) {
			loadLastInputs = false
			println "All inputs have been loaded"
		}
		
		if (model.ruadFile == null) {
			this.loadRuadFromFile(getPreferenceAsFile(LAST_RUAD_FILE))
		} else if (model.eLearnFile == null) {
			this.loadELearnFromFile(getPreferenceAsFile(LAST_ELEARN_FILE))
		} else if (model.gmtFile == null) {
			this.loadGmtFromFile(getPreferenceAsFile(LAST_GMT_FILE))
		} else if (model.iaFile == null) {
			this.loadIaFromFile(getPreferenceAsFile(LAST_IA_FILE))
		} else if (model.imrFile == null) {
			this.loadImrFromFile(getPreferenceAsFile(LAST_IMR_FILE))
		} else if (model.nrowsFile == null) {
			this.loadNrowsFromFile(getPreferenceAsFile(LAST_NROWS_FILE))
		} else if (model.manualFile == null) {
			this.loadManualFromFile(getPreferenceAsFile(LAST_MANUAL_FILE))
		} else if (model.esamsFile == null) {
			this.loadEsamsFromFile(getPreferenceAsFile(LAST_ESAMS_FILE))
		}
	}
	
	private void displayNotifyForTraining() {
		TrainingNagView view = new TrainingNagView(null ,model.summaryReport, model.individualSummaries)
		view.setVisible(true)
	}
	
	private void viewHistory() {
		JDialog dialog = new JDialog() 
		
		JFreeChart chart = bus.getHistoryService().createHistoryChart(
			model.summaryReport.history, 
			model.summaryReport.nrowsFile != null, model.summaryReport.esamsFile != null)
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(500, 270));
		
		dialog.setContentPane(chartPanel)
		
		dialog.pack();
		dialog.setLocationRelativeTo(view);
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true)
	}
	
	private void notifyForMissingInputs() {
		List<MemberWithMissingData> list = bus.getSummaryReportService().assembleMissingInformation(
			model.summaryReport)
		MissingInputsView dialog = new MissingInputsView(null, model.summaryReport, list)
		dialog.setVisible(true)
	}
	
}
