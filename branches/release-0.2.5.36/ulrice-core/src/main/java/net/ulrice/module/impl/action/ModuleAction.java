package net.ulrice.module.impl.action;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModuleAction {

	String actionId();
	String name() default "";
	String iconName() default "";
	boolean initiallyEnabled() default true;
	int orderIdx() default 0;
}
