package net.ulrice.databinding.directbinding.table;

import javax.swing.table.DefaultTableModel;




public class DefaultTableModelColumnAdapter implements ColumnAdapter {
    private final Class<?> type;
    private final DefaultTableModel tableModel;
    private final int column;
    private final boolean isReadOnly;

    public DefaultTableModelColumnAdapter (DefaultTableModel tableModel, Class<?> type, int column, boolean isReadOnly) {
    	this.type = type;
    	this.tableModel = tableModel;
    	this.column = column;
    	this.isReadOnly = isReadOnly;
    }

    public Object getValue (int index) {
        return tableModel.getValueAt (index, column);
    }

    public Class<?> getViewType () {
        return type;
    }

    public void setValue (int index, Object value) {
        tableModel.setValueAt (value, index, column);
    }

    public boolean isReadOnly () {
        return isReadOnly;
    }
}
