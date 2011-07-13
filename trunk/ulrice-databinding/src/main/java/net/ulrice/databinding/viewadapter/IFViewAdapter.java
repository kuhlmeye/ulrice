package net.ulrice.databinding.viewadapter;

import javax.swing.JComponent;

import net.ulrice.databinding.IFBinding;


public interface IFViewAdapter {

	void updateBinding(IFBinding binding);
	
	Object getValue();

    JComponent getComponent();
    
    void setEnabled (boolean enabled);
    boolean isEnabled();
	void setTooltipHandler(IFTooltipHandler tooltipHandler);
	void setStateMarker(IFStateMarker stateMarker);
	
    void addViewChangeListener (IFViewChangeListener l);
    void removeViewChangeListener (IFViewChangeListener l);

	Class<?> getViewType();
	void setBindWithoutValue(boolean withoutData);
}
