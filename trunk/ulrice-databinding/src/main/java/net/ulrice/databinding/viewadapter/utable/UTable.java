package net.ulrice.databinding.viewadapter.utable;

import java.awt.Insets;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;

public class UTable extends JTable {

	private UTableVAHeader uTableHeader;
	private UTable assocTable;

	public UTable(UTableViewAdapter viewAdapter, UTableModel model, ListSelectionModel selectionModel) {
		setModel(model);
		setSelectionModel(selectionModel);
		setAutoCreateColumnsFromModel(false);
		setAutoResizeMode(AUTO_RESIZE_OFF);
		setColumnSelectionAllowed(false);
		setRowSelectionAllowed(true);
		


		uTableHeader = new UTableVAHeader(getColumnModel(), new Insets(1, 1, 3, 1));
		setTableHeader(uTableHeader);
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
}
