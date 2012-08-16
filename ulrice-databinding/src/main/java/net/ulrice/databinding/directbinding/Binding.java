package net.ulrice.databinding.directbinding;

import java.util.List;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.modelaccess.IFModelValueAccessor;
import net.ulrice.databinding.modelaccess.Predicate;
import net.ulrice.databinding.validation.IFValidator;
import net.ulrice.databinding.viewadapter.IFViewAdapter;


/**
 * Direct binding class.
 * 
 * @author DL10KUH
 */
public class Binding implements IFBinding {
    private final IFViewAdapter viewAdapter;
    private final IFValueConverter converter;
    private final Predicate enabledPredicate;
    private final IFModelValueAccessor modelValueAccessor;
    private final List<IFValidator<?>> validators;
    
    private final boolean isReadOnly;
    
    
	private Object originalValue;
	private List<String> validationFailures;
	private boolean dirty;
	private boolean valid;

    public Binding (IFViewAdapter viewAdapter, IFValueConverter converter, Predicate enabledPredicate, IFModelValueAccessor modelValueAccessor, List<IFValidator<?>> validators, boolean isReadOnly) {
        this.viewAdapter = viewAdapter;
        this.converter = converter;
        this.enabledPredicate = enabledPredicate;
        this.modelValueAccessor = modelValueAccessor;
        this.validators = validators;
        this.isReadOnly = isReadOnly;
        setValid(true);
        setDirty(false);

        if(modelValueAccessor != null) {
        	this.originalValue = modelValueAccessor.getValue();
        }
    }

    public IFViewAdapter getViewAdapter () {
        return viewAdapter;
    }
    
    public IFValueConverter getConverter () {
        return converter;
    }
    
    public boolean isWidgetEnabled (boolean isValid, Object model) {
        return enabledPredicate.getValue (isValid, model);
    }
    
    public IFModelValueAccessor getModelValueAccessor () {
        return modelValueAccessor;
    }
    
    public List<IFValidator<?>> getValidators () {
        return validators;
    }
    
    public boolean isReadOnly () {
        return isReadOnly;
    }
    
    public boolean hasDataBinding () {
        return modelValueAccessor != null;
    }

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
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
			return getConverter().modelToView(getModelValueAccessor().getValue(), viewAdapter.getAttributeInfo());
		}
		return null;
	}
	
	protected void setCurrentValue(Object currentValue) {
		if(getModelValueAccessor() != null ){
			getModelValueAccessor().setValue(getConverter().viewToModel(currentValue, viewAdapter.getAttributeInfo()));
		}
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
}
