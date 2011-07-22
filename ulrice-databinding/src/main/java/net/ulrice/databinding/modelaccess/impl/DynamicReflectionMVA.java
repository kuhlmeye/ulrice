package net.ulrice.databinding.modelaccess.impl;

public class DynamicReflectionMVA extends AbstractReflectionMVA {

	public DynamicReflectionMVA(String id, String path) {
		super(id, null, path, path, false);
	}
	
	public DynamicReflectionMVA(Class rootClass, String path) {
		super(rootClass.getSimpleName() + "." + path, null, path, path, false);	
	}
}
