/**
 * 
 */
package net.ulrice.databinding.viewadapter.utable;

import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;
import net.ulrice.databinding.bufferedbinding.impl.Element;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;
import net.ulrice.databinding.bufferedbinding.impl.TableAMListener;
import net.ulrice.databinding.columnchooser.ColumnChooser;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;
import net.ulrice.databinding.viewadapter.IFCellStateMarker;
import net.ulrice.databinding.viewadapter.IFCellTooltipHandler;

/**
 * @author christof
 */
// TODO Check, if treeTableModelAdapter is respected in every method.
public class UTableViewAdapter extends AbstractViewAdapter implements TableModelListener, TableModel {
   
    private TreeTableModelAdapter treeTableModelAdapter;

    private EventListenerList listenerList = new EventListenerList();
    private TableAM attributeModel;

    private UTableComponent table;

    // only set if it was added
    private ColumnChooser columnChooser;

    private TableAMListener tableAMListener = new TableAMListener() {

        @Override
        public void columnValueRangeChanged(TableAM tableAM, ColumnDefinition< ?> colDef) {
            table.updateColumnModel();
            if(getFilter() != null){
                getFilter().rebuildFilter();
            }
        }

        @Override
        public void columnFilterModeChanged(TableAM tableAM, ColumnDefinition< ?> colDef) {
            if(getFilter() != null){
                getFilter().rebuildFilter();
            }
        }

        @Override
        public void columnRemoved(TableAM tableAM, ColumnDefinition< ?> colDef) {
            table.updateColumnModel();
            if(getFilter() != null){
                getFilter().rebuildFilter();
            }
        }

        @Override
        public void columnAdded(TableAM tableAM, ColumnDefinition< ?> colDef) {
            table.updateColumnModel();
            if(getFilter() != null){
                getFilter().rebuildFilter();
            }
        }
    };
    
