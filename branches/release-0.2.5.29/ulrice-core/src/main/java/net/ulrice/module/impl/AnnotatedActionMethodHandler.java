package net.ulrice.module.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.ulrice.Ulrice;
import net.ulrice.message.ModuleTranslationProvider;
import net.ulrice.message.TranslationUsage;
import net.ulrice.module.IFController;
import net.ulrice.module.impl.action.AppAction;
import net.ulrice.module.impl.action.ModuleAction;
import net.ulrice.module.impl.action.ModuleDelegationAction;
import net.ulrice.module.impl.action.UlriceAction;

public class AnnotatedActionMethodHandler {

	private Map<String, Method> actionMethods = new HashMap<String, Method>();
	private List<ModuleActionState> handledActions = new ArrayList<ModuleActionState>();
	private IFController controller;

	public AnnotatedActionMethodHandler(IFController controller, ModuleTranslationProvider tp) {
		this.controller = controller;
		
		Method[] methods = controller.getClass().getMethods();
		for(Method method : methods) {
			if(method.getParameterTypes().length == 0 && method.getReturnType().equals(Void.TYPE)) {
				ModuleAction moduleAction = method.getAnnotation(ModuleAction.class);
				if(moduleAction != null) {
					actionMethods.put(moduleAction.actionId(), method);
					
					Icon icon = null;
					if(!"".equals(moduleAction.iconName())) {
						URL resource = controller.getClass().getResource(moduleAction.iconName());
						if(resource != null) {
							icon = new ImageIcon(resource);
						}
					}
					
					String name = moduleAction.name();
					if("".equals(name)) {
						name = tp.getTranslationText(TranslationUsage.Action, moduleAction.actionId());
					}
					
					ModuleDelegationAction ulriceAction = new ModuleDelegationAction(moduleAction.actionId(), name, moduleAction.initiallyEnabled(), icon);

			        int orderIdx = moduleAction.orderIdx();
			        ulriceAction.putValue("SORTIDX", orderIdx);
					ModuleActionState actionState = new ModuleActionState(moduleAction.initiallyEnabled(), ulriceAction);
					handledActions.add(actionState);
				}
				
				AppAction appAction = method.getAnnotation(AppAction.class);
				if(appAction != null) {
					UlriceAction applicationAction = Ulrice.getActionManager().getApplicationAction(appAction.actionId());
					if(applicationAction != null) {
						ModuleActionState actionState = new ModuleActionState(moduleAction.initiallyEnabled(), applicationAction);
						handledActions.add(actionState);
					}
				}
			}
		}

	}

	public List<ModuleActionState> getHandledActions() {
		return handledActions;
	}

	public boolean performModuleAction(String actionId) {
		if(actionMethods.containsKey(actionId)) {
			Method method = actionMethods.get(actionId);
			try {
				method.invoke(controller);
				return true;
			} catch (IllegalAccessException e) {
				Ulrice.getMessageHandler().handleException(e);
			} catch (IllegalArgumentException e) {
				Ulrice.getMessageHandler().handleException(e);
			} catch (InvocationTargetException e) {
				Ulrice.getMessageHandler().handleException(e);
			}
		}
		
		return false;
	}
}
