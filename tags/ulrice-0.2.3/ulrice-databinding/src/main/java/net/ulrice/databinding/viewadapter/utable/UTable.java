package net.ulrice.databinding.viewadapter.utable;

import java.awt.Insets;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;


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
		
		uTableHeader = new UTableVAHeader(getColumnModel(), new Insets(1, 1, 3, 1));
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
        setDefaultRenderer(Boolean.class, new UTableVABooleanCellRenderer());
        setDefaultRenderer(Boolean.TYPE, new UTableVABooleanCellRenderer());
        setDefaultEditor(Character.class, new DefaultCellEditor(new JTextField()));
        // TODO add additional renderer
//        setDefaultRenderer(Object.class, new YTableTextCellRenderer()); // needed, or handled by default?
//        setDefaultRenderer(Icon.class, new UTableVAIconCellRenderer());
//        setDefaultRenderer(ImageIcon.class, new UTableVAIconCellRenderer());
//        setDefaultRenderer(JComboBox.class, new UTableVAComboBoxCellRenderer());
//        setDefaultRenderer(List.class, new UTableVATextCellRenderer()); // needed, or handled by default?                    
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