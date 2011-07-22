package net.ulrice.databinding.viewadapter.impl.factory;

import javax.swing.text.JTextComponent;

import net.ulrice.databinding.viewadapter.IFViewAdapter;
import net.ulrice.databinding.viewadapter.IFViewAdapterDescriptor;
import net.ulrice.databinding.viewadapter.impl.BorderStateMarker;
import net.ulrice.databinding.viewadapter.impl.DetailedTooltipHandler;
import net.ulrice.databinding.viewadapter.impl.JTextComponentViewAdapter;



public class JTextComponentAdapterDescriptor implements IFViewAdapterDescriptor {
	

	public boolean canHandle (Object widget) {
        return widget instanceof JTextComponent;
    }

    public IFViewAdapter createInstance (Object widget) {
        JTextComponent textComponent = (JTextComponent) widget;
		JTextComponentViewAdapter viewAdapter = new JTextComponentViewAdapter (textComponent);

        DetailedTooltipHandler tooltipHandler = new DetailedTooltipHandler();
        BorderStateMarker stateMarker = new BorderStateMarker();
    	
		viewAdapter.setTooltipHandler(tooltipHandler);
		viewAdapter.setStateMarker(stateMarker);
		
        textComponent.setBorder(stateMarker);
		
		return viewAdapter;
    }
}
