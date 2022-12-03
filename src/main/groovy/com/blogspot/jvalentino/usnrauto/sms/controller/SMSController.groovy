package com.blogspot.jvalentino.usnrauto.sms.controller

import javax.swing.event.TableModelListener;

import com.blogspot.jvalentino.usnrauto.commons.MessageService;
import com.blogspot.jvalentino.usnrauto.commons.message.EmailMessage;
import com.blogspot.jvalentino.usnrauto.commons.message.EmailSettings;
import com.blogspot.jvalentino.usnrauto.commons.message.MessageServiceListener;
import com.blogspot.jvalentino.usnrauto.commons.message.MessageType;
import com.blogspot.jvalentino.usnrauto.commons.message.SmsMessage;
import com.blogspot.jvalentino.usnrauto.component.message.MessageComposerDialog;
import com.blogspot.jvalentino.usnrauto.component.message.MessageComposerListener;
import com.blogspot.jvalentino.usnrauto.main.controller.BaseController;
import com.blogspot.jvalentino.usnrauto.sms.view.SMSView;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.blogspot.jvalentino.usnrauto.sms.model.SMSModel;
import com.blogspot.jvalentino.usnrauto.sms.model.TableModel;
import com.blogspot.jvalentino.usnrauto.sms.model.TableModelListener;
import com.blogspot.jvalentino.usnrauto.sms.service.ManualInputsService;
import com.blogspot.jvalentino.usnrauto.sms.service.ManualMessageService;
import com.blogspot.jvalentino.usnrauto.sms.component.ProgressDialog;
import com.blogspot.jvalentino.usnrauto.sms.data.RecipientVO;
import com.blogspot.jvalentino.usnrauto.util.DialogUtil;


class SMSController extends BaseController implements TableModelListener, MessageComposerListener, MessageServiceListener {

	private SMSView ui
	private SMSModel model = new SMSModel()
	
	private ManualInputsService manualInputsService = new ManualInputsService()
	private ManualMessageService messageService = new ManualMessageService()
	private MessageComposerDialog messageComposerDialog
		
