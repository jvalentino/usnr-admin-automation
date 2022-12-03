package com.blogspot.jvalentino.usnrauto.reportmerger.view

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.blogspot.jvalentino.usnrauto.reportmerger.controller.ReportMergerController;
import com.blogspot.jvalentino.usnrauto.util.ViewUtil;


class ReportMergerView extends JPanel {

	private static final int FILE_FIELD_LENGTH = 50
	
	ReportMergerController controller
	
	JTextField ruadField = new JTextField(FILE_FIELD_LENGTH)
	JButton ruadBrowseButton = new JButton("Browse...")
	JLabel ruadLabel = new JLabel(" ")
	JButton ruadViewButton = new JButton("View")
	
	JTextField imrField = new JTextField(FILE_FIELD_LENGTH)
	JButton imrBrowseButton = new JButton("Browse...")
	JLabel imrLabel = new JLabel(" ")
	JButton imrViewButton = new JButton("View")
	
	JTextField gmtField = new JTextField(FILE_FIELD_LENGTH)
	JButton gmtBrowseButton = new JButton("Browse...")
	JLabel gmtLabel = new JLabel(" ")
	JButton gmtViewButton = new JButton("View")
	
	JTextField eLearnField = new JTextField(FILE_FIELD_LENGTH)
	JButton eLearnBrowseButton = new JButton("Browse...")
	JLabel eLeanLabel = new JLabel(" ")
	JButton eLearnViewButton = new JButton("View")
	
	JTextField iaField = new JTextField(FILE_FIELD_LENGTH)
	JButton iaBrowseButton = new JButton("Browse...")
	JLabel iaLabel = new JLabel(" ")
	JButton iaViewButton = new JButton("View")
	
	JTextField nrowsField = new JTextField(FILE_FIELD_LENGTH)
	JButton nrowsBrowseButton = new JButton("Browse...")
	JLabel nrowsLabel = new JLabel(" ")
	JButton nrowsViewButton = new JButton("View")
	
	JTextField esamsField = new JTextField(FILE_FIELD_LENGTH)
	JButton esamsBrowseButton = new JButton("Browse...")
	JLabel esamsLabel = new JLabel(" ")
	JButton esamsViewButton = new JButton("View")
	
	JTextField manualField = new JTextField(FILE_FIELD_LENGTH)
	JButton manualBrowseButton = new JButton("Browse...")
	JLabel manualLabel = new JLabel(" ")
	JButton manualViewButton = new JButton("View")
	
	JTextField summaryField = new JTextField(FILE_FIELD_LENGTH)
	JButton summaryBrowseButton = new JButton("Browse...")
	
	JTextField individualField = new JTextField(FILE_FIELD_LENGTH)
	JButton individualBrowseButton = new JButton("Browse...")
	
	JButton generateButton = new JButton("Generate Reports")
	JButton loadPreviousButton = new JButton("Load Previous Inputs")
	JButton notifyForTrainingButton = new JButton("Send Training Notifications")
	JButton notifyForMissingInputsButton = new JButton("Send Notifications for Missing Inputs")
	
	JButton openSummary = new JButton("Open")
	JButton openAction = new JButton("Open")
	
	JCheckBox updateHistory = new JCheckBox("Update History on Generate")
	JCheckBox idcrseReport = new JCheckBox("Generate IDC RSE Report Inputs with PDF")
	
	JButton historyButton = new JButton("View History")
	
	JButton libreBrowseButton = new JButton("Browse...")
	JTextField libreField = new JTextField(FILE_FIELD_LENGTH)
	

	ReportMergerView() {
		controller = new ReportMergerController(this)
		

		JPanel panel = this.constructView()
		this.add(panel)

		controller.viewConstructed()
		
		
	}

	private JPanel constructView() {
		JPanel panel = new JPanel()
		panel.setBorder(new EmptyBorder(10, 10, 10, 10) )
		panel.setLayout(new GridBagLayout())
		GridBagConstraints c = new GridBagConstraints()
		
		int row = 0
		c.fill = GridBagConstraints.HORIZONTAL

		c.gridwidth = 3
		c.gridx = 0
		c.gridy = row
		panel.add(this.createLibrePanel(), c)
		
		row++
		
		c.gridwidth = 3
		c.gridx = 0
		c.gridy = row
		JPanel inputPanel = this.constructInputs()
		panel.add(inputPanel, c)
		
		row++
		
		c.gridwidth = 3
		c.gridx = 0
		c.gridy = row
		JPanel outputPanel = this.constructOutputs()
		panel.add(outputPanel, c)
		
		row++
		
		// row with the generate button
		c.gridwidth = 3
		c.gridx = 0
		c.gridy = row
		JPanel centerPanel = this.createButtonPanel()
		panel.add(centerPanel, c);
		
		return panel
	}
	
	private JPanel createButtonPanel() {
		JPanel panel = new JPanel()
		panel.setBorder(new EmptyBorder(5, 5, 5, 5) )
		panel.setLayout(new GridBagLayout())
		GridBagConstraints c = new GridBagConstraints()
		
		//c.weightx = c.weighty = 1.0;
		int row = 0
		
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridx = 0
		c.gridy = row
		panel.add(loadPreviousButton, c)
		
		c.gridx = 1
		c.gridy = row
		panel.add(generateButton, c)
		
		c.gridx = 2
		c.gridy = row
		panel.add(updateHistory, c)
		
		c.gridx = 3
		c.gridy = row
		panel.add(new JLabel(""), c)
		
		row++
		
		c.gridx = 0
		c.gridy = row
		panel.add(this.idcrseReport, c)
		
		row++
		
		c.gridx = 0
		c.gridy = row
		panel.add(notifyForTrainingButton, c)
		
		c.gridx = 1
		c.gridy = row
		panel.add(notifyForMissingInputsButton, c)		
		
		c.gridx = 2
		c.gridy = row
		panel.add(historyButton, c)
		
		return panel
	}
	
