package net.ulrice.databinding.modelaccess.impl;

public class DynamicReflectionMVA extends AbstractReflectionMVA {

	public DynamicReflectionMVA(Class rootClass, String path) {
		super(rootClass.getName() + "." + path, null, path, path, false);
		
	}
}
