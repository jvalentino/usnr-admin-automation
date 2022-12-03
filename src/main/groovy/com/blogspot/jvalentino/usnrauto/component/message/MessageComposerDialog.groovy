package com.blogspot.jvalentino.usnrauto.component.message

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.blogspot.jvalentino.usnrauto.commons.message.EmailSettings;
import com.blogspot.jvalentino.usnrauto.commons.message.MessageType;

class MessageComposerDialog extends JDialog {

	JTextArea messageArea
	JTextArea recipientArea
	JButton sendButton = new JButton("Send")
	JTextField authField = new JTextField()
	JTextField tlsField = new JTextField()
	JTextField hostField = new JTextField()
	JTextField portField = new JTextField()
	JTextField usernameField = new JTextField()
	JPasswordField passwordField = new JPasswordField()
	
	private MessageComposerController controller

	MessageComposerDialog(JComponent parent, MessageType type, 
		EmailSettings settings, String message, String info, List<String> names, 
		MessageComposerListener listener) {

		this.controller = new MessageComposerController(this, type, settings, message, names, listener) 
		
		this.setModal(true);
		this.setTitle(type.toString() + " Composer")

		this.construct(type, info)

		this.pack();
		this.setLocationRelativeTo(parent);
		this.setAlwaysOnTop(true);

		controller.viewConstructed()
	}

	private void construct(MessageType type, String info) {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(2,2,2,2);
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1;
		c.weighty = 1;
		this.getContentPane().setLayout(new GridBagLayout());
		int row = 0;
		int col = 0;
		
		if (info != null) {
			c.gridx = col;
			c.gridy = row++;
			this.getContentPane().add(this.createInfoArea(info), c);
		}

		c.gridx = col;
		c.gridy = row++;
		this.getContentPane().add(this.createMessageArea(), c);

		c.gridx = col;
		c.gridy = row++;
		this.getContentPane().add(this.createRecipientPanel(), c);

		if (type == MessageType.EMAIL) {
			c.gridx = col;
			c.gridy = row++;
			this.getContentPane().add(this.createEmailPanel(), c);
		}

		c.gridx = col;
		c.gridy = row++;
		this.getContentPane().add(this.createButtonPanel(), c);
	}

	private JPanel createInfoArea(String info) {
		JTextArea area = new JTextArea();
		area.setLineWrap(true);
		area.setEditable(false)
		area.setEnabled(false)

		//Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(area);
		scrollPane.setPreferredSize(new Dimension(400,100));

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Information"),
				BorderFactory.createEmptyBorder(5,5,5,5)));
		panel.add(scrollPane);

		area.setText(info)

		return panel;
	}

	private JPanel createMessageArea() {
		this.messageArea = new JTextArea();
		messageArea.setLineWrap(true);

		//Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(messageArea);
		scrollPane.setPreferredSize(new Dimension(400,200));

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Message"),
				BorderFactory.createEmptyBorder(5,5,5,5)));
		panel.add(scrollPane);

		return panel;
	}

	private JPanel createRecipientPanel() {
		recipientArea = new JTextArea();
		recipientArea.setWrapStyleWord(true);
		//Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(recipientArea);
		scrollPane.setPreferredSize(new Dimension(400,50));

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Recipients"),
				BorderFactory.createEmptyBorder(5,5,5,5)));
		panel.add(scrollPane);

		return panel

	}
	private JPanel createEmailPanel() {
		JPanel panel = new JPanel(new GridBagLayout())
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(2,2,2,2);
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1;
		c.weighty = 1;
		int row = 0;

		c.gridx = 0
		c.gridy = row
		panel.add(new JLabel("Authorization Required"), c)

		c.gridx = 1
		c.gridy = row
		panel.add(authField, c)

		row++

		c.gridx = 0
		c.gridy = row
		panel.add(new JLabel("TLS Enabled"), c)

		c.gridx = 1
		c.gridy = row
		panel.add(tlsField, c)

		row++

		c.gridx = 0
		c.gridy = row
		panel.add(new JLabel("Host"), c)

		c.gridx = 1
		c.gridy = row
		panel.add(hostField, c)

		row++

		c.gridx = 0
		c.gridy = row
		panel.add(new JLabel("Port"), c)

		c.gridx = 1
		c.gridy = row
		panel.add(portField, c)

		row++

		c.gridx = 0
		c.gridy = row
		panel.add(new JLabel("Username"), c)

		c.gridx = 1
		c.gridy = row
		panel.add(usernameField, c)

		row++

		c.gridx = 0
		c.gridy = row
		panel.add(new JLabel("Password"), c)

		c.gridx = 1
		c.gridy = row
		panel.add(passwordField, c)

		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Email Settings"),
				BorderFactory.createEmptyBorder(5,5,5,5)));

		return panel
	}

	private JPanel createButtonPanel() {
		JPanel panel = new JPanel();
		panel.add(sendButton)
		return panel
	}
}
