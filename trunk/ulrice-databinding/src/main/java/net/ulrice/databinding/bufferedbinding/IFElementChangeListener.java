/**
 * 
 */
package net.ulrice.databinding.bufferedbinding;

import java.util.EventListener;

import net.ulrice.databinding.DataState;

/**
 * @author christof
 *
 */
public interface IFElementChangeListener<T> extends EventListener {

	void dataChanged(Element<T> element, String columnId, Object newValue, Object oldValue);
	
	void stateChanged(Element<T> element, DataState newState, DataState oldState);
	
}
