package net.ulrice.databinding.converter.impl;

import java.lang.reflect.Method;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.converter.IFValueConverter;


public class GenericStringToNumberConverter <X extends Number> implements IFValueConverter <X, String> {
	public static final GenericStringToNumberConverter <Byte> BYTE = new GenericStringToNumberConverter<Byte> (Byte.class); 
	public static final GenericStringToNumberConverter <Short> SHORT = new GenericStringToNumberConverter<Short> (Short.class); 
	public static final GenericStringToNumberConverter <Integer> INT = new GenericStringToNumberConverter<Integer> (Integer.class); 
	public static final GenericStringToNumberConverter <Long> LONG = new GenericStringToNumberConverter<Long> (Long.class); 
	public static final GenericStringToNumberConverter <Float> FLOAT = new GenericStringToNumberConverter<Float> (Float.class); 
	public static final GenericStringToNumberConverter <Double> DOUBLE = new GenericStringToNumberConverter<Double> (Double.class); 
	
	private final Class<X> modelClass;
	private final Class<? extends Number> nonPrimitiveModelClass;
	private final Method valueOfMethod;

	private GenericStringToNumberConverter(Class<X> modelClass) {
		this.modelClass = modelClass;
		this.nonPrimitiveModelClass = asNonPrimitive (modelClass);
		this.valueOfMethod = valueOfMethod (nonPrimitiveModelClass);
	}

	private static Class <? extends Number> asNonPrimitive (Class<? extends Number> cls) {
		if (cls == Byte.TYPE) {
			return Byte.class;
		}
		if (cls == Short.TYPE) {
			return Short.class;
		}
		if (cls == Integer.TYPE) {
			return Integer.class;
		}
		if (cls == Long.TYPE) {
			return Long.class;
		}
		if (cls == Float.TYPE) {
			return Float.class;
		}
		if (cls == Double.TYPE) {
			return Double.class;
		}
			
		return cls;
	}

	private static Method valueOfMethod (Class <? extends Number> cls) {
		try {
			return cls.getMethod ("valueOf", String.class);
		}
		catch (Exception exc) {
			return null;
		}
	}
	
	@Override
	public Class<? extends String> getViewType(Class<? extends X> modelType) {
		return String.class;
	}

	@Override
	public Class<? extends X> getModelType(Class<? extends String> viewType) {
		return modelClass;
	}

	@Override
	public boolean canHandle(Class<? extends Object> modelType, Class<? extends Object> viewType) {
		return viewType.equals (String.class) && this.modelClass.isAssignableFrom (modelType);
	}

	@Override
	public X viewToModel(String o, IFAttributeInfo attributeInfo) {
		try {
			if (o == null || o.trim().length() == 0) {
				if (modelClass == nonPrimitiveModelClass) {
					return null;
				}
				else {
					o = "0";
				}
			}
			return (X) valueOfMethod.invoke(null, o);
		} catch (Exception e) {
			throw new RuntimeException (e);//TODO
		}
	}

	@Override
	public String modelToView(X o, IFAttributeInfo attributeInfo) {
		return (o == null) ? null : o.toString(); //TODO numberformat?!
	}
}
