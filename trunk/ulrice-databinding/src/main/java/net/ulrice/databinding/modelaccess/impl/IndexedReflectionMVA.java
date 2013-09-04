package net.ulrice.databinding.modelaccess.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import net.ulrice.databinding.modelaccess.IFIndexedModelValueAccessor;
import net.ulrice.databinding.modelaccess.IFModelValueAccessor;
import net.ulrice.databinding.reflect.ReflectionUtils;

public class IndexedReflectionMVA implements IFIndexedModelValueAccessor {

	private String id;
	private String path;
	private boolean readOnly;
	private Object rootObject;
	
	private IFModelValueAccessor sizeMVA;
	private Class<?> modelRowClass;

    public IndexedReflectionMVA(String id, Object rootObject, String path, boolean readOnly, IFModelValueAccessor sizeMVA) {
        this(id, rootObject, path, readOnly, sizeMVA, null);
    }

	
	public IndexedReflectionMVA(String id, Object rootObject, String path) {		
		this(id, rootObject, path, false, null);
	}
	
	public IndexedReflectionMVA(String id, Object rootObject, String path, boolean readOnly) {		
		this(id, rootObject, path, readOnly, null);
	}

	public IndexedReflectionMVA(Object rootObject, String path, Class<?> modelRowClass) {
		this(rootObject.getClass().getSimpleName() + "." + path, rootObject, path, false, null, modelRowClass);
	}
	
	public IndexedReflectionMVA(Object rootObject, String path) {		
		this(rootObject.getClass().getSimpleName() + "." + path, rootObject, path, false, null);
	}

	public IndexedReflectionMVA(Object rootObject, String path, boolean readOnly) {		
		this(rootObject.getClass().getSimpleName() + "." + path, rootObject, path, readOnly, null);
	}

	public IndexedReflectionMVA(Object rootObject, String path, IFModelValueAccessor sizeMVA) {		
		this(rootObject.getClass().getSimpleName() + "." + path, rootObject, path, false, sizeMVA);
	}

	public IndexedReflectionMVA(Object rootObject, String path, boolean readOnly, IFModelValueAccessor sizeMVA) {		
		this(rootObject.getClass().getSimpleName() + "." + path, rootObject, path, readOnly, sizeMVA);
	}
    
    public IndexedReflectionMVA(String id, Object rootObject, String path, boolean readOnly, IFModelValueAccessor sizeMVA, Class<?> modelRowClass) {
        this.rootObject = rootObject;
        this.path = path;
        this.readOnly = readOnly;
        this.id = id;
        this.sizeMVA = sizeMVA;
        
        if(modelRowClass != null) {
            this.modelRowClass = modelRowClass;
        } else if (path != null) {
            Field field = ReflectionUtils.getInstance().getFieldByReflection(rootObject.getClass(), path);
            Type genericFieldType = field.getGenericType();
            
            if(genericFieldType instanceof ParameterizedType){
                ParameterizedType aType = (ParameterizedType) genericFieldType;
                Type[] fieldArgTypes = aType.getActualTypeArguments();
                for(Type fieldArgType : fieldArgTypes){
                    this.modelRowClass = (Class<?>) fieldArgType;
                }
            }
        } 
    }
    

	@Override
	public Object getValue(int index) {
	    try {
    		Object listValue = ReflectionUtils.getInstance().getPropertyValue(rootObject, path);
    		if (listValue == null) {
    			throw new NullPointerException();
    		}
    
    		if (listValue instanceof List<?>) {
    			List<?> list = (List<?>) listValue;
    			return list.get(index);
    		}
    		if (listValue.getClass().isArray()) {
    			return ((Object[]) listValue)[index];
    		}
    
    		throw new ReflectionMVAException("Type: " + listValue.getClass() + " is not allowed for indexed model value access", null);
    
        }
        catch (IllegalArgumentException e) {
            throw new ReflectionMVAException("Error setting value to index " + index, e);            
        }
        catch (IllegalAccessException e) {
            throw new ReflectionMVAException("Error setting value to index " + index, e);            
        }
        catch (InvocationTargetException e) {
            throw new ReflectionMVAException("Error setting value to index " + index, e);            
        }
	}

