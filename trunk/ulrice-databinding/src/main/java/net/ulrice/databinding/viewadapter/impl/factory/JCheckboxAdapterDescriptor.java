package net.ulrice.databinding.viewadapter.impl.factory;

import javax.swing.JCheckBox;

import net.ulrice.databinding.viewadapter.IFViewAdapter;
import net.ulrice.databinding.viewadapter.IFViewAdapterDescriptor;
import net.ulrice.databinding.viewadapter.impl.JCheckBoxViewAdapter;



public class JCheckboxAdapterDescriptor implements IFViewAdapterDescriptor {
    public boolean canHandle (Object widget) {
        return widget instanceof JCheckBox;
    }

    public IFViewAdapter createInstance (Object widget) {
        return new JCheckBoxViewAdapter ((JCheckBox) widget);
    }
}
