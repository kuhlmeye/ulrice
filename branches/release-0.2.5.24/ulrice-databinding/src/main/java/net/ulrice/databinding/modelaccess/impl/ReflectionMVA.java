package net.ulrice.databinding.modelaccess.impl;

import java.lang.reflect.InvocationTargetException;

import net.ulrice.databinding.modelaccess.IFModelValueAccessor;
import net.ulrice.databinding.reflect.ReflectionUtils;
import net.ulrice.databinding.reflect.UlriceReflectionUtils;

public class ReflectionMVA implements IFModelValueAccessor {

	private final Object rootObject;
	private final String path;
	private final Class<?> modelType;

	private final boolean readOnly;

	private final String id;

	public ReflectionMVA(String id, Object rootObject, String path, boolean readOnly) {
	    this (id, rootObject, path, readOnly, UlriceReflectionUtils.getFieldType(rootObject.getClass(), path));
	}
	    
	public ReflectionMVA(String id, Object rootObject, String path, boolean readOnly, Class<?> valueType) {
		this.rootObject = rootObject;
		this.path = path;
		this.modelType = valueType;
		this.readOnly = readOnly;
		this.id = id;
	}

	public ReflectionMVA(Object rootObject, String path) {
		this(createID(rootObject, path), rootObject, path, false);
	}

	public ReflectionMVA(Object rootObject, String path, boolean readOnly) {		
		this(createID(rootObject, path), rootObject, path, readOnly);
	}

	public static String createID (Object rootObject, String path) {
	    return rootObject.getClass().getSimpleName() + "." + path;
	}
	
	@Override
	public String getAttributeId() {
		return id;
	}

	@Override
	public Object getValue() {
		try {
            return ReflectionUtils.getInstance().getPropertyValue(rootObject, path, true);
        }
        catch (IllegalArgumentException e) {
            throw new ReflectionMVAException("Error getting value at path " + path, e);
        }
        catch (IllegalAccessException e) {
            throw new ReflectionMVAException("Error getting value at path " + path, e);
        }
        catch (InvocationTargetException e) {
            throw new ReflectionMVAException("Error getting value at path " + path, e);
        }
	}

	@Override
	public void setValue(Object value) {
	    try {
            ReflectionUtils.getInstance().setPropertyValue(rootObject, path, value, true);
        }
        catch (IllegalArgumentException e) {
            throw new ReflectionMVAException("Error setting value at path " + path, e);
        }
        catch (IllegalAccessException e) {
            throw new ReflectionMVAException("Error setting value at path " + path, e);
        }
        catch (InvocationTargetException e) {
            throw new ReflectionMVAException("Error setting value at path " + path, e);
        }
        catch (InstantiationException e) {
            throw new ReflectionMVAException("Error setting value at path " + path, e);
        }
	}
	
	@Override
	public Class<?> getModelType() {
	    return modelType;
	}
	
	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

}
