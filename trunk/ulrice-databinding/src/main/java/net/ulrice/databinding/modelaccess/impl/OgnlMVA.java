package net.ulrice.databinding.modelaccess.impl;

import net.ulrice.databinding.ErrorHandler;
import net.ulrice.databinding.modelaccess.IFModelValueAccessor;
import ognl.Ognl;
import ognl.OgnlException;


public class OgnlMVA implements IFModelValueAccessor {
    private final Object _model;
    private final Object _ognlTree;
    private final Class<?> _type;
    private final boolean _readOnly;
	private String id;
    
    //TODO Typ und readOnly ermitteln
    
    public OgnlMVA (Object model, String ognlExpression, Class<?> type) {
        _model = model;
        _ognlTree = ognlParse (ognlExpression);
        _type = type;
        _readOnly = guessReadOnly ();
        id = _model.getClass().getName() + "." + _ognlTree;
    }
    
    public OgnlMVA (Object model, String ognlExpression, Class<?> type, boolean readOnly) {
        _model = model;
        _ognlTree = ognlParse (ognlExpression);
        _type = type;
        _readOnly = readOnly;
        id = _model.getClass().getName() + "." + _ognlTree;
    }

    private Object ognlParse (String ognlExpression) {
        try {
            return  Ognl.parseExpression (ognlExpression);
        } catch (Exception e) {
            ErrorHandler.handle (e);
            throw new RuntimeException ();
        }
    }
    
    private boolean guessReadOnly () {
        final Object oldValue = getValue ();
        try {
            setValue (oldValue);
            return false;
        }
        catch (Exception exc) {
            return true;
        }
    }
    
    public Object getValue () {
        try {
            return Ognl.getValue (_ognlTree, _model);
        } catch (OgnlException e) {
            ErrorHandler.handle (e);
            return null; // für den Compiler
        }
    }
    
    public void setValue (Object value) {
        try {
            Ognl.setValue (_ognlTree, _model, value);
        } catch (OgnlException e) {
            ErrorHandler.handle (e);
        }
    }

    public boolean isReadOnly () {
        return _readOnly;
    }
    
    public Class<?> getModelType () {
        return _type;
    }

	@Override
	public String getAttributeId() {
		return id;
	}
}
