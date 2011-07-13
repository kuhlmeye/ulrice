package net.ulrice.simpledatabinding;

import java.util.List;

import net.ulrice.databinding.DataState;
import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.modelaccess.IFModelValueAccessor;
import net.ulrice.databinding.modelaccess.Predicate;
import net.ulrice.databinding.validation.IFValidator;
import net.ulrice.databinding.validation.ValidationResult;
import net.ulrice.databinding.viewadapter.IFViewAdapter;




public class Binding implements IFBinding {
    private final IFViewAdapter _viewAdapter;
    private final IFValueConverter _converter;
    private final Predicate _enabledPredicate;
    private final IFModelValueAccessor _modelValueAccessor;
    private final List<IFValidator<?>> _validators;
    
    private final boolean _isReadOnly;
    
    
	private Object originalValue;
	private List<String> validationFailures;
	private DataState state;

    public Binding (IFViewAdapter viewAdapter, IFValueConverter converter, Predicate enabledPredicate, IFModelValueAccessor modelValueAccessor, List<IFValidator<?>> validators, boolean isReadOnly) {
        _viewAdapter = viewAdapter;
        _converter = converter;
        _enabledPredicate = enabledPredicate;
        _modelValueAccessor = modelValueAccessor;
        _validators = validators;
        _isReadOnly = isReadOnly;
        if(modelValueAccessor != null) {
	        state = DataState.NotChanged;
	        originalValue = modelValueAccessor.getValue();
        } else {
        	state = DataState.NotInitialized;
        }
       
        
    }

    public IFViewAdapter getViewAdapter () {
        return _viewAdapter;
    }
    
    public IFValueConverter getConverter () {
        return _converter;
    }
    
    public boolean isWidgetEnabled (boolean isValid, Object model) {
        return _enabledPredicate.getValue (isValid, model);
    }
    
    public IFModelValueAccessor getModelValueAccessor () {
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

	@Override
	public DataState getState() {
		return state;
	}

	public void setState(DataState state) {
		this.state = state;
	}

	@Override
	public List<String> getValidationFailures() {
		return validationFailures;
	}
	
	protected void setValidationFailures(List<String> validationFailures) {
		this.validationFailures = validationFailures;
	}

	@Override
	public Object getOriginalValue() {
		return originalValue;
	}
	
	protected void setOriginalValue(Object originalValue) {
		this.originalValue = originalValue;
	}

	@Override
	public Object getCurrentValue() {
		if(getModelValueAccessor() != null ){
			return getConverter().modelToView(getModelValueAccessor().getValue());
		}
		return null;
	}
	
	protected void setCurrentValue(Object currentValue) {
		if(getModelValueAccessor() != null ){
			getModelValueAccessor().setValue(getConverter().viewToModel(currentValue));
		}
	}

}
