package net.ulrice.databinding.impl.am;

import net.ulrice.databinding.IFAttributeModel;
import net.ulrice.databinding.modelaccess.IFDynDataAccessor;

/**
 * @author christof
 * 
 */
public class ColumnDefinition<T extends Object> {

    private String id;
    private IFDynDataAccessor<T> dataAccessor;
    private Class<T> columnClass;
    private FilterMode filterMode;

    public ColumnDefinition(String id, IFDynDataAccessor<T> dataAccessor, Class<T> columnClass) {
        this.id = id;
        this.dataAccessor = dataAccessor;
        this.columnClass = columnClass;
        
        this.filterMode = FilterMode.RegEx;
        if(Number.class.isAssignableFrom(columnClass)) {
            this.filterMode = FilterMode.Numeric;
        }
    }
    
    public ColumnDefinition(String id, IFDynDataAccessor<T> dataAccessor, Class<T> columnClass, FilterMode filterMode) {
        this.id = id;
        this.dataAccessor = dataAccessor;
        this.columnClass = columnClass;
        this.filterMode = filterMode;
    }

    public IFAttributeModel<T> createAM() {
        return new GenericAM<T>(id);
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
     * @return
     */
    public boolean isEditable() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @return the dataAccessor
     */
    public IFDynDataAccessor<T> getDataAccessor() {
        return dataAccessor;
    }

    /**
     * @param dataAccessor the dataAccessor to set
     */
    public void setDataAccessor(IFDynDataAccessor<T> dataAccessor) {
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

}
