package net.ulrice.databinding.bufferedbinding.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.border.Border;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.modelaccess.IFDynamicModelValueAccessor;
import net.ulrice.databinding.validation.IFValidator;
import net.ulrice.databinding.viewadapter.utable.UTableRenderer;

/**
 * Defines a column in the table am.
 *
 * @author christof
 */
public class ColumnDefinition<T extends Object> implements PropertyChangeListener {

    private String id;
    private IFDynamicModelValueAccessor dataAccessor;
    private Class<T> columnClass;
    private FilterMode filterMode;
    private IFValueConverter valueConverter;
    private List<IFValidator> validators = new ArrayList<IFValidator>();
    private IFAttributeInfo attributeInfo;
    private String columnName;
    private String columnTooltip;
	private boolean useAutoValueConverter = true;
	private boolean useValueRange = false;
	private List<T> valueRange;
	private Comparator<T> comparator;
	private boolean fixedColumn;

	private TableCellEditor cellEditor;
	private TableCellRenderer cellRenderer;

	private EventListenerList eventListeners = new EventListenerList();

	private ColumnColorOverride columnColorOverride;

	private ColumnType columnType = ColumnType.Editable;

	private Integer preferredWidth;

	private Border border;

	private List<UTableRenderer> preRendererList;
	private List<UTableRenderer> postRendererList;

    private boolean isListOrderRelevant = false;
    private boolean useListAM = false;
    
    private String preFilledFilterValue;

	public enum ColumnType {
		Editable,
		ReadOnly,
		NewEditable,
		Hidden
	}



    public ColumnDefinition(IFDynamicModelValueAccessor dataAccessor, Class<T> columnClass) {
        this.id = dataAccessor.getAttributeId();
        this.columnName = id;
        this.dataAccessor = dataAccessor;
        this.columnClass = columnClass;

        setFilterMode(columnClass);
    }

    public ColumnDefinition(IFDynamicModelValueAccessor dataAccessor, Class<T> columnClass, ColumnType columnType) {
        this.id = dataAccessor.getAttributeId();
        this.columnName = id;
        this.dataAccessor = dataAccessor;
        this.columnClass = columnClass;
        this.columnType = columnType;

        setFilterMode(columnClass);
    }

    public ColumnDefinition(String columnName, IFDynamicModelValueAccessor dataAccessor, Class<T> columnClass) {
        this.id = dataAccessor != null ? dataAccessor.getAttributeId() : columnName;
        this.columnName = columnName;
        this.dataAccessor = dataAccessor;
        this.columnClass = columnClass;

        setFilterMode(columnClass);
    }

    public ColumnDefinition(IFDynamicModelValueAccessor dataAccessor, Class<T> columnClass, FilterMode filterMode) {
        this(dataAccessor.getAttributeId(), dataAccessor, columnClass, filterMode);
    }


    public ColumnDefinition(String columnName, IFDynamicModelValueAccessor dataAccessor, Class<T> columnClass, FilterMode filterMode) {
        this(columnName, dataAccessor, columnClass, filterMode, ColumnType.Editable);
    }

    public ColumnDefinition(String columnName, IFDynamicModelValueAccessor dataAccessor, Class<T> columnClass, FilterMode filterMode, ColumnType columnType) {
        this.id = dataAccessor.getAttributeId();
        this.columnName = columnName;
        this.dataAccessor = dataAccessor;
        this.columnClass = columnClass;
        this.filterMode = filterMode;
        this.columnType = columnType;
    }

    /**
     * Create the generic attribute model for this column
     */
    public LightGenericAM<T> createLightAM() {
    	LightGenericAM<T> genericAM = new LightGenericAM<T>(id, getColumnType().equals(ColumnType.ReadOnly));
        if(getValidators() != null && !getValidators().isEmpty()) {
            for (IFValidator validator : getValidators()) {
                genericAM.addValidator(validator);
            }
        }
		return genericAM;
    }
    
    /**
     * Create the generic attribute model for this column
     */
    public IFElementInternalAM<List<T>> createListAM() {

    	ListAM<T> genericAM = new ListAM(id, attributeInfo, getColumnType().equals(ColumnType.ReadOnly), isListOrderRelevant);

        genericAM.setValueConverter(getValueConverter());

        if(getValidators() != null && !getValidators().isEmpty()) {
            for (IFValidator validator : getValidators()) {
                genericAM.addValidator(validator);
            }
        }
		return genericAM;
    }

    private ColumnDefinition<T> setFilterMode(Class<T> columnClass) {
        if (Number.class.isAssignableFrom(columnClass)) {
            setFilterMode(FilterMode.Numeric);
        }
        else if ((Boolean.class == columnClass) || (Boolean.TYPE == columnClass)) {
            setFilterMode(FilterMode.Boolean);
        }
        else if (Enum.class.isAssignableFrom(columnClass)) {
            setFilterMode(FilterMode.ComboBox);
        }
        else {
            setFilterMode(FilterMode.RegEx);
        }
        return this;
    }

