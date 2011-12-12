package net.ulrice.databinding.bufferedbinding.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.bufferedbinding.IFAttributeModel;
import net.ulrice.databinding.bufferedbinding.IFAttributeModelEventListener;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.modelaccess.IFIndexedModelValueAccessor;
import net.ulrice.databinding.validation.IFValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;
import net.ulrice.databinding.viewadapter.IFViewAdapter;

/**
 * @author christof
 */
public class TableAM implements IFAttributeModel {

    private IFIndexedModelValueAccessor tableMVA;

    // private ElementLifecycleListener uniqueConstraint = null;

    private List<ColumnDefinition< ? extends Object>> columns = new ArrayList<ColumnDefinition< ? extends Object>>();
    private Map<String, ColumnDefinition> columnIdMap = new HashMap<String, ColumnDefinition>();

    protected List<Element> elements = new ArrayList<Element>();
    protected Map<String, Element> elementIdMap = new HashMap<String, Element>();

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
    //prevent update ui, for mass editing
    private boolean massEditMode = false;

    // unique constraint handling
    private String[] columnIds = null;
    private Map<List< ?>, Set<String>> uniqueMap = new HashMap<List< ?>, Set<String>>();
    private Map<List< ?>, Set<String>> uniqueDeleteMap = new HashMap<List< ?>, Set<String>>();
    private Map<String, List< ?>> keyMap = new HashMap<String, List< ?>>();
    private Map<String, List< ?>> keyDeleteMap = new HashMap<String, List< ?>>();
    private Map<List< ?>, ValidationError> currentErrorMap = new HashMap<List< ?>, ValidationError>();

    
    public TableAM(IFIndexedModelValueAccessor tableMVA, IFAttributeInfo attributeInfo) {
        this(tableMVA, attributeInfo, false);
    }

    public TableAM(IFIndexedModelValueAccessor tableMVA, IFAttributeInfo attributeInfo, boolean readOnly) {
        this(tableMVA.getAttributeId(), tableMVA, attributeInfo, readOnly);
    }
    
    public TableAM(String id, IFIndexedModelValueAccessor tableMVA, IFAttributeInfo attributeInfo, boolean readOnly) {
        this.id = id;
        this.tableMVA = tableMVA;
        this.readOnly = readOnly;
        this.attributeInfo = attributeInfo;

        nextUniqueId = System.currentTimeMillis();
    }

    
    private void checkUniqueConstraint(Element element) {
        if (columnIds == null) {
            return;
        }

        List< ?> key = buildKey(element);
        if (handleKey(element.getUniqueId(), key)) {
            if (uniqueMap.containsKey(key)) {
                Set<String> uniqueIdSet = uniqueMap.get(key);
                uniqueIdSet.add(element.getUniqueId());
                if (uniqueIdSet.size() > 1) {
                    ValidationError uniqueConstraintError =
                            new ValidationError(this, "Unique key constraint error", null);
                    currentErrorMap.put(key, uniqueConstraintError);
                    for (String uniqueId : uniqueIdSet) {
                        Element elementById = getElementById(uniqueId);
                        elementById.addElementValidationError(uniqueConstraintError);
                    }
                }
            }
            else {
                Set<String> uniqueIdSet = new HashSet<String>();
                uniqueIdSet.add(element.getUniqueId());
                uniqueMap.put(key, uniqueIdSet);
            }
        }
    }

