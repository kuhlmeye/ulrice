package net.ulrice.module.impl.action;

public @interface AppAction {
		
	String actionId();
	boolean initiallyEnabled() default true;

}
