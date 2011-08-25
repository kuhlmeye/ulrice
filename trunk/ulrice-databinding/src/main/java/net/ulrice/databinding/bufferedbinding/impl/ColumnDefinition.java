package net.ulrice.databinding.bufferedbinding.impl;

import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.modelaccess.IFDynamicModelValueAccessor;
import net.ulrice.databinding.validation.IFValidator;

/**
 * @author christof
 * 
 */
public class ColumnDefinition<T extends Object> {

    private String id;
    private IFDynamicModelValueAccessor dataAccessor;
    private Class<T> columnClass;
    private FilterMode filterMode;
    private IFValueConverter valueConverter;
    private IFValidator validator;
    private boolean readOnly = false;
    private String columnName;
	private boolean useAutoValueConverter = true;

	   
    public ColumnDefinition(IFDynamicModelValueAccessor dataAccessor, Class<T> columnClass) {
        this.id = dataAccessor.getAttributeId();
        this.columnName = id;
        this.dataAccessor = dataAccessor;
        this.columnClass = columnClass;
        
        this.filterMode = FilterMode.RegEx;
        if(Number.class.isAssignableFrom(columnClass)) {
            this.filterMode = FilterMode.Numeric;
        }
    }
	
    public ColumnDefinition(String columnName, IFDynamicModelValueAccessor dataAccessor, Class<T> columnClass) {
        this.id = dataAccessor.getAttributeId();
        this.columnName = columnName;
        this.dataAccessor = dataAccessor;
        this.columnClass = columnClass;
        
        this.filterMode = FilterMode.RegEx;
        if(Number.class.isAssignableFrom(columnClass)) {
            this.filterMode = FilterMode.Numeric;
        }
    }
    
    public ColumnDefinition(IFDynamicModelValueAccessor dataAccessor, Class<T> columnClass, FilterMode filterMode) {
        this.id = dataAccessor.getAttributeId();
        this.columnName = id;
        this.dataAccessor = dataAccessor;
        this.columnClass = columnClass;
        this.filterMode = filterMode;
    }

    
    public ColumnDefinition(String columnName, IFDynamicModelValueAccessor dataAccessor, Class<T> columnClass, FilterMode filterMode) {
        this.id = dataAccessor.getAttributeId();
        this.columnName = columnName;
        this.dataAccessor = dataAccessor;
        this.columnClass = columnClass;
        this.filterMode = filterMode;
    }

    public GenericAM<T> createAM() {
        GenericAM<T> genericAM = new GenericAM<T>(id);
        genericAM.setReadOnly(isReadOnly());        
        genericAM.setValueConverter(getValueConverter());
        if(getValidator() != null) {
            genericAM.addValidator(getValidator());
        }
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
     * @return the dataAccessor
     */
    public IFDynamicModelValueAccessor getDataAccessor() {
        return dataAccessor;
    }

    /**
     * @param dataAccessor the dataAccessor to set
     */
    public void setDataAccessor(IFDynamicModelValueAccessor dataAccessor) {
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
	
    public boolean isUseAutoValueConverter() {
    	return useAutoValueConverter;
    }
        
    public void setUseAutoValueConverter(boolean useAutoValueConverter) {
		this.useAutoValueConverter = useAutoValueConverter;
	}
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * @param columnIndex
     * @return
     */
    public String getColumnName() {
        return columnName;
    }
}
