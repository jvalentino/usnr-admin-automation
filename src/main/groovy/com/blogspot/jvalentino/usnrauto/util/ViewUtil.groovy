package com.blogspot.jvalentino.usnrauto.util

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

class ViewUtil {
	
	static Font small = new Font("Arial", Font.BOLD, 11);

	static int createUploadRow(String label, JTextField field, JButton button, String text,
		GridBagConstraints c, JPanel panel, int row, JLabel info = null, JButton openButton = null) {
		
		c.gridwidth = 1
		
		c.gridx = 0
		c.gridy = row
		JLabel labelLabel = new JLabel(label)
		labelLabel.setPreferredSize(new Dimension(200, 25))
		labelLabel.setHorizontalAlignment(SwingConstants.RIGHT)
		labelLabel.setFont(small)
		panel.add(labelLabel, c);
		
		c.gridx = 1
		c.gridy = row
		panel.add(field, c);
		field.setFont(small)
		
		c.gridx = 2
		c.gridy = row
		panel.add(button, c);
		button.setFont(small)
		
		JTextArea infoLabel = new JTextArea()
		infoLabel.setText(text)
		infoLabel.setLineWrap(true)
		infoLabel.setOpaque(false)
		infoLabel.setWrapStyleWord(true)
		infoLabel.setPreferredSize(new Dimension(650,25));
		infoLabel.setFont(small)
		
		c.gridx = 1
		c.gridy = row + 1
		panel.add(infoLabel, c);
		
		if (openButton != null) {
			c.gridx = 2
			c.gridy = row + 1
			panel.add(openButton, c);
			openButton.setFont(small)
		}
		
		if (info != null) {
			
			c.gridx = 1
			c.gridy = row + 2
			info.setFont(small)
			
			panel.add(info, c);
			
			return row + 3
		} else {
			return row + 2
		}
		
		
		
	}
}
