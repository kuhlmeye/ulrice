package net.ulrice.configuration;

import java.util.HashMap;
import java.util.Map;



/**
 * Class loading helper methods.
 * 
 * @author ckuhlmeyer
 */
public abstract class ClassLoadingHelper {

	/** Map holding the loaded objects by full class name.  */
	private Map<String, Object> instanceMap = new HashMap<String, Object>();
	
	/**
	 * Creates a new abstract ulrice configuration.
	 */
	public ClassLoadingHelper() {
		super();
	}

	/**
	 * Load an instance of a class from the string.
	 * 
	 * @param className
	 *            The name of the class as a string.
	 * @return The instance of a class.
	 * @throws ConfigurationException
	 *             If the instance could not be created.
	 */
	protected Object loadClass(String className) throws ConfigurationException {

		// Check, if class name is null.
		if (className == null) {
			throw new ConfigurationException("Class name is null.", null);
		}
		
		if(instanceMap.containsKey(className)) {
			return instanceMap.get(className);
		}

		try {
			Class<?> instanceClass = Class.forName(className);
			Object instance = instanceClass.newInstance();
			
			instanceMap.put(className, instance);

			return instance;
		} catch (ClassNotFoundException e) {
			throw new ConfigurationException("Class (" + className + ") could not be found.", e);
		} catch (InstantiationException e) {
			throw new ConfigurationException("Could not instanciate class (" + className + ").", e);
		} catch (IllegalAccessException e) {
			throw new ConfigurationException("Could not access class (" + className + ").", e);
		}
	}
}