package net.ulrice.simpledatabinding.viewaccess.heuristic;

import javax.swing.JCheckBox;

import net.ulrice.simpledatabinding.viewaccess.ViewAdapter;
import net.ulrice.simpledatabinding.viewaccess.ViewAdapterDescriptor;
import net.ulrice.simpledatabinding.viewaccess.impl.JCheckBoxViewAdapter;



public class JCheckboxAdapterDescriptor implements ViewAdapterDescriptor {
    public boolean canHandle (Object widget) {
        return widget instanceof JCheckBox;
    }

    public ViewAdapter createInstance (Object widget) {
        return new JCheckBoxViewAdapter ((JCheckBox) widget);
    }
}
