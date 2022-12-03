package com.blogspot.jvalentino.usnrauto.commons.message

class SmsMessage {
	String name
	String cell
	String message
	
	SmsMessage(String name, String cell, String message) {
		this.name = name
		this.cell = cell
		this.message = message
	}
	
}
