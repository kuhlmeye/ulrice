package net.ulrice.databinding.viewadapter.utable;

import java.awt.Insets;
import java.math.BigInteger;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;


public class UTable extends JTable {
    private static final long serialVersionUID = -4005234806899231797L;

    private UTableVAHeader uTableHeader;
	private UTable assocTable;

    private UTableComponent tableComponent;    

	public UTable(UTableComponent tableComponent) {
	    this.tableComponent = tableComponent;
		setAutoCreateColumnsFromModel(false);
		setAutoResizeMode(AUTO_RESIZE_OFF);
		setColumnSelectionAllowed(false);
		setRowSelectionAllowed(true);

		putClientProperty("JTable.autoStartsEdit", Boolean.TRUE);
		putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		
		uTableHeader = new UTableVAHeader(getColumnModel(), new Insets(-2, 0, 1, 1));
		setTableHeader(uTableHeader);
		
        setDefaultRenderer(Integer.class, new UTableVANumericCellRenderer());
        setDefaultRenderer(Double.class, new UTableVANumericCellRenderer());
        setDefaultRenderer(Short.class, new UTableVANumericCellRenderer());
        setDefaultRenderer(Long.class, new UTableVANumericCellRenderer());
        setDefaultRenderer(Float.class, new UTableVANumericCellRenderer());
        setDefaultRenderer(Integer.TYPE, new UTableVANumericCellRenderer());
        setDefaultRenderer(Double.TYPE, new UTableVANumericCellRenderer());
        setDefaultRenderer(Short.TYPE, new UTableVANumericCellRenderer());
        setDefaultRenderer(Long.TYPE, new UTableVANumericCellRenderer());
        setDefaultRenderer(Float.TYPE, new UTableVANumericCellRenderer());
        setDefaultRenderer(Number.class, new UTableVANumericCellRenderer());
        setDefaultRenderer(BigInteger.class, new UTableVANumericCellRenderer());
        setDefaultRenderer(Boolean.class, new UTableVABooleanCellRenderer());
        setDefaultRenderer(Boolean.TYPE, new UTableVABooleanCellRenderer());
        setDefaultEditor(Character.class, new DefaultCellEditor(new JTextField()));                 
	}

	public void setRowHeight(int rowHeight) {
		super.setRowHeight(rowHeight);
		if(assocTable != null && assocTable.getRowHeight() != rowHeight) {
			assocTable.setRowHeight(rowHeight);
		}
	}
	
	
	public void setRowHeight(int row, int rowHeight) {
		super.setRowHeight(row, rowHeight);
		if(assocTable != null && assocTable.getRowHeight(row) != rowHeight) {
			assocTable.setRowHeight(row, rowHeight);
		}
	}

	public void setAssocTable(UTable assocTable) {
		this.assocTable = assocTable;
	}
	
	@Override
	public boolean editCellAt(int row, int column) {
		boolean result = super.editCellAt(row, column);
		
		if(assocTable != null && assocTable.getCellEditor() != null) {
			assocTable.getCellEditor().stopCellEditing();
		}
		return result;
	}
	
	@Override
	public boolean editCellAt(int row, int column, EventObject e) {
		boolean result = super.editCellAt(row, column, e);
		
		if(assocTable != null && assocTable.getCellEditor() != null) {
			assocTable.getCellEditor().stopCellEditing();
		}
		return result;
	}

	public UTableVAHeader getUTableHeader() {
		return uTableHeader;
	}
	
	public UTableComponent getTableComponent() {
        return tableComponent;
    }
}
