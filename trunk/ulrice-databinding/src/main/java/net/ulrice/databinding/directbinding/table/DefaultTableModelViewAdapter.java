package net.ulrice.databinding.directbinding.table;

import javax.swing.table.DefaultTableModel;


public class DefaultTableModelViewAdapter implements TableViewAdapter {
    private final DefaultTableModel _tableModel;
    
    public DefaultTableModelViewAdapter (DefaultTableModel tableModel) {
        _tableModel = tableModel;
    }

    public void setSize (int numRows, int numCols) {
        _tableModel.setRowCount    (numRows);
        _tableModel.setColumnCount (numCols);
    }
}
