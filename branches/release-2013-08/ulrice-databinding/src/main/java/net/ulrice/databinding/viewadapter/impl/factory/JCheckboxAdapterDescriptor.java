package net.ulrice.databinding.viewadapter.impl.factory;

import javax.swing.JCheckBox;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.viewadapter.IFViewAdapter;
import net.ulrice.databinding.viewadapter.IFViewAdapterDescriptor;
import net.ulrice.databinding.viewadapter.impl.BorderStateMarker;
import net.ulrice.databinding.viewadapter.impl.DetailedTooltipHandler;
import net.ulrice.databinding.viewadapter.impl.JCheckBoxViewAdapter;

public class JCheckboxAdapterDescriptor implements IFViewAdapterDescriptor {
    @Override
    public boolean canHandle(Object widget) {
        return widget instanceof JCheckBox;
    }

    @Override
    public IFViewAdapter createInstance(Object widget, IFAttributeInfo attributeInfo) {
        JCheckBox checkBox = (JCheckBox) widget;
        JCheckBoxViewAdapter viewAdapter = new JCheckBoxViewAdapter(checkBox, attributeInfo);

        DetailedTooltipHandler tooltipHandler = new DetailedTooltipHandler();
        BorderStateMarker stateMarker = new BorderStateMarker(checkBox.getBorder() != null, false, false);

        viewAdapter.setTooltipHandler(tooltipHandler);
        viewAdapter.setStateMarker(stateMarker);

        checkBox.setBorder(stateMarker);

        return viewAdapter;
    }
}
