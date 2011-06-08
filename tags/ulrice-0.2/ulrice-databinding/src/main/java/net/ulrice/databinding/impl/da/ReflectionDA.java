package net.ulrice.databinding.impl.da;

import java.util.Arrays;

import net.ulrice.databinding.IFDataAccessor;
import net.ulrice.databinding.IFDynDataAccessor;

/**
 * The reflection data accessor.
 * 
 * @author christof
 */
public class ReflectionDA<T> implements IFDataAccessor<T>, IFDynDataAccessor<T> {

	/** The root object from which the objects are loaded. */
	private Object rootObject;

	/** The path used for reading the attribute. */
	private String readPath;

	/** The path used for writing the attribute. */
	private String writePath;

    /**
     * Creates a new reflection data accessor.
     * 
     * @param rootObject
     *            The root object from which the objects are loaded.
     * @param path
     *            The path within the root object to access the attribute.
     */
    public ReflectionDA(Object rootObject, String path) {
        this(rootObject, path, path);
    }
    
    /**
     * Creates a new dynamic reflection data accessor.
     * 
     * @param path
     *            The path within the root object to access the attribute.
     */
    public ReflectionDA(String path) {
        this(null, path);
    }

	/**
	 * Creates a new reflection data accessor.
	 * 
	 * @param rootObject
	 *            The root object from which the objects are loaded.
	 * @param readPath
	 *            The path for reading
	 * @param writePath
	 */
	public ReflectionDA(Object rootObject, String readPath, String writePath) {
		this.rootObject = rootObject;
		this.readPath = readPath;
		this.writePath = writePath;
	}

	/**
	 * @see net.ulrice.databinding.IFDataAccessor#readValue()
	 */
	@Override
	public T readValue() {
		return readValue(rootObject);
	}

	/**
	 * @see net.ulrice.databinding.IFDataAccessor#writeValue(java.lang.Object)
	 */
	@Override
	public void writeValue(T value) {
		writeValue(rootObject, value);
	}

	/**
	 * @see net.ulrice.databinding.IFDynDataAccessor#readValue(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T readValue(Object root) {

		if (readPath == null) {
			return (T) root;
		}


		String[] path = readPath.split(".");

		
		Object object = root;
		try {
			for (String pathElement : path) {
				object = object.getClass().getField(pathElement).get(object);
			}
			if(path == null || path.length == 0) {
				object = object.getClass().getField(readPath).get(object);
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

		return (T) object;
	}

	/**
	 * @see net.ulrice.databinding.IFDynDataAccessor#writeValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void writeValue(Object root, T value) {
		if (writePath == null) {
			throw new ReflectionDataAccessorException("Write path must not be null.", null);
		}

		String[] path = writePath.split(".");
		String[] readPath = new String[0];
		if(path != null && path.length > 1) {
			readPath = Arrays.copyOf(path, path.length - 2);
		} 

		Object object = root;
		try {
			for (String pathElement : readPath) {
				object = object.getClass().getField(pathElement).get(object);
			}
			if(path != null && path.length > 1) {
				object.getClass().getField(path[path.length - 1]).set(object, value);
			} else {
				object.getClass().getField(writePath).set(object, value);
			}
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

}
