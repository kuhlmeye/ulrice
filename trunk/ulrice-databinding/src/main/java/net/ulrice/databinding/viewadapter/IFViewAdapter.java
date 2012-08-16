package net.ulrice.databinding.viewadapter;

import javax.swing.JComponent;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.converter.IFValueConverter;

/**
 * Interface for all view adapters. A view adapter is used as glue between the swing component
 * and the attribute model. It understands how to connect to the swing component and delivers
 * events from the component to the attribute model and vice versa
 * 
 * @author DL10KUH
 *
 * @param <M> Model class
 * @param <V> View class
 */
public interface IFViewAdapter <M, V> {

	/**
	 * Binds a view adapter to an attribute model
	 */
    void bind(IFBinding binding);
    
    /**
     * Detaches a view adapter from an attribute model
     */
    void detach(IFBinding binding);
    
    /**
     * Called by the binding as notification that something has chaned in the binding.
     */
	void updateFromBinding(IFBinding binding);
	
	/**
	 * Returns the current value from the binding.
	 */
	M getValue();
	
	/**
	 * Returns the raw value that is displayed in the Swing Component.
	 * In most cases this will be a String and will not be converted
	 * to the object that the model needs to have.
	 * @return
	 * The raw value that is displayed in the Swing Component.
	 */
	Object getDisplayedValue();

	/**
	 * Returns the view component this view adapter is connected to.
	 */
    JComponent getComponent();
	
    /**
     * Adds a binding listener.
     */
    void addBindingListener(ViewAdapterBindingListener l);
    
    /**
     * Removed a binding listener
     */
    void removeBindingListener(ViewAdapterBindingListener l);
    
    /**
     * Adds a view change listener
     */
    void addViewChangeListener (IFViewChangeListener l);
    
    /**
     * Removed a view change listener.
     */
    void removeViewChangeListener (IFViewChangeListener l);

    /**
     * Returns the type of the object used by the view.
     */
	Class<V> getViewType();
	
	/**
	 * Bind to the view adapter without data. Used by direct binding.
	 */
	void setBindWithoutValue(boolean withoutData);
    
	/**
	 * Enabled the swing component.
	 */
    void setComponentEnabled (boolean enabled);
    
    /**
     * Returns true, if the swing component is enabled.
     */
    boolean isComponentEnabled();
    
    /**
     * Sets the swing component to editable state.
     */
    void setEditable(boolean editable);
    
    /**
     * Returns, if the swing component is editable.
     */
    boolean isEditable();
    
    /**
     * Sets the class that handles the tooltip of this binding.
     */
	void setTooltipHandler(IFTooltipHandler tooltipHandler);
	
	/**
	 * Sets the object that marks the state of the binding.
	 */
	void setStateMarker(IFStateMarker stateMarker);
	
	/**
	 * Sets a value converter that converts the value between model and view.
	 */
	void setValueConverter(IFValueConverter<M, V> converter);
	
	/**
	 * Returns the value converter of this view adapter
	 */
	IFValueConverter<M, V> getValueConverter();
	
	/**
	 * Returns true if auto conversion should be used.
	 */
	boolean isUseAutoValueConverter();
	
	/**
	 * Returns the object which contains some information about the attribute
	 */
    IFAttributeInfo getAttributeInfo();
}
