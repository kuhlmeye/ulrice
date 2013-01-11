package net.ulrice.databinding.modelaccess.impl;

import java.util.List;

import net.ulrice.databinding.ErrorHandler;
import net.ulrice.databinding.modelaccess.IFIndexedModelValueAccessor;
import net.ulrice.databinding.modelaccess.IFModelValueAccessor;
import ognl.Ognl;
import ognl.OgnlException;


public class OgnlSingleListIndexedMVA implements IFIndexedModelValueAccessor {
    private final Class<?> type;
    private final boolean isReadOnly;

    private final Object model;

    private final Object ognlBaseTree;
    private final Object ognlElementTree;
    private final IFModelValueAccessor sizeMVA;
    private String id;

    public OgnlSingleListIndexedMVA (Class<?> type, Boolean isReadOnly, Object model, String baseExpression, String elementExpression, IFModelValueAccessor sizeMVA) {
        this.type = type;
        this.model = model;
        this.sizeMVA = sizeMVA;
        try {
        	this.ognlBaseTree = Ognl.parseExpression (baseExpression);
        	this.ognlElementTree = Ognl.parseExpression (elementExpression);
        } catch (OgnlException exc) {
            ErrorHandler.handle (exc);
            throw new RuntimeException (); // for the compiler
        }
        this.isReadOnly = isReadOnly != null ? isReadOnly : guessReadOnly ();
        id = this.model.getClass().getName() + "." + baseExpression + "." + elementExpression;
    }

    private boolean guessReadOnly () {
        try {
            Object o = null;
            try {
                o = getValue (0);
            }
            catch (Exception exc) {
                //TODO log that 'guessing' did not work
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
        return type;
    }

    public boolean isReadOnly () {
        return isReadOnly;
    }

    
    public Object getValue (int index) {
        try {
            final List<?> list = (List<?>) Ognl.getValue (ognlBaseTree, model);
            final Object baseValue = list.get (index);

            return Ognl.getValue (ognlElementTree, baseValue);
        }
        catch (OgnlException exc) {
            ErrorHandler.handle (exc);
            return null; // for the compiler
        }
    }

    @Override
    public void setValues (Object values) {
        if (values instanceof List) {
            final List<?> l = (List<?>) values;
            for (int i=0; i<l.size(); i++) {
                setValue (i, l.get(i));
            }
        }
        else {
            final Object[] l = (Object[]) values;
            for (int i=0; i<l.length; i++) {
                setValue (i, l[i]);
            }
        }
    }
    
    @Override
    public void setValue (int index, Object value) {
        try {
            final List<?> list = (List<?>) Ognl.getValue (ognlBaseTree, model);
            final Object baseValue = list.get (index);
            Ognl.setValue (ognlElementTree, baseValue, value);
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
    	return (Integer)sizeMVA.getValue();
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
