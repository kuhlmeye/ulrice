package net.ulrice.simpledatabinding;

import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.modelaccess.IFIndexedModelValueAccessor;
import net.ulrice.databinding.modelaccess.IndexedPredicate;
import net.ulrice.databinding.modelaccess.IFModelValueAccessor;
import net.ulrice.simpledatabinding.viewaccess.IndexedViewAdapter;


class IndexedBinding {
    private final IFModelValueAccessor _numEntriesAccessor;
    
    private final IndexedViewAdapter _viewAdapter;
    private final IFValueConverter _converter;
    private final IndexedPredicate _enabledPredicate;
    private final IFIndexedModelValueAccessor _modelValueAccessor;
//TODO    private final List<Validator> _validators;
    
    private final boolean _isReadOnly;

    public IndexedBinding (IFModelValueAccessor numEntriesAccessor, IndexedViewAdapter viewAdapter, IFValueConverter converter, IndexedPredicate enabledPredicate, IFIndexedModelValueAccessor modelValueAccessor, boolean isReadOnly) {
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
    
    public IFValueConverter getConverter () {
        return _converter;
    }
    
    public boolean isWidgetEnabled (boolean isValid, int index, Object model) {
        return _enabledPredicate.getValue (isValid, index, model);
    }
    
    public IFIndexedModelValueAccessor getModelValueAccessor () {
        return _modelValueAccessor;
    }
    
    public boolean isReadOnly () {
        return _isReadOnly;
    }
    
    public boolean hasDataBinding () {
        return _modelValueAccessor != null;
    }

}