	@Override
	public void setValues (Object values) {
	    if (values instanceof List) {
	        final List<?> l = (List<?>) values;
	        for (int i=0; i<l.size(); i++) {
	            setValue (i, l.get(i));
	        }
	    }
	    else {
	        final Object[] l = (Object[]) values;
	        for (int i=0; i<l.length; i++) {
	            setValue (i, l[i]);
	        }
	    }
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setValue(int index, Object value) {
        try {
            Object listValue = ReflectionUtils.getInstance().getPropertyValue(rootObject, path);
            if (listValue == null) {
                throw new NullPointerException();
            }

            if (List.class.isAssignableFrom(listValue.getClass())) {
                List list = (List) listValue;
                if(index < list.size()) {
                    list.set(index, value);
                } else {
                    list.add(value);
                }
            } else if (listValue.getClass().isArray()) {
                ((Object[]) listValue)[index] = value;
            } else {
                throw new ReflectionMVAException("Type: " + listValue.getClass() + " is not allowed for indexed model value access", null);
            }
        }
        catch (IllegalArgumentException e) {
            throw new ReflectionMVAException("Error setting value to index " + index, e);            
        }
        catch (IllegalAccessException e) {
            throw new ReflectionMVAException("Error setting value to index " + index, e);            
        }
        catch (InvocationTargetException e) {
            throw new ReflectionMVAException("Error setting value to index " + index, e);            
        }
	}
	
	@Override
	public Class<?> getModelType() {
		return modelRowClass;
	}
	
	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public String getAttributeId() {
		return id;
	}

	@Override
	public int getSize() {
		if(sizeMVA != null) {
			return (Integer)sizeMVA.getValue();
		} else {
			Object listValue;
            try {
                listValue = ReflectionUtils.getInstance().getPropertyValue(rootObject, path);
                if (listValue == null) {
                    return 0;
                }
            }
            catch (IllegalArgumentException e1) {
                throw new ReflectionMVAException("Error getting list value", e1);
            }
            catch (IllegalAccessException e1) {
                throw new ReflectionMVAException("Error getting list value", e1);
            }
            catch (InvocationTargetException e1) {
                throw new ReflectionMVAException("Error getting list value", e1);
            }
            

			if (listValue instanceof List<?>) {	
                try {
                    return ((List<?>) ReflectionUtils.getInstance().getPropertyValue(rootObject, path)).size();  
                }
                catch (IllegalArgumentException e) {
                    throw new ReflectionMVAException("Error getting length of list", e);
                }
                catch (IllegalAccessException e) {
                    throw new ReflectionMVAException("Error getting length of list", e);
                }
                catch (InvocationTargetException e) {
                    throw new ReflectionMVAException("Error getting length of list", e);
                }   
			}
			if (listValue.getClass().isArray()) {
			    try {
                    return (Integer) ReflectionUtils.getInstance().getPropertyValue(rootObject, path + ".length");
                }
                catch (IllegalArgumentException e) {
                    throw new ReflectionMVAException("Error getting length of array", e);
                }
                catch (IllegalAccessException e) {
                    throw new ReflectionMVAException("Error getting length of array", e);
                }
                catch (InvocationTargetException e) {
                    throw new ReflectionMVAException("Error getting length of array", e);
                }			    	
			}
		}
		
		throw new ReflectionMVAException("Could not get size for type: " + rootObject.getClass() + ". Set sizeMVA.", null);
	}

	@Override
	public Object newObjectInstance() {
		Class<?> modelType = getModelType();
		try {
			return modelType.newInstance();
		} catch (InstantiationException e) {
			throw new ReflectionMVAException("Could not create new instance of type: " + modelType, e);
		} catch (IllegalAccessException e) {
			throw new ReflectionMVAException("Could not create new instance of type: " + modelType, e);
		}
	}

	@Override
	public Object cloneObject(Object obj) {
		try {
            return ReflectionUtils.getInstance().createCopy(obj);
        }
        catch (IllegalArgumentException e) {
            throw new ReflectionMVAException("Error copying object.", e);
        }
        catch (IllegalAccessException e) {
            throw new ReflectionMVAException("Error copying object.", e);
        }
        catch (InstantiationException e) {
            throw new ReflectionMVAException("Error copying object.", e);
        }
	}
}
