package net.ulrice.databinding.bufferedbinding.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.RowSorter.SortKey;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import net.ulrice.Ulrice;
import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.bufferedbinding.IFAttributeModel;
import net.ulrice.databinding.bufferedbinding.IFAttributeModelEventListener;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.modelaccess.IFIndexedModelValueAccessor;
import net.ulrice.databinding.modelaccess.impl.IndexedReflectionMVA;
import net.ulrice.databinding.validation.IFValidator;
import net.ulrice.databinding.validation.UniqueKeyConstraintError;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;
import net.ulrice.databinding.viewadapter.IFViewAdapter;
import net.ulrice.databinding.viewadapter.utable.TreeTableModel;
import net.ulrice.databinding.viewadapter.utable.UTableViewAdapter;
import net.ulrice.module.IFController;
import net.ulrice.process.AbstractProcess;

/**
 * Table attribute model. Model for all UTableComponents
 * 
 * @author christof, EEXRADA
 */
@SuppressWarnings("rawtypes")
public class TableAM implements IFAttributeModel {

    private IFIndexedModelValueAccessor tableMVA;

    private List<ColumnDefinition< ? extends Object>> columns = new ArrayList<ColumnDefinition< ? extends Object>>();
    private Map<String, ColumnDefinition> columnIdMap = new HashMap<String, ColumnDefinition>();
    private Map<String, Integer> idModelIndexMap;

    protected List<Element> elements = new ArrayList<Element>();
    protected Map<Long, Element> elementIdMap = new HashMap<Long, Element>();

    private List<IFValidator> validators = new ArrayList<IFValidator>();
    private EventListenerList listenerList = new EventListenerList();
    private String id;
    private boolean readOnly;
    private long nextUniqueId;

    private Set<Element> newElements = new HashSet<Element>();
    private Set<Element> modElements = new HashSet<Element>();
    private Set<Element> delElements = new HashSet<Element>();
    private Set<Element> invElements = new HashSet<Element>();

    private List<IFViewAdapter> viewAdapterList = new ArrayList<IFViewAdapter>();
    
    private IFValueConverter valueConverter;

    private IFAttributeInfo attributeInfo;

    private boolean initialized = false;
    private boolean dirty = false;
    private boolean valid = true;
    
    /** Holds the state of the table */
    private boolean tableValid = true;

    // prevent update ui, for mass editing
    private boolean massEditMode = false;

    // unique constraint handling
    private String[] uniqueKeyColumnIds = null;
    private Map<List< ?>, Set<Long>> uniqueMap = new HashMap<List< ?>, Set<Long>>();
//    private Map<List< ?>, Set<String>> uniqueDeleteMap = new HashMap<List< ?>, Set<String>>(); wozu?
    private Map<Long, List< ?>> keyMap = new HashMap<Long, List< ?>>();
//    private Map<String, List< ?>> keyDeleteMap = new HashMap<String, List< ?>>(); wozu?
    private Map<List< ?>, ValidationError> currentErrorMap = new HashMap<List< ?>, ValidationError>();

    private String pathToChildren;

    private boolean displayRemovedEntries = true;

    private List<SortKey> defaultSortKeys;
    private List<SortKey> mandatorySortKeys;

    protected boolean treeStayOpen = false;
    protected boolean virtualTreeNodes = false;    


    /**
     * Create a table attribute model
     * 
     * @param tableMVA the model value accessor used to get the list data
     * @param attributeInfo additional information about the attribute (validation rules,...)
     */
    public TableAM(IFIndexedModelValueAccessor tableMVA, IFAttributeInfo attributeInfo) {
        this(tableMVA, attributeInfo, false);
    }

    /**
     * Create a table attribute model
     * 
     * @param tableMVA the model value accessor used to get the list data
     * @param attributeInfo additional information about the attribute (validation rules,...)
     * @param readOnly true, if this table attribute model should be read only
     */
    public TableAM(IFIndexedModelValueAccessor tableMVA, IFAttributeInfo attributeInfo, boolean readOnly) {
        this(tableMVA.getAttributeId(), tableMVA, attributeInfo, readOnly);
    }

    /**
     * Create a table attribute model.
     * 
     * @param id Identifier for this binding
     * @param tableMVA the model value accessor used to get the list data
     * @param attributeInfo additional information about the attribute (validation rules,...)
     * @param readOnly true, if this table attribute model should be read only
     */
    public TableAM(String id, IFIndexedModelValueAccessor tableMVA, IFAttributeInfo attributeInfo, boolean readOnly) {
        this.id = id;
        this.tableMVA = tableMVA;
        this.readOnly = readOnly;
        this.attributeInfo = attributeInfo;

        nextUniqueId = System.currentTimeMillis();
    }

    /**
     * Checks the unique constraints for an element. This method is called after an element was changed or added.
     */
    private void checkUniqueConstraint(Element element) {
        if (element.getChildCount() > 0) {
            return;
        }

        if (uniqueKeyColumnIds == null) {
            return;
        }

        List< ?> key = buildKey(element);
        //BUG:3137
        //RAD: setValue entfernt den Validation Error, und wenn sich der key nicht geaendert hat wird er nicht mehr gesetzt
        //if(checkKeyChangeAndUpdateDatastructure(element.getUniqueId(), key)) { 
        checkKeyChangeAndUpdateDatastructure(element.getUniqueId(), key);
               
            if (uniqueMap.containsKey(key)) {
                Set<Long> uniqueIdSet = uniqueMap.get(key);
                uniqueIdSet.add(element.getUniqueId());
                if (uniqueIdSet.size() > 1) {
                    UniqueKeyConstraintError uniqueConstraintError =
                            new UniqueKeyConstraintError(this, "Unique key constraint error", null);
                    currentErrorMap.put(key, uniqueConstraintError);
                    for (Long uniqueId : uniqueIdSet) {
                        Element elementById = getElementById(uniqueId);
                        elementById.putUniqueKeyConstraintError(uniqueConstraintError);
                    }
                }
            }
            else {
                Set<Long> uniqueIdSet = new HashSet<Long>();
                uniqueIdSet.add(element.getUniqueId());
                uniqueMap.put(key, uniqueIdSet);
            }
        //}
    }

