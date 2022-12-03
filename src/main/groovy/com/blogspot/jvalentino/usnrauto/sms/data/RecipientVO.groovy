package com.blogspot.jvalentino.usnrauto.sms.data

class RecipientVO {

	String phoneNumber;
	List<String> emails = new ArrayList<String>();
	String lastName;
	String firstName;
	// Rate/Rank
	String category1;
	// White Hat, Chief, Officer
	String category2;
	
	private boolean selected = true;

	public RecipientVO copy() {
		RecipientVO vo = new RecipientVO();
		vo.setCategory1(this.category1);
		vo.setCategory2(category2);
		for (String email : emails) {
			vo.emails.add(email)
		}
		vo.setFirstName(firstName);
		vo.setLastName(lastName);
		vo.setPhoneNumber(phoneNumber);
		return vo;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public String toString() {
		
		return this.lastName + " | " + this.firstName + " | " + 
			this.category1 + " | " + this.category2 + " | " + 
			this.phoneNumber + " | " + this.emailsToString()
	}
	
	public String emailsToString() {
		String result = ""
		for (String email : emails) {
			result += email + "; "
		}
		return result
	}
	
	
}