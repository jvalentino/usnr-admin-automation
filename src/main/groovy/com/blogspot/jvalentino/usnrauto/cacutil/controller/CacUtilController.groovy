package com.blogspot.jvalentino.usnrauto.cacutil.controller

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.KeyStore;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.blogspot.jvalentino.cac.CacUtil;
import com.blogspot.jvalentino.cac.security.CacIdentity;
import com.blogspot.jvalentino.usnrauto.cacutil.model.CacUtilModel;
import com.blogspot.jvalentino.usnrauto.cacutil.service.CacUtilService;
import com.blogspot.jvalentino.usnrauto.cacutil.view.CacUtilView;
import com.blogspot.jvalentino.usnrauto.component.FileByNameFilter;
import com.blogspot.jvalentino.usnrauto.main.AppState;
import com.blogspot.jvalentino.usnrauto.main.controller.BaseController;

class CacUtilController extends BaseController {

	private CacUtilView view
	private CacUtilModel model
	
	private CacUtilService cacUtilService = new CacUtilService()
	
	CacUtilController(CacUtilView view) {
		this.view = view
		this.model = new CacUtilModel()
	}
	
	void viewConstructed() {
		
		CacUtilController me = this
		
		view.loadButton.setEnabled(false)
		
		view.cacKeyBrowseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.browseForCacKey()
			}
		});
	
		view.loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.loadCac()
			}
		});
	
		view.identityList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent listSelectionEvent) {
				boolean adjust = listSelectionEvent.getValueIsAdjusting();
				if (!adjust) {
					me.certificateSelected(view.identityList.getSelectedIndex())
				}
			}
		});
	
		view.identityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
		
		this.handleInitialCacKeySettings()
		
		String pin = cacUtilService.getLastUsedPin()
		
		if (pin != null) {
			view.cacPinField.setText(pin)
		}
	}
	
	private void handleInitialCacKeySettings() {
		this.view.cacKeyField.setEnabled(false)
		this.validateCacKeySettings()
	}
	
	private void validateCacKeySettings() {
		File found = cacUtilService.searchForCacKeyLibrary()
		
		if (found != null) {
			view.cacKeyField.setText(found.getAbsolutePath())
			model.cacKey = true
		} else {
			model.cacKey = false
		}
		
		view.loadButton.setEnabled(model.cacKey)
	}
	
	private void browseForCacKey() {
		
		String fileName = cacUtilService.getCacKeyLibrary()
		
		final JFileChooser fc = new JFileChooser(getLastUserFolder())
		fc.setDialogTitle("Select the library for CACKey called " + fileName)
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileByNameFilter(fileName));
		
		int returnVal = fc.showOpenDialog(null)
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file =  fc.getSelectedFile()
			cacUtilService.storeLastUsedCacKeyLibrary(file)
			this.validateCacKeySettings()
		}
	}
	
	private void loadCac() {
		CacUtilController me = this
		
		String pin = new String(view.cacPinField.getPassword())
		
		
		this.showModalInThread("Loading CAC")
		
		(new Thread() {
			public void run() {
				try {
					File cacKey = cacUtilService.getLastUsedCacKeyLibrary()
					
					// create a card config
					File cardConfig = new File("card.config")
					cardConfig.write("")
					cardConfig.append("name = myConfig\n")
					cardConfig.append("library = " + cacKey.getAbsolutePath() + "\n")
					
					// Get the keystore
					KeyStore ks = CacUtil.getKeyStore(pin.toCharArray(), cardConfig)
					
					// Get the certificates
					me.model.identities = CacUtil.getCacIdentities(pin.toCharArray(), ks)
					
					
					// also store the identifies globally
					AppState.getInstance().identities = me.model.identities
					
					// store the pin
					cacUtilService.storePin(pin)
					
					// display the certificates
					DefaultListModel defaultModel = view.identityList.getModel()
					
					for (CacIdentity identity : me.model.identities) {
						
						// Format out the DOD ID in the friendly name
						String newName = ""
						char[] chars = identity.friendlyName.toCharArray()
						for (int i = 0; i <  chars.length; i++) {
							String c =  chars[i].toString()
							if (c.matches("\\d")) {
								newName += "X"
							} else {
								newName += c
							}
						}
						defaultModel.addElement(newName)
					}
					
				} catch (Exception e) {
					me.showExceptionInDialog(e)
				} finally {
					me.hideModalFromThread()
				}
							
			}
		}).start()
	}
	
	private void certificateSelected(int index) {
		CacIdentity selected = model.identities.get(index)
		view.certInfoArea.setText(selected.chain.toString())
		
		
	}
}
