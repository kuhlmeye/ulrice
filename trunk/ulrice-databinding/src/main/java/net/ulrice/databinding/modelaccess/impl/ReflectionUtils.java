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
 * @author christof
 */
public class ReflectionUtils {

	public static void setAccessible(final Field field) {
	    if (field.isAccessible()) {
	        return;
	    }
	    
		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Object run() {
				field.setAccessible(true);
				return null;
			}
		});

	}
	
	public static Field getFieldByReflection(Class<?> rootClass, String path) {
		
		String[] pathArr = path.split("\\.");
		Class<?> currentClass = rootClass;
		Field field = null;
		try {
		    for (String pathElement : pathArr) {
		        field = getFieldInHierarchy(currentClass, pathElement);
		        currentClass = field.getType();
		    }				
		} catch (IllegalArgumentException e) {
			throw new ReflectionMVAException("Could not read object from path: " + path, e);
		} catch (SecurityException e) {
			throw new ReflectionMVAException("Could not read object from path: " + path, e);
		}
		return field;
	}
	
	public static Class<?> getFieldType (Class<?> rootClass, String path) {
	    return getFieldByReflection(rootClass, path).getType();
	}
	
	public static Field getFieldInHierarchy (Class<?> cls, String fieldName) {
	    try {
            return getFieldInHierarchy (cls, cls, fieldName);
        }
        catch (NoSuchFieldException e) {
            throw new ReflectionMVAException("There is no field " + fieldName + " in " + cls.getName() + " or its subclasses", e);
        }
	}
	
	private static Field getFieldInHierarchy (Class<?> cls, Class<?> originalCls, String fieldName) throws NoSuchFieldException {
	    try {
	        Field result = cls.getDeclaredField(fieldName);
	        setAccessible(result);
	        return result;
	    } catch (SecurityException e) {
	        throw new ReflectionMVAException("Security exception while accessing field " + cls.getName() + "." + fieldName + ".", e);
	    } catch (NoSuchFieldException e) {
	        if (cls.getSuperclass() == null) {
	            throw e;
	        }
	        return getFieldInHierarchy(cls.getSuperclass(), originalCls, fieldName);
	    }
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
				
                try {
                    Field field = getFieldInHierarchy(object.getClass(), object.getClass(), pathElement);
                    object = field.get(object);
                }
                catch (NoSuchFieldException e) {
                    String methodName = "get" + String.valueOf(pathElement.charAt(0)).toUpperCase() + pathElement.substring(1);
                    try {
                        Method method = getMethodInHierarchy(object.getClass(), object.getClass(), methodName);
                        object = method.invoke(object);
                    }
                    catch (NoSuchMethodException e1) {
                        throw new ReflectionMVAException("Could not read object from path: " + path, e1);
                    }
                    catch (InvocationTargetException e1) {
                        throw new ReflectionMVAException("Could not read object from path: " + path, e1);
                    }
                    
                }
			}
		} catch (IllegalArgumentException e) {
			throw new ReflectionMVAException("Could not read object from path: " + path, e);
		} catch (SecurityException e) {
			throw new ReflectionMVAException("Could not read object from path: " + path, e);
		} catch (IllegalAccessException e) {
			throw new ReflectionMVAException("Could not read object from path: " + path, e);
		}

		return object;
	}

	private static Method getMethodInHierarchy(Class< ? extends Object> cls, Class< ? extends Object> originalCls,
        String methodName) throws NoSuchMethodException {
        try {
            Method result = cls.getDeclaredMethod(methodName);
            return result;
        } catch (SecurityException e) {
            throw new ReflectionMVAException("Security exception while accessing method " + cls.getName() + "." + methodName + ".", e);
        } catch (NoSuchMethodException e) {
            if (cls.getSuperclass() == null) {
                throw e;
            }
            return getMethodInHierarchy(cls.getSuperclass(), originalCls, methodName);
        }
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
				Field field = getFieldInHierarchy(object.getClass(), pathElement);
				if (!field.isAccessible()) {
					setAccessible(field);
				}
				object = field.get(object);
			}
			Field field = null;
			if (pathArr != null && pathArr.length > 1) {
				field = getFieldInHierarchy(object.getClass(), pathArr[pathArr.length - 1]);
			} else {
				field = getFieldInHierarchy(object.getClass(), path);
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
		}
	}
	
	@Deprecated
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
