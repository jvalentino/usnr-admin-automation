package com.blogspot.jvalentino.usnrauto.sms.model

import java.io.File;
import java.util.List;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.ManualInputReport;
import com.blogspot.jvalentino.usnrauto.sms.data.RecipientVO;

class SMSModel {

	File lastFile
	List<RecipientVO> people = new ArrayList<RecipientVO>()
	ManualInputReport manual
}
