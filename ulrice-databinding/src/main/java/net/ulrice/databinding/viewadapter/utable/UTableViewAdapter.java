/**
 * 
 */
package net.ulrice.databinding.viewadapter.utable;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
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
public class UTableViewAdapter extends AbstractViewAdapter implements
		TableModelListener, TableModel {

	private static final int RESIZE_MARGIN = 2;

	private EventListenerList listenerList = new EventListenerList();
	private TableAM attributeModel;

	private UTableComponent table;

	public UTableViewAdapter(int fixedColumns) {
		super(List.class);

		table = new UTableComponent(this, fixedColumns);

		table.getScrollTable().getSelectionModel()
				.addListSelectionListener(new ListSelectionListener() {
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
							for (ListSelectionListener l : listenerList
									.getListeners(ListSelectionListener.class)) {
								l.valueChanged(e);
							}
						} finally {
							nested = false;
						}
					}
				});

		MouseListener mouseListener = new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				MouseListener[] listeners = listenerList.getListeners(MouseListener.class);
				if (listeners != null) {
					for (MouseListener listener : listeners) {
						listener.mouseClicked(adaptMouseEvent(e));
					}
				}

			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseListener[] listeners = listenerList.getListeners(MouseListener.class);
				if (listeners != null) {
					for (MouseListener listener : listeners) {
						listener.mouseClicked(adaptMouseEvent(e));
					}
				}

			}

			@Override
			public void mouseExited(MouseEvent e) {
				MouseListener[] listeners = listenerList.getListeners(MouseListener.class);
				if (listeners != null) {
					for (MouseListener listener : listeners) {
						listener.mouseClicked(adaptMouseEvent(e));
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				MouseListener[] listeners = listenerList.getListeners(MouseListener.class);
				if (listeners != null) {
					for (MouseListener listener : listeners) {
						listener.mouseClicked(adaptMouseEvent(e));
					}
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				MouseListener[] listeners = listenerList
						.getListeners(MouseListener.class);
				if (listeners != null) {
					for (MouseListener listener : listeners) {
						listener.mouseClicked(adaptMouseEvent(e));
					}
				}
			}

			private MouseEvent adaptMouseEvent(MouseEvent e) {
				return new MouseEvent(table, e.getID(), e.getWhen(),
						e.getModifiers(), e.getX(), e.getY(), e.getXOnScreen(),
						e.getYOnScreen(), e.getClickCount(),
						e.isPopupTrigger(), e.getButton());
			}
		};
		table.getStaticTable().addMouseListener(mouseListener);
		table.getScrollTable().addMouseListener(mouseListener);

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
		if (this.attributeModel == null
				|| !this.attributeModel.equals(attributeModel)) {
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
		TableModelListener[] listeners = listenerList
				.getListeners(TableModelListener.class);
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

	public void addMouseListener(MouseListener l) {
		listenerList.add(MouseListener.class, l);
	}

	public void removeMouseListener(MouseListener l) {
		listenerList.remove(MouseListener.class, l);
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
	public IFStateMarker getCellStateMarker() {
		return table.getCellStateMarker();
	}

	/**
	 * @see net.ulrice.databinding.IFGuiAccessor#getTooltipHandler()
	 */
	public IFTooltipHandler<Element> getCellTooltipHandler() {
		return table.getCellTooltipHandler();
	}

	/**
	 * @see net.ulrice.databinding.IFGuiAccessor#setStateMarker(net.ulrice.databinding.viewadapter.IFStateMarker)
	 */
	public void setCellStateMarker(IFStateMarker stateMarker) {
		table.setCellStateMarker(stateMarker);
	}

	/**
	 * @see net.ulrice.databinding.IFGuiAccessor#setTooltipHandler(net.ulrice.databinding.viewadapter.IFTooltipHandler)
	 */
	public void setCellTooltipHandler(IFTooltipHandler<Element> tooltipHandler) {
		table.setCellTooltipHandler(tooltipHandler);
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
			getStateMarker().updateState(isDirty(), isValid(), table);
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

	public void sizeColumn(JTable table, int colIndex, int margin,
			boolean includeHeader) {
		TableColumn col = table.getColumnModel().getColumn(colIndex);
		int maxWidth = calcMaxSize(table, colIndex, includeHeader, col);
		col.setPreferredWidth(maxWidth + 2 * margin);
	}

	private int calcMaxSize(JTable table, int vColIndex, boolean includeHeader,
			TableColumn col) {
		int maxWidth = 0;

		if (includeHeader) {
			TableCellRenderer renderer = col.getHeaderRenderer();
			if (renderer == null) {
				renderer = table.getTableHeader().getDefaultRenderer();
			}
			Component comp = renderer.getTableCellRendererComponent(table,
					col.getHeaderValue(), false, false, 0, 0);
			maxWidth = comp.getPreferredSize().width;
		}

		for (int r = 0; r < table.getRowCount(); r++) {
			TableCellRenderer renderer = table.getCellRenderer(r, vColIndex);
			Component comp = renderer.getTableCellRendererComponent(table,
					table.getValueAt(r, vColIndex), false, false, r, vColIndex);
			maxWidth = Math.max(maxWidth, comp.getPreferredSize().width);
		}
		return maxWidth;
	}

	public void addRow() {
		getAttributeModel().addElement(null);
	}

	public void delRow(int index) {
		getAttributeModel().delElement(getElementAt(index));
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

	public void delSelectedRows() {
		List<Element> elements = getSelectedElements();
		if (elements != null) {
			for (Element element : elements) {
				getAttributeModel().delElement(element);
			}
		}
	}

	public int[] getSelectedRowsViewIndex() {
		int min = table.getSelectionModel().getMinSelectionIndex();
		int max = table.getSelectionModel().getMaxSelectionIndex();

		int[] tmpRows = new int[max - min + 1];
		int idx = 0;
		for (int i = min; i <= max; i++) {
			if (table.getSelectionModel().isSelectedIndex(i)) {
				tmpRows[idx++] = i;
			}
		}

		int[] result = new int[idx];
		System.arraycopy(tmpRows, 0, result, 0, idx);
		return result;
	}

	public int[] getSelectedRowsModelIndex() {
		int min = table.getSelectionModel().getMinSelectionIndex();
		int max = table.getSelectionModel().getMaxSelectionIndex();

		int[] tmpRows = new int[max - min + 1];
		int idx = 0;
		for (int i = min; i <= max; i++) {
			if (table.getSelectionModel().isSelectedIndex(i)) {
				tmpRows[idx++] = getRowSorter().convertRowIndexToModel(i);
			}
		}

		int[] result = new int[idx];
		System.arraycopy(tmpRows, 0, result, 0, idx);
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getSelectedObjects() {
		int[] rowsInModel = getSelectedRowsModelIndex();
		List result = new ArrayList(rowsInModel.length);
		for (int row : rowsInModel) {
			result.add(getAttributeModel().getCurrentValueAt(row));
		}
		return result;
	}

	public List<Element> getSelectedElements() {
		int[] rowsInModel = getSelectedRowsModelIndex();
		List<Element> result = new ArrayList<Element>(rowsInModel.length);
		for (int row : rowsInModel) {
			result.add(getAttributeModel().getElementAt(row));
		}
		return result;
	}

	public Object getSelectedObject() {
		int rowInModel = getSelectedRowModelIndex();
		return getAttributeModel().getCurrentValueAt(rowInModel);
	}

	public boolean isDirty() {
		return getAttributeModel() != null ? getAttributeModel().isDirty()
				: false;
	}

	public boolean isValid() {
		return getAttributeModel() != null ? getAttributeModel().isValid()
				: true;
	}

	public boolean isCellDirty(int row, int col) {
		int modelRow = getRowSorter().convertRowIndexToModel(row);
		int modelCol = table.convertColumnIndexToModel(col);
		return getAttributeModel() != null ? getAttributeModel().isCellDirty(
				modelRow, modelCol) : false;
	}

	public boolean isCellValid(int row, int col) {
		int modelRow = getRowSorter().convertRowIndexToModel(row);
		int modelCol = table.convertColumnIndexToModel(col);
		return getAttributeModel() != null ? getAttributeModel().isCellValid(
				modelRow, modelCol) : true;
	}

	public Element getElementAt(int row) {
		int modelRow = getRowSorter().convertRowIndexToModel(row);
		return getAttributeModel() != null ? getAttributeModel().getElementAt(
				modelRow) : null;
	}
}
