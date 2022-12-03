package com.blogspot.jvalentino.usnrauto.sms.service

import java.io.File;
import java.util.List;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.ManualInputReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.manual.ManualMemberRecord;
import com.blogspot.jvalentino.usnrauto.reportmerger.service.ServiceBus;
import com.blogspot.jvalentino.usnrauto.sms.data.RecipientVO;
import com.blogspot.jvalentino.usnrauto.util.FormatUtil;

class ManualInputsService {

	ManualInputReport loadManualInputs(File file) {
		ManualInputReport manual = ServiceBus.getInstance().getManualInputService().parse(file)
		return manual
	}
	
	/**
	 * Converts a Manual inputs file into a list of RecipientVOs to be used for SMS
	 * @param file
	 * @return
	 */
	public List<RecipientVO> load(ManualInputReport manual) {
		List<RecipientVO> list = new ArrayList<RecipientVO>()
				
		for (ManualMemberRecord member : manual.members) {
			RecipientVO vo = new RecipientVO()
			
			// find the cell phone and emails
			for (int i = 0; i < manual.secondaryColumnHeaders.size(); i++) {
				String value =  manual.secondaryColumnHeaders.get(i).toLowerCase()
				
				if (value.contains("cell #")) {
					if (FormatUtil.isValidPhone(member.values[i])) {
						vo.phoneNumber = member.values[i]
					}
				} else if (value.contains("email")) {
					if (member.values[i].contains("@")) {
						vo.emails.add(member.values[i])
					}
				}
			}
			
			// determine if WHITEHAT or KHAKI
			String category2 = "WHITEHAT"
			if (ServiceBus.getInstance().summaryReportService.officerRanks.contains(member.rank)) {
				category2 = "KHAKI"
			} else if (member.rank.endsWith("C") || member.rank.endsWith("CS") || member.rank.endsWith("CM")) {
				category2 = "KHAKI"
			}
			
			vo.firstName = member.firstName
			vo.lastName = member.lastName
			vo.category1 = member.rank
			vo.category2 = category2
			
			list.add(vo)
			
		}
		
		return list
	}
	
	List<RecipientVO> loadRecipientsFromFile(File file) throws Exception {
		List<RecipientVO> list = new ArrayList<RecipientVO>();
		
		BufferedReader ins = new BufferedReader(new FileReader(file));
		
		String line;
		int lineNumber = 0;
		while (( line = ins.readLine()) != null)  {
			line = line.trim();
			
			lineNumber++;
			
			if (lineNumber == 1) {
				continue;
			}
			
			if (line.length() == 0) {
				continue;
			}
			
			String[] split = line.split(",");
			
			if (split.length != 6) {
				String message = "Error in person.csv on line " + lineNumber;
				message += "\nA person must consist of First Name,Last Name,Category 1,Category 2,Cell,Email";
				message += "\nIf you don't know one of those fields, use a ?";
				ins.close();
				throw new Exception(message);
			}
			
			RecipientVO vo = new RecipientVO();
			vo.setFirstName(split[0]);
			vo.setLastName(split[1]);
			vo.setCategory1(split[2]);
			vo.setCategory2(split[3]);
			vo.setPhoneNumber(split[4]);
			vo.setEmails(split[5].split(";"));
			list.add(vo);
			
		}
		
		ins.close();
		
		return list;
	}
	
}
