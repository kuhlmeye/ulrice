package net.ulrice.databinding.viewadapter.utable;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;


public class UTableModel implements TableModel, TableModelListener {

	private int offset;
	private TableModel model;
	private boolean scrollTable;
	private EventListenerList listener = new EventListenerList();

	public UTableModel(boolean scrollTable, int offset, TableModel model) {
		this.scrollTable = scrollTable;
		this.offset = offset;
		this.model = model;
		model.addTableModelListener(this);
	}
	
	@Override
	public int getRowCount() {
		return model.getRowCount();
	}


	@Override
	public int getColumnCount() {
		if(scrollTable) {
			return model.getColumnCount() >= offset ? model.getColumnCount() - offset : 0;
		} else {
			return model.getColumnCount() >= offset ? offset : 0;
		}
	}

	@Override
	public String getColumnName(int columnIndex) {
		return model.getColumnName(fixColumnIndex(columnIndex));
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return model.getColumnClass(fixColumnIndex(columnIndex));
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return model.isCellEditable(rowIndex, fixColumnIndex(columnIndex));
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return model.getValueAt(rowIndex, fixColumnIndex(columnIndex));
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		model.setValueAt(aValue, rowIndex, fixColumnIndex(columnIndex));
	}

	private int fixColumnIndex(int columnIndex) {
		return scrollTable ? columnIndex + offset : columnIndex;
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		listener.add(TableModelListener.class, l);
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		listener.remove(TableModelListener.class, l);
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		int column = e.getColumn();
		if(column == -1 || scrollTable && column >= offset || !scrollTable && column < offset) {
			TableModelEvent event = new TableModelEvent(this, e.getFirstRow(), e.getLastRow(), fixColumnIndex(e.getColumn()), e.getType());
			TableModelListener[] listeners = listener.getListeners(TableModelListener.class);
			if(listeners != null) {
				for(TableModelListener listener : listeners) {
					listener.tableChanged(event);
				}
			}
		}
	}

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
