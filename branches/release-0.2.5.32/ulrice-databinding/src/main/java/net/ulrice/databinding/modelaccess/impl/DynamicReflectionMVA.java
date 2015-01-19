package net.ulrice.databinding.modelaccess.impl;

import java.lang.reflect.InvocationTargetException;

import net.ulrice.databinding.modelaccess.IFDynamicModelValueAccessor;
import net.ulrice.databinding.reflect.ReflectionUtils;

public class DynamicReflectionMVA implements IFDynamicModelValueAccessor {

	private String id;
	private String path;

	public DynamicReflectionMVA(String id,String path) {
		this.path = path;
		this.id = id;
	}
	
	public DynamicReflectionMVA(Class<?> rootClass, String path) {
		this(rootClass.getSimpleName() + "." + path, path);	
	}
	
	@Override
	public Object getValue(Object root) {
		try {
            return ReflectionUtils.getInstance().getPropertyValue(root, path);
        }
        catch (IllegalArgumentException e) {
            throw new ReflectionMVAException("Error getting property value at path " + path, e);
        }
        catch (IllegalAccessException e) {
            throw new ReflectionMVAException("Error getting property value at path " + path, e);
        }
        catch (InvocationTargetException e) {
            throw new ReflectionMVAException("Error getting property value at path " + path, e);
        }
	}

	@Override
	public void setValue(Object root, Object value) {
	    try {
            ReflectionUtils.getInstance().setPropertyValue(root, path, value);
        }
        catch (IllegalArgumentException e) {
            throw new ReflectionMVAException("Error setting property value at path " + path, e);
        }
        catch (IllegalAccessException e) {
            throw new ReflectionMVAException("Error setting property value at path " + path, e);
        }
        catch (InvocationTargetException e) {
            throw new ReflectionMVAException("Error setting property value at path " + path, e);
        }
	}

	@Override
	public String getAttributeId() {
		return id;
	}

	@Override
	public Class<?> getModelType(Class<?> rootType) {
		return ReflectionUtils.getInstance().getParentClassForDotSeparatedFieldType(rootType, path);
	}
}
