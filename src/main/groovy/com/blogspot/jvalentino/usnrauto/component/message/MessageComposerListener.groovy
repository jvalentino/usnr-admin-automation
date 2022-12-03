package com.blogspot.jvalentino.usnrauto.component.message

import com.blogspot.jvalentino.usnrauto.commons.message.EmailSettings;

interface MessageComposerListener {

	void sendEmails(EmailSettings settings, String message, String password)
	
	void sendTextMessages(String message)
	
}
