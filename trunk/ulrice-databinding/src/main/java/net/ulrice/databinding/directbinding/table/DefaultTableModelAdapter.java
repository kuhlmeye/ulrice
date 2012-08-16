package net.ulrice.databinding.directbinding.table;

import javax.swing.table.DefaultTableModel;


public class DefaultTableModelAdapter implements TableModelAdapter {
    private final DefaultTableModel tableModel;
    
    public DefaultTableModelAdapter (DefaultTableModel tableModel) {
    	this.tableModel = tableModel;
    }

    public void setSize (int numRows, int numCols) {
        tableModel.setRowCount    (numRows);
        tableModel.setColumnCount (numCols);
    }
}