    /**
     * Update the data structures for the unique key handling in case that the key of the element was changed
     * 
     * @param uniqueId The unique id of the referenced element.
     * @param key The unique key of the element as list of values, if key is null the element will be deleted
     * @return true, if key was changed, false otherwise
     */
    private boolean checkKeyChangeAndUpdateDatastructure(long uniqueId, List< ?> key) {
        List< ?> oldKey = keyMap.get(uniqueId);
        if (oldKey == null && key != null) {
            // String oldUniqueId = checkForOldUniqueId(key, uniqueId);
            keyMap.put(uniqueId, key);
            return true;
        }

        if (key == null || !oldKey.equals(key)) {
            if (oldKey != null) {
                Set<Long> uniqueKeySet = uniqueMap.get(oldKey);
                uniqueKeySet.remove(uniqueId);

                if (uniqueKeySet.isEmpty()) {
                    uniqueMap.remove(oldKey);
                }

                // should not happen
//                if (uniqueDeleteMap.containsKey(oldKey)) { wozu?
//                    Set<String> uniqueDeleteKeySet = uniqueDeleteMap.get(oldKey);
//                    uniqueDeleteKeySet.add(uniqueId);
//                }
//                else {
//                    Set<String> uniqueIdSet = new HashSet<String>();
//                    uniqueIdSet.add(uniqueId);
//                    uniqueDeleteMap.put(oldKey, uniqueIdSet);
//                }
                if (uniqueKeySet.size() <= 1 && currentErrorMap.containsKey(oldKey)) {
                    ValidationError validationError = currentErrorMap.remove(oldKey);
                    getElementById(uniqueId).removeElementValidationError(validationError);
                    getElementById(uniqueId).removeUniqueKeyConstraintErrors();
                    
                    for (Long uniqueElementId : uniqueKeySet) {
                        getElementById(uniqueElementId).removeElementValidationError(validationError);
                        getElementById(uniqueElementId).removeUniqueKeyConstraintErrors();
                    }

                }
                //keyDeleteMap.put(uniqueId, oldKey);
                keyMap.put(uniqueId, key);
            }
            return true;
        }
        return false;
    }

    /**
     * Returns the key of an element.
     */
    private List< ?> buildKey(Element element) {
        List<Object> key = new ArrayList<Object>(uniqueKeyColumnIds != null ? uniqueKeyColumnIds.length : 0);
        if (uniqueKeyColumnIds != null) {
            for (String columnId : uniqueKeyColumnIds) {
                key.add(element.getValueAt(columnId));
            }
        }
        return key;
    }

//    /**
//     * Checks added or changed elements against the list of deleted elements. If there is already a deleted element
//     * with the same unique key, change the unique id of the added or changed element to the unique id of the deleted
//     * element => After that the added/changed element is known as the removed one.
//     * 
//     * @param element The added/changed element.
//     * @return The orignal element, if key is not known in the list of removed elements or the removed element with
//     *         the values of the added/changed element.
//     */
//    private Element checkAddedOrChangedElementAgainstDeletedElements(Element element) {
//        Element oldElement = null;
//        String oldUniqueId = checkForOldUniqueId(buildKey(element), element.getUniqueId());
//
//        if (oldUniqueId == null) {
//            return element;
//        }
//        else {
//            // delete old element from delElements
//            // how to find old element?
//            Iterator<Element> iter = delElements.iterator();
//            while (iter.hasNext()) {
//                Element el = iter.next();
//                if (oldUniqueId.equals(el.getUniqueId())) {
//                    oldElement = el;
//                    oldElement.setRemoved(false);
//                    iter.remove();
//                    break;
//                }
//            }
//            if (oldElement != null) {
//                oldElement.setCurrentValue(element.getCurrentValue());
//            }
//            else {
//                oldElement = element;
//            }
//
//            elementIdMap.put(oldUniqueId, oldElement);
//            return oldElement;
//        }
//    }

//    /**
//     * Check, if a newly created or changed element with a given unique key is already known in the datastructures
//     * with a different unique id.
//     * 
//     * @param key The list of value of the element defining the unique key
//     * @param newUniqueId The generated unique id of the newly created or changed element
//     * @return null, if there is no unique id available for the key or the unique id of the already known element
//     */
//    private String checkForOldUniqueId(List< ?> key, String newUniqueId) {
//        String oldUniqueId = null;
//        if (keyDeleteMap.containsValue(key)) {
//            for (Entry<String, List< ?>> entry : keyDeleteMap.entrySet()) {
//                if (key.equals(entry.getValue())) {
//                    oldUniqueId = entry.getKey();
//                    // remove Element from new Elements and replace it with the former deleted one
//                    // newElements.remove(getElementById(newUniqueId));
//                    // keyDeleteMap.remove(oldUniqueId);
//                }
//            }
//        }
//        return oldUniqueId;
//    }

    // end of unique constraint handling

    /**
     * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Returns, if a table cell is valid.
     */
    public boolean isCellValid(int row, int column) {
        return getElementAt(row).isColumnValid(column);
    }

    /**
     * Returns, if a table cell is dirty.
     */
    public boolean isCellDirty(int row, int column) {
        return getElementAt(row).isColumnDirty(column);
    }

    /**
     * Internal method for creating a new element with a new unique identifier
     */
    protected Element createElement(Object value, boolean dirty, boolean valid, boolean inserted) {
        Element elem = new Element(this, nextUniqueId++, value, isReadOnly(), dirty, valid, inserted);

        if (isForTreeTable()) {
            addChildsToElement(value, dirty, valid, inserted, elem);
        }

        return elem;
    }

    /**
     * Internal method for adding a child to an element.
     */
    protected void addChildsToElement(Object value, boolean dirty, boolean valid, boolean inserted, Element element) {
        IFIndexedModelValueAccessor mva = new IndexedReflectionMVA(value, getPathToChildren());
        for (int i = 0; i < mva.getSize(); i++) {
            Object child = mva.getValue(i);
            element.addChildElement(createElement(child, dirty, valid, inserted));
        }
      //do this only once in the end
//        if (mva.getSize() > 0) {
//            element.clearElementValidationErrors(); 
//        }
    }

    /**
     * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#addAttributeModelEventListener(net.ulrice.databinding.bufferedbinding.IFAttributeModelEventListener)
     */
    @Override
    public void addAttributeModelEventListener(IFAttributeModelEventListener listener) {
        listenerList.add(IFAttributeModelEventListener.class, listener);
    }

    /**
     * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#removeAttributeModelEventListener(net.ulrice.databinding.bufferedbinding.IFAttributeModelEventListener)
     */
    @Override
    public void removeAttributeModelEventListener(IFAttributeModelEventListener listener) {
        listenerList.remove(IFAttributeModelEventListener.class, listener);
    }

    /**
     * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#setValidator(net.ulrice.databinding.validation.IFValidator)
     */
    @Override
    public void addValidator(IFValidator validator) {
        this.validators.add(validator);
    }

