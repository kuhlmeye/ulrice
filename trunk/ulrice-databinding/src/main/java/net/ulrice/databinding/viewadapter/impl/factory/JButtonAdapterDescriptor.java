package net.ulrice.databinding.viewadapter.impl.factory;

import javax.swing.JButton;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.viewadapter.IFViewAdapter;
import net.ulrice.databinding.viewadapter.IFViewAdapterDescriptor;
import net.ulrice.databinding.viewadapter.impl.BorderStateMarker;
import net.ulrice.databinding.viewadapter.impl.DetailedTooltipHandler;
import net.ulrice.databinding.viewadapter.impl.JButtonViewAdapter;



public class JButtonAdapterDescriptor implements IFViewAdapterDescriptor {
    public boolean canHandle (Object widget) {
        return widget instanceof JButton;
    }

    public IFViewAdapter createInstance (Object widget, IFAttributeInfo attributeInfo) {
        
        JButton button = (JButton) widget;
        JButtonViewAdapter viewAdapter = new JButtonViewAdapter(button, attributeInfo);

        DetailedTooltipHandler tooltipHandler = new DetailedTooltipHandler();
        BorderStateMarker stateMarker = new BorderStateMarker(button.getBorder());
    	
		viewAdapter.setTooltipHandler(tooltipHandler);
		viewAdapter.setStateMarker(stateMarker);
		
		button.setBorder(stateMarker);
		
		return viewAdapter;
        
    }
}
