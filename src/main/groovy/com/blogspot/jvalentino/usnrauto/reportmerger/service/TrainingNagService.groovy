package com.blogspot.jvalentino.usnrauto.reportmerger.service

import java.util.List;

import org.dozer.DozerBeanMapper;

import com.blogspot.jvalentino.usnrauto.main.AppState;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.individual.IndividualSummary;
import com.blogspot.jvalentino.usnrauto.commons.MessageService;
import com.blogspot.jvalentino.usnrauto.commons.message.EmailMessage;
import com.blogspot.jvalentino.usnrauto.commons.message.EmailSettings;
import com.blogspot.jvalentino.usnrauto.commons.message.SmsMessage;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.nag.IndividualNagSummary;
import com.blogspot.jvalentino.usnrauto.commons.message.MessageType;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.SummaryReport;
import com.blogspot.jvalentino.usnrauto.util.FormatUtil;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

class TrainingNagService extends MessageService {

	private DozerBeanMapper dozerBeanMapper = new DozerBeanMapper()
		
	EmailMessage generateEmailMessageForMember(String header, String from, IndividualNagSummary member) {
		
		// if this member has no training, do no generate a message...
		if(member.categoryTwoGMTsToDo.size() == 0 && member.eLearningCoursesToDo.size() == 0) {
			return null
		}
		
		EmailMessage message = new EmailMessage()
		
		message.from = from
		message.to = member.emails
		message.title = "USNR Admin Automation: Training Notification"
		message.content = ""
		
		message.content += member.rank + " " + member.lastName + ",\n"
		message.content += "\n"
		message.content += header + "\n"
		message.content += "\n"
		
		for (String course : member.categoryTwoGMTsToDo) {
			message.content += course + "\n"
		}
		
		for (String course : member.eLearningCoursesToDo) {
			message.content += course + "\n"
		}
		
		message.content += "\n"
		
		message.content += "This message was generated and sent by the USNR Admin Automation Project "
		message.content += AppState.getInstance().version + "." + AppState.getInstance().buildNumber
		
		return message
	}
	
	SmsMessage generateSmsMessage(String header, IndividualNagSummary member) {
		
		// if this member has no training, do no generate a message...
		if(member.categoryTwoGMTsToDo.size() == 0 && member.eLearningCoursesToDo.size() == 0) {
			return null
		}
		
		String content = ""
		content += member.rank + " " + member.lastName + ", "
		content += header + "\n"
		
		for (String course : member.categoryTwoGMTsToDo) {
			content += course + ", "
		}
		
		for (String course : member.eLearningCoursesToDo) {
			content += course + ", "
		}
		
		SmsMessage message = new SmsMessage(member.rank + " " + member.lastName, member.cell, content)
		
		return message
		
	}
	
	/**
	 * This is used for converting the individual member summary into something with more
	 * specific details about how to contact that individual
	 * 
	 * @param summaryReport
	 * @param inputs
	 * @return
	 */
	List<IndividualNagSummary> convert(SummaryReport summaryReport, List<IndividualSummary> inputs) {
		List<IndividualNagSummary> outputs = new ArrayList<IndividualNagSummary>()
		
		for (IndividualSummary input : inputs) {
			IndividualNagSummary output = dozerBeanMapper.map(input, IndividualNagSummary.class)
			outputs.add(output)
			
			output.cell = ServiceBus.getInstance().getSummaryReportService().getMemberCell(
				summaryReport, input)
						
			output.emails = ServiceBus.getInstance().getSummaryReportService().getMemberEmailAddresses(
				summaryReport, input)
		}
		
		return outputs
	}
	
	List<IndividualNagSummary> scrubListForPeopleWhoCannotReceiveMessage(
		MessageType type, List<IndividualNagSummary> list) {
		
		if (type == MessageType.SMS) {
			return scrubListForPeopleWithNoCell(list)
		} else {
			return scrubListForPeopleWithNoEmail(list)
		}
	}
	
	List<IndividualNagSummary> scrubListForPeopleWithNoEmail(List<IndividualNagSummary> list) {
		List<IndividualNagSummary> result = new ArrayList<IndividualNagSummary>()
		
		for (IndividualNagSummary member : list) {
			if (member.emails.size() != 0) {
				result.add(member)
			}
		}
		
		return result
	}
	
	List<IndividualNagSummary> scrubListForPeopleWithNoCell(List<IndividualNagSummary> list) {
		List<IndividualNagSummary> result = new ArrayList<IndividualNagSummary>()
		
		for (IndividualNagSummary member : list) {
			if (member.cell != null) {
				result.add(member)
			}
		}
		
		return result
	}
	
	List<EmailMessage> generateEmailMessages(List<IndividualNagSummary> people, 
		String message, EmailSettings settings) {
		
		people = this.scrubListForPeopleWhoCannotReceiveMessage(MessageType.EMAIL, people)
		
		List<EmailMessage> emails = new ArrayList<EmailMessage>()
		for (IndividualNagSummary member : people) {
			
			EmailMessage email = this.generateEmailMessageForMember(
				message, settings.emailUsername, member)
			
			if (email != null) {
				emails.add(email)
			}
			
		}
		
		return emails
	}
		
	List<SmsMessage> generateSmsMessages(List<IndividualNagSummary> people, String message) {
		
		people = this.scrubListForPeopleWhoCannotReceiveMessage(MessageType.SMS, people)
		
		// generate a list of messages to be sent
		List<SmsMessage> messages = new ArrayList<SmsMessage>()
		for (IndividualNagSummary member :people) {
			SmsMessage sms = this.generateSmsMessage(message, member)
			
			if (sms != null) {
				messages.add(sms)
			}
		}
		
		return messages
	}
}
