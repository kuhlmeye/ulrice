package net.ulrice.databinding.modelaccess.impl;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;

/**
 * The reflection data accessor.
 * 
 * @author christof
 */
public abstract class AbstractReflectionMVA {




	public void setAccessible(final Field field) {
		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Object run() {
				field.setAccessible(true);
				return null;
			}
		});

	}
	
	public Field getFieldByReflection(Class<?> rootClass, String path) {
		
		String[] pathArr = path.split("\\.");
		Class<?> rootObject = rootClass;
		Field field = null;
		try {
			if (pathArr == null || pathArr.length == 0) {
				field = rootObject.getClass().getDeclaredField(path);
				if (!field.isAccessible()) {
					setAccessible(field);
				}
			} else {
				for (String pathElement : pathArr) {
					field = rootObject.getDeclaredField(pathElement);
					if (!field.isAccessible()) {
						setAccessible(field);
					}
					rootObject = field.getType();
				}				
			}
		} catch (IllegalArgumentException e) {
			throw new ReflectionMVAException("Could not read object from path: " + path, e);
		} catch (SecurityException e) {
			throw new ReflectionMVAException("Could not read object from path: " + path, e);
		} catch (NoSuchFieldException e) {
			throw new ReflectionMVAException("Could not read object from path: " + path, e);
		}
		return field;
	}
	
		
	public Object getValueByReflection(Object root, String path) {

		if (path == null) {
			return root;
		}

		String[] pathArr = path.split("\\.");

		Object object = root;
		try {
			for (String pathElement : pathArr) {
				if(object == null) {
					return null;
				}
				Field field = object.getClass().getDeclaredField(pathElement);
				if (!field.isAccessible()) {
					setAccessible(field);
				}
				object = field.get(object);
			}
			if (pathArr == null || pathArr.length == 0) {
				Field field = object.getClass().getDeclaredField(path);
				if (!field.isAccessible()) {
					setAccessible(field);
				}
				object = field.get(object);
			}
		} catch (IllegalArgumentException e) {
			throw new ReflectionMVAException("Could not read object from path: " + path, e);
		} catch (SecurityException e) {
			throw new ReflectionMVAException("Could not read object from path: " + path, e);
		} catch (IllegalAccessException e) {
			throw new ReflectionMVAException("Could not read object from path: " + path, e);
		} catch (NoSuchFieldException e) {
			throw new ReflectionMVAException("Could not read object from path: " + path, e);
		}

		return object;
	}

	public void setValueByReflection(Object root, Object value, String path) {
		if (path == null) {
			throw new ReflectionMVAException("Write path must not be null.", null);
		}

		String[] pathArr = path.split("\\.");
		String[] writePath = new String[0];
		if (pathArr != null && pathArr.length > 1) {
			writePath = Arrays.copyOf(pathArr, pathArr.length - 1);
		}

		Object object = root;
		try {
			for (String pathElement : writePath) {
				Field field = object.getClass().getDeclaredField(pathElement);
				if (!field.isAccessible()) {
					setAccessible(field);
				}
				object = field.get(object);
			}
			Field field = null;
			if (pathArr != null && pathArr.length > 1) {
				field = object.getClass().getDeclaredField(pathArr[pathArr.length - 1]);
			} else {
				field = object.getClass().getDeclaredField(path);
			}
			if (!field.isAccessible()) {
				setAccessible(field);
			}
			field.set(object, value);
		} catch (IllegalArgumentException e) {
			throw new ReflectionMVAException("Could not write object to path: " + path, e);
		} catch (SecurityException e) {
			throw new ReflectionMVAException("Could not write object to path: " + path, e);
		} catch (IllegalAccessException e) {
			throw new ReflectionMVAException("Could not write object to path: " + path, e);
		} catch (NoSuchFieldException e) {
			throw new ReflectionMVAException("Could not write object to path: " + path, e);
		}
	}
}
