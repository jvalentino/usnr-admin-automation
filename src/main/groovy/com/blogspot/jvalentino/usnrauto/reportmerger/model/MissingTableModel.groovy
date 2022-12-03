package com.blogspot.jvalentino.usnrauto.reportmerger.model

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.nag.IndividualNagSummary;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.MemberWithMissingData;

class MissingTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L
	
	static final int SELECTED = 0
	static final int FIRST_NAME = 1
	static final int LAST_NAME = 2
	static final int RANK = 3
	static final int MISSING_STUFF = 4
	static final int CELL = 5
	static final int EMAILS = 6
	
	private List<MemberWithMissingData> list
	
	MissingTableModel(List<MemberWithMissingData> members) {
		this.list = members
	}
	
	List<MemberWithMissingData> getPeople() {
		return list
	}

	@Override
	public int getRowCount() {
		return list.size()
	}

	@Override
	public int getColumnCount() {
		return 8;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		MemberWithMissingData entity = list.get(rowIndex)
		
		switch(columnIndex) {
		case FIRST_NAME:
			return entity.member.firstName
		case LAST_NAME:
			return entity.member.lastName
		case SELECTED:
			return entity.selected
		case RANK:
			return entity.member.rank
		case MISSING_STUFF:
			return entity.missing.size()
		case CELL:
			return entity.cell != null ? entity.cell : ""
		case EMAILS:
			String value = ""
			for (String email : entity.emails) {
				value += email + ";"
			}
			return value
			
		}
		
		return "";
	}
	
	@Override
	public String getColumnName(int col) {
		switch(col) {
		case FIRST_NAME:
			return "First Name";
		case LAST_NAME:
			return "Last Name";
		case SELECTED:
			return "Selected?";
		case RANK:
			return "Rank";
		case MISSING_STUFF:
			return "Missing Data Elements"
		case CELL:
			return "Cell #"
		case EMAILS:
			return "Emails"
		}
		
		return "";
	}
	
	public void setValueAt(Object value, int row, int column) {
		if (column == SELECTED) {
			list.get(row).setSelected((boolean) value);
		}
	}

	public boolean isCellEditable(int row, int column) {
		return (column == SELECTED);
	}
	
	public Class<?> getColumnClass(int column) {
		return (getValueAt(0, column).getClass());
	 }

}
