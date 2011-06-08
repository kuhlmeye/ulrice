/**
 * 
 */
package net.ulrice.databinding;

import java.util.EventListener;

/**
 * @author christof
 *
 */
public interface IFAttributeModelEventListener<T> extends EventListener {

    void dataChanged(IFGuiAccessor<?, ?> gaSource, IFAttributeModel<T> amSource, T oldValue, T newValue, DataState state);
    void stateChanged(IFGuiAccessor<?, ?> gaSource, IFAttributeModel<T> amSource, DataState oldState, DataState newState);
	
}
