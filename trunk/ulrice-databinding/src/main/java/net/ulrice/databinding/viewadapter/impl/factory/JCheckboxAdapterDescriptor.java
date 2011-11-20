package net.ulrice.databinding.viewadapter.impl.factory;

import javax.swing.JCheckBox;

import net.ulrice.databinding.viewadapter.IFViewAdapter;
import net.ulrice.databinding.viewadapter.IFViewAdapterDescriptor;
import net.ulrice.databinding.viewadapter.impl.BorderStateMarker;
import net.ulrice.databinding.viewadapter.impl.DetailedTooltipHandler;
import net.ulrice.databinding.viewadapter.impl.JCheckBoxViewAdapter;



public class JCheckboxAdapterDescriptor implements IFViewAdapterDescriptor {
    public boolean canHandle (Object widget) {
        return widget instanceof JCheckBox;
    }

    public IFViewAdapter createInstance (Object widget) {
        JCheckBox checkBox = (JCheckBox) widget;
        JCheckBoxViewAdapter viewAdapter = new JCheckBoxViewAdapter(checkBox);

        DetailedTooltipHandler tooltipHandler = new DetailedTooltipHandler();
        BorderStateMarker stateMarker = new BorderStateMarker(checkBox.getBorder());
    	
		viewAdapter.setTooltipHandler(tooltipHandler);
		viewAdapter.setStateMarker(stateMarker);
		
		checkBox.setBorder(stateMarker);
		
		return viewAdapter;
    }
}
