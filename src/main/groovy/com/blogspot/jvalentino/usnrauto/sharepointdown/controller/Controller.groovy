package com.blogspot.jvalentino.usnrauto.sharepointdown.controller

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import org.apache.commons.lang.SystemUtils;
import org.joda.time.DateTime;

import com.blogspot.jvalentino.cac.security.CacIdentity;
import com.blogspot.jvalentino.usnrauto.component.JProgressDialog;
import com.blogspot.jvalentino.usnrauto.component.generaltable.ButtonColumn;
import com.blogspot.jvalentino.usnrauto.component.progress.ProgressAreaDialog;
import com.blogspot.jvalentino.usnrauto.main.AppState;
import com.blogspot.jvalentino.usnrauto.main.controller.BaseController;
import com.blogspot.jvalentino.usnrauto.sharepointdown.data.Download;
import com.blogspot.jvalentino.usnrauto.sharepointdown.model.DownloadTableModel;
import com.blogspot.jvalentino.usnrauto.sharepointdown.model.SharePointDownModel;
import com.blogspot.jvalentino.usnrauto.sharepointdown.service.SharePointService;
import com.blogspot.jvalentino.usnrauto.sharepointdown.view.SharePointDownView;
import com.blogspot.jvalentino.usnrauto.util.FormatUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

class Controller extends BaseController {
	
	private SharePointDownView view
	private SharePointDownModel model
	private SharePointService service = new SharePointService()
	
	private static final String DOWNLOADS = "DOWNLOADS"
	
	private ProgressAreaDialog progress
	
	Controller(SharePointDownView view) {
		this.view = view
		this.model = new SharePointDownModel()
	}
	
	void viewContructed() {
		this.view.urlField.setText("https://private.navyreserve.navy.mil/...")
		this.view.localField.setEnabled(false)
		this.view.downloadButton.setEnabled(false)
		
		this.model.defaultLocation = new File(
			SystemUtils.getUserHome().getAbsolutePath() + File.separator + "sharepoint")
		this.model.currentLocation = this.model.defaultLocation
		
		this.view.localField.setText(model.defaultLocation.getAbsolutePath())
		
		this.addListeners()
		
		this.view.table.setRowSelectionAllowed(false)
		this.reloadTable()
		
	}
	
	private void addListeners() {
		Controller me = this
		
		this.view.browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.browseButtonPressed()
			}
		});
		
		this.view.addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.addButtonPressed()
			}
		});
	
		this.view.downloadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				me.downloadButtonPressed()
			}
		});
	}
	
	private void reloadTable() {
		Controller me = this
		
		//Grab the downloads from preferences
		String json = this.getPreferenceAsString(DOWNLOADS)
		if (json != null) {
			model.downloads = new Gson().fromJson(json, new TypeToken<List<Download>>(){}.getType());
		}
		
		DownloadTableModel model = new DownloadTableModel(model.downloads);
		view.table.setModel(model);
		
		Action delete = new AbstractAction() {
			void actionPerformed(ActionEvent e) {
				 JTable table = (JTable) e.getSource()
				 int modelRow = Integer.valueOf( e.getActionCommand() )
				 me.deleteEntry(modelRow)
			}
		};
		
		ButtonColumn button = new ButtonColumn(view.table, delete, DownloadTableModel.BUTTON)
		view.table.getColumnModel().getColumn(DownloadTableModel.BUTTON).setCellEditor(button);
		view.table.getColumnModel().getColumn(DownloadTableModel.BUTTON).setCellRenderer(button);
		view.table.getColumnModel().getColumn(0).setPreferredWidth(500)
		view.table.getColumnModel().getColumn(1).setPreferredWidth(200)
		
		boolean enableDownload = this.model.downloads.size() > 0
		view.downloadButton.setEnabled(enableDownload)
		
	}
	
	private void browseButtonPressed() {
		this.model.defaultLocation.mkdirs()
		
		JFileChooser fc = new JFileChooser(this.model.defaultLocation);
		
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setDialogType(JFileChooser.SAVE_DIALOG);
		fc.setApproveButtonText("Select");
		int returnVal = fc.showSaveDialog(null)
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			this.model.currentLocation = fc.getSelectedFile()
			this.view.localField.setText(this.model.currentLocation.getAbsolutePath())
		}
				
	}
	
	private void addButtonPressed() {
		String url = this.view.urlField.getText().trim()
		String error = service.isLocationValid(url, this.model.currentLocation)

		if (error != null) {
			JOptionPane.showMessageDialog(null,
					error,
					"ERROR",
					JOptionPane.ERROR_MESSAGE)
		} else {
			this.addNewEntry(url, this.model.currentLocation)
		}
	}
	
	private void addNewEntry(String url, File location) {
		Download d = new Download(url, location.getAbsolutePath())
		this.model.downloads.add(d)
		storeDownloadsAndRefresh()
	}
	
	private void deleteEntry(int row) {
		this.model.downloads.remove(row)
		storeDownloadsAndRefresh()
	}
	
	private void storeDownloadsAndRefresh() {
		// convert the download list to JSON
		String json = new Gson().toJson(this.model.downloads);
		this.storeStringAsPreference(DOWNLOADS, json)
		
		this.reloadTable()
	}
	
	private void downloadButtonPressed() {
		// do we have a CAC?
		List<CacIdentity> identities = AppState.getInstance().identities
		
		if (identities.size() == 0) {
			JOptionPane.showMessageDialog(null,
				"You need to load the certificates from your CAC using the CAC Utility",
				"ERROR",
				JOptionPane.ERROR_MESSAGE)
			return
		}
		
		Controller me = this
		
		DateTime start = new DateTime()
		
		me.progress = new ProgressAreaDialog("Downloading SharePoint")
		me.progress.setModal(true)
		
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				me.progress.setVisible(true)
			}
		});
		
		(new Thread() {
			public void run() {
				try {
					service.download(identities, model.downloads, me.progress)
					
					DateTime end = new DateTime()
					String display = FormatUtil.getFriendlyDifference(start, end)
					
					me.progress.appendLine("")
					me.progress.appendLine(display)
					me.progress.readyToClose()
					
				} catch (Exception e) {
					me.progress.setVisible(false)
					me.showExceptionInDialog(e)
				} 
							
			}
		}).start()
		
	}
}
