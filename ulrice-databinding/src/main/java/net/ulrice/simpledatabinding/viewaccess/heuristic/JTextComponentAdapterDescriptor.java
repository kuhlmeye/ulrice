package net.ulrice.simpledatabinding.viewaccess.heuristic;

import javax.swing.text.JTextComponent;

import net.ulrice.simpledatabinding.viewaccess.ViewAdapter;
import net.ulrice.simpledatabinding.viewaccess.ViewAdapterDescriptor;
import net.ulrice.simpledatabinding.viewaccess.impl.JTextComponentViewAdapter;



public class JTextComponentAdapterDescriptor implements ViewAdapterDescriptor {
    public boolean canHandle (Object widget) {
        return widget instanceof JTextComponent;
    }

    public ViewAdapter createInstance (Object widget) {
        return new JTextComponentViewAdapter ((JTextComponent) widget);
    }
}
