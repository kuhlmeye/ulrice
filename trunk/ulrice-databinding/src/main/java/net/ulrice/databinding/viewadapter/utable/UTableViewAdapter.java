/**
 * 
 */
package net.ulrice.databinding.viewadapter.utable;

import java.awt.Component;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.impl.Element;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;
import net.ulrice.databinding.viewadapter.IFStateMarker;
import net.ulrice.databinding.viewadapter.IFTooltipHandler;

/**
 * @author christof
 * 
 */
public class UTableViewAdapter extends AbstractViewAdapter implements TableModelListener, TableModel {

	private static final int RESIZE_MARGIN = 2;

	private EventListenerList listenerList = new EventListenerList();
	private TableAM attributeModel;

	private UTableComponent table;

	public UTableViewAdapter(int fixedColumns) {
		super(List.class);

		table = new UTableComponent(this, fixedColumns);
		
		table.getScrollTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		    private boolean nested = false;
		    
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                
                if (nested) {
                    return;
                }
                
                nested = true;
                try {
                    for (ListSelectionListener l: listenerList.getListeners(ListSelectionListener.class)) {
                        l.valueChanged(e);
                    }
                }
                finally {
                    nested = false;
                }
            }
		});
	}

	public UTableViewAdapter() {
		this(0);
	}

	/**
	 * @see net.ulrice.databinding.IFGuiAccessor#getComponent()
	 */
	@Override
	public UTableComponent getComponent() {
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
			table.updateColumnModel(attributeModel);
			fireTableStructureChanged();
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
		for (TableModelListener listener : listeners) {
		    listener.tableChanged(e);
		}
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		listenerList.add(TableModelListener.class, l);
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		listenerList.remove(TableModelListener.class, l);
	}

	public void addListSelectionListener(ListSelectionListener l) {
	    listenerList.add(ListSelectionListener.class, l);
	}

	public void removeListSelectionListener(ListSelectionListener l) {
	    listenerList.remove(ListSelectionListener.class, l);
	}
	
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

	/**
	 * @see net.ulrice.databinding.IFGuiAccessor#getStateMarker()
	 */
	public IFStateMarker getStateMarker() {
		return table.getStateMarker();
	}

	/**
	 * @see net.ulrice.databinding.IFGuiAccessor#getTooltipHandler()
	 */
	public IFTooltipHandler getTooltipHandler() {
		return table.getTooltipHandler();
	}

	/**
	 * @see net.ulrice.databinding.IFGuiAccessor#setStateMarker(net.ulrice.databinding.viewadapter.IFStateMarker)
	 */
	public void setStateMarker(IFStateMarker stateMarker) {
		table.setStateMarker(stateMarker);
	}

	/**
	 * @see net.ulrice.databinding.IFGuiAccessor#setTooltipHandler(net.ulrice.databinding.viewadapter.IFTooltipHandler)
	 */
	public void setTooltipHandler(IFTooltipHandler tooltipHandler) {
		table.setTooltipHandler(tooltipHandler);
	}

	/**
	 * @return the rowSorter
	 */
	public UTableRowSorter getRowSorter() {
		return table.getRowSorter();
	}

	/**
	 * @return the filter
	 */
	public UTableVAFilter getFilter() {
		return table.getFilter();
	}

	@Override
	public void updateFromBinding(IFBinding binding) {
		if (binding instanceof TableAM) {
			setAttributeModel((TableAM) binding);
		}
		if (!isInNotification()) {
			int selRow = table.getSelectionModel().getMinSelectionIndex();
			int selColumn = table.getSelectedColumn();
			fireTableChanged(new TableModelEvent(this));
			if (selColumn >= 0) {
				table.setSelectedColumn(selColumn);
			}
			if (selRow >= 0 && selRow < getRowCount()) {
				table.getSelectionModel().setSelectionInterval(selRow, selRow);
			}

		}
		if (getTooltipHandler() != null) {
			getTooltipHandler().updateTooltip(binding, table);
		}
		if (getStateMarker() != null) {
			getStateMarker().updateState(binding, table);
		}
	}

	public int insertEmptyRow() {
		Element element = attributeModel.addElement(null);
		int row = attributeModel.getIndexOfElement(element);
		fireTableChanged(new TableModelEvent(this, row));
		return row;
	}

	@Override
	public void setEnabled(boolean enabled) {
		table.setEnabled(enabled);
	}

	@Override
	public boolean isEnabled() {
		return table.isEnabled();
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
		for (int c = 0; c < table.getStaticTable().getColumnCount(); c++) {
			sizeColumn(table.getStaticTable(), c, RESIZE_MARGIN, includeHeader);
		}
		for (int c = 0; c < table.getScrollTable().getColumnCount(); c++) {
			sizeColumn(table.getScrollTable(), c, RESIZE_MARGIN, includeHeader);
		}
	}

	public void sizeColumn(JTable table, int colIndex, int margin, boolean includeHeader) {
		TableColumn col = table.getColumnModel().getColumn(colIndex);
		int maxWidth = calcMaxSize(table, colIndex, includeHeader, col);
		col.setPreferredWidth(maxWidth + 2 * margin);
	}

	private int calcMaxSize(JTable table, int vColIndex, boolean includeHeader, TableColumn col) {
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
			Component comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, vColIndex), false, false, r, vColIndex);
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

	public int getSelectedRowViewIndex() {
		return table.getSelectionModel().getMinSelectionIndex();
	}

	public int getSelectedRowModelIndex() {
		int viewIndex = getSelectedRowViewIndex();
		if (viewIndex >= 0) {
			return getRowSorter().convertRowIndexToModel(viewIndex);
		}
		return -1;
	}
}