    /**
     * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getValidator()
     */
    @Override
    public List<IFValidator> getValidators() {
        return validators;
    }

    /**
     * Returns the element at the model index.
     */
    public Element getElementAt(int index) {
        if (index >= 0 && index < elements.size()) {
            return elements.get(index);
        }
        return null;
    }

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return columns == null ? 0 : columns.size();
    }

    /**
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class< ?> getColumnClass(int columnIndex) {
        if (isForTreeTable() && columnIndex == 0) {
            return TreeTableModel.class;
        }
        return columns.get(columnIndex).getColumnClass();
    }

    /**
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int columnIndex) {
        return columns.get(columnIndex).getColumnName();
    }

    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return elements == null ? 0 : elements.size();
    }

    /**
     * Returns true, if the element is in "new" state. This means, it was newly created and is not yet saved.
     */
    public boolean isNew(Element element) {
        return newElements.contains(element);
    }

    /**
     * Returns true, if the element is in "removed" state. This means, it was deleted from the table and is not yet saved.
     */
    public boolean isRemoved(Element element) {
        return delElements.contains(element);
    }

    /**
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return !isReadOnly() && getElementAt(rowIndex) != null && !getElementAt(rowIndex).isReadOnly(columnIndex) && !getElementAt(rowIndex).isRemoved();
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        final Element element = getElementAt(rowIndex);
        return element == null ? null : element.getValueAt(columnIndex);
    }

    /**
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        getElementAt(rowIndex).setValueAt(columnIndex, aValue);
    }

    /**
     * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#isReadOnly()
     */
    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * a fireUpdateView which is executed very often leads to  updateFromBinding, which executes a fireTableDataChanged.
     * a fireTableDataChanged rebuilds the tree structure, because its to unspecific about the change.
     *
     * the treestay open flag, prevents the tree from rebuilding its structure.
     * consumeTreeStayOpen is called by the UTreeTableComponent
     *
     * if true it ignores the tableChanged event
     *
     */
    public boolean consumeTreeStayOpen() {
        boolean temp = treeStayOpen;
        treeStayOpen = false;
        return temp;
    }

    public void setTreeStayOpen() {
        if (isForTreeTable()) {
            treeStayOpen = true;
        }

    }

    /**
     * This method is called by the element after a value was changed. It triggers the events..
     * 
     * @param element The element that was changed.
     * @param columnId The id of the column in which the value was changed.
     */
    protected void handleElementDataChanged(final Element element, final String columnId) {

        setTreeStayOpen();
        
        fireUpdateViews(element);
        
        if (uniqueKeyColumnIds != null) {
            checkUniqueConstraint(element);
//            if (keyDeleteMap.containsValue(buildKey(element))) {
//                checkAddedOrChangedElementAgainstDeletedElements(element);
//            }
//            checkAddedOrChangedElementAgainstDeletedElements(element);
        }
        
        boolean stateChanged = handleValidity();
        if (stateChanged){
            fireStateChanged();
        }

        // Inform the event listeners.
        ElementLifecycleListener[] listeners = listenerList.getListeners(ElementLifecycleListener.class);
        if (listeners != null) {
            for (final ElementLifecycleListener constraint : listeners) {
                if (!SwingUtilities.isEventDispatchThread()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            constraint.elementChanged(TableAM.this, element, columnId);
                        }
                    });
                } else {
                    constraint.elementChanged(TableAM.this, element, columnId);
                }
            }
        }

        fireDataChanged();
    }

	private void handleTableValidation() {
		List<IFValidator> tableValidators = getValidators();        
        if(tableValidators != null) {
        	tableValid = true;
        	for(IFValidator val : tableValidators) {
        		@SuppressWarnings("unchecked")
				ValidationResult valResult = val.isValid(this, elements, elements);        		
        		tableValid &= valResult == null || valResult.isValid();
        	}
        }
	}
    
    

    /**
     * This method is called by the element after the state of the element was changed.
     * It updates the data structures in which the element states are tracked and fires the needed events.
     * 
     * @param element The element with the changed state.
     */
    protected void handleElementStateChange(final Element element) {
        boolean virtualTreeNodeElement = element.getChildCount() != 0 && virtualTreeNodes;
        if (virtualTreeNodeElement) {
            invElements.remove(element);
            modElements.remove(element);
            delElements.remove(element);
        }
        if (element.isValid() || element.isRemoved()) {
            invElements.remove(element);
        }
        else {
            invElements.add(element);
        }

        if (element.isDirty() && !element.isInsertedOrRemoved() && elementIdMap.containsKey(element.getUniqueId())
            && !newElements.contains(element) && !delElements.contains(element) && !virtualTreeNodeElement) {
            modElements.add(element);
        }

        if (!element.isDirty() && elementIdMap.containsKey(element.getUniqueId())) {
            modElements.remove(element);
        }
        
        boolean stateChanged = handleValidity();

        fireElementStatusChanged(element);

        if (stateChanged){
            fireStateChanged();
        }
    }

    /**
     * @return True, if validation state has been changed
     */
	private boolean handleValidity() {
		boolean oldTableValid = tableValid;
        boolean oldValid = valid;
        boolean oldDirty = dirty;
        
        handleTableValidation();
        
        valid = invElements.isEmpty() && tableValid;
        dirty = !modElements.isEmpty() || !delElements.isEmpty() || !newElements.isEmpty();

        return (oldValid != valid || oldDirty != dirty || oldTableValid != tableValid);
	}

    /**
     * Inform the model event listeners about a data change.
     */
    @SuppressWarnings("unchecked")
    private void fireDataChanged() {
        if(massEditMode){
            return;
        }
        IFAttributeModelEventListener[] listeners = listenerList.getListeners(IFAttributeModelEventListener.class);
        if (listeners != null) {
            for (final IFAttributeModelEventListener listener : listeners) {
                if (!SwingUtilities.isEventDispatchThread()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            listener.dataChanged(null, TableAM.this);
                        }
                    });
                } else {
                    listener.dataChanged(null, this);
                }
            }
        }
    }

    /**
     * Inform the model event listeners about a state change
     */
    @SuppressWarnings("unchecked")
    private void fireStateChanged() {
        if(massEditMode){
            return;
        }
        
        IFAttributeModelEventListener[] listeners = listenerList.getListeners(IFAttributeModelEventListener.class);
        if (listeners != null) {
            for (final IFAttributeModelEventListener listener : listeners) {
                if (!SwingUtilities.isEventDispatchThread()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            listener.stateChanged(null, TableAM.this);
                        }
                    });
                } else {
                    listener.stateChanged(null, this);
                }
            }
        }
    }


    /**
     * Add a column to this table model
     * 
     * @param columnDefinition The definition of the column that should be added.
     */
    public void addColumn(final ColumnDefinition< ?> columnDefinition) {
        addColumn(columnDefinition, -1);
    }
    
    /**
     * Add a column to this table model on a specific position
     * 
     * @param columnDefinition The definition of the column that should be added.
     * @param index the position of the new column
     */
    public void addColumn(final ColumnDefinition< ?> columnDefinition, int index) {
        columnDefinition.addChangeListener(new ColumnDefinitionChangedListener() {
            @Override
            public void valueRangeChanged(final ColumnDefinition< ?> colDef) {
                TableAMListener[] listeners = listenerList.getListeners(TableAMListener.class);
                if (listeners != null) {
                    for (final TableAMListener listener : listeners) {
                        if (!SwingUtilities.isEventDispatchThread()) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    listener.columnValueRangeChanged(TableAM.this, colDef);
                                }
                            });
                        } else {
                            listener.columnValueRangeChanged(TableAM.this, colDef);
                        }
                    }
                }
            }

            @Override
            public void filterModeChanged(final ColumnDefinition< ?> colDef) {

                TableAMListener[] listeners = listenerList.getListeners(TableAMListener.class);
                if (listeners != null) {
                    for (final TableAMListener listener : listeners) {
                        if (!SwingUtilities.isEventDispatchThread()) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    listener.columnFilterModeChanged(TableAM.this, colDef);
                                }
                            });
                        } else {
                            listener.columnFilterModeChanged(TableAM.this, colDef);
                        }
                    }
                }
            }
        });
        if(index != -1){
            columns.add(index, columnDefinition);
        }else{
            columns.add(columnDefinition);
        }
        
        columnIdMap.put(columnDefinition.getId(), columnDefinition);

        for(Element element : elements) {
            element.readObject();
        }
        
        TableAMListener[] listeners = listenerList.getListeners(TableAMListener.class);
        if (listeners != null) {
            for (final TableAMListener listener : listeners) {
                if (!SwingUtilities.isEventDispatchThread()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            listener.columnAdded(TableAM.this, columnDefinition);
                        }
                    });
                } else {
                    listener.columnAdded(TableAM.this, columnDefinition);
                }
            }
        }
    }

    /**
     * Returns true, if a column definition is managed by this table attribute model
     */
    public boolean containsColumn(ColumnDefinition< ?> column) {
        return columns.contains(column);
    }

    /**
     * Delete a column from this table model.
     * 
     * @param columnDefinition The definition of the column that should be removed.
     */
    public void delColumn(final ColumnDefinition< ?> columnDefinition) {
        columns.remove(columnDefinition);
        columnIdMap.remove(columnDefinition.getId());

        TableAMListener[] listeners = listenerList.getListeners(TableAMListener.class);
        if (listeners != null) {
            for (final TableAMListener listener : listeners) {
                if (!SwingUtilities.isEventDispatchThread()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            listener.columnRemoved(TableAM.this, columnDefinition);
                        }
                    });
                } else {
                    listener.columnRemoved(TableAM.this, columnDefinition);
                }
            }
        }
    }

    /**
     * Delete all columns from this table model.
     */
    public void delAllColumns() {
        List<ColumnDefinition< ? extends Object>> list = new ArrayList<ColumnDefinition< ?>>(columns);
        for (ColumnDefinition< ?> colDef : list) {
            delColumn(colDef);
        }
    }

    /**
     * Add a table attribute model listener to the list of listeners.
     */
    public void addTableAMListener(TableAMListener listener) {
        listenerList.add(TableAMListener.class, listener);
    }

    /**
     * Remove a table attribute model listener from the list of listeners.
     */
    public void removeTableAMListener(TableAMListener listener) {
        listenerList.add(TableAMListener.class, listener);
    }

    /**
     * Returns the list of all column definitions
     */
    public List<ColumnDefinition< ? extends Object>> getColumns() {
        return columns;
    }

    /**
     * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#gaChanged(net.ulrice.databinding.IFGuiAccessor, java.lang.Object)
     */
    @Override
    public void gaChanged(IFViewAdapter viewAdapter, Object value) {
        fireUpdateViews();
    }

    /**
     * Inform the connected view adapters about a change in the attribute model.
     */
    public void fireUpdateViews() {
       fireUpdateViews(null);
    }
    
    /**
     * Inform the connected view adapters about a change in an element of the attribute model.
     */
    public void fireUpdateViews(final Element element) {        
        if(massEditMode){
            return;
        }
        if (viewAdapterList != null) {
            for (final IFViewAdapter viewAdapter : viewAdapterList) {
                if (!SwingUtilities.isEventDispatchThread()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if(element != null && viewAdapter instanceof UTableViewAdapter){
                                ((UTableViewAdapter)viewAdapter).updateFromBinding(TableAM.this, element);
                            }else{
                                viewAdapter.updateFromBinding(TableAM.this);
                            }                            
                        }
                    });
                } else {
                    if(element != null && viewAdapter instanceof UTableViewAdapter){
                        ((UTableViewAdapter)viewAdapter).updateFromBinding(this, element);
                    }else{
                        viewAdapter.updateFromBinding(this);
                    }
                }
            }
        }
    }
    
    

    /**
     * Connect a view adapter to this table attribute model
     */
    @Override
    public void addViewAdapter(IFViewAdapter viewAdapter) {
        viewAdapterList.add(viewAdapter);
        viewAdapter.bind(this);
        viewAdapter.updateFromBinding(this);
    }

    /**
     * Detach a view adapter from this attribute model.
     */
    @Override
    public void removeViewAdapter(IFViewAdapter viewAdapter) {
        viewAdapterList.remove(viewAdapter);
        viewAdapter.detach(this);
    }

    /**
     * Returns the index of an element in the table attribute model.
     */
    public int getIndexOfElement(Element element) {
        return elements.indexOf(element);
    }

    public IFValueConverter getValueConverter() {
        // TODO KUH Currently not used.
        return valueConverter;
    }

    @Override
    public void setValueConverter(IFValueConverter valueConverter) {
        this.valueConverter = valueConverter;
    }

    /**
     * True, if this table attribute model is initialized (data was read from the model)
     */
    @Override
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * True, if this table attribute model is dirty.
     */
    @Override
    public boolean isDirty() {
        return dirty;
    }

    /**
     * True, if this table attribute model is valid.
     */
    @Override
    public boolean isValid() {
        return valid;
    }

    /**
     * Returns the validation results. If table model is not valid, the validation result contains all validation errors
     */
    @Override
    public ValidationResult getValidationResult() {
        ValidationResult result = new ValidationResult();
        if (invElements != null) {
            for (Element element : invElements) {
                result.addValidationErrors(element.getValidationErrors());
            }
        }

        return result;
    }

    /**
     * Returns the validation results as list of string.
     */
    @Override
    public List<String> getValidationFailures() {
        List<String> result = new ArrayList<String>();
        if (invElements != null) {
            for (Element element : invElements) {
                result.addAll(element.getValidationFailures());
            }
        }

        return result;
    }

    /**
     * Returns the list of current objects managed by this table attribute model.
     */
    @Override
    public Object getCurrentValue() {
        List<Object> result = new ArrayList<Object>(elements == null ? 0 : elements.size());

        for (Element element : elements) {
            result.add(element.getCurrentValue());
        }

        return result;
    }

    /**
     * Returns the list of objects originally read from the model.
     */
    @Override
    public Object getOriginalValue() {
        List<Object> result = new ArrayList<Object>(elements == null ? 0 : elements.size());

        for (Element element : elements) {
            result.add(element.getOriginalValue());
        }

        return result;
    }

    /**
     * Read the data from the model.
     */
    @Override
    public void read() {
        clear();
        initialized = true;

        int numRows = tableMVA.getSize();
        for (int i = 0; i < numRows; i++) {

            Object value = tableMVA.getValue(i);
            Element elem = createElement(value, false, true, false);

            addElementToIdMap(elem);
            elements.add(elem);
            fireElementAdded(elem);
        }
        
        if(isForTreeTable()){
            clearAllElementValidationErrors();
        }
        
        fireUpdateViews();
        
        if (handleValidity()){
            fireStateChanged();
        }
    }

    private void clearAllElementValidationErrors() {
        for(Element e :elements){
               e.clearElementValidationErrors();
           }
    }

    public AbstractProcess<Void, Void> createLoader(final IFController controller, final boolean blocking, final ListDataProvider<?> provider) {
        return createLoader(controller, blocking, provider, null);
    }

    public AbstractProcess<Void, Void> createLoader(final IFController controller, final boolean blocking, final ListDataProvider<?> provider, final Runnable finishCallback) {
        for (IFViewAdapter viewAdapter : viewAdapterList) {
            if (viewAdapter instanceof UTableViewAdapter) {
                UTableViewAdapter tableViewAdapter = (UTableViewAdapter) viewAdapter;
                tableViewAdapter.getComponent().getRowSorter().disableRowSorter();
            }
        }

        return new AbstractProcess<Void, Void>(controller, blocking) {

            private boolean cancelled = false;

            @Override
            public boolean hasProgressInformation() {
                return true;
            }

            @Override
            public boolean supportsCancel() {
                return true;
            }

            @Override
            public void cancelProcess() {
                cancelled = true;
            }

            @Override
            protected Void work() throws Exception {
                clear();

                initialized = true;

                final List data = provider.getData();
                int numLoaded = 0;                
                if (data != null) {
                    for (Object item : data) {
                        Element elem = createElement(item, false, true, false);
                        addElementToIdMap(elem);
                        elements.add(elem);
                        fireElementAdded(elem);
                        int loadedPercent = Math.round(100.0f / data.size() * numLoaded);
                        setProgress(loadedPercent > 100 ? 100 : loadedPercent);
                        fireProgressChanged();

                        if (cancelled) {
                            break;
                        }
                    }
                }

                return null;
            }

            @Override
            protected void finished(Void result) {
                for (IFViewAdapter viewAdapter : viewAdapterList) {
                    if (viewAdapter instanceof UTableViewAdapter) {
                        UTableViewAdapter tableViewAdapter = (UTableViewAdapter) viewAdapter;
                        tableViewAdapter.getComponent().getRowSorter().reEnableRowSorter();
                        tableViewAdapter.getComponent().sizeColumns(true);
                    }
                }
                fireUpdateViews();
                
                if (handleValidity()){
                    fireStateChanged();
                }
                
                if(finishCallback != null) {
                    finishCallback.run();
                }
            }

            @Override
            protected void failed(Throwable t) {
                Ulrice.getMessageHandler().handleException(getOwningController(), t);
            }
        };
    }

    public AbstractProcess<Void, Void> createIncrementalLoader(final IFController controller, final IncrementalTableDataProvider provider) {

        for (IFViewAdapter viewAdapter : viewAdapterList) {
            if (viewAdapter instanceof UTableViewAdapter) {
                UTableViewAdapter tableViewAdapter = (UTableViewAdapter) viewAdapter;
                tableViewAdapter.getComponent().getRowSorter().disableRowSorter();
            }
        }

        return new AbstractProcess<Void, Void>(controller, provider.isBlocking()) {

            private boolean cancelled = false;
            private int numLoaded = 0;

            @Override
            public boolean hasProgressInformation() {
                return true;
            }

            @Override
            public boolean supportsCancel() {
                return true;
            }

            @Override
            public void cancelProcess() {
                cancelled = true;
            }

            @Override
            protected Void work() throws Exception {
                clear();

                initialized = true;
                int totalNumRows = provider.getNumRows();
                int chunkSize = provider.getChunkSize();
                int upperBound = provider.getUpperBound();
                final int max = Math.min(upperBound, totalNumRows);

                while (numLoaded < max && !cancelled && !isCancelled()) {
                    final List chunk = provider.getNextChunk(numLoaded, chunkSize);

                    if (chunk != null) {
                        for (Object item : chunk) {
                            Element elem = createElement(item, false, true, false);
                            addElementToIdMap(elem);
                            elements.add(elem);
                            fireElementAdded(elem);
                        }
                        fireUpdateViews();
                    }

                    numLoaded += chunk.size();
                    int loadedPercent = Math.round(100.0f / totalNumRows * numLoaded);
                    setProgress(loadedPercent > 100 ? 100 : loadedPercent);
                    fireProgressChanged();

                    if (chunk.size() == 0) {
                        break;
                    }
                }
                return null;
            }

            @Override
            protected void finished(Void result) {
                for (IFViewAdapter viewAdapter : viewAdapterList) {
                    if (viewAdapter instanceof UTableViewAdapter) {
                        UTableViewAdapter tableViewAdapter = (UTableViewAdapter) viewAdapter;
                        tableViewAdapter.getComponent().getRowSorter().reEnableRowSorter();
                        tableViewAdapter.getComponent().sizeColumns(true);
                    }
                    
                    if (handleValidity()){
                        fireStateChanged();
                    }
                }
            }

            @Override
            protected void failed(Throwable t) {
                Ulrice.getMessageHandler().handleException(getOwningController(), t);
            }
        };
    }

    /**
     * Read the data from a given list.
     * 
     * @param valueList The list of data
     * @param append true, if data should be appended to the current list of data
     */
    public void read(List< ?> valueList, boolean append) {
        if (!append) {
            clear();
        }
        initialized = true;

        for (int i = 0; i < valueList.size(); i++) {

            Object value = valueList.get(i);
            Element elem = createElement(value, false, true, false);

            addElementToIdMap(elem);
            elements.add(elem);
            fireElementAdded(elem);
        }
        fireUpdateViews();

        
        if (handleValidity()){
            fireStateChanged();
        }
    }

    /**
     * Read the data from the model.
     * 
     * @param append true, if data should be appended
     * @param firstRow Index of first row that should be read from the model.
     */
    public void read(boolean append, int firstRow) {

        if (!append) {
            clear();
        }
        initialized = true;

        for (int i = firstRow; i < tableMVA.getSize(); i++) {

            Object value = tableMVA.getValue(i);
            Element elem = createElement(value, false, true, false);

            addElementToIdMap(elem);
            elements.add(elem);
            fireElementAdded(elem);
        }
        fireUpdateViews();

        
        if (handleValidity()){
            fireStateChanged();
        }
    }

    /**
     * Clear the data from the model. The model is not initialized after execution of this method
     */
    public void clear() {
        boolean oldValid = valid;
        boolean oldDirty = dirty;

        initialized = false;
        dirty = false;
        valid = true;

        elementIdMap.clear();
        newElements.clear();
        modElements.clear();
        delElements.clear();
        invElements.clear();
        elements.clear();

        for(IFValidator validator : getValidators()){
            validator.clearValidationErrors();
        }
        
        fireUpdateViews();
        fireTableCleared();
        fireDataChanged();

        if (oldValid != valid || oldDirty != dirty) {
            fireStateChanged();
        }
    }

    /**
     * Write the current data into the model.
     */
    @Override
    public void write() {
        final List<Object> values = new ArrayList<Object>();

        for (Element elem : elements) {
            if (!elem.isRemoved()) {
                elem.writeObject();
                values.add(elem.getOriginalValue());
            }
        }

        tableMVA.setValues(values);
    }

    /**
     * Create an empty object for a newly created element. The object is set as the original value in the element.
     * This method delegates to the table mva and can be overridden in special cases.
     */
    protected Object createEmptyElementObject() {
        return tableMVA.newObjectInstance();
    }

    /**
     * Add a new element
     * @param value The original value of the element.
     * @return The newly created element
     */
    public Element addElement(Object value) {
        return addElement(value, false, true);
    }

    /**
     * Add a new element with a given state
     * 
     * @param value The original value.
     * @param dirty True, if the added value should be marked as dirty.
     * @param valid True, if the added value should be marked as valid.
     * @return The newly created element
     */
    public Element addElement(Object value, boolean dirty, boolean valid) {
        return addElement(-1, value, dirty, valid, false);
    }

    /**
     * Add a new element with a given index.
     * 
     * @param index The index at which the element should be added.
     * @param value The original value.
     * @return The newly created element
     */
    public Element addElement(int index, Object value) {
        return addElement(index, value, false, true, false);
    }

    /**
     * Add an element
     * 
     * @param index The index at which the element should be added.*
     * @param value The original value.
     * @param dirty True, if the added value should be marked as dirty.
     * @param valid True, if the added value should be marked as valid.
     * @return The newly created element
     */
    public Element addElement(int index, Object value, boolean dirty, boolean valid, boolean onlyChild) {
        if (value == null) {
            value = createEmptyElementObject();
        }

        Element element = createElement(value, dirty, valid, true);

        // unique constraint handling
        Element oldElement = element;
//        if (keyDeleteMap.containsValue(buildKey(element))) {
//            oldElement = checkAddedOrChangedElementAgainstDeletedElements(element);
//        }
//        else {
            addElementToIdMap(element);
            element.setInserted(true);
            newElements.add(element);
//        //}

        // end of unique constraint handling
        if (!onlyChild) {
        if (index >= 0) {
            elements.add(index, oldElement);
        }
        else {
            elements.add(oldElement);
        }

        }

        fireElementAdded(oldElement);
        handleElementStateChange(oldElement);
        fireUpdateViews();
        return oldElement;
    }

    /**
     * Delete an element from this model.
     */
    public boolean delElement(int index) {
        return delElement(elements.get(index));
    }

    /**
     * Delete an element from this model.
     */
    public boolean delElement(Element element) {
        if (element == null) {
            return false;
        }

        if (!isDisplayRemovedEntries() || element.isInserted()) {
            boolean removed = elements.remove(element);
            if (!removed) {
                return false;
            }
        }

        if (!newElements.contains(element)) {
            delElements.add(element);
        }
        invElements.remove(element);
        newElements.remove(element);
        modElements.remove(element);
        element.setRemoved(true);
        handleElementStateChange(element);
        fireElementDeleted(element);

        if (element.isInserted()) {
            elementIdMap.remove(element.getUniqueId());
            //keyDeleteMap.remove(element.getUniqueId());
            keyMap.remove(element.getUniqueId());
            //List< ?> functionalKey = buildKey(element);
            //uniqueDeleteMap.remove(functionalKey);
        }
        setTreeStayOpen();
        fireUpdateViews(element);
        
        return true;
    }

    /**
     * Move the element one position up.
     */
    public void moveElementUp(Element element) {
        if (element == null) {
            return;
        }
        int idx = getIndexOfElement(element);
        if (idx > 0) {
            elements.remove(idx);
            idx--;
            elements.add(idx, element);
        }
        fireUpdateViews();
    }

    /**
     * Move the element one position down.
     * @param element
     */
    public void moveElementDown(Element element) {
        if (element == null) {
            return;
        }
        int idx = getIndexOfElement(element);
        if (idx < elements.size() - 1) {
            elements.remove(idx);
            idx++;
            elements.add(idx, element);
        }
        fireUpdateViews();
    }

    /**
     * Returns the list of objects marked as deleted
     */
    @SuppressWarnings("unchecked")
    public List getDeletedObjects() {
        List result = new ArrayList();
        for (Element element : delElements) {
            result.add(element.getCurrentValue());
        }
        return result;
    }

    /**
     * Returns the list of objects marked as created
     */
    @SuppressWarnings("unchecked")
    public List getCreatedObjects() {
        List result = new ArrayList();
        for (Element element : newElements) {
            result.add(element.getCurrentValue());
        }
        return result;
    }

    /**
     * Returns the list of objects marked as modified
     */
    @SuppressWarnings("unchecked")
    public List getModifiedObjects() {
        List result = new ArrayList();
        for (Element element : modElements) {
            result.add(element.getCurrentValue());
        }
        return result;
    }

    /**
     * Returns the list of objects marked as invalid
     */
    @SuppressWarnings("unchecked")
    public List getInvalidObjects() {
        List result = new ArrayList();
        for (Element element : invElements) {
            result.add(element.getCurrentValue());
        }
        return result;
    }

    /**
     * Returns the number of objects marked as new.
     */
    public int getCreatedCount() {
        return newElements.size();
    }

    /**
     * Returns the number of objects marked as modified.
     */
    public int getModifiedCount() {
        return modElements.size();
    }

    /**
     * Returns the number of objects marked as deleted.
     */
    public int getDeletedCount() {
        return delElements.size();
    }

    /**
     * Returns the number of objects marked as invalid.
     */
    public int getInvalidCount() {
        return invElements.size();
    }

    /**
     * Returns the element by the unique id.
     */
    public Element getElementById(Long uniqueId) {
        return elementIdMap.get(uniqueId);
    }

    /**
     * Clones an original objects of an element. This is used during the copy process of an element. This method delegates to the table model accessor
     */
    protected Object cloneObject(Object value) {
        return tableMVA.cloneObject(value);
    }

    /**
     * Sets this table attribute model to read only
     */
    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * Returns the list of elements marked as new.
     */
    public List<Element> getCreatedElements() {
        return new ArrayList<Element>(newElements);
    }

    /**
     * Returns the list of elements marked as modified.
     */
    public List<Element> getModifiedElements() {
        return new ArrayList<Element>(modElements);
    }

    /**
     * Returns the list of elements marked as deleted.
     */
    public List<Element> getDeletedElements() {
        return new ArrayList<Element>(delElements);
    }

    /**
     * Returns the list of elements
     */
    public List<Element> getElements() {
        return new ArrayList<Element>(elements);
    }

    /**
     * Commit an element. This accepts all changes that were made in this object
     */
    public void commitElement(Element element) {

        boolean removed = element.isRemoved();
        if (element.isRemoved() && isDisplayRemovedEntries()) {
            if(!elements.remove(element) && isForTreeTable()){
                for(Element e: elements){
                    if(e.removeChild(element)){
                        break;
                    }
                }
            }
        }

        element.writeObject();
        element.readObject();
        newElements.remove(element);
        delElements.remove(element);
        handleElementStateChange(element);
        element.setInserted(false);
        element.setRemoved(false);
        setTreeStayOpen();
        if(removed){
            fireUpdateViews();
        }else{
            fireUpdateViews(element);
        }
        
        
    }

    /**
     * Rollback the element. This discards all changes that were made in this object
     */
    public void rollbackElement(Element element) {
        boolean wasInserted = element.isInserted();
        if (wasInserted) {
            delElement(element);
        }

        element.readObject();
        if (!wasInserted && element.isRemoved() && isDisplayRemovedEntries()) {
            element.setRemoved(false);
            delElements.remove(element);
        }

        handleElementStateChange(element);
        fireUpdateViews();
    }

    /**
     * Marks an element as faulty.
     */
    public void markAsFaulty(Element element, String message, Throwable th) {
        element.addElementValidationError(new ValidationError(null, message, th));
    }

    /**
     * Returns the current value from the element located at a given row.
     */
    public Object getCurrentValueAt(int row) {
        Element element = getElementAt(row);
        return element != null ? element.getCurrentValue() : null;
    }

    /**
     * Set the names of the columns which are the unique key.
     */
    public void setUniqueConstraint(String... uniqueKeyColumnIds) {
        this.uniqueKeyColumnIds = uniqueKeyColumnIds;
    }

    /**
     * Add an element lifecycle listener to the list of listeners.
     */
    public void addElementLifecycleListener(ElementLifecycleListener constraint) {
        listenerList.add(ElementLifecycleListener.class, constraint);
    }
    
    public ElementLifecycleListener[] getElementLifecycleListeners() {
        return listenerList.getListeners(ElementLifecycleListener.class);
    }

    /**
     * Remove an element lifecycle listener from the list of listeners.
     */
    public void removeElementLifecycleListener(ElementLifecycleListener constraint) {
        listenerList.remove(ElementLifecycleListener.class, constraint);
    }

    /**
     * Inform the element lifecycle listeners about an added element.
     */
    private void fireElementAdded(final Element element) {
        if (uniqueKeyColumnIds != null) {
            checkUniqueConstraint(element);
        }

        if (handleValidity()){
            fireStateChanged();
        }

        if (!massEditMode) {
            ElementLifecycleListener[] listeners = listenerList.getListeners(ElementLifecycleListener.class);
            if (listeners != null) {
                for (final ElementLifecycleListener constraint : listeners) {
                    if (!SwingUtilities.isEventDispatchThread()) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                constraint.elementAdded(TableAM.this, element);
                            }
                        });
                    }
                    else {
                        constraint.elementAdded(this, element);
                    }
                }
            }
        }
    }

    /**
     * Add an element to the internal data structures
     * 
     *
     * @param element
     */
    private void addElementToIdMap(Element element) {
        elementIdMap.put(element.getUniqueId(), element);
        for (int i = 0; i < element.getChildCount(); i++) {
            addElementToIdMap(element.getChild(i));
        }
    }

    /**
     * Inform the element lifecycle listeners about a removed element
     */
    private void fireElementDeleted(final Element element) {
        if (uniqueKeyColumnIds != null) {
            checkKeyChangeAndUpdateDatastructure(element.getUniqueId(), null);

            // uniqueConstraint.elementRemoved(this, element);
        }

        if (handleValidity()){
            fireStateChanged();
        }

        ElementLifecycleListener[] listeners = listenerList.getListeners(ElementLifecycleListener.class);
        if (listeners != null) {
            for (final ElementLifecycleListener constraint : listeners) {
                if (!SwingUtilities.isEventDispatchThread()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            constraint.elementRemoved(TableAM.this, element);
                        }
                    });
                } else {
                    constraint.elementRemoved(this, element);
                }
            }
        }
    }

    /**
     * Inform element lifecycle listeners about the clear event.
     */
    private void fireTableCleared() {
        if (uniqueKeyColumnIds != null) {
            uniqueMap.clear();
            keyMap.clear();
//            uniqueDeleteMap.clear();
//            keyDeleteMap.clear();
        }

        ElementLifecycleListener[] listeners = listenerList.getListeners(ElementLifecycleListener.class);
        if (listeners != null) {
            for (final ElementLifecycleListener constraint : listeners) {
                if (!SwingUtilities.isEventDispatchThread()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            constraint.tableCleared(TableAM.this);
                        }
                    });
                } else {
                    constraint.tableCleared(this);
                }
            }
        }
    }

    /**
     * Inform the element lifecycle listeners about the status change of an element
     */
    private void fireElementStatusChanged(final Element element) {
        if (uniqueKeyColumnIds != null) {
            // uniqueConstraint.elementStateChanged(this, element);
        }

        ElementLifecycleListener[] listeners = listenerList.getListeners(ElementLifecycleListener.class);
        if (listeners != null) {
            for (final ElementLifecycleListener constraint : listeners) {
                if (!SwingUtilities.isEventDispatchThread()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            constraint.elementStateChanged(TableAM.this, element);
                        }
                    });
                } else {
                    constraint.elementStateChanged(this, element);
                }
            }
        }
    }

    @Override
    public IFAttributeInfo getAttributeInfo() {
        return attributeInfo;
    }

    public ColumnDefinition getColumnById(String key) {
        return columnIdMap.get(key);
    }

    public ColumnDefinition getColumnByIndex(int index) {
        return columns.get(index);
    }

    /**
     * Returns the Element, if an Element contains the given object, else null
     * 
     * TODO: RAD wir haben das else bewusst entfernt... bin mir aber nicht mehr sicher warum
     */
    public Element getElementOfObjectWithoutTempElement(Object object) {
        List<Object> key = new ArrayList<Object>(uniqueKeyColumnIds != null ? uniqueKeyColumnIds.length : 0);
        if (uniqueKeyColumnIds != null) {
            for (String columnId : uniqueKeyColumnIds) {
                ColumnDefinition colDef = columnIdMap.get(columnId);
                key.add(colDef.getDataAccessor().getValue(object));
            }

            Set<Long> idSet = uniqueMap.get(key);
            if (idSet != null) {
                if (idSet.size() == 1) {
                    return getElementById(idSet.iterator().next());
                }
                for (Long id : idSet) {
                    Element element = getElementById(id);
                    if (element.getCurrentValue().equals(object)) {
                        return element;
                    }
                }
            }
        } else {
            if (elements != null) {
                for (Element element : elements) {
                    if (element.getCurrentValue().equals(object)) {
                        return element;
                    }
                    if (isForTreeTable()) {
                        Element found = element.getChildByCurrentValue(object);
                        if (found != null) {
                            return found;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Return an element of a current value or null, if element is not available
     */
    public Element getElementOfObject(Object object) {
        if (uniqueKeyColumnIds != null) {
            Element tempElement = createElement(object, false, false, true);
            List< ?> key = buildKey(tempElement);
            Set<Long> idSet = uniqueMap.get(key);
            if (idSet != null) {
                if (idSet.size() == 1) {
                    return getElementById(idSet.iterator().next());
                }
                for (Long id : idSet) {
                    Element element = getElementById(id);
                    if (element.getCurrentValue().equals(object)) {
                        return element;
                    }
                }
            }
        }
        
            if (elements != null) {
                for (Element element : elements) {
                    if (element.getCurrentValue().equals(object)) {
                        return element;
                    }
                    if (isForTreeTable()) {
                        Element found = element.getChildByCurrentValue(object);
                        if (found != null) {
                            return found;
                        }
                    }
                }
            }

        

        return null;
    }

    /**
     * Activate the mass edit mode, where fireUpdateViews and fireDataChanged are not active
     */
    public void activateMassEditMode() {
        this.massEditMode = true;
    }

    /**
     * Deactivate the mass edit mode, where fireUpdateViews and fireDataChanged are active
     * and execute fireUpdateViews and fireDataChanged
     */
    public void deactivateMassEditModeAndUpdate() {
        this.massEditMode = false;

        fireUpdateViews();
        fireDataChanged();
        fireStateChanged();
    }

    @Override
    public void addExternalValidationError(String translatedMessage) {
        // TODO Implement me..
    }

    @Override
    public void clearExternalValidationErrors() {
        // TODO Implement me..
    }
    

    @Override
    public void addExternalValidationError(ValidationError validationError) {
        // TODO Implement me..
    }

    public String getPathToChildren() {
        return pathToChildren;
    }

    public void setPathToChildren(String pathToChildren) {
        this.pathToChildren = pathToChildren;
    }

    public boolean isForTreeTable() {
        return pathToChildren != null;
    }

    public List<SortKey> getDefaultSortKeys() {
        return defaultSortKeys;
    }

    /**
     * set a list of default sort keys for the table
     */
    public void setDefaultSortKeys(List<SortKey> defaultSortKeys) {
        this.defaultSortKeys = defaultSortKeys;
    }

    public boolean isDisplayRemovedEntries() {
        return displayRemovedEntries;
    }

    /**
     * True, if removed entries should be still displayed in the table.
     */
    public void setDisplayRemovedEntries(boolean displayRemovedEntries) {
        this.displayRemovedEntries = displayRemovedEntries;
    }

    @Override
    public String toString() {
        return elements.size() + " Elements";
    }

    public List<SortKey> getMandatorySortKeys() {
        return mandatorySortKeys;
    }

    /**
     * TODO RAD Comment me..
     */
    public void setMandatorySortKeys(List<SortKey> mandatorySortKeys) {
        this.mandatorySortKeys = mandatorySortKeys;
    }
    /**
     * @return the selectOnlyNodes
     */
    public boolean isVirtualTreeNodes() {
        return virtualTreeNodes;
    }

    /**
     * @param selectOnlyNodes the selectOnlyNodes to set
     */
    public void setVirtualTreeNodes(boolean virtualTreeNodes) {
        this.virtualTreeNodes = virtualTreeNodes;
    }

    public void addChildsToElement(Object value, Element element) {
        Element child = addElement(-1, value, false, true, true);
        element.addChildElement(child);
        // TODO RAD check if needed
        elements.remove(child);
    }
    
    public List<Element> getLeafNodes(){
        List<Element> result = new ArrayList<Element>();
        for(Element element : elements){
            element.addLeafNodes(result);
        }
        return result;
    }
    
    public Map<String, Integer> getIdModelIndexMap() {
        return idModelIndexMap;
    }
    
    public void setIdModelIndexMap(Map<String, Integer> idModelIndexMap) {
        this.idModelIndexMap = idModelIndexMap;
    }
}
