/**
 * 
 */
package net.ulrice.databinding.bufferedbinding;

import java.util.EventListener;

import net.ulrice.databinding.bufferedbinding.impl.Element;

/**
 * @author christof
 *
 */
public interface IFElementChangeListener extends EventListener {

	void dataChanged(Element element, String columnId, Object newValue, Object oldValue);
	
	void stateChanged(Element element);
	
}
