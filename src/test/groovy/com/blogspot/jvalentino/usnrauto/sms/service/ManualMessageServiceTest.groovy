package com.blogspot.jvalentino.usnrauto.sms.service

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.blogspot.jvalentino.usnrauto.commons.message.EmailMessage;
import com.blogspot.jvalentino.usnrauto.commons.message.SmsMessage;
import com.blogspot.jvalentino.usnrauto.sms.data.RecipientVO;

class ManualMessageServiceTest {

	private ManualMessageService service
	
	@Before
	void setup() {
		service = new ManualMessageService()
	}
	
	private List<RecipientVO> generatePeople() {
		List<RecipientVO> people = new ArrayList<RecipientVO>()
		
		RecipientVO one = new RecipientVO()
		one.category1 = "Foo"
		one.category2 = "Bar"
		one.emails.add("foo@bar.com")
		one.firstName = "John"
		one.lastName = "Smith"
		one.phoneNumber = null
		one.selected = true
		
		people.add(one)
		
		RecipientVO two = new RecipientVO()
		two.category1 = "Foo"
		two.category2 = "Bar"
		two.firstName = "Kay"
		two.lastName = "Which"
		two.phoneNumber = "555-555-5555"
		two.selected = true
		
		people.add(two)
		
		return people
	}
	
	@Test
	void testGenerateSmsMessages() {
		List<RecipientVO> people = this.generatePeople()
		
		List<SmsMessage> results = service.generateSmsMessages(people, "Hello world")
		
		assertThat(results.size(), is(1))
		
		SmsMessage result = results.get(0)
		assertThat(result.getCell(), is("555-555-5555"))
		assertThat(result.getMessage(), is("Hello world"))
		
	}
	
	@Test
	void testGenerateEmailMessages() {
		List<RecipientVO> people = this.generatePeople()
		
		List<EmailMessage> results = service.generateEmailMessages(
			people, "Hello World", "This is dog", "john@goo.com")
		
		assertThat(results.size(), is(1))
		
		EmailMessage result = results.get(0)
		assertThat(result.content, is("This is dog"))
		assertThat(result.from, is("john@goo.com"))
		assertThat(result.title, is("Hello World"))
		assertThat(result.to.size(), is(1))
		assertThat(result.to.get(0), is("foo@bar.com"))
	}
	
}
