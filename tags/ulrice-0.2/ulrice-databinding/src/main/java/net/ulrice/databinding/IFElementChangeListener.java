/**
 * 
 */
package net.ulrice.databinding;

import java.util.EventListener;

import net.ulrice.databinding.impl.am.Element;

/**
 * @author christof
 *
 */
public interface IFElementChangeListener<T> extends EventListener {

	void dataChanged(Element<T> element, String columnId, Object newValue, Object oldValue);
	
	void stateChanged(Element<T> element, DataState newState, DataState oldState);
	
}