    /**
     * Sets the default filter mode for this column..
     */
    public ColumnDefinition<T> setFilterMode(FilterMode filterMode) {
        this.filterMode = filterMode;
        fireFilterModeChanged();
        return this;
    }

    /**
     * Disables the filter for this column
     *
     * @return the column definition itself
     */
    public ColumnDefinition<T> noFilter() {
        return setFilterMode(FilterMode.NoFilter);
    }
    
    /**
     * Returns the class of the data in this column
     */
    public Class<?> getColumnClass() {
        return columnClass;
    }

    /**
     * Returns the data accessor used to get the data from the model.
     */
    public IFDynamicModelValueAccessor getDataAccessor() {
        return dataAccessor;
    }

    /**
     * Set the data accessor to get the from the model.
     */
    public void setDataAccessor(IFDynamicModelValueAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
    }

    /**
     * Returns the id.
     */
    public String getId() {
        return id;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return getId().equals(((ColumnDefinition<?>) obj).getId());
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getColumnName();
    }

    /**
     * @return the filterMode
     */
    public FilterMode getFilterMode() {
        return filterMode;
    }

    /**
     * Returns the default value converter used by this column definition
     */
	public IFValueConverter getValueConverter() {
		return valueConverter;
	}

	/**
	 * Sets the value converter used by this column
	 */
	public void setValueConverter(IFValueConverter valueConverter) {
		this.valueConverter = valueConverter;
	}

	/**
	 * Returns the list of standard validators that should be used in this column
	 */
	public List<IFValidator> getValidators() {
		return validators;
	}

	/**
	 * Add a validator to the list of validators for this column
	 */
	public ColumnDefinition<T> addValidator(IFValidator validator) {
		validators.add(validator);
		return this;
	}

	/**
	 * Returns true, if an auto value converter should be used. This converter tries
	 * to automatically convert objects, if model and column class are different.
	 */
    public boolean isUseAutoValueConverter() {
    	return useAutoValueConverter;
    }

    /**
     * Set to true, if auto value converter should be used.
     */
    public ColumnDefinition<T> setUseAutoValueConverter(boolean useAutoValueConverter) {
		this.useAutoValueConverter = useAutoValueConverter;
		return this;
	}

    /**
     * Sets the name of the column.
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * Sets the type of the column
     */
    public ColumnDefinition<T> setColumnType(ColumnType columnType) {
		this.columnType = columnType;
		return this;
	}

    /**
     * Returns the type of this column
     */
    public ColumnType getColumnType() {
		return columnType;
	}

    /**
     * Returns the name of this column
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Returns true, if a value range is defined for this column. Definition of a value range
     * will result in a combobox in the table cell editor.
     */
    public boolean isUseValueRange() {
        return useValueRange;
    }

    /**
     * Set to true, if a value range should be used in this column
     */
    public ColumnDefinition<T> setUseValueRange(boolean useValueRange) {
        this.useValueRange = useValueRange;
        return this;
    }

    /**
     * Returns the list of valid value for this column
     */
    public List<T> getValueRange() {
        return valueRange;
    }

    /**
     * Set the list of valid values for this column
     */
    public ColumnDefinition<T> setValueRange(List<T> valueRange) {
        this.valueRange = valueRange;
        fireValueRangeChanged();
        return this;
    }

    private void fireValueRangeChanged() {
        ColumnDefinitionChangedListener[] listeners = eventListeners.getListeners(ColumnDefinitionChangedListener.class);
        if(listeners != null) {
            for(ColumnDefinitionChangedListener listener : listeners) {
                listener.valueRangeChanged(this);
            }
        }
    }

    private void fireFilterModeChanged() {
        ColumnDefinitionChangedListener[] listeners = eventListeners.getListeners(ColumnDefinitionChangedListener.class);
        if(listeners != null) {
            for(ColumnDefinitionChangedListener listener : listeners) {
                listener.filterModeChanged(this);
            }
        }
    }

    /**
     * Register a change listener to this column
     */
    public void addChangeListener(ColumnDefinitionChangedListener listener) {
        eventListeners.add(ColumnDefinitionChangedListener.class, listener);
    }

    /**
     * Remove a registered change listener from this columnn
     */
    public void removeChangeListener(ColumnDefinitionChangedListener listener) {
        eventListeners.remove(ColumnDefinitionChangedListener.class, listener);
    }

    /**
     * The default table cell editor that should be used in this column
     */
    public TableCellEditor getCellEditor() {
        return cellEditor;
    }

    /**
     * Set the default table cell editor that should be used by this column
     */
    public ColumnDefinition<T> setCellEditor(TableCellEditor cellEditor) {
        this.cellEditor = cellEditor;
        return this;
    }

