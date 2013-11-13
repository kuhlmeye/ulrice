package net.ulrice.databinding.directbinding;

import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.directbinding.table.ColumnAdapter;
import net.ulrice.databinding.modelaccess.IFIndexedModelValueAccessor;
import net.ulrice.databinding.modelaccess.IndexedPredicate;


class IndexedBinding {

    private final ColumnAdapter viewAdapter;
    private final IFValueConverter converter;
    private final IndexedPredicate enabledPredicate;
    private final IFIndexedModelValueAccessor modelValueAccessor;
    //TODO private final List<Validator> _validators;
    
    private final boolean isReadOnly;

    public IndexedBinding (ColumnAdapter viewAdapter, IFValueConverter converter, IndexedPredicate enabledPredicate, IFIndexedModelValueAccessor modelValueAccessor, boolean isReadOnly) {
        this.viewAdapter = viewAdapter;
        this.converter = converter;
        this.enabledPredicate = enabledPredicate;
        this.modelValueAccessor = modelValueAccessor;
        this.isReadOnly = isReadOnly;
    }

    
    public ColumnAdapter getViewAdapter () {
        return viewAdapter;
    }
    
    public IFValueConverter getConverter () {
        return converter;
    }
    
    public boolean isWidgetEnabled (boolean isValid, int index, Object model) {
        return enabledPredicate.getValue (isValid, index, model);
    }
    
    public IFIndexedModelValueAccessor getModelValueAccessor () {
        return modelValueAccessor;
    }
    
    public boolean isReadOnly () {
        return isReadOnly;
    }
    
    public boolean hasDataBinding () {
        return modelValueAccessor != null;
    }

}
