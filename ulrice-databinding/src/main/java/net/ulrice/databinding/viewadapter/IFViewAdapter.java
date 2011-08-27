package net.ulrice.databinding.viewadapter;

import javax.swing.JComponent;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.converter.IFValueConverter;


public interface IFViewAdapter <M, V> {

	void updateFromBinding(IFBinding binding);
	
	M getValue();

    JComponent getComponent();
	
    void addViewChangeListener (IFViewChangeListener l);
    void removeViewChangeListener (IFViewChangeListener l);

	Class<V> getViewType();
	void setBindWithoutValue(boolean withoutData);
    
    void setEnabled (boolean enabled);
    boolean isEnabled();
    
	void setTooltipHandler(IFTooltipHandler<IFBinding> tooltipHandler);
	void setStateMarker(IFStateMarker stateMarker);
	void setValueConverter(IFValueConverter<M, V> converter);
	IFValueConverter<M, V> getValueConverter();
	boolean isUseAutoValueConverter();
}
