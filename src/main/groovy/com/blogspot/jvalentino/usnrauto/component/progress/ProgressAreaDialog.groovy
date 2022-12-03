package com.blogspot.jvalentino.usnrauto.component.progress

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

class ProgressAreaDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private int count;
	private  JProgressBar progress;
	private JTextArea area = new JTextArea();
	private JButton closeButton = new JButton("OK")
	
	public ProgressAreaDialog(String title) {
		this.setTitle(title)
		this.setSize(700, 400)				
		create();
		
		this.setLocationRelativeTo(null);
	}
	
	private void create() {
		ProgressAreaDialog me = this
		
		JPanel bodyPanel = new JPanel();
		
		bodyPanel.setLayout(new BorderLayout());
		
		progress = new JProgressBar()
		
		progress.setStringPainted(true)
		progress.setIndeterminate(true)
        progress.setPreferredSize(new Dimension(700,20));
        
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		 
		DefaultCaret caret = (DefaultCaret) area.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		//area.setPreferredSize(new Dimension(600,400));
		JScrollPane pane = new JScrollPane(area);
		   
        bodyPanel.add(BorderLayout.NORTH, progress);
        bodyPanel.add(BorderLayout.CENTER, pane);
        
        this.getContentPane().add(bodyPanel);
		
		JPanel buttonPanel = new JPanel()
		buttonPanel.add(closeButton)
		closeButton.setEnabled(false)
		
		bodyPanel.add(BorderLayout.SOUTH, buttonPanel);
        
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setAlwaysOnTop(true);
		this.setModal(true);
		
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.setVisible(false)
			}
		});
	}
	
	public void appendLine(String text) {
		area.append(text + "\n")
	}
	
	void readyToClose() {
		appendLine("")
		appendLine("All Downloading complete")
		closeButton.setEnabled(true)
		progress.setVisible(false)
	}
}

