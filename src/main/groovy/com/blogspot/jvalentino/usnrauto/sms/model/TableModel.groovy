package com.blogspot.jvalentino.usnrauto.sms.model

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.blogspot.jvalentino.usnrauto.sms.data.RecipientVO;


class TableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	public static final int SELECTED = 0;
	public static final int FIRST_NAME = 1;
	public static final int LAST_NAME = 2;
	public static final int CAT_1 = 3;
	public static final int CAT_2 = 4;
	public static final int CELL = 5;
	public static final int EMAIL = 6;
	
	private List<RecipientVO> list;
	
	private TableModelListener listener;
	
	public TableModel(TableModelListener listener, List<RecipientVO> list) {
		this.list = list;
		this.listener = listener;
	}
	
	public  List<RecipientVO> getPeople() {
		return list;
	}
	
	@Override
	public int getRowCount() {
		return list.size();
	}

	@Override
	public int getColumnCount() {
		return EMAIL + 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object result = null;
		
		RecipientVO entity = list.get(rowIndex);
		
		switch(columnIndex) {
		case FIRST_NAME:
			result = entity.getFirstName();
			break;
		case LAST_NAME:
			result = entity.getLastName();
			break;
		case CAT_1:
			result = entity.getCategory1();
			break;
		case CAT_2:
			result = entity.getCategory2();
			break;
		case CELL:
			result = entity.getPhoneNumber();
			break;
		case EMAIL:
			result = entity.emailsToString();
			break;
		case SELECTED:
			result = entity.isSelected();
			break;
		}
		
		return result;
	}

	@Override
	public String getColumnName(int col) {
		switch(col) {
		case FIRST_NAME:
			return "First Name";
		case LAST_NAME:
			return "Last Name";
		case CAT_1:
			return "Category 1";
		case CAT_2:
			return "Category 2";
		case CELL:
			return "Cell";
		case EMAIL:
			return "Email";
		case SELECTED:
			return "Selected?";
		}
		
		return "";
	}
	
	public void setValueAt(Object value, int row, int column) {
		if (column == SELECTED) {
			list.get(row).setSelected((boolean) value); 
			this.listener.selectionStateChanged();
		}
	}

	public boolean isCellEditable(int row, int column) {
		return (column == SELECTED);
	}
	
	public Class<?> getColumnClass(int column) {
	    return (getValueAt(0, column).getClass());
	  }

}