    private boolean handleKey(String uniqueId, List< ?> key) {
        List< ?> oldKey = keyMap.get(uniqueId);
        if (oldKey == null && key != null) {
            // String oldUniqueId = checkForOldUniqueId(key, uniqueId);
            keyMap.put(uniqueId, key);
            return true;
        }

        if (key == null || !oldKey.equals(key)) {
            if (oldKey != null) {
                Set<String> uniqueKeySet = uniqueMap.get(oldKey);
                uniqueKeySet.remove(uniqueId);
                // should not happen
                if (uniqueDeleteMap.containsKey(oldKey)) {
                    Set<String> uniqueDeleteKeySet = uniqueDeleteMap.get(oldKey);
                    uniqueDeleteKeySet.add(uniqueId);
                }
                else {
                    Set<String> uniqueIdSet = new HashSet<String>();
                    uniqueIdSet.add(uniqueId);
                    uniqueDeleteMap.put(oldKey, uniqueIdSet);
                }
                if (uniqueKeySet.size() <= 1 && currentErrorMap.containsKey(oldKey)) {
                    ValidationError validationError = currentErrorMap.remove(oldKey);
                    getElementById(uniqueId).removeElementValidationError(validationError);
                    for (String uniqueElementId : uniqueKeySet) {
                        getElementById(uniqueElementId).removeElementValidationError(validationError);
                    }

                }
                keyDeleteMap.put(uniqueId, oldKey);
                keyMap.put(uniqueId, key);
            }
            return true;
        }
        return false;
    }

    private List< ?> buildKey(Element element) {
        List<Object> key = new ArrayList<Object>(columnIds != null ? columnIds.length : 0);
        if(columnIds != null) {
            for (String columnId : columnIds) {
                key.add(element.getValueAt(columnId));
            }
        }
        return key;
    }

    private String checkForOldUniqueId(List< ?> key, String newUniqueId) {
        String oldUniqueId = null;
        if (keyDeleteMap.containsValue(key)) {
            for (Entry<String, List< ?>> entry : keyDeleteMap.entrySet()) {
                if (key.equals(entry.getValue())) {
                    oldUniqueId = entry.getKey();
                    // remove Element from new Elements and replace it with the former deleted one
                    // newElements.remove(getElementById(newUniqueId));
//                    keyDeleteMap.remove(oldUniqueId);
                }
            }
        }
        return oldUniqueId;
    }
    
    private Element checkAgainstDeletedElements(Element element) {
    	Element oldElement = null;
    	String oldUniqueId = checkForOldUniqueId(buildKey(element), element.getUniqueId());

        if (oldUniqueId == null) {
            return element;
        }
        else {
            // delete old element from delElements
            // how to find old element?
            Iterator<Element> iter = delElements.iterator();
            while (iter.hasNext()) {
                Element el = iter.next();
                if (oldUniqueId.equals(el.getUniqueId())) {
                    oldElement = el;
                    oldElement.setInsertedOrRemoved(false);
                    iter.remove();
                    break;
                }
            }
            if (oldElement != null) {
                oldElement.setCurrentValue(element.getCurrentValue());
            }
            else {
                oldElement = element;
            }

            elementIdMap.put(oldUniqueId, oldElement);
            return oldElement;
        }
    }

    // end of unique constraint handling

