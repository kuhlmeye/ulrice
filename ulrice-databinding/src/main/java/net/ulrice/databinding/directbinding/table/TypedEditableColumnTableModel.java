package net.ulrice.databinding.directbinding.table;

import java.util.List;

import javax.swing.table.DefaultTableModel;




public class TypedEditableColumnTableModel extends DefaultTableModel implements WithTypesPerColumn, EditableTableModel {
    private List<Class<?>> _columnTypes;
    private List<Boolean> _columnsEditable;

    @Override
    public Class<?> getColumnClass (int columnIndex) {
        if (_columnTypes == null)
            return super.getColumnClass (columnIndex);
        
        return _columnTypes.get (columnIndex);
    }
    
    @Override
    public boolean isCellEditable (int row, int column) {
        if (_columnsEditable == null)
            return false;
        
        return _columnsEditable.get (column);
    }
    
    @Override
    public void setColumnTypes (List<Class<?>> types) {
        _columnTypes = types;
    }

    @Override
    public void setEditable (List<Boolean> columnsEditable) {
        _columnsEditable = columnsEditable;
    }
}
