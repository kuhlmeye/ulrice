/**
 * 
 */
package net.ulrice.databinding.viewadapter.impl;

import java.awt.Insets;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.AbstractTableAM;
import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;
import net.ulrice.databinding.bufferedbinding.impl.Element;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;
import net.ulrice.databinding.viewadapter.IFStateMarker;
import net.ulrice.databinding.viewadapter.IFTooltipHandler;
import net.ulrice.databinding.viewadapter.impl.tableutil.JTableVADefaultRenderer;
import net.ulrice.databinding.viewadapter.impl.tableutil.JTableVAFilter;
import net.ulrice.databinding.viewadapter.impl.tableutil.JTableVAHeader;
import net.ulrice.databinding.viewadapter.impl.tableutil.JTableVARowSorter;

/**
 * @author christof
 * 
 */
public class JTableViewAdapter extends AbstractViewAdapter implements TableModelListener, TableModel {

    private AbstractTableAM attributeModel;
    private JTableVARowSorter rowSorter;
    private EventListenerList listenerList = new EventListenerList();


    private JTableVAFilter filter;
    
    
    
	private JTable table;
	private JTableVADefaultRenderer defaultRenderer;
	private JTableVAHeader tableHeader;

    public JTableViewAdapter(JTable table) {
    	super(List.class);
        this.table = table;

    	table.setModel(this);
        table.setAutoCreateColumnsFromModel(false);

        rowSorter = new JTableVARowSorter(this);
        table.setRowSorter(rowSorter);

        defaultRenderer = new JTableVADefaultRenderer(this);
		table.setDefaultRenderer(Object.class, defaultRenderer);

        tableHeader = new JTableVAHeader(table.getColumnModel(), new Insets(1, 1, 3, 1));
        table.setTableHeader(tableHeader);
        filter = new JTableVAFilter(rowSorter, tableHeader, table.getColumnModel());
        rowSorter.setRowFilter(filter);

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
    public AbstractTableAM getAttributeModel() {
        return attributeModel;
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#setAttributeModel(net.ulrice.databinding.IFAttributeModel)
     */
    public void setAttributeModel(AbstractTableAM attributeModel) {
        this.attributeModel = attributeModel;
        updateColumnModel(attributeModel);
        fireTableStructureChanged();
    }

    /**
     * @param attributeModel
     */
    private void updateColumnModel(AbstractTableAM attributeModel) {
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

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#getStateMarker()
     */
    public IFStateMarker getStateMarker() {
        return defaultRenderer.getStateMarker();
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#setStateMarker(net.ulrice.databinding.viewadapter.IFStateMarker)
     */
    public void setStateMarker(IFStateMarker stateMarker) {
    	defaultRenderer.setStateMarker(stateMarker);
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#getTooltipHandler()
     */
    public IFTooltipHandler getTooltipHandler() {
        return defaultRenderer.getTooltipHandler();
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#setTooltipHandler(net.ulrice.databinding.viewadapter.IFTooltipHandler)
     */
    public void setTooltipHandler(IFTooltipHandler tooltipHandler) {
        defaultRenderer.setTooltipHandler(tooltipHandler);
    }

    /**
     * @return the rowSorter
     */
    public JTableVARowSorter getRowSorter() {
        return rowSorter;
    }

    /**
     * @return the filter
     */
    public JTableVAFilter getFilter() {
        return filter;
    }

	@Override
	public void updateFromBinding(IFBinding binding) {
		if(binding instanceof AbstractTableAM) {
			setAttributeModel((AbstractTableAM)binding);									
		}
		if(!isInNotification()) {    
            fireTableChanged(new TableModelEvent(this));
		}		
		if(getTooltipHandler() != null) {
			getTooltipHandler().updateTooltip(binding, table);
		}
		if(getStateMarker() != null) {
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


	public void sizeColumns() {
		for(int i = 0; i < 4; i++) {
			tableHeader.setResizingColumn(table.getColumnModel().getColumn(i));
			table.doLayout();
			table.revalidate();
	        table.repaint();
		}
	}
}