    /**
     * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    public boolean isCellValid(int row, int column) {
        return getElementAt(row).isColumnValid(column);
    }

    public boolean isCellDirty(int row, int column) {
        return getElementAt(row).isColumnDirty(column);
    }

    /**
     * @param value
     * @return
     */
    protected Element createElement(Object value, boolean dirty, boolean valid, boolean insertedOrDeleted) {
        String uniqueId = Long.toHexString(nextUniqueId++);
        Element elem = new Element(this, uniqueId, columns, value, isReadOnly(), dirty, valid, insertedOrDeleted);
        return elem;
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

    public boolean isNew(Element element) {
        return newElements.contains(element);
    }

    public boolean isRemoved(Element element) {
        return delElements.contains(element);
    }

    /**
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return !isReadOnly() && getElementAt(rowIndex) != null && !getElementAt(rowIndex).isReadOnly(columnIndex);
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Element element = getElementAt(rowIndex);
        if (element != null) {
            return element.getValueAt(columnIndex);
        }
        return null;
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

    protected void elementDataChanged(final Element element, final String columnId) {
        fireUpdateViews();
        
    	if (columnIds != null) {
            checkUniqueConstraint(element);
            if (keyDeleteMap.containsValue(buildKey(element))) {
            	checkAgainstDeletedElements(element);
            }
            checkAgainstDeletedElements(element);
        }
        
		fireElementChanged(element, columnId);
		
		fireDataChanged();
    }

    protected void elementStateChanged(final Element element) {
        if (element.isValid()) {
            invElements.remove(element);
        }
        else {
            invElements.add(element);
        }

        if (element.isDirty() && !element.isInsertedOrRemoved() && elementIdMap.containsKey(element.getUniqueId())
            && !newElements.contains(element) && !delElements.contains(element)) {
            modElements.add(element);
        }

        if (!element.isDirty() && elementIdMap.containsKey(element.getUniqueId())) {
            modElements.remove(element);
        }

        boolean oldValid = valid;
        boolean oldDirty = dirty;

        valid = invElements.isEmpty();
        dirty = !modElements.isEmpty() || !delElements.isEmpty() || !newElements.isEmpty();

        fireElementStatusChanged(element);

        if (oldValid != valid || oldDirty != dirty) {
        	fireStateChanged();
        }

    }
    
    private void fireElementChanged(final Element element, final String columnId) {
    	ElementLifecycleListener[] listeners = listenerList.getListeners(ElementLifecycleListener.class);
        if (listeners != null) {
            for (final ElementLifecycleListener constraint : listeners) {
                if(!SwingUtilities.isEventDispatchThread()) {
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
    }

    private void fireDataChanged() {
        IFAttributeModelEventListener[] listeners = listenerList.getListeners(IFAttributeModelEventListener.class);
        if (listeners != null && !massEditMode) {
            for (final IFAttributeModelEventListener listener : listeners) {
                if(!SwingUtilities.isEventDispatchThread()) {
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

    private void fireStateChanged() {
        IFAttributeModelEventListener[] listeners = listenerList.getListeners(IFAttributeModelEventListener.class);
        if (listeners != null) {
            for (final IFAttributeModelEventListener listener : listeners) {
                if(!SwingUtilities.isEventDispatchThread()) {
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
     * @param columnDefinition
     */
    public void addColumn(ColumnDefinition< ?> columnDefinition) {
        columnDefinition.addChangeListener(new ColumnDefinitionChangedListener() {
            @Override
            public void valueRangeChanged(ColumnDefinition< ?> colDef) {
                fireColumnValueRangeChanged(colDef);
            }

            @Override
            public void filterModeChanged(ColumnDefinition< ?> colDef) {
                fireColumnFilterModeChanged(colDef);
            }
        });
        columns.add(columnDefinition);
        columnIdMap.put(columnDefinition.getId(), columnDefinition);
        fireColumnAdded(columnDefinition);
    }

    public void delColumn(ColumnDefinition< ?> columnDefinition) {
        columns.remove(columnDefinition);
        columnIdMap.remove(columnDefinition.getId());
        fireColumnRemoved(columnDefinition);
    }

    public void delAllColumns() {
        List<ColumnDefinition< ? extends Object>> list = new ArrayList<ColumnDefinition< ?>>(columns);
        for (ColumnDefinition< ?> colDef : list) {
            delColumn(colDef);
        }
    }

    public void addTableAMListener(TableAMListener listener) {
        listenerList.add(TableAMListener.class, listener);
    }

    public void removeTableAMListener(TableAMListener listener) {
        listenerList.add(TableAMListener.class, listener);
    }

    private void fireColumnValueRangeChanged(final ColumnDefinition< ?> colDef) {
        TableAMListener[] listeners = listenerList.getListeners(TableAMListener.class);
        if (listeners != null) {
            for (final TableAMListener listener : listeners) {
                if(!SwingUtilities.isEventDispatchThread()) {
                    SwingUtilities.invokeLater(new Runnable() {                    
                        @Override
                        public void run() {
                            listener.columnValueRangeChanged(TableAM.this, colDef);
                        }
                    });
                } else {
                    listener.columnValueRangeChanged(this, colDef);
                }
            }
        }
    }

    private void fireColumnAdded(final ColumnDefinition< ?> colDef) {
        TableAMListener[] listeners = listenerList.getListeners(TableAMListener.class);
        if (listeners != null) {
            for (final TableAMListener listener : listeners) {
                if(!SwingUtilities.isEventDispatchThread()) {
                    SwingUtilities.invokeLater(new Runnable() {                    
                        @Override
                        public void run() {
                            listener.columnAdded(TableAM.this, colDef);
                        }
                    });
                } else {
                    listener.columnAdded(this, colDef);
                }
            }
        }
    }

    private void fireColumnRemoved(final ColumnDefinition< ?> colDef) {
        TableAMListener[] listeners = listenerList.getListeners(TableAMListener.class);
        if (listeners != null) {
            for (final TableAMListener listener : listeners) {
                if(!SwingUtilities.isEventDispatchThread()) {
                    SwingUtilities.invokeLater(new Runnable() {                    
                        @Override
                        public void run() {
                            listener.columnRemoved(TableAM.this, colDef);
                        }
                    });
                } else {
                    listener.columnRemoved(this, colDef);
                }
            }
        }
    }

    private void fireColumnFilterModeChanged(final ColumnDefinition< ?> colDef) {
        TableAMListener[] listeners = listenerList.getListeners(TableAMListener.class);
        if (listeners != null) {
            for (final TableAMListener listener : listeners) {
                if(!SwingUtilities.isEventDispatchThread()) {
                    SwingUtilities.invokeLater(new Runnable() {                    
                        @Override
                        public void run() {
                            listener.columnFilterModeChanged(TableAM.this, colDef);
                        }
                    });
                } else {
                    listener.columnFilterModeChanged(this, colDef);
                }
            }
        }
    }

    /**
     * @return the columns
     */
    public List<ColumnDefinition< ? extends Object>> getColumns() {
        return columns;
    }

    /**
     * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#gaChanged(net.ulrice.databinding.IFGuiAccessor,
     *      java.lang.Object)
     */
    @Override
    public void gaChanged(IFViewAdapter viewAdapter, Object value) {
        fireUpdateViews();
    }

    public void fireUpdateViews() {
        if (viewAdapterList != null && !massEditMode) {
            for (final IFViewAdapter viewAdapter : viewAdapterList) {
                if(!SwingUtilities.isEventDispatchThread()) {
                    SwingUtilities.invokeLater(new Runnable() {                    
                        @Override
                        public void run() {
                            viewAdapter.updateFromBinding(TableAM.this);
                        }
                    });
                } else {
                    viewAdapter.updateFromBinding(this);
                }
            }
        }
    }

    @Override
    public void addViewAdapter(IFViewAdapter viewAdapter) {
        viewAdapterList.add(viewAdapter);
        viewAdapter.bind(this);
        viewAdapter.updateFromBinding(this);
    }

    public void removeViewAdapter(IFViewAdapter viewAdapter) {
        viewAdapterList.remove(viewAdapter);
        viewAdapter.detach(this);
    }

    public int getIndexOfElement(Element element) {
        return elements.indexOf(element);
    }

    public IFValueConverter getValueConverter() {
        return valueConverter;
    }

    @Override
    public void setValueConverter(IFValueConverter valueConverter) {
        this.valueConverter = valueConverter;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    protected void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    protected void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    protected void setValid(boolean valid) {
        this.valid = valid;
    }

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

    @Override
    public Object getCurrentValue() {
        List<Object> result = new ArrayList<Object>(elements == null ? 0 : elements.size());

        for (Element element : elements) {
            result.add(element.getCurrentValue());
        }

        return result;
    }

    @Override
    public Object getOriginalValue() {
        List<Object> result = new ArrayList<Object>(elements == null ? 0 : elements.size());

        for (Element element : elements) {
            result.add(element.getOriginalValue());
        }

        return result;
    }

    /**
     * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#read()
     */
    @Override
    public void read() {
        clear();
        initialized = true;

        int numRows = tableMVA.getSize();
        for (int i = 0; i < numRows; i++) {

            Object value = tableMVA.getValue(i);
            Element elem = createElement(value, false, true, false);
            elem.readObject();

            elementIdMap.put(elem.getUniqueId(), elem);
            elements.add(elem);
            fireElementAdded(elem);
        }
        fireUpdateViews();
    }

    public void read(List< ?> valueList, boolean append) {
        if (!append) {
            clear();
        }
        initialized = true;

        for (int i = 0; i < valueList.size(); i++) {

            Object value = valueList.get(i);
            Element elem = createElement(value, false, true, false);
            elem.readObject();

            elementIdMap.put(elem.getUniqueId(), elem);
            elements.add(elem);
            fireElementAdded(elem);
        }
        fireUpdateViews();
    }

    public void read(boolean append, int firstRow) {

        if (!append) {
            clear();
        }
        initialized = true;

        for (int i = firstRow; i < tableMVA.getSize(); i++) {

            Object value = tableMVA.getValue(i);
            Element elem = createElement(value, false, true, false);
            elem.readObject();

            elementIdMap.put(elem.getUniqueId(), elem);
            elements.add(elem);
            fireElementAdded(elem);
        }
        fireUpdateViews();
    }

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

        fireTableCleared();
        fireDataChanged();
        
        if (oldValid != valid || oldDirty != dirty) {
            fireStateChanged();
        }
    }

    /**
     * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#write()
     */
    @Override
    public void write() {
        final List<Object> values = new ArrayList<Object>();

        for (Element elem : elements) {
            elem.writeObject();
            values.add(elem.getOriginalValue());
        }

        tableMVA.setValues(values);
    }

    protected Object createEmptyElementObject() {
        return tableMVA.newObjectInstance();
    }

    public Element addElement(Object value) {
        return addElement(value, false, true);
    }

    public Element addElement(Object value, boolean dirty, boolean valid) {
        return addElement(-1, value, dirty, valid);
    }

    public Element addElement(int index, Object value) {
        return addElement(index, value, false, true);
    }

    public Element addElement(int index, Object value, boolean dirty, boolean valid) {
        if (value == null) {
            value = createEmptyElementObject();
        }

        Element element = createElement(value, dirty, valid, true);

        // unique constraint handling
        Element oldElement = element;
        if (keyDeleteMap.containsValue(buildKey(element))) {
        	oldElement = checkAgainstDeletedElements(element);
        }
        else {
        	registerNewElement(element);
        }
        
        // end of unique constraint handling

        if (index >= 0) {
            elements.add(index, oldElement);
        }
        else {
            elements.add(oldElement);
        }

        fireElementAdded(oldElement);
        elementStateChanged(oldElement);
        fireUpdateViews();
        return oldElement;
    }

    public boolean delElement(int index) {
        return delElement(elements.get(index));
    }

    public boolean delElement(Element element) {
        if (element == null) {
            return false;
        }

        boolean removed = elements.remove(element);
        if (!removed) {
            return false;
        }
        if (!newElements.contains(element)) {
            delElements.add(element);
        }
        invElements.remove(element);
        newElements.remove(element);
        modElements.remove(element);
        elementStateChanged(element);
        element.setInsertedOrRemoved(true);
        fireElementDeleted(element);
        fireUpdateViews();
        return true;
    }

    public List getDeletedObjects() {
        List result = new ArrayList();
        for (Element element : delElements) {
            result.add(element.getCurrentValue());
        }
        return result;
    }

    public List getCreatedObjects() {
        List result = new ArrayList();
        for (Element element : newElements) {
            result.add(element.getCurrentValue());
        }
        return result;
    }

    public List getModifiedObjects() {
        List result = new ArrayList();
        for (Element element : modElements) {
            result.add(element.getCurrentValue());
        }
        return result;
    }

    public List getInvalidObjects() {
        List result = new ArrayList();
        for (Element element : invElements) {
            result.add(element.getCurrentValue());
        }
        return result;
    }

    public int getCreatedCount() {
        return newElements.size();
    }

    public int getModifiedCount() {
        return modElements.size();
    }

    public int getDeletedCount() {
        return delElements.size();
    }

    public int getInvalidCount() {
        return invElements.size();
    }

    public Element getElementById(String uniqueId) {
        return elementIdMap.get(uniqueId);
    }

    protected Object cloneObject(Object value) {
        return tableMVA.cloneObject(value);
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public List<Element> getCreatedElements() {
        return new ArrayList<Element>(newElements);
    }

    public List<Element> getModifiedElements() {
        return new ArrayList<Element>(modElements);
    }

    public List<Element> getDeletedElements() {
        return new ArrayList<Element>(delElements);
    }

    public void commitElement(Element element) {
        element.writeObject();
        element.readObject();
        newElements.remove(element);
        delElements.remove(element);
        elementStateChanged(element);
        element.setInsertedOrRemoved(false);
        fireUpdateViews();
    }

    public void rollbackElement(Element element) {
        element.readObject();
        elementStateChanged(element);
        fireUpdateViews();
    }

    public void markAsFaulty(Element element, String message, Throwable th) {
        element.addElementValidationError(new ValidationError(null, message, th));
    }

    public Object getCurrentValueAt(int row) {
        Element element = getElementAt(row);
        return element != null ? element.getCurrentValue() : null;
    }

    public void setUniqueConstraint(String... columnIds) {
        // this.uniqueConstraint = new UniqueConstraint(columnIds);
        this.columnIds = columnIds;
    }

    public void addElementLifecycleListener(ElementLifecycleListener constraint) {
        listenerList.add(ElementLifecycleListener.class, constraint);
    }

    public void removeElementLifecycleListener(ElementLifecycleListener constraint) {
        listenerList.remove(ElementLifecycleListener.class, constraint);
    }

    private void fireElementAdded(final Element element) {
        if (columnIds != null) {
            checkUniqueConstraint(element);
        }

        ElementLifecycleListener[] listeners = listenerList.getListeners(ElementLifecycleListener.class);
        if (listeners != null) {
            for (final ElementLifecycleListener constraint : listeners) {
                if(!SwingUtilities.isEventDispatchThread()) {
                    SwingUtilities.invokeLater(new Runnable() {                    
                        @Override
                        public void run() {
                            constraint.elementAdded(TableAM.this, element);
                        }
                    });
                } else {
                    constraint.elementAdded(this, element);
                }
            }
        }
    }

    private void registerNewElement(Element element) {
        elementIdMap.put(element.getUniqueId(), element);
        newElements.add(element);
    }

    private void fireElementDeleted(final Element element) {
        if (columnIds != null) {
            handleKey(element.getUniqueId(), null);
            // uniqueConstraint.elementRemoved(this, element);
        }

        ElementLifecycleListener[] listeners = listenerList.getListeners(ElementLifecycleListener.class);
        if (listeners != null) {
            for (final ElementLifecycleListener constraint : listeners) {
                if(!SwingUtilities.isEventDispatchThread()) {
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

    private void fireTableCleared() {
        if (columnIds != null) {
            uniqueMap.clear();
            keyMap.clear();
            uniqueDeleteMap.clear();
            keyDeleteMap.clear();
        }

        ElementLifecycleListener[] listeners = listenerList.getListeners(ElementLifecycleListener.class);
        if (listeners != null) {
            for (final ElementLifecycleListener constraint : listeners) {
                if(!SwingUtilities.isEventDispatchThread()) {
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

    private void fireElementStatusChanged(final Element element) {
        if (columnIds != null) {
            // uniqueConstraint.elementStateChanged(this, element);
        }

        ElementLifecycleListener[] listeners = listenerList.getListeners(ElementLifecycleListener.class);
        if (listeners != null) {
            for (final ElementLifecycleListener constraint : listeners) {
                if(!SwingUtilities.isEventDispatchThread()) {
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

    public Element deleteElementOfObject(Object object) {
        if(columnIds != null) {
            Element tempElement = createElement(object, false, false, true);
            List<?> key = buildKey(tempElement);
            Set<String> idSet = uniqueMap.get(key);
            if(idSet != null) {
                if(idSet.size() == 1) {
                    return getElementById(idSet.iterator().next());
                }
                for(String id : idSet) {
                    Element element = getElementById(id);
                    if(element.getCurrentValue().equals(object)) {
                        return element;
                    }
                }
            }
        } else {
            if(elements != null) {
                for(Element element : elements) {
                    if(element.getCurrentValue().equals(object)) {
                        return element;
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
    public void deactivateMassEditModeAndUpdate(){
        this.massEditMode = false;
        fireUpdateViews();
        fireDataChanged();
    }
    
}
