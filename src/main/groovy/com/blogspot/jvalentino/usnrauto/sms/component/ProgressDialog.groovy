package com.blogspot.jvalentino.usnrauto.sms.component

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

class ProgressDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private int count;
	private  JProgressBar progBar;
	private JLabel progressLabel = new JLabel(" ");
	
	public ProgressDialog(int count) {
		
		this.count = count;
				
		create();
	}
	
	private void create() {
		
		JPanel bodyPanel = new JPanel();
		
		bodyPanel.setLayout(new BorderLayout());
		
		progBar = new JProgressBar(0, this.count);
		progBar.setValue(0);
        progBar.setBorderPainted(true);
        progBar.setPreferredSize(new Dimension(400,40));
        
        
        JPanel panel = new JPanel();
        panel.add(progBar);
		
        bodyPanel.add(panel, BorderLayout.CENTER);
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		 
		   
        bodyPanel.add(BorderLayout.NORTH, new JLabel("Progress..."));
        bodyPanel.add(BorderLayout.SOUTH, progressLabel);
        
        this.getContentPane().add(bodyPanel);
        
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setAlwaysOnTop(true);
		this.setModal(true);
	}
	
	public void update(String text, int newCount) {
		progressLabel.setText(text);
		progBar.setValue(newCount);
	}
}

