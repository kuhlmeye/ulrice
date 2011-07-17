package net.ulrice.databinding.bufferedbinding;

import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.modelaccess.IFDynDataAccessor;
import net.ulrice.databinding.validation.IFValidator;

/**
 * @author christof
 * 
 */
public class ColumnDefinition<T extends Object> {

    private String id;
    private IFDynDataAccessor dataAccessor;
    private Class<T> columnClass;
    private FilterMode filterMode;
    private IFValueConverter valueConverter;
    private IFValidator validator;
    private boolean readOnly = false;

    public ColumnDefinition(String id, IFDynDataAccessor dataAccessor, Class<T> columnClass) {
        this.id = id;
        this.dataAccessor = dataAccessor;
        this.columnClass = columnClass;
        
        this.filterMode = FilterMode.RegEx;
        if(Number.class.isAssignableFrom(columnClass)) {
            this.filterMode = FilterMode.Numeric;
        }
    }
    
    public ColumnDefinition(String id, IFDynDataAccessor dataAccessor, Class<T> columnClass, FilterMode filterMode) {
        this.id = id;
        this.dataAccessor = dataAccessor;
        this.columnClass = columnClass;
        this.filterMode = filterMode;
    }

    public GenericAM<T> createAM() {
        GenericAM<T> genericAM = new GenericAM<T>(id);
        genericAM.setReadOnly(isReadOnly());        
        genericAM.setValueConverter(getValueConverter());
        genericAM.setValidator(getValidator());
		return genericAM;
    }

    /**
     * @param columnIndex
     * @return
     */
    public Class<?> getColumnClass() {
        return columnClass;
    }

    /**
     * @param columnIndex
     * @return
     */
    public String getColumnName() {
        // TODO Auto-generated method stub
        return id;
    }


    /**
     * @return the dataAccessor
     */
    public IFDynDataAccessor getDataAccessor() {
        return dataAccessor;
    }

    /**
     * @param dataAccessor the dataAccessor to set
     */
    public void setDataAccessor(IFDynDataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
    }

    /**
     * @return the id
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
    public String toString() {
        return getColumnName();
    }

    /**
     * @return the filterMode
     */
    public FilterMode getFilterMode() {
        return filterMode;
    }

	public IFValueConverter getValueConverter() {
		return valueConverter;
	}

	public void setValueConverter(IFValueConverter valueConverter) {
		this.valueConverter = valueConverter;
	}

	public boolean isReadOnly() {
		return readOnly;
	}
	
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public IFValidator getValidator() {
		return validator;
	}
	
	public void setValidator(IFValidator validator) {
		this.validator = validator;
	}
}
