package net.ulrice.databinding.viewadapter;

import javax.swing.JComponent;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.converter.IFValueConverter;


public interface IFViewAdapter <M, V> {

    void bind(IFBinding binding);
    void detach(IFBinding binding);
	void updateFromBinding(IFBinding binding);
	
	M getValue();

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
    
	void setTooltipHandler(IFTooltipHandler<IFBinding> tooltipHandler);
	void setStateMarker(IFStateMarker stateMarker);
	void setValueConverter(IFValueConverter<M, V> converter);
	IFValueConverter<M, V> getValueConverter();
	boolean isUseAutoValueConverter();
}
