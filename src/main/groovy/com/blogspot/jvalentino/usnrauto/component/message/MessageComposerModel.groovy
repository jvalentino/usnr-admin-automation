package com.blogspot.jvalentino.usnrauto.component.message

import com.blogspot.jvalentino.usnrauto.commons.message.EmailSettings;
import com.blogspot.jvalentino.usnrauto.commons.message.MessageType;

class MessageComposerModel {

	MessageType type
	EmailSettings settings
	String message
	List<String> names
}
