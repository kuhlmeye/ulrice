package net.ulrice.databinding.directbinding.table;

import java.util.List;

import javax.swing.table.DefaultTableModel;




public class TypedEditableColumnTableModel extends DefaultTableModel implements WithTypesPerColumn, EditableTableModel {
    private List<Class<?>> columnTypes;
    private List<Boolean> columnsEditable;

    @Override
    public Class<?> getColumnClass (int columnIndex) {
        if (columnTypes == null)
            return super.getColumnClass (columnIndex);
        
        return columnTypes.get (columnIndex);
    }
    
    @Override
    public boolean isCellEditable (int row, int column) {
        if (columnsEditable == null)
            return false;
        
        return columnsEditable.get (column);
    }
    
    @Override
    public void setColumnTypes (List<Class<?>> types) {
        columnTypes = types;
    }

    @Override
    public void setEditable (List<Boolean> columnsEditable) {
    	this.columnsEditable = columnsEditable;
    }
}
