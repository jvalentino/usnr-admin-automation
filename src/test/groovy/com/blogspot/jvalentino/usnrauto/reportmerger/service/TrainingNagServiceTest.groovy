package com.blogspot.jvalentino.usnrauto.reportmerger.service

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.individual.IndividualSummary;
import com.blogspot.jvalentino.usnrauto.commons.message.EmailMessage;
import com.blogspot.jvalentino.usnrauto.commons.message.EmailSettings;
import com.blogspot.jvalentino.usnrauto.commons.message.SmsMessage;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.nag.IndividualNagSummary;
import com.blogspot.jvalentino.usnrauto.commons.message.MessageType;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.SummaryReport;

class TrainingNagServiceTest {

	private ServiceBus bus = ServiceBus.getInstance()
	private TrainingNagService service
	
	@Before
	void setup() {
		service = bus.getTrainingNagService()
	}
	
	private SummaryReport generateReport() {
		SummaryReport report = new SummaryReport()
		report.secondaryColumnHeaders.add("cell #")
		report.secondaryColumnHeaders.add("work email")
		return report
	}
	
	private List<IndividualSummary> generateRawMembers() {
		List<IndividualSummary> members = new ArrayList<IndividualSummary>()
		
		IndividualSummary one = new IndividualSummary()
		one.categoryTwoGMTsToDo.add("ALPHA")
		one.eLearningCoursesToDo.add("BRAVO")
		one.firstName = "JOHN"
		one.lastName = "SMITH"
		one.rank = "CTN1"
		one.values = new String[2]
		one.values[0] = "555-555-5555"
		one.values[1] = ""
		
		IndividualSummary two = new IndividualSummary()
		two.categoryTwoGMTsToDo.add("ALPHA")
		two.eLearningCoursesToDo.add("BRAVO")
		two.firstName = "JAMES"
		two.lastName = "WITH"
		two.rank = "CTR1"
		two.values = new String[2]
		two.values[0] = ""
		two.values[1] = "foo@bar.com"
		
		members.add(one)
		members.add(two)
		
		return members
	}
	
	private List<IndividualNagSummary> generateMembers() {
		SummaryReport report = this.generateReport()
		
		List<IndividualSummary> members = this.generateRawMembers()
		
		List<IndividualNagSummary> results = service.convert(report, members)
		
		return results
	}
	
	@Test
	void testConvert() {
		
		List<IndividualNagSummary> results = generateMembers()
		
		assertThat(results.size(), is(2))
		
		assertThat(results.get(0).firstName, is("JOHN"))
		assertThat(results.get(0).lastName, is("SMITH"))
		assertThat(results.get(0).rank, is("CTN1"))
		assertThat(results.get(0).cell, is("555-555-5555"))
		assertThat(results.get(0).emails.size(), is(0))
		
		assertThat(results.get(1).firstName, is("JAMES"))
		assertThat(results.get(1).lastName, is("WITH"))
		assertThat(results.get(1).rank, is("CTR1"))
		assertThat(results.get(1).cell, nullValue())
		assertThat(results.get(1).emails.size(), is(1))
		assertThat(results.get(1).emails.get(0), is("foo@bar.com"))
		
	}
	
	@Test
	void testScrubListForPeopleWhoCannotReceiveMessage_Email() {
		List<IndividualNagSummary> members = generateMembers()
		assertThat(members.size(), is(2))
		
		List<IndividualNagSummary> results = 
			service.scrubListForPeopleWhoCannotReceiveMessage(MessageType.EMAIL, members)
		
		assertThat(results.size(), is(1))
		
		assertThat(results.get(0).firstName, is("JAMES"))
		assertThat(results.get(0).lastName, is("WITH"))
		assertThat(results.get(0).rank, is("CTR1"))
		assertThat(results.get(0).cell, nullValue())
		assertThat(results.get(0).emails.size(), is(1))
		assertThat(results.get(0).emails.get(0), is("foo@bar.com"))
		
	}
	
	@Test
	void testScrubListForPeopleWhoCannotReceiveMessage_Sms() {
		List<IndividualNagSummary> members = generateMembers()
		assertThat(members.size(), is(2))
		
		List<IndividualNagSummary> results =
			service.scrubListForPeopleWhoCannotReceiveMessage(MessageType.SMS, members)
		
		assertThat(results.size(), is(1))
		
		assertThat(results.get(0).firstName, is("JOHN"))
		assertThat(results.get(0).lastName, is("SMITH"))
		assertThat(results.get(0).rank, is("CTN1"))
		assertThat(results.get(0).cell, is("555-555-5555"))
		assertThat(results.get(0).emails.size(), is(0))
	}
	
