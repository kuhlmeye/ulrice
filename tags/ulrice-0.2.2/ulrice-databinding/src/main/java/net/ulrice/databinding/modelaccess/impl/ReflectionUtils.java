package net.ulrice.databinding.modelaccess.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;

import net.ulrice.databinding.ErrorHandler;

/**
 * The reflection data accessor.
 * 
 * @author christof
 */
public class ReflectionUtils {




	public static void setAccessible(final Field field) {
		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Object run() {
				field.setAccessible(true);
				return null;
			}
		});

	}
	
	public static Field getFieldByReflection(Class<?> rootClass, String path) {
		
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
	
		
	public static Object getValueByReflection(Object root, String path) {

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

	public static void setValueByReflection(Object root, Object value, String path) {
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
	
	public static Object cloneObject(Object obj) {
		if (obj == null) {
			return null;
		}

		if (obj instanceof Cloneable) {
			Class<?> clazz = obj.getClass();
			Method m;
			try {
				m = clazz.getMethod("clone", (Class[]) null);
			} catch (NoSuchMethodException ex) {
				throw new NoSuchMethodError(ex.getMessage());
			}
			try {
				return m.invoke(obj, (Object[]) null);
			} catch (InvocationTargetException ex) {
				ErrorHandler.handle(ex);
			} catch (IllegalAccessException ex) {
				ErrorHandler.handle(ex);
			}
		} else if (obj instanceof Serializable) {

			ByteArrayOutputStream bytes = new ByteArrayOutputStream() {

				public synchronized byte[] toByteArray() {
					return buf;
				}
			};

			try {
				ObjectOutputStream out = new ObjectOutputStream(bytes);
				out.writeObject(obj);
				out.close();

				ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
				Object objCopy = in.readObject();
				return objCopy;
			} catch (IOException e) {
				ErrorHandler.handle(e);
			} catch (ClassNotFoundException e) {
				ErrorHandler.handle(e);
			}

		} else {
			Object clone = null;
			try {
				clone = obj.getClass().newInstance();
			} catch (InstantiationException e) {
				ErrorHandler.handle(e);
			} catch (IllegalAccessException e) {
				ErrorHandler.handle(e);
			}

			for (Class objClass = obj.getClass(); !objClass.equals(Object.class); objClass = objClass.getSuperclass()) {
				Field[] fields = objClass.getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					fields[i].setAccessible(true);
					try {
						fields[i].set(clone, fields[i].get(obj));
					} catch (IllegalArgumentException e) {
						ErrorHandler.handle(e);
					} catch (IllegalAccessException e) {
						ErrorHandler.handle(e);
					}
				}
			}
			return clone;
		}
		return null;
	}
}
