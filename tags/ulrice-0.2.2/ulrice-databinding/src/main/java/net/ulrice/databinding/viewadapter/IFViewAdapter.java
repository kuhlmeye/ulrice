package net.ulrice.databinding.viewadapter;

import javax.swing.JComponent;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.converter.IFValueConverter;


public interface IFViewAdapter {

	void updateFromBinding(IFBinding binding);
	
	Object getValue();

    JComponent getComponent();
	
    void addViewChangeListener (IFViewChangeListener l);
    void removeViewChangeListener (IFViewChangeListener l);

	Class<?> getViewType();
	void setBindWithoutValue(boolean withoutData);
    
    void setEnabled (boolean enabled);
    boolean isEnabled();
    
	void setTooltipHandler(IFTooltipHandler tooltipHandler);
	void setStateMarker(IFStateMarker stateMarker);
	void setValueConverter(IFValueConverter converter);
	IFValueConverter getValueConverter();
	boolean isUseAutoValueConverter();
}
