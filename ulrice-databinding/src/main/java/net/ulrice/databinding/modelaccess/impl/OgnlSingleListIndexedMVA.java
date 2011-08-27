package net.ulrice.databinding.modelaccess.impl;

import java.util.List;

import net.ulrice.databinding.ErrorHandler;
import net.ulrice.databinding.modelaccess.IFIndexedModelValueAccessor;
import net.ulrice.databinding.modelaccess.IFModelValueAccessor;
import ognl.Ognl;
import ognl.OgnlException;


public class OgnlSingleListIndexedMVA implements IFIndexedModelValueAccessor {
    private final Class<?> _type;
    private final boolean _isReadOnly;

    private final Object _model;

    private final Object _ognlBaseTree;
    private final Object _ognlElementTree;
    private final IFModelValueAccessor _sizeMVA;
    private String id;

    public OgnlSingleListIndexedMVA (Class<?> type, Boolean isReadOnly, Object model, String baseExpression, String elementExpression, IFModelValueAccessor sizeMVA) {
        _type = type;
        _model = model;
        _sizeMVA = sizeMVA;
        try {
            _ognlBaseTree = Ognl.parseExpression (baseExpression);
            _ognlElementTree = Ognl.parseExpression (elementExpression);
        } catch (OgnlException exc) {
            ErrorHandler.handle (exc);
            throw new RuntimeException (); // für den Compiler
        }
        _isReadOnly = isReadOnly != null ? isReadOnly : guessReadOnly ();
        id = _model.getClass().getName() + "." + baseExpression + "." + elementExpression;
    }

    private boolean guessReadOnly () {
        try {
            Object o = null;
            try {
                o = getValue (0);
            }
            catch (Exception exc) {
                //TODO logging, dass das "Raten" nicht funktioniert hat
                return true;
            }
            
            setValue (0, o);
            return false;
        }
        catch (Exception exc) {
            return true;
        }
    }
    
    public Class<?> getModelType () {
        return _type;
    }

    public boolean isReadOnly () {
        return _isReadOnly;
    }

    public Object getValue (int index) {
        try {
            final List<?> list = (List<?>) Ognl.getValue (_ognlBaseTree, _model);
            final Object baseValue = list.get (index);

            return Ognl.getValue (_ognlElementTree, baseValue);
        }
        catch (OgnlException exc) {
            ErrorHandler.handle (exc);
            return null; // für den Compiler
        }
    }

    public void setValue (int index, Object value) {
        try {
            final List<?> list = (List<?>) Ognl.getValue (_ognlBaseTree, _model);
            final Object baseValue = list.get (index);
            Ognl.setValue (_ognlElementTree, baseValue, value);
        }
        catch (OgnlException exc) {
            ErrorHandler.handle (exc);
        }
    }
    
    @Override
    public String getAttributeId() {
    	return id;
    }
    
    @Override
    public int getSize() {
    	return (Integer)_sizeMVA.getValue();
    }

	@Override
	public Object cloneObject(Object obj) {
		return UlriceReflectionUtils.cloneObject(obj);
	}

	@Override
	public Object newObjectInstance() {
		try {
			return getModelType().newInstance();
		} catch (InstantiationException e) {
			ErrorHandler.handle(e);
		} catch (IllegalAccessException e) {
			ErrorHandler.handle(e);
		}
		return null;
	}
}
