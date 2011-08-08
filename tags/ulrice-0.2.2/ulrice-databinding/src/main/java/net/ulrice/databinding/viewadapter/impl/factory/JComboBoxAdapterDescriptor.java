package net.ulrice.databinding.viewadapter.impl.factory;

import javax.swing.JComboBox;

import net.ulrice.databinding.viewadapter.IFViewAdapter;
import net.ulrice.databinding.viewadapter.IFViewAdapterDescriptor;
import net.ulrice.databinding.viewadapter.impl.BorderStateMarker;
import net.ulrice.databinding.viewadapter.impl.DetailedTooltipHandler;
import net.ulrice.databinding.viewadapter.impl.JComboBoxViewAdapter;


public class JComboBoxAdapterDescriptor implements IFViewAdapterDescriptor {

	@Override
	public boolean canHandle(Object viewElement) {
		return viewElement instanceof JComboBox; //TODO Items sind ObjectWithPresentation
	}

	@Override
	public IFViewAdapter createInstance(Object viewElement) {
		JComboBox comboBox = (JComboBox) viewElement;
		JComboBoxViewAdapter viewAdapter = new JComboBoxViewAdapter(comboBox);

        DetailedTooltipHandler tooltipHandler = new DetailedTooltipHandler();
        BorderStateMarker stateMarker = new BorderStateMarker();
    	
		viewAdapter.setTooltipHandler(tooltipHandler);
		viewAdapter.setStateMarker(stateMarker);
		
		comboBox.setBorder(stateMarker);
		
		return viewAdapter;
	}
}
