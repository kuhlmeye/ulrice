package net.ulrice.module.impl;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import net.ulrice.module.ControllerProviderCallback;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModule;
import net.ulrice.module.IFModuleTitleProvider;
import net.ulrice.module.ModuleIconSize;
import net.ulrice.module.ModuleType;
import net.ulrice.module.exception.ModuleInstantiationException;

/**
 * Implementation of a module instanciated by reflection.
 * 
 * @author ckuhlmeyer
 */
public class ReflectionModule<T extends IFController> implements IFModule<T> {

	/** The unique id of the reflection module. */
	private String uniqueId;

	/** The module title renderer used by this module. */
	private IFModuleTitleProvider titleRenderer;

	/** The class name of the controller. */
	private String controllerClassName;

	/** Type of the module. */
	private ModuleType moduleType;

	/** A small icon for this module. */
	private ImageIcon smallIcon;

	/** A medium icon for this module. */
	private ImageIcon mediumIcon;
	
	private Map<String, Object> parameterMap;

	/**
	 * Creates a new reflection module. It takes the unique id as the module title.
	 * 
	 * @param uniqueId The unique id of the module
	 * @param controllerClassName The name of the controller class that should be instanciated.
	 * @param titleRenderer The renderer used to render the title of this module.
	 */
	public ReflectionModule(String uniqueId, ModuleType moduleType, String controllerClassName, String iconName) {
		this(uniqueId, moduleType, controllerClassName, iconName, new SimpleModuleTitleRenderer(uniqueId));
	}

	/**
	 * Creates a new reflection module.
	 * 
	 * @param uniqueId The unique id of the module
	 * @param controllerClassName The name of the controller class that should be instanciated.
	 * @param titleRenderer The renderer used to render the title of this module.
	 */
	public ReflectionModule(String uniqueId, ModuleType moduleType, String controllerClassName, String iconName,
			IFModuleTitleProvider titleRenderer) {
		super();
		this.uniqueId = uniqueId;
		this.moduleType = moduleType;
		this.controllerClassName = controllerClassName;
		this.titleRenderer = titleRenderer;
		this.smallIcon = findIcon(iconName, ModuleIconSize.Size_16x16);
		this.mediumIcon = findIcon(iconName, ModuleIconSize.Size_32x32);
		this.parameterMap = new HashMap<String, Object>();
	}

	/**
	 * Finds an icon by icon size. Tries to find the best fitting size.
	 * 
	 * @param iconName The name of the icon
	 * @param size The preferred size of the icon.
	 * @return The icon or null, if no icon could be found.
	 */
	private ImageIcon findIcon(String iconName, ModuleIconSize size) {
		ImageIcon icon = null;
		switch (size) {
		case Size_32x32:
			icon = getIcon(iconName, ModuleIconSize.Size_32x32);
			if (icon == null) {
				icon = getIcon(iconName, ModuleIconSize.Size_16x16);
			}
			break;
		case Size_16x16:
			icon = getIcon(iconName, ModuleIconSize.Size_16x16);
			break;
		}
		if (icon != null) {
			return icon;
		}
		return getIcon(iconName, null);
	}

	private ImageIcon getIcon(String iconName, ModuleIconSize size) {
		if(iconName == null) {
			return null;
		}
		
		int extStartPosition = iconName.lastIndexOf('.');
		String extension = iconName.substring(extStartPosition);
		String name = iconName.substring(0, extStartPosition);

		StringBuilder builder = new StringBuilder();
		builder.append("/");
		builder.append(name);
		if (size != null) {
			switch (size) {
			case Size_16x16:
				builder.append("16");
				break;
			case Size_32x32:
				builder.append("32");
				break;
			}
		}
		builder.append(extension);

		String iconPath = builder.toString();
		URL location = ReflectionModule.class.getResource(iconPath);
		if (location == null) {
			return null;
		}
		return new ImageIcon(location);
	}

	
	@Override
	public void instantiateModule(ControllerProviderCallback<T> callback, IFController parent) {
	    try {
	        callback.onControllerReady (instantiateModuleInternal());
	    }
	    catch (ModuleInstantiationException exc) {
	        callback.onFailure(exc);
	    }
	}
	
	private T instantiateModuleInternal() throws ModuleInstantiationException {
		if (controllerClassName == null) {
			throw new ModuleInstantiationException("Controller class name is null.", null);
		}

		try {
			@SuppressWarnings("unchecked")
			Class<? extends IFController> controllerClass = (Class<? extends IFController>) Class.forName(controllerClassName);
			@SuppressWarnings("unchecked")
			T controller = (T) controllerClass.newInstance();
			return controller;
		} catch (ClassNotFoundException e) {
			throw new ModuleInstantiationException("Controller class (" + controllerClassName + ") could not be found.", e);
		} catch (InstantiationException e) {
			throw new ModuleInstantiationException("Could not instanciate controller class (" + controllerClassName	+ ").", e);
		} catch (IllegalAccessException e) {
			throw new ModuleInstantiationException("Could not access controller class (" + controllerClassName + ").", e);
		}
	}

	/**
	 * @see net.ulrice.module.IFModule#getUniqueId()
	 */
	public String getUniqueId() {
		return uniqueId;
	}

	/**
	 * @see net.ulrice.module.IFModule#getModuleInstanceType()
	 */
	public ModuleType getModuleInstanceType() {
		return moduleType;
	}

	/**
	 * @return the controllerClassName
	 */
	public String getControllerClassName() {
		return controllerClassName;
	}

	/**
	 * @see net.ulrice.module.IFModule#getIcon(net.ulrice.module.ModuleIconSize)
	 */
	public ImageIcon getIcon(ModuleIconSize preferredSize) {
		switch (preferredSize) {
		case Size_16x16:
			return smallIcon;
		case Size_32x32:
			return mediumIcon;
		}
		return null;
	}

	/**
	 * @see net.ulrice.module.IFModuleTitleProvider#getModuleTitle(net.ulrice.module.IFModuleTitleProvider.Usage)
	 */
	public String getModuleTitle(Usage usage) {
		if (getTitleRenderer() != null) {
			return getTitleRenderer().getModuleTitle(usage);
		}
		return "";
	}

	/**
	 * @return the titleRenderer
	 */
	public IFModuleTitleProvider getTitleRenderer() {
		return titleRenderer;
	}

	/**
	 * @param titleRenderer the titleRenderer to set
	 */
	public void setTitleRenderer(IFModuleTitleProvider titleRenderer) {
		this.titleRenderer = titleRenderer;
	}
	
	public void putParameter(String key, Object value) {
		parameterMap.put(key, value);
	}
	
	public Object getParameter(String key) {
		return parameterMap.get(key);
	}
}
