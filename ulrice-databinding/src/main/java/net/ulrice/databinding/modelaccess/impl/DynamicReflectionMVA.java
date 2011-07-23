package net.ulrice.databinding.modelaccess.impl;

import java.lang.reflect.Field;

import net.ulrice.databinding.modelaccess.IFDynamicModelValueAccessor;

public class DynamicReflectionMVA extends AbstractReflectionMVA implements IFDynamicModelValueAccessor {

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
		return getValueByReflection(root, path);
	}

	@Override
	public void setValue(Object root, Object value) {
		setValueByReflection(root, value, path);
	}

	@Override
	public String getAttributeId() {
		return id;
	}

	@Override
	public Class<?> getModelType(Class<?> rootType) {
		Field field = getFieldByReflection(rootType, path);
		return field.getType();
	}
}
