package com.blogspot.jvalentino.usnrauto.sharepointdown.model

import javax.swing.table.AbstractTableModel;

import com.blogspot.jvalentino.usnrauto.sharepointdown.data.Download;


class DownloadTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L

	static final int URL = 0
	static final int FILE = 1
	static final int BUTTON = 2
	
	List<Download> list
	
	DownloadTableModel(List<Download> list) {
		this.list = list
	}

	@Override
	public int getRowCount() {
		return list.size()
	}

	@Override
	public int getColumnCount() {
		return 3
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Download result = list.get(rowIndex)
		switch(columnIndex) {
			case URL:
				return result.url
			case FILE:
				return result.directory
			case BUTTON:
				return "Delete"
		}
		return ""
	}	
	
	public String getColumnName(int col) {
		switch(col) {
		case URL:
			return "SharePoint URL";
		case FILE:
			return "Download to Directory";
		}
		
		return "";
	}
	
	public boolean isCellEditable(int row, int column)
	{
	  return column == BUTTON;
	}
	

}
