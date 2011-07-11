package net.ulrice.simpledatabinding;

import java.util.List;

import net.ulrice.databinding.IFBindingIdentifier;
import net.ulrice.databinding.IFValidator;
import net.ulrice.simpledatabinding.converter.ValueConverter;
import net.ulrice.simpledatabinding.modelaccess.ModelValueAccessor;
import net.ulrice.simpledatabinding.modelaccess.Predicate;
import net.ulrice.simpledatabinding.viewaccess.ViewAdapter;




class Binding implements IFBindingIdentifier {
    private final ViewAdapter _viewAdapter;
    private final ValueConverter _converter;
    private final Predicate _enabledPredicate;
    private final ModelValueAccessor _modelValueAccessor;
    private final List<IFValidator<?>> _validators;
    
    private final boolean _isReadOnly;

    public Binding (ViewAdapter viewAdapter, ValueConverter converter, Predicate enabledPredicate, ModelValueAccessor modelValueAccessor, List<IFValidator<?>> validators, boolean isReadOnly) {
        _viewAdapter = viewAdapter;
        _converter = converter;
        _enabledPredicate = enabledPredicate;
        _modelValueAccessor = modelValueAccessor;
        _validators = validators;
        _isReadOnly = isReadOnly;
    }

    public ViewAdapter getViewAdapter () {
        return _viewAdapter;
    }
    
    public ValueConverter getConverter () {
        return _converter;
    }
    
    public boolean isWidgetEnabled (boolean isValid, Object model) {
        return _enabledPredicate.getValue (isValid, model);
    }
    
    public ModelValueAccessor getModelValueAccessor () {
        return _modelValueAccessor;
    }
    
    public List<IFValidator<?>> getValidators () {
        return _validators;
    }
    
    public boolean isReadOnly () {
        return _isReadOnly;
    }
    
    public boolean hasDataBinding () {
        return _modelValueAccessor != null;
    }

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}
}