    /**
     * Returns the default table cell renderer that should be used by this column
     */
    public TableCellRenderer getCellRenderer() {
        return cellRenderer;
    }

    /**
     * Sets the default cell renderer that should be used by this column
     */
    public ColumnDefinition<T> setCellRenderer(TableCellRenderer cellRenderer) {
        this.cellRenderer = cellRenderer;
        return this;
    }

    /**
     * Sets the attribute info of this column
     */
    public ColumnDefinition<T> setAttributeInfo(IFAttributeInfo attributeInfo) {
        this.attributeInfo = attributeInfo;
        return this;
    }

    /**
     * Returns the attribute info of this column
     */
    public IFAttributeInfo getAttributeInfo() {
        return attributeInfo;
    }

    /**
     * Sets the default header tooltip
     */
    public ColumnDefinition<T> setColumnTooltip(String columnTooltip) {
        this.columnTooltip = columnTooltip;
        return this;
    }

    /**
     * Returns the header tooltip
     */
    public String getColumnTooltip() {
        return columnTooltip;
    }

    /**
     * Returns the comparator of this column that should be used by the row sorter
     */
    public Comparator<T> getComparator() {
        return comparator;
    }

    /**
     * Sets the comparator of this column that is used by the row sorter
     */
    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    /**
     * Returns true, if this is a fixed column
     */
    public boolean isFixedColumn() {
        return fixedColumn;
    }

    /**
     * Set to true, if this is a fixed column
     */
    public void setFixedColumn(boolean fixedColumn) {
        this.fixedColumn = fixedColumn;
    }

    public ColumnColorOverride getColumnColorOverride() {
        return columnColorOverride;
    }

    public ColumnDefinition<T> setColumnColorOverride(ColumnColorOverride columnColorOverride) {
        this.columnColorOverride = columnColorOverride;
        return this;

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if("preferredWidth".equals(evt.getPropertyName())){
            Integer newValue = (Integer) evt.getNewValue();
            if(preferredWidth == null || preferredWidth.intValue() != newValue.intValue()){
                preferredWidth = newValue;
            }
        }
    }

    /**
     * Returns the preferred width of this column. This value is used by the column reiszer
     */
    public Integer getPreferredWidth() {
        return preferredWidth;
    }

    /**
     * Sets the preferreed width of this column.
     */
    public ColumnDefinition<T> setPreferredWidth(Integer preferredWidth) {
        this.preferredWidth = preferredWidth;
        return this;
    }

    public Border getBorder() {
        return border;
    }

    public ColumnDefinition<T> setBorder(Border border) {
        this.border = border;
        return this;
    }

    public ColumnDefinition<T> addPostRenderer(UTableRenderer renderer){
        if(postRendererList == null){
            postRendererList = new ArrayList<UTableRenderer>();
        }
        postRendererList.add(renderer);
        return this;

    }
    public ColumnDefinition<T> addPreRenderer(UTableRenderer renderer){
        if(preRendererList == null){
            preRendererList = new ArrayList<UTableRenderer>();
        }
        preRendererList.add(renderer);
        return this;

    }

    public List<UTableRenderer> getPostRendererList() {
        return postRendererList;
    }

    public List<UTableRenderer> getPreRendererList() {
        return preRendererList;
    }
   

    public String getPreFilledFilterValue() {
        return preFilledFilterValue;
    }

    public ColumnDefinition<T> setPreFilledFilterValue(String preFilledFilterValue) {
        this.preFilledFilterValue = preFilledFilterValue;
        return this;
    }

    /**
     * @return the isListOrderRelevant
     */
    public boolean isListOrderRelevant() {
        return isListOrderRelevant;
    }

    /**
     * @param isListOrderRelevant the isListOrderRelevant to set
     */
    public ColumnDefinition<T> setListOrderRelevant(boolean isListOrderRelevant) {
        this.isListOrderRelevant = isListOrderRelevant;
        return this;
    }

    /**
     * @return the useListAM
     */
    public boolean isUseListAM() {
        return useListAM;
    }

    /**
     * @param useListAM the useListAM to set
     */
    public ColumnDefinition<T> setUseListAM(boolean useListAM) {
        this.useListAM = useListAM;
        return this;
    }

    @SuppressWarnings("rawtypes")
    public ColumnDefinition<T> removeValidators(Class< ? extends IFValidator>... validators) {
        if(getValidators() != null && validators != null && validators.length > 0){
            for (Iterator<IFValidator> iterator = getValidators().iterator(); iterator.hasNext();) {
                IFValidator validator = iterator.next();
                if(validator != null){
                    for(Class<? extends IFValidator> type : validators){
                        if(type.isAssignableFrom(validator.getClass())){
                            iterator.remove();
                        }
                    }
                }
            }
        }
        return this;
    }
}
