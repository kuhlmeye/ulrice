package net.ulrice.module.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ulrice.Ulrice;
import net.ulrice.message.MessageSeverity;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModel;
import net.ulrice.module.IFModule;
import net.ulrice.module.IFModuleTitleRenderer;
import net.ulrice.module.IFView;

/**
 * Creates an abstract controller.
 * 
 * @author ckuhlmeyer
 */
public abstract class AbstractController<T extends IFModel, U extends IFView> implements IFController {

	/** The module this controller is belonging to. */
	private IFModule module;

	/** The view of this controller. */
	private U view;

	/** The model used by this controller. */
	private T model;
	
	/** The set of all module actions that are handled by this controller. */
	private List<ModuleActionState> moduleActionStates;
	
	private Set<Object> blockSet = new HashSet<Object>();
	
	@Override
	public boolean isBlocked() {
		return !blockSet.isEmpty();
	}
	/**
	 * Creates an abstract controller.
	 */
	public AbstractController() {
		super();
	}

	/**
	 * @see net.ulrice.module.IFController#getModel()
	 */
	public T getModel() {
		if (this.model == null) {
			this.model = instanciateModel();
		}
		return model;
	}

	/**
	 * Instanciate the model. This method is called once during the
	 * instanciation of the model.
	 * 
	 * @return The instance of the model.
	 */
	protected abstract T instanciateModel();

	/**
	 * @see net.ulrice.module.IFController#getModule()
	 */
	public IFModule getModule() {
		return module;
	}

	/**
	 * @see net.ulrice.module.IFController#getModuleTitleRenderer()
	 */
	public IFModuleTitleRenderer getModuleTitleRenderer() {
		return getModule();
	}

	/**
	 * @see net.ulrice.module.IFController#getView()
	 */
	public U getView() {
		if (this.view == null) {
			this.view = instanciateView();
		}
		return view;
	}

	/**
	 * Instanciate the view of this controller. This method is called once
	 * during the instanciation of the module
	 * 
	 * @return The instance of the view.
	 */
	protected abstract U instanciateView();

	/**
	 * Initializes the sub-components.
	 * <ol>
	 * <li>Initialization of model.</li>
	 * <li>Initialization of view.</li>
	 * <li>Initialization of controller.</li>
	 * </ol>
	 * 
	 * @see net.ulrice.module.IFController#postCreation(net.ulrice.module.IFModule)
	 */	
	public void preCreationEvent(IFModule module) {
		getModel().initialize(this);
		getView().initialize(this);
		this.module = module;

		ModuleActionState[] moduleActionStateArray = getHandledActions();
		if(moduleActionStateArray != null) {
			moduleActionStates = new ArrayList<ModuleActionState>(moduleActionStateArray.length);
			for(ModuleActionState moduleActionState : moduleActionStateArray) {
				moduleActionStates.add(moduleActionState);
			}
		}
		
		preEventInitialization();
	}

	/**
	 * Define the actions that are handled by this module.
	 * 
	 * @return An array of module action state objects.
	 */
	protected ModuleActionState[] getHandledActions() {
		return new ModuleActionState[0];
	}

	/**
	 * @see net.ulrice.module.IFController#postCreationEvent(net.ulrice.module.IFModule)
	 */
	public void postCreationEvent(IFModule module) {
		postEventInitialization();
	}

	/**
	 * Initialize the controller. In this method the basic initialization of the
	 * module should take place.
	 */
	protected void preEventInitialization() {
		// Empty default implementation
	}

	/**
	 * Initialize the controller. In this method the module is fully known by
	 * ulrice. So in this method data the usage of ulrice-components is allowed
	 * (message handler,...)
	 */
	protected void postEventInitialization() {
		// Empty default implementation
	}

	/**
	 * Post an exception to the exception and message handler.
	 *  
	 * @param th The exception.
	 */
	public void postException(Throwable th) {
		Ulrice.getMessageHandler().handleException(this, th);
	}

	/**
	 * Post an exception to the exception and message handler with additional information
	 * 
	 * @param message The additional message.
	 * @param th The exception.
	 */
	public void postException(String message, Throwable th) {
		Ulrice.getMessageHandler().handleException(this, message, th);
	}

	/**
	 * Post an informational message to the message handler.
	 * 
	 * @param message the message.
	 */
	public void postInfoMessage(String message) {
		Ulrice.getMessageHandler().handleInformationMessage(this, message);		
	}
	
	/**
	 * Post a  message to the message handler.
	 * 
	 * @param severity The severity of the message.
	 * @param message the message.
	 */
	public void postMessage(MessageSeverity severity, String message) {
		Ulrice.getMessageHandler().handleMessage(this, severity, message);
	}

	/**
	 * @see net.ulrice.module.IFController#getModuleActionStates()
	 */
	@Override
	public List<ModuleActionState> getModuleActionStates() {
		return moduleActionStates;
	}

	/**
	 * @see net.ulrice.module.IFController#performModuleAction(java.lang.String)
	 */
	@Override
	public boolean performModuleAction(String actionId) {
		return false;
	}
	

	@Override
	public void block(Object blocker) {
		if(blockSet.isEmpty()) {
			Ulrice.getModuleManager().fireControllerBlocked(this);
		}
		blockSet.add(blocker);
	}

	@Override
	public void unblock(Object blocker) {
		blockSet.remove(blocker);
		if(blockSet.isEmpty()) {
			Ulrice.getModuleManager().fireControllerUnblocked(this);
		}
	}
}
