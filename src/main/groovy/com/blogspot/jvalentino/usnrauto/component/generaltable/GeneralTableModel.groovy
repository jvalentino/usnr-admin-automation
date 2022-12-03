package com.blogspot.jvalentino.usnrauto.component.generaltable

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.swing.table.AbstractTableModel;

import com.blogspot.jvalentino.usnrauto.util.FormatUtil;

class GeneralTableModel extends AbstractTableModel {

    List<Object> list;
	List<Field> fields
	List<String> friendlyColumnNames

    public GeneralTableModel(List<Object> list){
        this.list = list;
		
		fields = this.determineFields(list)
		
		friendlyColumnNames = new ArrayList<String>()
		
		for (Field field : fields) {
			
			FriendlyName fn = field.getAnnotation(FriendlyName.class)
			if (fn != null) {
				friendlyColumnNames.add(fn.column())
			} else {
				friendlyColumnNames.add(field.getName())
			}
		}
		
    }
	
	List<Field> determineFields(List<Object> list) {
		List<Field> result = new ArrayList<Field>()
		
		if (list.size() == 0) {
			return result
		}
		
		Object obj = list.get(0)
		
		// determine the number of fields
		List<Field> rootFields = getFieldsFromClass(obj.getClass())
		List<Field> superFields = getFieldsFromClass(obj.getClass().getSuperclass())
		List<Field> supersuper =  getFieldsFromClass(obj.getClass().getSuperclass().getSuperclass())
		
		for (Field field : rootFields) {
			result.add(field)
		}
		
		for (Field field : superFields) {
			result.add(field)
		}
		
		for (Field field : supersuper) {
			result.add(field)
		}
		
		return result
	}
	
	List<Field> getFieldsFromClass(Class clazz) {
		
		List<Field> result = new ArrayList<Field>()
		
		if (!clazz.toString().startsWith("class com.blogspot.jvalentino")) {
			return result
		}
		
		Field[] fields = clazz.getDeclaredFields();
		
		for (Field field : fields) {
			if (field.getName().startsWith("\$")) {
				continue
			}
			
			if (field.getName().startsWith("_")) {
				continue
			}
			
			if (field.getName().startsWith("metaClass")) {
				continue
			}
			
			result.add(field)
		}
		
		return result
	}

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return fields.size()
    }

    /*public void add(Staff staff) {
        int size = getSize();
        staffs.add(staff);
        fireTableRowsInserted(size, size);
    }*/

    /*public void remove(Staff staff) {
        if (staffs.contains(staff) {
            int index = stafff.indexOf(staff);
            staffs.remove(staff);
            fireTableRowsRemove(index, index);
        }
    }*/

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object element = list.get(rowIndex);
		
		Field field = fields.get(columnIndex);
		field.accessible = true
		
		Object value = field.get(element);
		
		if (value == null) {
			return ""
		} else if (value instanceof Date) {
			return FormatUtil.dateToFormatTwoString((Date) value)
		}
		
		
        return value.toString()
    }
	
	@Override
	public String getColumnName(int col) {
		return friendlyColumnNames.get(col)
	}

}
