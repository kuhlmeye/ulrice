package net.ulrice.databinding.viewadapter.impl.factory;

import javax.swing.text.JTextComponent;

import net.ulrice.databinding.viewadapter.IFViewAdapter;
import net.ulrice.databinding.viewadapter.IFViewAdapterDescriptor;
import net.ulrice.databinding.viewadapter.impl.JTextComponentViewAdapter;



public class JTextComponentAdapterDescriptor implements IFViewAdapterDescriptor {
    public boolean canHandle (Object widget) {
        return widget instanceof JTextComponent;
    }

    public IFViewAdapter createInstance (Object widget) {
        return new JTextComponentViewAdapter ((JTextComponent) widget);
    }
}
