package net.ulrice.databinding.viewadapter.impl.factory;

import javax.swing.JButton;

import net.ulrice.databinding.viewadapter.IFViewAdapter;
import net.ulrice.databinding.viewadapter.IFViewAdapterDescriptor;
import net.ulrice.databinding.viewadapter.impl.JButtonViewAdapter;



public class JButtonAdapterDescriptor implements IFViewAdapterDescriptor {
    public boolean canHandle (Object widget) {
        return widget instanceof JButton;
    }

    public IFViewAdapter createInstance (Object widget) {
        return new JButtonViewAdapter ((JButton) widget);
    }
}