    public UTableViewAdapter(final UTableComponent table, IFAttributeInfo attributeInfo) {
        super(List.class, attributeInfo);

        this.table = table;        
        setEditable(table.isEnabled());
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#getComponent()
     */
    @Override
    public UTableComponent getComponent() {
        return table;
    }

    @Override
    protected void setEditableInternal(boolean editable) {
        table.setEnabled(editable);
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
            if (this.attributeModel != null) {
               
                fireAttributeModelDetached(this.attributeModel);
                this.attributeModel.removeTableAMListener(tableAMListener);
            }

            this.attributeModel = attributeModel;
            if(this.attributeModel != null){
                this.attributeModel.addTableAMListener(tableAMListener);

                if(this.attributeModel.getColumnChooserID() != null){
                    columnChooser = new ColumnChooser(this.attributeModel, table, this.attributeModel.getColumnChooserID(), this.attributeModel.getDefaultInvisibleColumns());
                }
            }
            table.setAttributeModel(attributeModel);

//            table.updateColumnModel();
            fireAttributeModelBound(this.attributeModel);
            //fireTableStructureChanged();
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
     * Sends a {@link TableModelEvent} to all registered listeners to inform them that the table structure has
     * changed.
     */
    public void fireTableStructureChanged() {
        fireTableChanged(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
    }

    /**
     * Sends a {@link TableModelEvent} to all registered listeners to inform them that the table structure has
     * changed.
     */
    public void fireTableDataChanged() {        
        fireTableChanged(new TableModelEvent(this));
    }
    

    /**
     * @param e
     */
    protected void fireTableChanged(TableModelEvent e) {
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

    @Override
    public Class< ?> getColumnClass(int columnIndex) {
        if(getTreeTableModelAdapter() != null) {
            return getTreeTableModelAdapter().getColumnClass(columnIndex);
        } else if (getAttributeModel() != null) {
            return getAttributeModel().getColumnClass(columnIndex);
        }
        return null;
    }

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        if(getTreeTableModelAdapter() != null) {
            return getTreeTableModelAdapter().getColumnCount();
        } else if (getAttributeModel() != null) {
            return getAttributeModel().getColumnCount();
        }
        return 0;
    }

    /**
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int columnIndex) {
        if(getTreeTableModelAdapter() != null) {
            return getTreeTableModelAdapter().getColumnName(columnIndex);
        } else if (getAttributeModel() != null) {
            return getAttributeModel().getColumnName(columnIndex);
        }
        return null;
    }

    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
    public int getRowCount() {
        if(getTreeTableModelAdapter() != null) {
            return getTreeTableModelAdapter().getRowCount();
        } else if (getAttributeModel() != null) {
            return getAttributeModel().getRowCount();
        }
        return 0;
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(getTreeTableModelAdapter() != null) {
            return getTreeTableModelAdapter().getValueAt(rowIndex, columnIndex);
        } else if (getAttributeModel() != null) {
            return getAttributeModel().getValueAt(rowIndex, columnIndex);
        }
        return null;
    }

    /**
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(!isEditable() || attributeModel.isReadOnly()) {
            return false;
        }
        
        if(getTreeTableModelAdapter() != null) {
            return getTreeTableModelAdapter().isCellEditable(rowIndex, columnIndex);
        } else if (getAttributeModel() != null) {
            return getAttributeModel().isCellEditable(rowIndex, columnIndex);
        }
        return false;
    }

    /**
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(getTreeTableModelAdapter() != null) {
            getTreeTableModelAdapter().setValueAt(aValue, rowIndex, columnIndex);
        } else if (getAttributeModel() != null) {
            getAttributeModel().setValueAt(aValue, rowIndex, columnIndex);
        }
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#getStateMarker()
     */
    public IFCellStateMarker getCellStateMarker() {
        return table.getCellStateMarker();
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#getTooltipHandler()
     */
    public IFCellTooltipHandler getCellTooltipHandler() {
        return table.getCellTooltipHandler();
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#setStateMarker(net.ulrice.databinding.viewadapter.IFStateMarker)
     */
    public void setCellStateMarker(IFCellStateMarker stateMarker) {
        table.setCellStateMarker(stateMarker);
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#setTooltipHandler(net.ulrice.databinding.viewadapter.IFTooltipHandler)
     */
    public void setCellTooltipHandler(IFCellTooltipHandler tooltipHandler) {
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
    public void bind(IFBinding binding) {
        if (binding instanceof TableAM) {
            setAttributeModel((TableAM) binding);
        }
        super.bind(binding);
    }
    
    @Override
    public void detach(IFBinding binding) {
        if (binding instanceof TableAM) {
            setAttributeModel(null);
        }
        super.detach(binding);
    }
    
    public void updateFromBinding(IFBinding binding, Element element){
        
        
        if (!isInNotification()) {
            int selRow = -1;
            if(table != null && table.getSelectionModel() != null) {
                selRow = table.getSelectionModel().getMinSelectionIndex();
            }           
            int selColumn = table.getSelectedColumn();
            // TODO Not very performant
//            if(getRowSorter() != null)
//                getRowSorter().allRowsChanged();
            
            final int indexOfElement = attributeModel.getIndexOfElement(element);
            if (indexOfElement == -1) {
                fireTableDataChanged();
            }
            else {
                final int changedRow = getComponent().convertRowIndexToView(indexOfElement);
                fireTableChanged(new TableModelEvent(this, changedRow));
            }
            
            
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
            getStateMarker().updateState(binding, binding.isReadOnly() && isEditable(), isDirty(), isValid(), table);
        }
        
        table.updateUI(); //because of row specific event, we have to repaint the table
    }
    @Override
    public void updateFromBinding(IFBinding binding) {
      updateFromBinding(binding, null);
        
    }

    public int insertEmptyRow() {
        Element element = attributeModel.addElement(null);
        int row = attributeModel.getIndexOfElement(element);
        fireTableChanged(new TableModelEvent(this, row));
        return row;
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




    public boolean isDirty() {
        return getAttributeModel() != null ? getAttributeModel().isDirty() : false;
    }

    public boolean isValid() {
        return getAttributeModel() != null ? getAttributeModel().isValid() : true;
    }

    public boolean isCellDirty(int row, int col) {
        return table.isCellDirty(row, col);
    }

    public boolean isCellValid(int row, int col) {
        return table.isCellValid(row, col);
    }
    
    public List<Element> getVisibleElements(){
        
        if(getRowSorter() != null){
           return getRowSorter().getVisibleElements();
        }else{
            if(getAttributeModel().isForTreeTable()){
                return getAttributeModel().getLeafNodes();
            }
            return getAttributeModel().getElements();
        }
    }
    
    
    public Element getElementAt(int viewRowIndex) {      
        return table.getElementAtViewIndex(viewRowIndex);
    }
    
    public Object getCurrentValueAt(int row) {
        Element element = getElementAt(row);
        return element != null ? element.getCurrentValue() : null;
    }
    
    public Element getElementAtUsingModelIndex(int modelRowIndex) {
        if(getTreeTableModelAdapter() != null) {
            return getTreeTableModelAdapter().getElementForRow(modelRowIndex);
        }        
        return table.getElementAtModelIndex(modelRowIndex);
    }

    public boolean stopEditing() {
        return table.stopEditing();
    }

    public void setUpperInfoArea(JComponent upperInfoArea) {
        table.setUpperInfoArea(upperInfoArea);
    }

    public void setLowerInfoArea(JComponent bottomInfoArea) {
        table.setLowerInfoArea(bottomInfoArea);
    }

    public void addListSelectionListener(ListSelectionListener listSelectionListener) {
        table.addListSelectionListener(listSelectionListener);
    }

    public void removeListSelectionListener(ListSelectionListener listSelectionListener) {
        table.removeListSelectionListener(listSelectionListener);
    }

    /**
     * Resize the columns of the table according to the content and the header value, if flagged
     */
    public void sizeColumns(boolean includeHeader) {
        table.sizeColumns(includeHeader);
    }

    /**
     * Add a mouse listener to the list of listeners.
     */
    public void addMouseListener(MouseListener mouseListener) {
        table.addMouseListener(mouseListener);
    }

    /**
     * Removes a mouse listener from the list of available listeners.
     */
    public void removeMouseListener(MouseListener mouseListener) {
        table.removeMouseListener(mouseListener);
    }
    
    /**
     * Return the view index of the selected row.
     */
    public int getSelectedRowViewIndex() {
        return table.getSelectionModel().getMinSelectionIndex();
    }

    /**
     * Returns the model index of the selected row.
     * @return
     */
    public int getSelectedRowModelIndex() {
        return table.getSelectedRowModelIndex();
    }
    
    /**
     * Returns an array of view indices that are selected
     */
    public int[] getSelectedRowsViewIndex() {
        return table.getSelectedRowsViewIndex();
    }
  
    /**
     * Returns an array of model indices that are selected
     */
    public int[] getSelectedRowsModelIndex() {
        return table.getSelectedRowsModelIndex();
    }
    
    /**
     * Add a new and empty row to the table
     */
    public void addRow() {
        table.addRow();
    }

    /**
     * Delete the row with the given model index.
     */
    public void delRowWithModelIndex(int modelIndex) {
        table.delRowWithModelIndex(modelIndex);
    }

    /**
     * Delete the row with the given view index.
     */
    public void delRowWithViewIndex(int viewIndex) {
        table.delRowWithViewIndex(viewIndex);
    }

    /**
     * Delete the selected rows
     */
    public void delSelectedRows() {
        table.delSelectedRows();
    }

    /**
     * Returns the list of objects of the selected elements 
     */
    public List getSelectedObjects() {
        return table.getSelectedObjects();
    }

    /**
     * Returns the list of selected elements
     */
    public List<Element> getSelectedElements() {
        return table.getSelectedElements();
    }
    
    /**
     * Returns the object of the selected element.
     */
    public Object getSelectedObject() {
        return table.getSelectedObject();
    }

    /**
     * Select the element with the given index
     */
    public void selectElement(int index) {
        table.selectElement(index);
    }

    /**
     * Select elements with the given index. However, the behaviour depends on the
     * {@link ListSelectionModel#getSelectionMode()}:
     * <ul>
     * <li>For {@link ListSelectionModel#SINGLE_SELECTION} only the 1st of the given indexes will be selected.</li>
     * <li>For {@link ListSelectionModel#SINGLE_INTERVAL_SELECTION} the range between the 1st and the last given
     * indexes will be selected.</li>
     * <li>For {@link ListSelectionModel#MULTIPLE_INTERVAL_SELECTION} all of the given indexes will be selected.</li>
     * </ul>
     * 
     * @param indexes
     */
    public void selectElements(int... indexes) {
        table.selectElements(indexes);
    }

    public void setTreeTableModelAdapter(TreeTableModelAdapter treeTableModelAdapter) {
        this.treeTableModelAdapter = treeTableModelAdapter;
    }

    public TreeTableModelAdapter getTreeTableModelAdapter() {
        return treeTableModelAdapter;
    }

    @Override
    public Object getDisplayedValue() {
        return null;
    }       
}
