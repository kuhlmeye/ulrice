package net.ulrice.databinding.modelaccess.impl;

public class ReflectionMVA extends AbstractReflectionMVA {

	public ReflectionMVA(Object rootObject, String path) {
		super(rootObject.getClass().getName() + "." + path, rootObject, path, path, false);
	}

	public ReflectionMVA(Object rootObject, String path, boolean readOnly) {		
		super(rootObject.getClass().getName() + "." + path, rootObject, path, path, readOnly);
	}
	
	public ReflectionMVA(Object rootObject, String readPath, String writePath, boolean readOnly) {
		super(rootObject.getClass().getName() + "." + readPath, rootObject, readPath, writePath, readOnly);
	}
}
