package net.ulrice.databinding.viewadapter;

import javax.swing.JComponent;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.converter.IFValueConverter;


public interface IFViewAdapter <M, V> {

    void bind(IFBinding binding);
    void detach(IFBinding binding);
	void updateFromBinding(IFBinding binding);
	
	M getValue();
	
	/**
	 * Returns the raw value that is displayed in the Swing Component.
	 * In most cases this will be a String and will not be converted
	 * to the object that the model needs to have.
	 * @return
	 * The raw value that is displayed in the Swing Component.
	 */
	Object getDisplayedValue();

    JComponent getComponent();
	
    void addBindingListener(ViewAdapterBindingListener l);
    void removeBindingListener(ViewAdapterBindingListener l);
    
    void addViewChangeListener (IFViewChangeListener l);
    void removeViewChangeListener (IFViewChangeListener l);

	Class<V> getViewType();
	void setBindWithoutValue(boolean withoutData);
    
    void setComponentEnabled (boolean enabled);
    boolean isComponentEnabled();
    
    void setEditable(boolean editable);
    boolean isEditable();
    
	void setTooltipHandler(IFTooltipHandler tooltipHandler);
	void setStateMarker(IFStateMarker stateMarker);
	void setValueConverter(IFValueConverter<M, V> converter);
	IFValueConverter<M, V> getValueConverter();
	boolean isUseAutoValueConverter();
}
