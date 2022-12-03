package com.blogspot.jvalentino.usnrauto.reportmerger.controller

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import com.blogspot.jvalentino.usnrauto.main.controller.BaseController;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.individual.IndividualSummary;
import com.blogspot.jvalentino.usnrauto.commons.message.EmailMessage;
import com.blogspot.jvalentino.usnrauto.commons.message.EmailSettings;
import com.blogspot.jvalentino.usnrauto.commons.message.MessageServiceListener;
import com.blogspot.jvalentino.usnrauto.commons.message.SmsMessage;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.nag.IndividualNagSummary;
import com.blogspot.jvalentino.usnrauto.commons.message.MessageType;
import com.blogspot.jvalentino.usnrauto.component.message.MessageComposerDialog;
import com.blogspot.jvalentino.usnrauto.component.message.MessageComposerListener;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.SummaryReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.model.MemberTableModel;
import com.blogspot.jvalentino.usnrauto.reportmerger.model.TrainingNagModel;
import com.blogspot.jvalentino.usnrauto.reportmerger.service.ServiceBus;
import com.blogspot.jvalentino.usnrauto.reportmerger.view.TrainingNagView;
import com.blogspot.jvalentino.usnrauto.util.DialogUtil;

class TrainingNagController extends BaseController implements MessageComposerListener, MessageServiceListener {
	private ServiceBus bus = ServiceBus.getInstance()
	
	private TrainingNagModel model
	private TrainingNagView view
	private MessageComposerDialog dialog 
	
	TrainingNagController(TrainingNagView view, SummaryReport summaryReport, List<IndividualSummary> members) {
		this.view = view
		this.model = new TrainingNagModel()
		this.model.members = bus.getTrainingNagService().convert(summaryReport, members)
		this.model.summaryReport = summaryReport
	}
	
	void viewConstructed() {
		TrainingNagController me = this
		
		view.smsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.displayComposer(MessageType.SMS)
			}
		});
	
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
	
		for (IndividualNagSummary vo : model.members) {
			vo.selected = value
		}
		
		reloadTableModel()
	}
	
	private void displayComposer(MessageType type) {
		
		EmailSettings settings = new EmailSettings()
		settings.emailRequiresAuthorization = model.summaryReport.emailRequiresAuthorization
		settings.emailTlsEnabled = model.summaryReport.emailTlsEnabled
		settings.emailHost = model.summaryReport.emailHost
		settings.emailPort = model.summaryReport.emailPort
		settings.emailUsername = model.summaryReport.emailUsername
		
		String message
		
		if (type == MessageType.EMAIL) {
			message = model.summaryReport.trainingEmailHeader	
		} else {
			message = model.summaryReport.trainingSmsHeader
		}
		
		List<String> names = new ArrayList<String>()
		for (IndividualNagSummary member : this.getSelectedPeople()) {
			names.add(member.rank + " " + member.firstName + " " + member.lastName)
		}
		
		String info = "This will allow you to send a message to all of the specified recipients" +
			" that starts with the below message, and that is followed by a list of all of the " + 
			" eLearning and category II GMT courses that the individual recipient is required to do online."
		
		dialog = new MessageComposerDialog(null, type, 
			settings, message, info, names, this)
		dialog.setVisible(true)
	}
	
	private void reloadTableModel() {
		MemberTableModel model = new MemberTableModel(this.model.members);
		view.getTable().setModel(model);
		
		view.getTable().getColumnModel().getColumn(MemberTableModel.SELECTED ).setPreferredWidth(20);
		
	}
		
	/**
	 * Returns only members which have been selected
	 * @return
	 */
	private List<IndividualSummary> getSelectedPeople() {
		List<IndividualNagSummary> selected = new ArrayList<IndividualNagSummary>();
		for (IndividualNagSummary vo : ((MemberTableModel) view.getTable().getModel()).getPeople()) {
			if (vo.selected) {
				selected.add(vo)
			}
		}
		
		return selected;
	}

	@Override
	public void sendEmails(EmailSettings settings, String message, String password) {
		
		List<EmailMessage> emails = bus.getTrainingNagService().generateEmailMessages( 
			this.getSelectedPeople(), message, settings)
				
		bus.getTrainingNagService().sendEmailsAsync(view, this, settings, password, emails)
		
	}

	@Override
	public void sendTextMessages(String message) {
		// generate a list of messages to be sent
		List<SmsMessage> messages = bus.getTrainingNagService().generateSmsMessages(
			this.getSelectedPeople(), message)
		
		bus.getTrainingNagService().sendSmsAsync(view, this, messages)
		
	}

	@Override
	public void messagingComplete(List<String> errors) {
		
		DialogUtil.displayMessages(dialog, errors)
		dialog.setVisible(false)
	}

}
