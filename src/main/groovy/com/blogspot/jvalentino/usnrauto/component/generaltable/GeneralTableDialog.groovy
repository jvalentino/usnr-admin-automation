package com.blogspot.jvalentino.usnrauto.component.generaltable

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

class GeneralTableView extends JDialog {

	JScrollPane pane
	JTable table 
	GeneralTableModel model
	JButton close = new JButton("Close")
	
	GeneralTableView(Component view, String title, List<Object> list) {
		this.setSize(950, 750)
		this.setTitle(title)
		
		this.getContentPane().setLayout(new BorderLayout())
		
		JPanel panel = (JPanel )this.getContentPane();
		panel.setBorder(new EmptyBorder(10, 10, 10, 10) )
		
		model = new GeneralTableModel(list)
		table = new JTable(model)
		JTable rowTable = new RowNumberTable(table);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		pane = new JScrollPane(
			table, 
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
		
		pane.setRowHeaderView(rowTable);
		pane.setCorner(JScrollPane.UPPER_LEFT_CORNER,
			rowTable.getTableHeader());
		
		this.add(BorderLayout.CENTER, pane);
		JPanel buttonPanel = new JPanel()
		buttonPanel.add(close)
		this.add(BorderLayout.SOUTH, buttonPanel);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		GeneralTableView me = this
		
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.setVisible(false)	
			}
		});
		
		
		this.setLocationRelativeTo(view)
	}
}