	private JPanel createLibrePanel() {
		JPanel panel = new JPanel()
		panel.setLayout(new GridBagLayout())
		GridBagConstraints c = new GridBagConstraints()
		
		Border border = BorderFactory.createLineBorder(Color.black);
		panel.setBorder(BorderFactory.createTitledBorder(border, "Libre Office"));
		
		int row = 0
		c.fill = GridBagConstraints.HORIZONTAL
		
		row = ViewUtil.createUploadRow(
			"soffice",
			this.libreField,
			this.libreBrowseButton,
			"libreoffice.org: Used to convert XLS to XLSX files automatically. Without it you " +
			"will have to do it manually using Excel. The file is "+
			"LibreOffice.app on Mac on and soffice.exe on Windows.",
			c,
			panel,
			row,
			null,
			null)
		
		return panel
	}
	
	private JPanel constructInputs() {
		JPanel panel = new JPanel()
		panel.setLayout(new GridBagLayout())
		GridBagConstraints c = new GridBagConstraints()
		
		Border border = BorderFactory.createLineBorder(Color.black);
		panel.setBorder(BorderFactory.createTitledBorder(border, "Inputs"));
		
		int row = 0
		c.fill = GridBagConstraints.HORIZONTAL

		// RUAD
		row = ViewUtil.createUploadRow(
			"RUAD XLSX File",
			ruadField,
			ruadBrowseButton,
			"You get this report from NRRM by exporting the Smart RUAD and getting an XLS file." +
			" You then have open to it and save it XLSX format if you don't have LibreOffice.",
			c,
			panel,
			row,
			ruadLabel,
			ruadViewButton)
		
		// IMR
		row = ViewUtil.createUploadRow(
			"IMR CSV File",
			imrField,
			imrBrowseButton,
			"You get this report from NRRM by going to Individual Medical Readiness and doing an export. " +
			"The result will be a CSV file that contains your unit's individual IMR statuses.",
			c,
			panel,
			row,
			imrLabel,
			imrViewButton)
		
		// GMT
		row = ViewUtil.createUploadRow(
			"GMT XLSX File",
			gmtField,
			gmtBrowseButton,
			"You get this report from FLTMPS by exporting the GMT report an an XLS file." +
			" You then have open to it and save it XLSX format if you don't have LibreOffice.",
			c,
			panel,
			row,
			gmtLabel,
			gmtViewButton)
		
		// e-Learning
		row = ViewUtil.createUploadRow(
			"e-Learning XLSX File",
			eLearnField,
			eLearnBrowseButton,
			"You get this report from FLTMPS by exporting the e-Learning report an an XLS file." +
			" You then have open to it and save it XLSX format if you don't have LibreOffice.",
			c,
			panel,
			row,
			eLeanLabel,
			eLearnViewButton)
		
		// IA
		row = ViewUtil.createUploadRow(
			"IA XLSX File",
			iaField,
			iaBrowseButton,
			"You get this report from FLTMPS by exporting the Individual Augmentee report an an XLS file." +
			" You then have open to it and save it XLSX format if you don't have LibreOffice.",
			c,
			panel,
			row,
			iaLabel,
			iaViewButton)
		
		// NROWS
		row = ViewUtil.createUploadRow(
			"NROWS TXT File*",
			nrowsField,
			nrowsBrowseButton,
			"NROWS: Miscellaneous Report >> Selres Report. Report Type: CURRENT & HISTORICAL ORDERS. " +
			"Options: NAME, END DATE, DELIMITED.",
			c,
			panel,
			row,
			nrowsLabel,
			nrowsViewButton)
		
		// ESAMS
		row = ViewUtil.createUploadRow(
			"ESAMS XLSX File*",
			esamsField,
			esamsBrowseButton,
			"You get this report from ESAMS by exporting the course completions as an XLS file." +
			" You then have open to it and save it XLSX format if you don't have LibreOffice.",
			c,
			panel,
			row,
			esamsLabel,
			esamsViewButton)
		
		// manual
		row = ViewUtil.createUploadRow(
			"Manual XLSX File*",
			manualField,
			manualBrowseButton,
			"This is an XLSX file used to track manual inputs for your unit. This is to contain information" +
			" that cannot be retrieved easily from other systems.",
			c,
			panel,
			row,
			manualLabel,
			manualViewButton)
		
		return panel
	}
	
	private JPanel constructOutputs() {
		JPanel panel = new JPanel()
		panel.setLayout(new GridBagLayout())
		GridBagConstraints c = new GridBagConstraints()
		
		Border border = BorderFactory.createLineBorder(Color.black);
		panel.setBorder(BorderFactory.createTitledBorder(border, "Outputs"));
		
		int row = 0
		c.fill = GridBagConstraints.HORIZONTAL
		
		// summary
		row = ViewUtil.createUploadRow(
			"Unit Tracker",
			summaryField,
			summaryBrowseButton,
			"This is the location which the unit tracker will be exported. This is a report that combines " +
			"the information for each unit member from all of the other reports into a single spreadsheet.",
			c,
			panel,
			row,
			null,
			openSummary)
		
		// individual
		row = ViewUtil.createUploadRow(
			"Individual Member Action Plan",
			individualField,
			individualBrowseButton,
			"This is the location which the Individual Member Action Plan will be exported. This is a " +
			"PDF that uses one page per unit member to list everything that member needs to do.",
			c,
			panel,
			row,
			null,
			openAction)
		
		return panel
		
	}
	
	
}
