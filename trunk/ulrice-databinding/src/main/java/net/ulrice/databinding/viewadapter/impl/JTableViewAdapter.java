/**
 * 
 */
package net.ulrice.databinding.viewadapter.impl;

import java.awt.Component;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;
import net.ulrice.databinding.bufferedbinding.impl.Element;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;

/**
 * @author christof
 * 
 */
public class JTableViewAdapter extends AbstractViewAdapter implements TableModelListener, TableModel {

	private static final int RESIZE_MARGIN = 2;
	private TableAM attributeModel;
	private EventListenerList listenerList = new EventListenerList();

	private JTable table;

	public JTableViewAdapter(JTable table, IFAttributeInfo attributeInfo) {
		super(List.class, attributeInfo);
		this.table = table;

		table.setModel(this);
		table.setAutoCreateColumnsFromModel(false);
        setEditable(isComponentEnabled());
	}

	/**
	 * @see net.ulrice.databinding.IFGuiAccessor#getComponent()
	 */
	@Override
	public JTable getComponent() {
		return table;
	}

	/**
	 * @see net.ulrice.databinding.IFGuiAccessor#getAttributeModel()
	 */
	public TableAM getAttributeModel() {
		return attributeModel;
	}

	/**
	 * @see net.ulrice.databinding.IFGuiAccessor#setAttributeModel(net.ulrice.databinding.IFAttributeModel)
	 */
	public void setAttributeModel(TableAM attributeModel) {
		if (this.attributeModel == null || !this.attributeModel.equals(attributeModel)) {
			this.attributeModel = attributeModel;
			updateColumnModel(attributeModel);
			fireTableStructureChanged();
		}
	}

	/**
	 * @param attributeModel
	 */
	private void updateColumnModel(TableAM attributeModel) {
		if (table != null) {
			TableColumnModel columnModel = table.getColumnModel();
			for (int i = columnModel.getColumnCount() - 1; i >= 0; i--) {
				columnModel.removeColumn(columnModel.getColumn(i));
			}

			List<ColumnDefinition<? extends Object>> columnDefinitions = attributeModel.getColumns();
			if (columnDefinitions != null) {
				for (int i = 0; i < columnDefinitions.size(); i++) {
					ColumnDefinition<?> columnDefinition = columnDefinitions.get(i);
					TableColumn column = new TableColumn();
					column.setIdentifier(columnDefinition.getId());
					column.setHeaderValue(columnDefinition);
					column.setModelIndex(i);

					columnModel.addColumn(column);
				}
			}
		}
	}

	/**
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
		fireTableChanged(e);
	}

	/**
	 * Sends a {@link TableModelEvent} to all registered listeners to inform
	 * them that the table structure has changed.
	 */
	public void fireTableStructureChanged() {
		fireTableChanged(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
	}

	/**
	 * @param e
	 */
	private void fireTableChanged(TableModelEvent e) {
		TableModelListener[] listeners = listenerList.getListeners(TableModelListener.class);
		if (listeners != null) {
			for (TableModelListener listener : listeners) {
				listener.tableChanged(e);
			}
		}
	}

	/**
	 * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
	 */
	@Override
	public void addTableModelListener(TableModelListener l) {
		listenerList.add(TableModelListener.class, l);
	}

	/**
	 * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
	 */
	@Override
	public void removeTableModelListener(TableModelListener l) {
		listenerList.remove(TableModelListener.class, l);
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (getAttributeModel() != null) {
			return getAttributeModel().getColumnClass(columnIndex);
		}
		return null;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		if (getAttributeModel() != null) {
			return getAttributeModel().getColumnCount();
		}
		return 0;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		if (getAttributeModel() != null) {
			return getAttributeModel().getColumnName(columnIndex);
		}
		return null;
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		if (getAttributeModel() != null) {
			return getAttributeModel().getRowCount();
		}
		return 0;
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (getAttributeModel() != null) {
			return getAttributeModel().getValueAt(rowIndex, columnIndex);
		}
		return null;
	}

	/**
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (getAttributeModel() != null) {
			return getAttributeModel().isCellEditable(rowIndex, columnIndex);
		}
		return false;
	}

	/**
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (getAttributeModel() != null) {
			getAttributeModel().setValueAt(aValue, rowIndex, columnIndex);
		}
	}




	@Override
	public void updateFromBinding(IFBinding binding) {
		if (binding instanceof TableAM) {			
			setAttributeModel((TableAM) binding);
		}
		if (!isInNotification()) {			
			int selRow = table.getSelectedRow();		
			int selColumn = table.getSelectedColumn();
			fireTableChanged(new TableModelEvent(this));
			if(selColumn >= 0) {
				table.setColumnSelectionInterval(selColumn, selColumn);
			}
			if(selRow >= 0 && selRow < getRowCount()) {
				table.setRowSelectionInterval(selRow, selRow);
			}
			
		}
		if (getTooltipHandler() != null) {
			getTooltipHandler().updateTooltip(binding, table);
		}
		if (getStateMarker() != null) {
			getStateMarker().updateState(binding, isEditable(), binding.isDirty(), binding.isValid(), table);
		}
	}

	public int insertEmptyRow() {
		Element element = attributeModel.addElement(null);
		int row = attributeModel.getIndexOfElement(element);
		fireTableChanged(new TableModelEvent(this, row));
		return row;
	}

	@Override
	public void setComponentEnabled(boolean enabled) {
		table.setEnabled(enabled);
	}

	@Override
	public Object getValue() {
		return null;
	}

	@Override
	protected void addComponentListener() {
	}

	@Override
	protected void setValue(Object value) {
	}

	@Override
	protected void removeComponentListener() {
	}

	public void sizeColumns(boolean includeHeader) {
		for (int c = 0; c < table.getColumnCount(); c++) {
			sizeColumn(c, RESIZE_MARGIN, includeHeader);
		}
	}

	public void sizeColumn(int colIndex, int margin, boolean includeHeader) {
		TableColumn col = table.getColumnModel().getColumn(colIndex);
		int maxWidth = calcMaxSize(colIndex, includeHeader, col);
		col.setPreferredWidth(maxWidth + 2 * margin);
	}

	private int calcMaxSize(int vColIndex, boolean includeHeader, TableColumn col) {
		int maxWidth = 0;

		if (includeHeader) {
			TableCellRenderer renderer = col.getHeaderRenderer();
			if (renderer == null) {
				renderer = table.getTableHeader().getDefaultRenderer();
			}
			Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
			maxWidth = comp.getPreferredSize().width;
		}

		for (int r = 0; r < table.getRowCount(); r++) {
			TableCellRenderer renderer = table.getCellRenderer(r, vColIndex);
			Component comp = renderer.getTableCellRendererComponent(table, getValueAt(r, vColIndex), false, false, r, vColIndex);
			maxWidth = Math.max(maxWidth, comp.getPreferredSize().width);
		}
		return maxWidth;
	}

	public void addRow() {
		getAttributeModel().addElement(null);
	}

	public void delRow(int index) {
		getAttributeModel().delElement(index);
	}

    @Override
    public boolean isComponentEnabled() {
        return table.isEnabled();
    }

    @Override
    public Object getDisplayedValue() {
        return null;
    }
}
