package net.ulrice.simpledatabinding.viewaccess.heuristic;

import javax.swing.JButton;

import net.ulrice.simpledatabinding.viewaccess.ViewAdapter;
import net.ulrice.simpledatabinding.viewaccess.ViewAdapterDescriptor;
import net.ulrice.simpledatabinding.viewaccess.impl.JButtonViewAdapter;



public class JButtonAdapterDescriptor implements ViewAdapterDescriptor {
    public boolean canHandle (Object widget) {
        return widget instanceof JButton;
    }

    public ViewAdapter createInstance (Object widget) {
        return new JButtonViewAdapter ((JButton) widget);
    }
}
