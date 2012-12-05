package net.ulrice.databinding.viewadapter.impl.factory;

import javax.swing.JButton;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.viewadapter.IFViewAdapter;
import net.ulrice.databinding.viewadapter.IFViewAdapterDescriptor;
import net.ulrice.databinding.viewadapter.impl.BorderStateMarker;
import net.ulrice.databinding.viewadapter.impl.DetailedTooltipHandler;
import net.ulrice.databinding.viewadapter.impl.JButtonViewAdapter;

public class JButtonAdapterDescriptor implements IFViewAdapterDescriptor {
    @Override
    public boolean canHandle(Object widget) {
        return widget instanceof JButton;
    }

    @Override
    public IFViewAdapter createInstance(Object widget, IFAttributeInfo attributeInfo) {

        JButton button = (JButton) widget;
        JButtonViewAdapter viewAdapter = new JButtonViewAdapter(button, attributeInfo);

        DetailedTooltipHandler tooltipHandler = new DetailedTooltipHandler();
        BorderStateMarker stateMarker = new BorderStateMarker(button.getBorder() != null, false, false);

        viewAdapter.setTooltipHandler(tooltipHandler);
        viewAdapter.setStateMarker(stateMarker);

        button.setBorder(stateMarker);

        return viewAdapter;

    }
}
