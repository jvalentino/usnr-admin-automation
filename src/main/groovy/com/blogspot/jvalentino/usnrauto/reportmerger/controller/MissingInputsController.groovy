package com.blogspot.jvalentino.usnrauto.reportmerger.controller

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import com.blogspot.jvalentino.usnrauto.commons.message.EmailMessage;
import com.blogspot.jvalentino.usnrauto.commons.message.EmailSettings;
import com.blogspot.jvalentino.usnrauto.commons.message.MessageServiceListener;
import com.blogspot.jvalentino.usnrauto.commons.message.MessageType;
import com.blogspot.jvalentino.usnrauto.commons.message.SmsMessage;
import com.blogspot.jvalentino.usnrauto.component.message.MessageComposerDialog;
import com.blogspot.jvalentino.usnrauto.component.message.MessageComposerListener;
import com.blogspot.jvalentino.usnrauto.main.controller.BaseController;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.individual.IndividualSummary;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.MemberWithMissingData;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.SummaryReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.model.MissingInputsModel;
import com.blogspot.jvalentino.usnrauto.reportmerger.model.MissingTableModel;
import com.blogspot.jvalentino.usnrauto.reportmerger.service.ServiceBus;
import com.blogspot.jvalentino.usnrauto.reportmerger.view.MissingInputsView;
import com.blogspot.jvalentino.usnrauto.util.DialogUtil;

class MissingInputsController extends BaseController implements MessageComposerListener, MessageServiceListener {
	
	private MissingInputsView view
	private ServiceBus bus = ServiceBus.getInstance()
	private MissingInputsModel model
	private MessageComposerDialog dialog
	
	
	MissingInputsController(MissingInputsView view, SummaryReport report, 
		List<MemberWithMissingData> list) {
		
		this.view = view
		this.model = new MissingInputsModel()
		this.model.report = report
		this.model.list = list
		
	}
	
	void viewConstructed() {
		MissingInputsController me = this
		
		/*view.smsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.displayComposer(MessageType.SMS)
			}
		});*/
	
		view.emailButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.displayComposer(MessageType.EMAIL)
			}
		});
	
		view.unselectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.select(false)
			}
		});
	
		view.selectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.select(true)
			}
		});
		
		reloadTableModel()
	}
	
	private void select(boolean value) {
		
		for (MemberWithMissingData vo : model.list) {
			vo.selected = value
		}
		
		reloadTableModel()
	}
	
	private void displayComposer(MessageType type) {
		
		EmailSettings settings = new EmailSettings()
		settings.emailRequiresAuthorization = model.report.emailRequiresAuthorization
		settings.emailTlsEnabled = model.report.emailTlsEnabled
		settings.emailHost = model.report.emailHost
		settings.emailPort = model.report.emailPort
		settings.emailUsername = model.report.emailUsername
		
		String message = "We are missing the following non-PII information from you, " +
			"which we manually track in our unit. Please reply with the requested information, " +
			"making sure NOT to provide any PII. Examples of PII: Date of Birth, any part of your " +
			"social security number, personal email address, home phone number, medical information"
		
		List<String> names = new ArrayList<String>()
		for (MemberWithMissingData missing : this.getSelectedPeople()) {
			names.add(missing.member.rank + " " +missing. member.firstName + " " + missing.member.lastName)
		}
		
		String info = null
		
		dialog = new MessageComposerDialog(null, type,
			settings, message, info, names, this)
		dialog.setVisible(true)
	}
	
	private List<MemberWithMissingData> getSelectedPeople() {
		List<MemberWithMissingData> selected = new ArrayList<MemberWithMissingData>();
		for (MemberWithMissingData vo : ((MissingTableModel) view.getTable().getModel()).getPeople()) {
			if (vo.selected) {
				selected.add(vo)
			}
		}
		
		return selected;
	}
	
	private void reloadTableModel() {
		MissingTableModel model = new MissingTableModel(this.model.list);
		view.getTable().setModel(model);
		
		view.getTable().getColumnModel().getColumn(MissingTableModel.SELECTED ).setPreferredWidth(20);
		
	}
	
	@Override
	public void sendEmails(EmailSettings settings, String message, String password) {
		
		// generate a list of emails to be sent
		List<EmailMessage> emails = new ArrayList<EmailMessage>()
		
		for (MemberWithMissingData member : this.getSelectedPeople()) {
			
			EmailMessage email = bus.getSummaryReportService().generateEmailForMissingData(
				message, settings.emailUsername, member)
			
			if (email != null) {
				emails.add(email)
			}
		}
		
		bus.getTrainingNagService().sendEmailsAsync(view, this, settings, password, emails)
		
	}

	@Override
	public void sendTextMessages(String message) {
		
		
	}

	@Override
	public void messagingComplete(List<String> errors) {
		
		DialogUtil.displayMessages(dialog, errors)
		dialog.setVisible(false)
	}
}
