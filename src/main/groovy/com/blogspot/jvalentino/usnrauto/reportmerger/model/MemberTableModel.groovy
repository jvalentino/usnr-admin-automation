package com.blogspot.jvalentino.usnrauto.reportmerger.model

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.individual.IndividualSummary;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.nag.IndividualNagSummary;

class MemberTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L
	
	static final int SELECTED = 0
	static final int FIRST_NAME = 1
	static final int LAST_NAME = 2
	static final int RANK = 3
	static final int ELEARNING_COUNT = 4
	static final int CAT_2_GMT_COUNT = 5
	static final int CELL = 6
	static final int EMAILS = 7
	
	private List<IndividualNagSummary> list
	
	MemberTableModel(List<IndividualNagSummary> members) {
		this.list = members
	}
	
	List<IndividualNagSummary> getPeople() {
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
		
		IndividualNagSummary entity = list.get(rowIndex)
		
		switch(columnIndex) {
		case FIRST_NAME:
			return entity.firstName
		case LAST_NAME:
			return entity.lastName
		case SELECTED:
			return entity.selected
		case RANK:
			return entity.rank
		case ELEARNING_COUNT:
			return entity.geteLearningCoursesToDo().size()
		case CAT_2_GMT_COUNT:
			return entity.getCategoryTwoGMTsToDo().size()
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
		case ELEARNING_COUNT:
			return "eLearning Courses Left"
		case CAT_2_GMT_COUNT:
			return "Category II Courses Left"
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