	@Test
	void testGenerateEmailMessageForMember() {
		IndividualNagSummary member = generateMembers().get(1)
		
		EmailMessage email = service.generateEmailMessageForMember("Foobar", "bar@bar.com", member)
		
		this.assertEmail(email)
		
	}
	
	private void assertEmail(EmailMessage email) {
		assertThat(email.from, is("bar@bar.com"))
		assertThat(email.to.get(0), is("foo@bar.com"))
		assertThat(email.title, is("USNR Admin Automation: Training Notification"))
		
		String[] lines = email.content.split("\n")
		
		assertThat(lines[0], is("CTR1 WITH,"))
		assertThat(lines[1], is(""))
		assertThat(lines[2], is("Foobar"))
		assertThat(lines[3], is(""))
		assertThat(lines[4], is("ALPHA"))
		assertThat(lines[5], is("BRAVO"))
		assertThat(lines[6], is(""))
		assertThat(lines[7], is("This message was generated and sent by the USNR Admin Automation Project 1.0.0"))
	}
	
	@Test
	void testGenerateEmailMessageForMember_NoTraining() {
		IndividualNagSummary member = generateMembers().get(1)
		member.categoryTwoGMTsToDo.clear()
		member.eLearningCoursesToDo.clear()
		
		EmailMessage email = service.generateEmailMessageForMember("Foobar", "bar@bar.com", member)
		
		assertThat(email, nullValue())
		
	}
	
	@Test
	void testGenerateSmsMessage() {
		IndividualNagSummary member = generateMembers().get(0)
		
		SmsMessage message = service.generateSmsMessage("Foobar", member)
		
		this.assertSms(message)
	}
	
	private void assertSms(SmsMessage message) {
		String[] lines = message.message.split("\n")
		
		assertThat(lines[0], is("CTN1 SMITH, Foobar"))
		assertThat(lines[1], is("ALPHA, BRAVO, "))
	}
	
	@Test
	void testGenerateSmsMessage_NoTraining() {
		IndividualNagSummary member = generateMembers().get(0)
		member.categoryTwoGMTsToDo.clear()
		member.eLearningCoursesToDo.clear()
		
		SmsMessage message = service.generateSmsMessage("Foobar", member)
		
		assertThat(message, nullValue())
	}
	
	@Test
	void testGenerateEmailMessages() {
		List<IndividualNagSummary> members = this.generateMembers()
		String message = "Foobar"
		EmailSettings settings = new EmailSettings()
		settings.emailUsername = "bar@bar.com"
		
		List<EmailMessage> emails = service.generateEmailMessages(members, message, settings)
		
		assertThat(emails.size(), is(1))
		this.assertEmail(emails.get(0))
		
	}
	
	@Test
	void testGenerateEmailMessages_NoTraining() {
		List<IndividualNagSummary> members = this.generateMembers()
		String message = "Foobar"
		EmailSettings settings = new EmailSettings()
		settings.emailUsername = "bar@bar.com"
		
		IndividualNagSummary member = members.get(1)
		member.categoryTwoGMTsToDo.clear()
		member.eLearningCoursesToDo.clear()
		
		List<EmailMessage> emails = service.generateEmailMessages(members, message, settings)
		
		assertThat(emails.size(), is(0))
		
	}
	
	@Test
	void testGenerateSmsMessages() {
		List<IndividualNagSummary> members = this.generateMembers()
		String message = "Foobar"
		
		List<SmsMessage> messages = service.generateSmsMessages(members, "Foobar")
		
		assertThat(messages.size(), is(1))
		assertSms(messages.get(0))
		
	}
	
	@Test
	void testGenerateSmsMessages_NoTraining() {
		List<IndividualNagSummary> members = this.generateMembers()
		String message = "Foobar"
		
		IndividualNagSummary member = members.get(0)
		member.categoryTwoGMTsToDo.clear()
		member.eLearningCoursesToDo.clear()
		
		List<SmsMessage> messages = service.generateSmsMessages(members, "Foobar")
		
		assertThat(messages.size(), is(0))
		
	}
}
