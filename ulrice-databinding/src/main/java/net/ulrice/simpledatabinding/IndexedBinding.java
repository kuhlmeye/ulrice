package net.ulrice.simpledatabinding;

import net.ulrice.databinding.modelaccess.IndexedModelValueAccessor;
import net.ulrice.databinding.modelaccess.IndexedPredicate;
import net.ulrice.databinding.modelaccess.IFModelValueAccessor;
import net.ulrice.simpledatabinding.converter.ValueConverter;
import net.ulrice.simpledatabinding.viewaccess.IndexedViewAdapter;


class IndexedBinding {
    private final IFModelValueAccessor _numEntriesAccessor;
    
    private final IndexedViewAdapter _viewAdapter;
    private final ValueConverter _converter;
    private final IndexedPredicate _enabledPredicate;
    private final IndexedModelValueAccessor _modelValueAccessor;
//TODO    private final List<Validator> _validators;
    
    private final boolean _isReadOnly;

    public IndexedBinding (IFModelValueAccessor numEntriesAccessor, IndexedViewAdapter viewAdapter, ValueConverter converter, IndexedPredicate enabledPredicate, IndexedModelValueAccessor modelValueAccessor, boolean isReadOnly) {
        _numEntriesAccessor = numEntriesAccessor;
        _viewAdapter = viewAdapter;
        _converter = converter;
        _enabledPredicate = enabledPredicate;
        _modelValueAccessor = modelValueAccessor;
        _isReadOnly = isReadOnly;
    }

    public IFModelValueAccessor getNumEntriesAccessor () {
        return _numEntriesAccessor;
    }
    
    public IndexedViewAdapter getViewAdapter () {
        return _viewAdapter;
    }
    
    public ValueConverter getConverter () {
        return _converter;
    }
    
    public boolean isWidgetEnabled (boolean isValid, int index, Object model) {
        return _enabledPredicate.getValue (isValid, index, model);
    }
    
    public IndexedModelValueAccessor getModelValueAccessor () {
        return _modelValueAccessor;
    }
    
    public boolean isReadOnly () {
        return _isReadOnly;
    }
    
    public boolean hasDataBinding () {
        return _modelValueAccessor != null;
    }

}