	SMSController(SMSView ui) {
		this.ui = ui
	}
	
	
	public void show() {
		
		SMSController me = this
		
		
		ui.getReloadButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.reload();
			}
		});
		
		ui.getFirstCombo().addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	me.filter();
		    }
		});
		
		ui.getLastCombo().addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	me.filter();
		    }
		});
		
		ui.getCatOneCombo().addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	me.filter();
		    }
		});
		ui.getCatTwoCombo().addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	me.filter();
		    }
		});
		
		ui.getClearButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.clear();
			}
		});
		
		ui.getComposeButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.compose(MessageType.SMS);
			}
		});
	
		ui.emailButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.compose(MessageType.EMAIL);
			}
		});
		
		
		ui.manualInputsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.loadManualInputs()
			}
		});
	
		ui.showEmailList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.copyEmails()
			}
		});
	
		ui.getReloadButton().setEnabled(false)
		ui.getComposeButton().setEnabled(false)
		ui.emailButton.setEnabled(false)
		ui.getClearButton().setEnabled(false)
		ui.showEmailList.setEnabled(false)
	}
	
	
	
	

	
	public List<RecipientVO> loadPeople() {
		try {
			this.model.people = manualInputsService.loadRecipientsFromFile(model.lastFile);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this.ui, e.getMessage());
		}
		
		return this.model.people;
	}
	
	private void clear() {
		ui.getFirstCombo().setSelectedIndex(0);
		ui.getLastCombo().setSelectedIndex(0);
		ui.getCatOneCombo().setSelectedIndex(0);
		ui.getCatTwoCombo().setSelectedIndex(0);
		
		updateSelectedPeople();
	}
	
	private void reload() {
		this.loadPeople();
		
		updateFromPeople()
	}
	
	private void updateFromPeople() {
		reloadTableModel(this.model.people);


		ui.getFirstCombo().setModel( this.getFirstNames() );
		ui.getLastCombo().setModel( this.getLastNames() );
		ui.getCatOneCombo().setModel( this.getCatOnes() );
		ui.getCatTwoCombo().setModel( this.getCatTwos() );

		this.updateSelectedPeople();

		ui.getClearButton().setEnabled(true)
		ui.showEmailList.setEnabled(true)
	}
	
	private void filter() {
		String firstName = ui.getFirstCombo().getSelectedItem().toString();
		String lastName = ui.getLastCombo().getSelectedItem().toString();
		String catOne = ui.getCatOneCombo().getSelectedItem().toString();
		String catTwo = ui.getCatTwoCombo().getSelectedItem().toString();
				
		List<RecipientVO> list = new ArrayList<RecipientVO>();
		
		for (RecipientVO vo : this.model.people) {
			
			// first name
			if (firstName.equals("") || vo.getFirstName().equalsIgnoreCase(firstName)) {
				// the first name is a match
			} else {
				continue;
			}
			
			// last name
			if (lastName.equals("")
					|| vo.getLastName().equalsIgnoreCase(lastName)) {
				// the last name is a match
			} else {
				continue;
			}
			
			if (catOne.equals("")
					|| vo.getCategory1().equalsIgnoreCase(catOne)) {
				// the last name is a match
			} else {
				continue;
			}
			
			if (catTwo.equals("")
					|| vo.getCategory2().equalsIgnoreCase(catTwo)) {
				// the last name is a match
			} else {
				continue;
			}
			
			list.add(vo.copy());
		}
		
		reloadTableModel(list);
		
	}
	
	private void reloadTableModel(List<RecipientVO> list) {
		TableModel model = new TableModel(this, list);
		ui.getTable().setModel(model);
		
		ui.getTable().getColumnModel().getColumn(TableModel.SELECTED ).setPreferredWidth(20);
		
		updateSelectedPeople();
	}
	
	private DefaultComboBoxModel<String> getFirstNames() {
		DefaultComboBoxModel<String> dcm = new DefaultComboBoxModel<String>();
		dcm.addElement("");
		for (RecipientVO vo : this.model.people) {
			if (dcm.getIndexOf(vo.getFirstName()) == -1)
				dcm.addElement(vo.getFirstName());
		}
		return dcm;
	}
	
	private DefaultComboBoxModel<String> getLastNames() {
		DefaultComboBoxModel<String> dcm = new DefaultComboBoxModel<String>();
		dcm.addElement("");
		for (RecipientVO vo : this.model.people) {
			if (dcm.getIndexOf(vo.getLastName()) == -1)
				dcm.addElement(vo.getLastName());
		}
		return dcm;
	}
	
	private DefaultComboBoxModel<String> getCatOnes() {
		DefaultComboBoxModel<String> dcm = new DefaultComboBoxModel<String>();
		dcm.addElement("");
		for (RecipientVO vo : this.model.people) {
			if (dcm.getIndexOf(vo.getCategory1()) == -1)
				dcm.addElement(vo.getCategory1());
		}
		return dcm;
	}
	
	private DefaultComboBoxModel<String> getCatTwos() {
		DefaultComboBoxModel<String> dcm = new DefaultComboBoxModel<String>();
		dcm.addElement("");
		for (RecipientVO vo : this.model.people) {
			if (dcm.getIndexOf(vo.getCategory2()) == -1)
				dcm.addElement(vo.getCategory2());
		}
		return dcm;
	}
	
	

	@Override
	public void selectionStateChanged() {
		updateSelectedPeople();
	}
	
	private void updateSelectedPeople() {
		String text = null;
		String emailText = null
		
		int count = getSelectedPeople().size();		
		
		if (count == 1) {
			text = "SMS to 1 Person";
			emailText = "Email to 1 Person"
		} else {
			text = "SMS to " + count + " People"
			emailText = "Email to " + count + " People"
		}
		
		ui.getComposeButton().setText(text);
		ui.emailButton.setText(emailText)
		
		if (count == 0) {
			ui.getComposeButton().setEnabled(false);
			ui.emailButton.setEnabled(false)
		} else {
			ui.getComposeButton().setEnabled(true);
			ui.emailButton.setEnabled(true)
		}
	}
	
	private List<RecipientVO> getSelectedPeople() {
		List<RecipientVO> selected = new ArrayList<RecipientVO>();
		for (RecipientVO vo : ((TableModel) ui.getTable().getModel()).getPeople()) {
			if (vo.isSelected()) {
				selected.add(vo.copy());
			}
		}
		
		return selected;
	}
	
	private void compose(MessageType type) {
		
		List<String> names = new ArrayList<String>()
		for (RecipientVO vo : this.getSelectedPeople()) {
			names.add(vo.firstName + " " + vo.lastName)
		}
		
		EmailSettings settings = new EmailSettings()
		settings.emailRequiresAuthorization = model.manual.emailRequiresAuthorization
		settings.emailTlsEnabled = model.manual.emailTlsEnabled
		settings.emailHost = model.manual.emailHost
		settings.emailPort = model.manual.emailPort
		settings.emailUsername = model.manual.emailUsername
		
		messageComposerDialog = new MessageComposerDialog(null, type,
			settings, "", null, names, this)
		messageComposerDialog.setVisible(true)
	}
	
	
	private void loadManualInputs() {
		SMSController me = this
		
		File file = this.browseForFileType("xlsx", "Open Manual Inputs XLSX file")
		
		if (file == null) {
			return
		}
		
		this.showModalInThread("Loading Manual Inputs")
		
		(new Thread() {
			public void run() {
				try {
					me.model.manual = manualInputsService.loadManualInputs(file)
					me.model.people = manualInputsService.load(me.model.manual)
					me.updateFromPeople()
				} catch (Exception e) {
					me.showExceptionInDialog(e)
				} finally {
					me.hideModalFromThread()
				}
							
			}
		}).start()
		
	}	
	
	private void copyEmails() {
		String emails = ""
		for (RecipientVO vo : getSelectedPeople()) {
			
			emails += vo.emailsToString()
			
		}
				
		StringSelection selection = new StringSelection(emails);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
		
		println emails
	}


	@Override
	public void messagingComplete(List<String> errors) {
		DialogUtil.displayMessages(messageComposerDialog, errors)
		messageComposerDialog.setVisible(false)
	}


	@Override
	public void sendEmails(EmailSettings settings, String message,
			String password) {
		
		List<EmailMessage> messages = messageService.generateEmailMessages(
			this.getSelectedPeople(), "USNR Admin Automation Project Message", message, settings.emailUsername)
		
		messageService.sendEmailsAsync(ui, this, settings, password, messages)
	}


	@Override
	public void sendTextMessages(String message) {
		// generate a list of messages
		List<SmsMessage> messages = messageService.generateSmsMessages(this.getSelectedPeople(), message)
		
		// send them in a thread
		messageService.sendSmsAsync(ui, this, messages)
		
	}
}
