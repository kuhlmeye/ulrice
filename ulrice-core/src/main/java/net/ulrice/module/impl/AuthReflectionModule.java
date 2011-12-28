package net.ulrice.module.impl;

import net.ulrice.module.IFModuleTitleProvider;
import net.ulrice.module.ModuleType;
import net.ulrice.security.Authorization;

/**
 * Extended reflection module handling default authentication
 * 
 * @author ckuhlmeyer
 */
public class AuthReflectionModule extends ReflectionModule {


	private Authorization authorization;
	
	/**
	 * Creates a new reflection module. It takes the unique id as the module title.
	 * 
	 * @param uniqueId The unique id of the module
	 * @param controllerClassName The name of the controller class that should be instanciated.
	 * @param titleRenderer The renderer used to render the title of this module.
	 */
	public AuthReflectionModule(String uniqueId, ModuleType moduleType, String controllerClassName, String iconName) {
		this(uniqueId, moduleType, controllerClassName, iconName, new SimpleModuleTitleRenderer(uniqueId));
	}

	/**
	 * Creates a new reflection module.
	 * 
	 * @param uniqueId The unique id of the module
	 * @param controllerClassName The name of the controller class that should be instanciated.
	 * @param titleRenderer The renderer used to render the title of this module.
	 */
	public AuthReflectionModule(String uniqueId, ModuleType moduleType, String controllerClassName, String iconName,
			IFModuleTitleProvider titleRenderer) {
		super(uniqueId, moduleType, controllerClassName, iconName, titleRenderer);
	}

	/**
	 * Returns the authorization object.
	 * 
	 * @return The authorization
	 */
	public Authorization getAuthorization() {
		return authorization;
	}
	
	/**
	 * Sets the authorization object.
	 * 
	 * @param authorization The authorization object.
	 */
	public void setAuthorization(Authorization authorization) {
		this.authorization = authorization;
	}
}
