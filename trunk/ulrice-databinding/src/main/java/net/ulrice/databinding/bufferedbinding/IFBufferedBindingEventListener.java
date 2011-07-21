/**
 * 
 */
package net.ulrice.databinding.bufferedbinding;

import java.util.EventListener;

import net.ulrice.databinding.viewadapter.IFViewAdapter;

/**
 * @author christof
 *
 */
public interface IFBufferedBindingEventListener<T> extends EventListener {

    void dataChanged(IFViewAdapter viewAdapter, IFBufferedBinding<T> amSource);
    void stateChanged(IFViewAdapter viewAdapter, IFBufferedBinding<T> amSource);
	
}
