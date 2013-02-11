package net.ulrice.databinding.modelaccess.impl;

import net.ulrice.databinding.ErrorHandler;
import net.ulrice.databinding.modelaccess.IFModelValueAccessor;
import ognl.Ognl;
import ognl.OgnlException;


public class OgnlMVA implements IFModelValueAccessor {
    private final Object model;
    private final Object ognlTree;
    private final Class<?> type;
    private final boolean readOnly;
	private String id;
    
    //TODO Typ und readOnly ermitteln
    
    public OgnlMVA (Object model, String ognlExpression, Class<?> type) {
    	this.model = model;
    	this.ognlTree = ognlParse (ognlExpression);
    	this.type = type;
    	this.readOnly = guessReadOnly ();
        id = this.model.getClass().getName() + "." + ognlTree;
    }
    
    public OgnlMVA (Object model, String ognlExpression, Class<?> type, boolean readOnly) {
    	this.model = model;
    	this.ognlTree = ognlParse (ognlExpression);
    	this.type = type;
    	this.readOnly = readOnly;
        id = this.model.getClass().getName() + "." + ognlTree;
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
            return Ognl.getValue (ognlTree, model);
        } catch (OgnlException e) {
            ErrorHandler.handle (e);
            return null; // für den Compiler
        }
    }
    
    public void setValue (Object value) {
        try {
            Ognl.setValue (ognlTree, model, value);
        } catch (OgnlException e) {
            ErrorHandler.handle (e);
        }
    }

    public boolean isReadOnly () {
        return readOnly;
    }
    
    public Class<?> getModelType () {
        return type;
    }

	@Override
	public String getAttributeId() {
		return id;
	}
}
