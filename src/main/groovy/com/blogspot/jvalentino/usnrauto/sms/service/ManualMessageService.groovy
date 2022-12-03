package com.blogspot.jvalentino.usnrauto.sms.service

import com.blogspot.jvalentino.usnrauto.commons.MessageService;
import com.blogspot.jvalentino.usnrauto.commons.message.EmailMessage;
import com.blogspot.jvalentino.usnrauto.commons.message.SmsMessage;
import com.blogspot.jvalentino.usnrauto.sms.data.RecipientVO;

class ManualMessageService extends MessageService {

	List<SmsMessage> generateSmsMessages(List<RecipientVO> people, String text) {
		List<SmsMessage> messages = new ArrayList<SmsMessage>()
		
		for (RecipientVO vo : people) {
			
			if (vo.phoneNumber == null) {
				continue
			}
			
			SmsMessage message = new SmsMessage(vo.firstName + " " + vo.lastName, vo.phoneNumber, text)
			messages.add(message)
		}
		
		return messages
	}
	/**
	 * We are really just constructing a single message with a recipient for every listed person's email
	 * @param people
	 * @param title
	 * @param text
	 * @param from
	 * @return
	 */
	List<EmailMessage> generateEmailMessages(List<RecipientVO> people, String title, String text, String from) {
		List<EmailMessage> messages = new ArrayList<EmailMessage>()
		
		EmailMessage message = new EmailMessage()
		message.content = text
		message.from = from
		message.title = title
		
		for (RecipientVO vo : people) {
			
			if (vo.emails.size() == 0) {
				continue
			}
			
			for (String email : vo.emails) {
				message.to.add(email)
			}			
			
		}
		
		messages.add(message)
		
		return messages
	}
	
}
