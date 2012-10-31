package net.ulrice.databinding.viewadapter.impl.factory;

import javax.swing.text.JTextComponent;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.viewadapter.IFViewAdapter;
import net.ulrice.databinding.viewadapter.IFViewAdapterDescriptor;
import net.ulrice.databinding.viewadapter.impl.BorderStateMarker;
import net.ulrice.databinding.viewadapter.impl.DetailedTooltipHandler;
import net.ulrice.databinding.viewadapter.impl.JTextComponentViewAdapter;

public class JTextComponentAdapterDescriptor implements IFViewAdapterDescriptor {

    @Override
    public boolean canHandle(Object widget) {
        return widget instanceof JTextComponent;
    }

    @Override
    public IFViewAdapter createInstance(Object widget, IFAttributeInfo attributeInfo) {
        JTextComponent textComponent = (JTextComponent) widget;
        JTextComponentViewAdapter viewAdapter = new JTextComponentViewAdapter(textComponent, attributeInfo);

        DetailedTooltipHandler tooltipHandler = new DetailedTooltipHandler();
        BorderStateMarker stateMarker = new BorderStateMarker(textComponent.getBorder() != null, false, false);

        viewAdapter.setTooltipHandler(tooltipHandler);
        viewAdapter.setStateMarker(stateMarker);

        textComponent.setBorder(stateMarker);

        return viewAdapter;
    }
}
