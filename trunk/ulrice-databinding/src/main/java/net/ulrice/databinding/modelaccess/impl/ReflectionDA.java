package net.ulrice.databinding.modelaccess.impl;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;

import net.ulrice.databinding.modelaccess.IFDynDataAccessor;
import net.ulrice.databinding.modelaccess.IFModelValueAccessor;

/**
 * The reflection data accessor.
 * 
 * @author christof
 */
public class ReflectionDA implements IFModelValueAccessor, IFDynDataAccessor {

	private Object rootObject;

	private String readPath;

	private String writePath;

	private boolean readOnly;

	public ReflectionDA(Object rootObject, String path) {
		this(rootObject, path, path, false);
	}

	public ReflectionDA(Object rootObject, String path, boolean readOnly) {
		this(rootObject, path, path, readOnly);
	}

	public ReflectionDA(String path) {
		this(null, path);
	}

	public ReflectionDA(Object rootObject, String readPath, String writePath, boolean readOnly) {
		this.rootObject = rootObject;
		this.readPath = readPath;
		this.writePath = writePath;
		this.readOnly = readOnly;
	}

	@Override
	public Object readValue(Object root) {
		
		if (readPath == null) {
			return root;
		}

		String[] path = readPath.split(".");

		Object object = root;
		try {
			for (String pathElement : path) {
				object = object.getClass().getDeclaredField(pathElement).get(object);
			}
			if (path == null || path.length == 0) {
				Field field = object.getClass().getDeclaredField(readPath);
				if (!field.isAccessible()) {
					setAccessible(field);
				}
				object = field.get(object);
			}
		} catch (IllegalArgumentException e) {
			throw new ReflectionDataAccessorException("Could not read object from path: " + readPath, e);
		} catch (SecurityException e) {
			throw new ReflectionDataAccessorException("Could not read object from path: " + readPath, e);
		} catch (IllegalAccessException e) {
			throw new ReflectionDataAccessorException("Could not read object from path: " + readPath, e);
		} catch (NoSuchFieldException e) {
			throw new ReflectionDataAccessorException("Could not read object from path: " + readPath, e);
		}

		return object;
	}

	public void setAccessible(final Field field) {
		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Object run() {
				field.setAccessible(true);
				return null;
			}
		});

	}

	@Override
	public void writeValue(Object root, Object value) {
		if (writePath == null) {
			throw new ReflectionDataAccessorException("Write path must not be null.", null);
		}

		String[] path = writePath.split(".");
		String[] readPath = new String[0];
		if (path != null && path.length > 1) {
			readPath = Arrays.copyOf(path, path.length - 2);
		}

		Object object = root;
		try {
			for (String pathElement : readPath) {
				object = object.getClass().getDeclaredField(pathElement).get(object);
			}						
			Field field = null;
			if (path != null && path.length > 1) {
				field = object.getClass().getDeclaredField(path[path.length - 1]);
			} else {
				field = object.getClass().getDeclaredField(writePath);
			}
			if (!field.isAccessible()) {
				setAccessible(field);
			}
			field.set(object, value);			
		} catch (IllegalArgumentException e) {
			throw new ReflectionDataAccessorException("Could not write object to path: " + path, e);
		} catch (SecurityException e) {
			throw new ReflectionDataAccessorException("Could not write object to path: " + path, e);
		} catch (IllegalAccessException e) {
			throw new ReflectionDataAccessorException("Could not write object to path: " + path, e);
		} catch (NoSuchFieldException e) {
			throw new ReflectionDataAccessorException("Could not write object to path: " + path, e);
		}
	}


	@Override
	public Object getValue() {
		return readValue(rootObject);
	}

	@Override
	public void setValue(Object value) {
		writeValue(rootObject, value);
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public Class<?> getModelType() {
		if (readPath != null) {

			Object object = rootObject;
			String[] path = readPath.split(".");
			try {
				Field field = null;
				if(path.length > 1) {
					for (int i = 0; i < path.length - 1; i++) {
						object = object.getClass().getField(path[i]).get(object);
					}					
					field = object.getClass().getDeclaredField(path[path.length - 1]);
				}
				else {
					field = object.getClass().getDeclaredField(readPath);
				}
				return field.getType();
			} catch (IllegalArgumentException e) {
				throw new ReflectionDataAccessorException("Could not read object from path: " + readPath, e);
			} catch (SecurityException e) {
				throw new ReflectionDataAccessorException("Could not read object from path: " + readPath, e);
			} catch (IllegalAccessException e) {
				throw new ReflectionDataAccessorException("Could not read object from path: " + readPath, e);
			} catch (NoSuchFieldException e) {
				throw new ReflectionDataAccessorException("Could not read object from path: " + readPath, e);
			}
		}			
		return null;
	}
}
