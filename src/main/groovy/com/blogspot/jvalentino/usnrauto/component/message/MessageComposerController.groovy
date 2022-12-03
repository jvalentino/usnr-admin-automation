package com.blogspot.jvalentino.usnrauto.component.message

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JOptionPane;

import com.blogspot.jvalentino.usnrauto.commons.message.EmailSettings;
import com.blogspot.jvalentino.usnrauto.commons.message.MessageType;
import com.blogspot.jvalentino.usnrauto.main.controller.BaseController;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.nag.IndividualNagSummary;
import com.blogspot.jvalentino.usnrauto.util.FormatUtil;

class MessageComposerController extends BaseController {

	private MessageComposerDialog view
	private MessageComposerModel model
	private MessageComposerListener listener
		
	MessageComposerController(MessageComposerDialog view, MessageType type, EmailSettings settings, 
		String message, List<String> names, MessageComposerListener listener) {
		
		this.view = view
		
		this.model = new MessageComposerModel()
		this.model.settings = settings
		this.model.message = message
		this.model.names = names
		this.model.type = type	
		
		this.listener = listener	
	}
	
	protected void viewConstructed() {
		MessageComposerController me = this
		
		this.view.messageArea.setText(model.message)
		
		String text = ""
		for (String person : model.names) {
			text += person + ", "
		}
		this.view.recipientArea.setText(text)
		
		if (model.settings != null) {
			this.view.authField.setText(model.settings.emailRequiresAuthorization.toString())
			this.view.tlsField.setText(model.settings.emailTlsEnabled.toString())
			this.view.hostField.setText(model.settings.emailHost)
			this.view.portField.setText(model.settings.emailPort.toString())
			this.view.usernameField.setText(model.settings.emailUsername)
		}
		
		String password = this.getPreferenceAsString("emailPassword")
		if (password != null) {
			this.view.passwordField.setText(password)
		}
		
		view.sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.sendPressed()
			}
		});
	}
	
	protected void sendPressed() {
		if (model.type == MessageType.EMAIL) {
			handleSendingEmail()
		} else {
			handleSendingSms()
		}
	}
	
	protected void handleSendingEmail() {
		
		try {
			// update email settings
			model.settings.emailRequiresAuthorization = FormatUtil.stringToBoolean(this.view.authField.getText())
			model.settings.emailTlsEnabled = FormatUtil.stringToBoolean(this.view.tlsField.getText())
			model.settings.emailHost = this.view.hostField.getText()
			model.settings.emailPort = Integer.parseInt(this.view.portField.getText())
			model.settings.emailUsername = this.view.usernameField.getText()
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(view,
				e.getMessage(),
				"Error",
				JOptionPane.ERROR_MESSAGE);
			return
		}
		
		model.message = view.messageArea.getText()
		
		// get the password and store it
		String password = new String(this.view.passwordField.getPassword())
		this.storeStringAsPreference("emailPassword", password)
		
		listener.sendEmails(model.settings, model.message, password)
		
	}
	
	protected void handleSendingSms() {
		model.message = view.messageArea.getText()
		listener.sendTextMessages(model.message)
	}

	
}
