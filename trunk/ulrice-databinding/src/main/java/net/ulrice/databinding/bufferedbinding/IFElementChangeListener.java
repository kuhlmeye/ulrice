/**
 * 
 */
package net.ulrice.databinding.bufferedbinding;

import java.util.EventListener;

/**
 * @author christof
 *
 */
public interface IFElementChangeListener extends EventListener {

	void dataChanged(Element element, String columnId, Object newValue, Object oldValue);
	
	void stateChanged(Element element);
	
}
