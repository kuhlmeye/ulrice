package net.ulrice.databinding.modelaccess.impl;

import java.lang.reflect.Field;

import net.ulrice.databinding.modelaccess.IFModelValueAccessor;

public class ReflectionMVA extends AbstractReflectionMVA implements IFModelValueAccessor {

	private Object rootObject;
	private String path;

	private boolean readOnly;

	private String id;

	public ReflectionMVA(String id, Object rootObject, String path, boolean readOnly) {
		this.rootObject = rootObject;
		this.path = path;
		this.readOnly = readOnly;
		this.id = id;
	}

	public ReflectionMVA(Object rootObject, String path) {		
		this(rootObject.getClass().getSimpleName() + "." + path, rootObject, path, false);
	}

	public ReflectionMVA(Object rootObject, String path, boolean readOnly) {		
		this(rootObject.getClass().getSimpleName() + "." + path, rootObject, path, readOnly);
	}

	@Override
	public String getAttributeId() {
		return id;
	}

	@Override
	public Object getValue() {
		return getValueByReflection(rootObject, path);
	}

	@Override
	public void setValue(Object value) {
		setValueByReflection(rootObject, value, path);
	}
	
	@Override
	public Class<?> getModelType() {
		if (path != null) {
			Field field = getFieldByReflection(rootObject.getClass(), path);
			return field.getType();
		} else {
			return rootObject.getClass();
		}
	}
	
	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

}
