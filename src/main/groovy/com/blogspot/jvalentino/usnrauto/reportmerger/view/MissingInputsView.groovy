package com.blogspot.jvalentino.usnrauto.reportmerger.view

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.blogspot.jvalentino.usnrauto.reportmerger.controller.MissingInputsController;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.MemberWithMissingData;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.SummaryReport;

class MissingInputsView extends JDialog {
	
	private MissingInputsController controller
	
	JTable table
	JButton emailButton = new JButton("Send Email")
	//JButton smsButton = new JButton("Send SMS")
	JButton unselectButton = new JButton("Unselect All")
	JButton selectButton = new JButton("Select All")
	
	MissingInputsView(JComponent parent, SummaryReport report, List<MemberWithMissingData> list) {
		
		this.setModal(true);
		this.setTitle("Missing Inputs Notification")
		this.controller = new MissingInputsController(this, report, list)
		
		this.construct()
		
		this.pack();
		this.setLocationRelativeTo(parent);
		this.setAlwaysOnTop(true);
		
		controller.viewConstructed()
	}
	
	private void construct() {
		
		this.getContentPane().setLayout(new BorderLayout());
		
		
		this.add(this.createTable(), BorderLayout.CENTER);
		
		this.add(this.createBottomPanel(), BorderLayout.SOUTH);
		
	}
	
	private JScrollPane createTable() {
		this.table = new JTable();
		
		table.setPreferredScrollableViewportSize(new Dimension(950, 400));
		table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);
 
		return scrollPane
	}
	
	private JPanel createBottomPanel() {
		JPanel panel = new JPanel();
		
		panel.add(emailButton)
		//panel.add(smsButton)
		panel.add(new JLabel("      "))
		panel.add(unselectButton)
		panel.add(selectButton)
		
		return panel;
	}

}
