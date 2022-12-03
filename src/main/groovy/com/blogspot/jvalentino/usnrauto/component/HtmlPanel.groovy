package com.blogspot.jvalentino.usnrauto.component

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

class HtmlPanel extends JPanel {
	JScrollPane pane
	JEditorPane editorPane
	String url
	
	HtmlPanel() {
		this.setLayout(new BorderLayout())
				
		editorPane = createEditorPane();
		editorPane.setBorder(new EmptyBorder(10, 10, 10, 10) )
		
		pane = new JScrollPane(editorPane)
		pane.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		this.setBackground(Color.white)
				
		this.add(pane, BorderLayout.CENTER)

	}
	
	private JEditorPane createEditorPane() {
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
 
		return editorPane;
	}
	
	void updateUrl(String location) {
		this.url = location
		
		java.net.URL helpURL = HtmlPanel.class.getResource(url);
		if (helpURL != null) {
			try {
				editorPane.setPage(helpURL);
			} catch (IOException e) {
				System.err.println("Attempted to read a bad URL: " + helpURL);
			}
		} else {
			System.err.println("Couldn't find file: TextSampleDemoHelp.html");
		}
		
	}
}
