package net.ulrice.databinding.directbinding.table;

import javax.swing.table.DefaultTableModel;




public class DefaultTableModelColumnViewAdapter implements IndexedViewAdapter {
    private final Class<?> _type;
    private final DefaultTableModel _tableModel;
    private final int _column;
    private final boolean _isReadOnly;

    public DefaultTableModelColumnViewAdapter (DefaultTableModel tableModel, Class<?> type, int column, boolean isReadOnly) {
        _type = type;
        _tableModel = tableModel;
        _column = column;
        _isReadOnly = isReadOnly;
    }

    public Object getValue (int index) {
        return _tableModel.getValueAt (index, _column);
    }

    public Class<?> getViewType () {
        return _type;
    }

    public void setValue (int index, Object value) {
        _tableModel.setValueAt (value, index, _column);
    }

    public boolean isReadOnly () {
        return _isReadOnly;
    }
}
