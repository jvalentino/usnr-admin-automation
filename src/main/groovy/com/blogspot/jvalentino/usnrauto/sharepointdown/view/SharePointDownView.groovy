package com.blogspot.jvalentino.usnrauto.sharepointdown.view

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.blogspot.jvalentino.usnrauto.sharepointdown.controller.Controller;

class SharePointDownView  extends JPanel {

	private static final int FILE_FIELD_LENGTH = 50
	
	private Controller controller
	
	JTextField urlField = new JTextField(FILE_FIELD_LENGTH)
	JTextField localField = new JTextField(FILE_FIELD_LENGTH)
	JButton browseButton = new JButton("Browse...")
	JButton addButton = new JButton("Add Location")
	JTable table = new JTable()
	JButton downloadButton = new JButton("Start Download")
	
	SharePointDownView() {
		this.controller = new Controller(this)
		this.construct()
		this.controller.viewContructed()
	}

	private void construct() {
		
		this.setBorder(new EmptyBorder(10, 10, 10, 10) )
		this.setLayout(new GridBagLayout())
		GridBagConstraints c = new GridBagConstraints()
		
		int row = 0
		c.fill = GridBagConstraints.HORIZONTAL
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridx = 0
		
		//c.gridwidth = 1
		//c.gridheight = 1
		c.weightx = 1
		//c.weighty = 1
		c.gridy = row++
		this.add(this.createAboutPanel(), c)
		
		c.gridy = row++
		this.add(this.createAddNewPanel(), c)
		
		c.gridy = row++
		c.fill = GridBagConstraints.BOTH
		c.weighty = 1
		this.add(this.createTable(), c)
		
		c.gridy = row++
		c.fill = GridBagConstraints.HORIZONTAL
		c.weighty = 0
		this.add(this.createButtonPanel(), c)
		
		/*c.fill = GridBagConstraints.BOTH
		c.gridy = row++
		c.weighty = 1
		this.add(new JPanel(), c)*/
		
		row++
		
	}
	
	private JPanel createAboutPanel() {
		JPanel panel = new JPanel()
		panel.setLayout(new BorderLayout())
		
		Border border = BorderFactory.createLineBorder(Color.black);
		panel.setBorder(BorderFactory.createTitledBorder(border, "About SharePoint Downloader"));
				
		String text = "Having adopted SharePoint over local storage and network storage, the USNR "
		text += "is highly dependent on SharePoint. This is especially true during a drill weekend. "
		text += "However, this is the time where SharePoint is most likely to be unavailable. "
		text += "For this reason we have resorted to downloading the entire unit SharePoint prior to "
		text += "the drill weekend, writing it to CD/DVD, and destroying the media after the weekend. "
		text += "This tool can be used to download every file and folder at a given SharePoint location."
		
		JTextArea area = new JTextArea(text)
		area.setLineWrap(true)
		panel.add(area, BorderLayout.CENTER)
		
		return panel
	}
	
	private JPanel createAddNewPanel() {
		JPanel panel = new JPanel()
		
		Border border = BorderFactory.createLineBorder(Color.black);
		panel.setBorder(BorderFactory.createTitledBorder(border, "Add SharePoint Location to Download"));
		
		panel.setLayout(new GridBagLayout())
		GridBagConstraints c = new GridBagConstraints()
		
		int row = 0
		c.fill = GridBagConstraints.HORIZONTAL
		//c.anchor = GridBagConstraints.NORTHWEST
		//c.gridwidth = 1
		//c.gridheight = 1
		//c.weightx = 1
		//c.weighty = 1
		
		c.gridy = 0
		c.gridx = 0
		panel.add(new JLabel("SharePoint URL"), c)
		
		c.gridy = 0
		c.gridx = 1
		panel.add(urlField, c)
		
		c.gridy = 1
		c.gridx = 0
		panel.add(new JLabel("Download To"), c)
		
		c.gridy = 1
		c.gridx = 1
		panel.add(localField, c)
		
		c.gridy = 1
		c.gridx = 2
		panel.add(browseButton, c)
		
		JPanel buttonPanel = new JPanel()
		buttonPanel.add(addButton)
		buttonPanel.setBorder(new EmptyBorder(0, 0, 0, 0) );
		c.gridy = 2
		c.gridx = 0
		c.gridwidth = 3
		panel.add(buttonPanel, c)
		
		
		return panel
	}
	
	JPanel createTable() {
		JPanel panel = new JPanel(new BorderLayout())
		//table.setPreferredScrollableViewportSize(new Dimension(900, 500));
		table.setFillsViewportHeight(true);
		
 
		//Create the scroll pane and add the table to it.
		JScrollPane pane = new JScrollPane(table);
 
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Locations"),
				BorderFactory.createEmptyBorder(5,5,5,5)));
		panel.add(pane, BorderLayout.CENTER);
		
		return panel;
	}
	
	JPanel createButtonPanel() {
		JPanel panel = new JPanel()
		
		panel.add(downloadButton)
		
		return panel
	}
	
	
}
