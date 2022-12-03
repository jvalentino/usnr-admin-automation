package com.blogspot.jvalentino.usnrauto.reportmerger.model

import java.util.List;

import com.blogspot.jvalentino.usnrauto.commons.message.EmailSettings;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.nag.IndividualNagSummary;
import com.blogspot.jvalentino.usnrauto.commons.message.MessageType;

class MessageComposerModel {

	List<IndividualNagSummary> members
	MessageType type
	EmailSettings settings
	String message
}
