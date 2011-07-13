/**
 * 
 */
package net.ulrice.databinding.bufferedbinding;

import java.util.EventListener;

import net.ulrice.databinding.DataState;
import net.ulrice.databinding.viewadapter.IFViewAdapter;

/**
 * @author christof
 *
 */
public interface IFAttributeModelEventListener<T> extends EventListener {

    void dataChanged(IFViewAdapter viewAdapter, IFAttributeModel<T> amSource, T oldValue, T newValue, DataState state);
    void stateChanged(IFViewAdapter viewAdapter, IFAttributeModel<T> amSource, DataState oldState, DataState newState);
	
}
