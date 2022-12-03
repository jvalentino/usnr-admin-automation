package com.blogspot.jvalentino.usnrauto.commons

import javax.mail.Session;
import java.awt.Component;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.json.JSONObject;

import com.blogspot.jvalentino.usnrauto.commons.message.EmailMessage;
import com.blogspot.jvalentino.usnrauto.commons.message.EmailSettings;
import com.blogspot.jvalentino.usnrauto.commons.message.SmsMessage;
import com.blogspot.jvalentino.usnrauto.commons.message.MessageServiceListener;
import com.blogspot.jvalentino.usnrauto.sms.component.ProgressDialog;
import com.blogspot.jvalentino.usnrauto.util.FormatUtil;
import com.blogspot.jvalentino.usnrauto.util.TextBeltUtil;

class MessageService {
	
	protected boolean testMode = false
	
	/**
	 * Creates a Java mail session using the given mail settings and password.
	 * Note that if incorrect settings are given this will not fail. This will
	 * not fail until you attempt to send a message.
	 */
	Session createMailSession(EmailSettings settings, String password) {
		Properties props = new Properties();
		props.put("mail.smtp.auth", settings.emailRequiresAuthorization.toString());
		props.put("mail.smtp.starttls.enable", settings.emailTlsEnabled.toString());
		props.put("mail.smtp.host", settings.emailHost);
		props.put("mail.smtp.port", settings.emailPort.toString());

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(settings.emailUsername, password);
					}
				});

		return session
	}

	/**
	 * This will attempt to send an email message using the given session and message.
	 * If there is a problem an exception will be thrown.
	 * 
	 * @param session
	 * @param email
	 */
	void sendMail(Session session, EmailMessage email) throws Exception {

		if (testMode) {
			println " "
			println "TO: " + FormatUtil.arrayToCommaSeparatedString(email.to as String[])
			println "FROM: " + email.from
			println "TITLE: " + email.title
			println "MESSAGE: " + email.content
			println " "
			throw new Exception("TEST MODE: Email not actually sent to " + email.to.get(0))
		}
		
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(email.from));
		for (String value : email.to) {
			message.addRecipient(Message.RecipientType.BCC, InternetAddress.parse(value)[0]);
		}
		message.setSubject(email.title);
		message.setText(email.content);

		
		Transport.send(message);
		
	}
	
	/**
	 * Attempts to send an SMS message through TextBelt. If there is a problem a non-null
	 * String will be returned as the error message.
	 * 
	 * @return
	 */
	String sendSms(SmsMessage sms) {
		String result = null
		
		if (!testMode) {
			try {
				result = TextBeltUtil.sendSmsThroughTextBelt(sms.cell, sms.message)
			} catch (Exception e) {
				return sms.name  + " - " + e.getMessage()
			}
		} else {
			println " "
			println "TO: " + sms.cell
			println "MESSAGE: " + sms.message
			println " "
			
			result = '{"success":false,"message":"Application in TEST MODE, message not sent to ' + sms.name + '"}'	
		}
		
		try {
			JSONObject json = new JSONObject(result);
			boolean success = json.getBoolean("success");
			if (json.has("message")) {
				String message = json.getString("message");
				System.out.println(success.toString() + " " + message);
				return message
			} else {
				System.out.println(success);
				return null
			}
		} catch (Exception e) {
			return "There was an unknown connection error for textbelt.com for " + sms.name
		}
	}
	
	/**
	 * Handles starting a thread the does the emailing, when done the listener is called.
	 * While this is going on a dialog is modal to the given view.
	 */
	void sendEmailsAsync(Component view, MessageServiceListener listener, EmailSettings settings, 
		String password, List<EmailMessage> emails) {
		
		MessageService me = this
		
		ProgressDialog dialog = new ProgressDialog(emails.size());
		dialog.setLocationRelativeTo(view);
		dialog.pack();
		
		(new Thread() {
			public void run() {
				
				List<String> errors = new ArrayList<String>()
				
				Session session = me.createMailSession(settings, password)
				
				for (int i = 0; i < emails.size(); i++) {
					EmailMessage email = emails.get(i)
					String progress = "Emailing " + email.to.get(0)
					println progress
					dialog.update(progress, i + 1)
					
					try {
						me.sendMail(session, email)
					} catch (Exception e) {
						e.printStackTrace()
						if (e.message != null)
							errors.add(e.message)
						else
							errors.add(e.getClass().toString())
						
						break
					}
				}
				
				dialog.setVisible(false);
				
				// notify listener
				listener.messagingComplete(errors)
			}
		}).start()
		
		dialog.setVisible(true);
	}
	
	/**
	 * Sends a list of text messages in a thread, and returns a list of errors if there were any.
	 * This method also includes displaying a progress dialog.
	 */
	void sendSmsAsync(Component view, MessageServiceListener listener, List<SmsMessage> messages) {
				
		List<String> errors = new ArrayList<String>()
		
		MessageService me = this
		
		ProgressDialog dialog = new ProgressDialog(messages.size());
		dialog.setLocationRelativeTo(view);
		dialog.pack();
		
		(new Thread() {
			public void run() {
								
				for (int i = 0; i < messages.size(); i++) {
					SmsMessage message = messages.get(i)
					String progress = "Sending to " + message.name + " " + message.cell
					println progress
					dialog.update(progress, i + 1)
					String error = me.sendSms(message);
					
					if (error != null) {
						errors.add(error)
					}
				}
				
				dialog.setVisible(false);
				
				// notify listener
				listener.messagingComplete(errors)
			}
		}).start()
		
		dialog.setVisible(true);
	}
		
	
}
