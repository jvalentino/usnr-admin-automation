package com.blogspot.jvalentino.usnrauto.cacutil.view

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.blogspot.jvalentino.usnrauto.cacutil.controller.CacUtilController;
import com.blogspot.jvalentino.usnrauto.util.ViewUtil;

class CacUtilView extends JPanel {

	private static final int FILE_FIELD_LENGTH = 50
	
	private CacUtilController controller
	
	JTextField cacKeyField = new JTextField(FILE_FIELD_LENGTH)
	JButton cacKeyBrowseButton = new JButton("Browse...")
	JList identityList = new JList(new DefaultListModel())
	JTextArea certInfoArea = new JTextArea(20, 10)
	JButton loadButton = new JButton("Load")
	JPasswordField cacPinField = new JPasswordField(FILE_FIELD_LENGTH)
	
	CacUtilView() {
		this.controller = new CacUtilController(this)
		this.construct()
		this.controller.viewConstructed()
	}
	
	private void construct() {
		JPanel panel = new JPanel()
		panel.setBorder(new EmptyBorder(10, 10, 10, 10) )
		panel.setLayout(new GridBagLayout())
		GridBagConstraints c = new GridBagConstraints()
		
		int row = 0
		c.fill = GridBagConstraints.HORIZONTAL

		c.gridx = 0
		c.gridy = row++
		panel.add(this.createCacKeyRow(), c)
		
		c.gridx = 0
		c.gridy = row++
		panel.add(this.createCacInfoRow(), c)
		
		c.gridx = 0
		c.gridy = row++
		panel.add(this.createCacTextRow(), c)
		
		c.gridx = 0
		c.gridy = row++
		panel.add(this.createButtonRow(), c)
		
		this.add(panel)
	}
	
	private JPanel createCacKeyRow() {
		JPanel panel = new JPanel()
		panel.setLayout(new GridBagLayout())
		GridBagConstraints c = new GridBagConstraints()
		
		Border border = BorderFactory.createLineBorder(Color.black);
		panel.setBorder(BorderFactory.createTitledBorder(border, "CAC Key"));
		
		int row = 0
		c.fill = GridBagConstraints.HORIZONTAL
		
		row = ViewUtil.createUploadRow(
			"CAC Key Library",
			this.cacKeyField,
			this.cacKeyBrowseButton,
			"CACKey: Used for CAC interaction. " + 
			"On Windows you are looking for libcackey.dll, and on Mac you are looking for libcackey.dylib. " +
			"Warning: I have not tested this on Windows yet.",
			c,
			panel,
			row,
			null,
			null)
		
		return panel
	}
	
	private JPanel createCacInfoRow() {
		JPanel panel = new JPanel(new BorderLayout())
		
		Border border = BorderFactory.createLineBorder(Color.black);
		panel.setBorder(BorderFactory.createTitledBorder(border, "CAC Certificates (Identities)"));
		
		identityList.setPreferredSize(new Dimension(50,150))
		panel.add(identityList, BorderLayout.CENTER)
		
		return panel
	}
	
	private JPanel createCacTextRow() {
		JPanel panel = new JPanel(new BorderLayout())
		
		Border border = BorderFactory.createLineBorder(Color.black);
		panel.setBorder(BorderFactory.createTitledBorder(border, "Selected Certificate Information"));
		
		JScrollPane pane = new JScrollPane(certInfoArea)
		panel.add(pane, BorderLayout.CENTER)
		
		return panel
	}
	
	private JPanel createButtonRow() {
		JPanel panel = new JPanel()
		panel.setLayout(new GridBagLayout())
		GridBagConstraints c = new GridBagConstraints()
		
		Border border = BorderFactory.createLineBorder(Color.black);
		panel.setBorder(BorderFactory.createTitledBorder(border, "CAC Certificate Loading"));
		
		int row = 0
		c.fill = GridBagConstraints.HORIZONTAL
		
		row = ViewUtil.createUploadRow(
			"PIN Code",
			this.cacPinField,
			this.loadButton,
			"Enter the PIN for your CAC, and press the Load button.",
			c,
			panel,
			row,
			null,
			null)
		
		return panel
	}
}
