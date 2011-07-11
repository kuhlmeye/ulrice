/**
 * 
 */
package net.ulrice.databinding.impl.ga;

import java.awt.Insets;
import java.util.List;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import net.ulrice.databinding.IFGuiAccessor;
import net.ulrice.databinding.IFStateMarker;
import net.ulrice.databinding.IFTooltipHandler;
import net.ulrice.databinding.impl.am.AbstractTableAM;
import net.ulrice.databinding.impl.am.ColumnDefinition;
import net.ulrice.databinding.impl.am.ListAM;

/**
 * @author christof
 * 
 */
public class TableGA implements IFGuiAccessor<JTable, ListAM<? extends List<?>, ?>>, TableModelListener, TableModel {

    private ListAM<? extends List<?>, ?> attributeModel;
    private String id;
    private JTable component;
    private TableGARowSorter rowSorter;
    private EventListenerList listenerList = new EventListenerList();

    /** The class marking the current state at the component. */
    private IFStateMarker stateMarker;

    /** The tooltip handler of the component. */
    private IFTooltipHandler tooltipHandler;
    private TableGAFilter filter;

    public TableGA(String id) {
        this.id = id;
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#getComponent()
     */
    @Override
    public JTable getComponent() {
        if (component == null) {
            component = new JTable(this);
            component.setAutoCreateColumnsFromModel(false);

            rowSorter = new TableGARowSorter(this);
            component.setRowSorter(rowSorter);

            component.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

            component.setDefaultRenderer(Object.class, new TableGADefaultRenderer());

            // Add filter components to table header.
            TableGAHeader tableHeader = new TableGAHeader(component.getColumnModel(), new Insets(1, 1, 3, 1));
            component.setTableHeader(tableHeader);
            filter = new TableGAFilter(rowSorter, tableHeader, component.getColumnModel());
            rowSorter.setRowFilter(filter);
        }

        return component;
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#getAttributeModel()
     */
    @Override
    public ListAM<? extends List<?>, ?> getAttributeModel() {
        return attributeModel;
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#setAttributeModel(net.ulrice.databinding.IFAttributeModel)
     */
    @Override
    public void setAttributeModel(ListAM<? extends List<?>, ?> attributeModel) {
        if (this.attributeModel != null) {
            this.attributeModel.removeTableModelListener(this);
        }
        this.attributeModel = attributeModel;
        this.attributeModel.addTableModelListener(this);

        updateColumnModel(attributeModel);

        fireTableStructureChanged();
    }

    /**
     * @param attributeModel
     */
    private void updateColumnModel(ListAM<? extends List<?>, ?> attributeModel) {
        if (component != null) {
            TableColumnModel columnModel = component.getColumnModel();
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
        return stateMarker;
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#setStateMarker(net.ulrice.databinding.IFStateMarker)
     */
    public void setStateMarker(IFStateMarker stateMarker) {
        this.stateMarker = stateMarker;
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#getTooltipHandler()
     */
    public IFTooltipHandler getTooltipHandler() {
        return tooltipHandler;
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#setTooltipHandler(net.ulrice.databinding.IFTooltipHandler)
     */
    public void setTooltipHandler(IFTooltipHandler tooltipHandler) {
        this.tooltipHandler = tooltipHandler;
    }

    /**
     * @return the rowSorter
     */
    public TableGARowSorter getRowSorter() {
        return rowSorter;
    }

    /**
     * @return the filter
     */
    public TableGAFilter getFilter() {
        return filter;
    }
}
