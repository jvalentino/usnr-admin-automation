package com.blogspot.jvalentino.usnrauto.sms.view

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.blogspot.jvalentino.usnrauto.sms.controller.SMSController;

class SMSView extends JPanel {
	private SMSController controller;
	
	private JTable table;
	private JButton reloadButton;
	private JComboBox<String> firstCombo;
	private JComboBox<String> lastCombo;
	private JComboBox<String> catOneCombo;
	private JComboBox<String> catTwoCombo;
	private JButton clearButton;
	private JButton composeButton;
	
	JButton emailButton = new JButton("Email")
	
	JButton manualInputsButton = new JButton("Open Manual Inputs XLSX")
	JButton showEmailList = new JButton("Copy Emails to Clipboard")
	
	
	SMSView() {
		this.controller = new SMSController(this);
		
		this.create();
		
		controller.show();
	}
	
	private void create() {
		
		
		
		
		this.setLayout(new BorderLayout());
		
		this.add(this.createTopPanel(), BorderLayout.NORTH);
		
		this.add(this.createTable(), BorderLayout.CENTER);
		
		this.add(this.createBottomPanel(), BorderLayout.SOUTH);
		
	}
	
	private JPanel createTopPanel() {
		JPanel panel = new JPanel();
		
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(2,2,2,2); 
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1;
		c.weighty = 1;
		panel.setLayout(new GridBagLayout());
		
		int row = 0;
		
		c.gridx = 0;
		c.gridy = row;
		c.gridwidth = 1;
		panel.add(this.createFilterPanel(), c);

		
		
		return panel;
	}
	
	private JPanel createFilterPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Filter By"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(2,2,2,2); 
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1;
		c.weighty = 1;
		panel.setLayout(new GridBagLayout());
		int row = 0;
		int col = 0;
		
		this.firstCombo = new JComboBox<String>();
		
		c.gridx = col++;
		c.gridy = row;
		panel.add(new JLabel("First Name"), c);
		
		c.gridx = col++;
		c.gridy = row;
		panel.add(firstCombo, c);
				
		this.lastCombo = new JComboBox<String>();
		
		c.gridx = col++;
		c.gridy = row;
		panel.add(new JLabel("Last Name"), c);
		
		c.gridx = col++;
		c.gridy = row;
		panel.add(lastCombo, c);
		
		this.catOneCombo = new JComboBox<String>();
		
		c.gridx = col++;
		c.gridy = row;
		panel.add(new JLabel("Category 1"), c);
		
		c.gridx = col++;
		c.gridy = row;
		panel.add(catOneCombo, c);
		
		this.catTwoCombo = new JComboBox<String>();
		
		c.gridx = col++;
		c.gridy = row;
		panel.add(new JLabel("Category 2"), c);
		
		c.gridx = col++;
		c.gridy = row;
		panel.add(catTwoCombo, c);
		
		this.clearButton = new JButton("Reset Filters");
		c.gridx = col++;
		c.gridy = row;
		panel.add(clearButton, c);
		
		return panel;
	}
	
	private JPanel createTable() {
		this.table = new JTable();
		
		table.setPreferredScrollableViewportSize(new Dimension(900, 500));
        table.setFillsViewportHeight(true);
        
 
        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
 
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("People"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
        panel.add(scrollPane);
        
        return panel;
	}
	
	private JPanel createBottomPanel() {
		JPanel panel = new JPanel();
		
		
		this.reloadButton = new JButton("Reload File");
		//panel.add(reloadButton);
		
		panel.add(manualInputsButton)
		
		this.composeButton = new JButton("SMS");
		panel.add(composeButton);
		
		panel.add(emailButton)
		
		panel.add(showEmailList)
		
		
		return panel;
	}
	
	public JTable getTable() {
		return this.table;
	}
	
	public JButton getReloadButton() {
		return this.reloadButton;
	}
	
	public JComboBox<String> getFirstCombo() {
		return this.firstCombo;
	}
	
	public JComboBox<String> getLastCombo() {
		return this.lastCombo;
	}
	
	public JComboBox<String> getCatOneCombo() {
		return this.catOneCombo;
	}
	
	public JComboBox<String> getCatTwoCombo() {
		return this.catTwoCombo;
	}
	
	public JButton getClearButton() {
		return this.clearButton;
	}
	
	public JButton getComposeButton() {
		return this.composeButton;
	}
	
	public JMenuItem getOpenItem() {
		return this.openItem;
	}
	
	public JMenuItem getAboutItem() {
		return this.aboutItem;
	}
	
}
