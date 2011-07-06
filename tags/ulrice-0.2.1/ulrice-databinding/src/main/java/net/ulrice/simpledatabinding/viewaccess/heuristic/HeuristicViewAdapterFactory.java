package net.ulrice.simpledatabinding.viewaccess.heuristic;

import java.util.ArrayList;
import java.util.List;

import net.ulrice.simpledatabinding.viewaccess.ViewAdapter;
import net.ulrice.simpledatabinding.viewaccess.ViewAdapterDescriptor;
import net.ulrice.simpledatabinding.viewaccess.impl.JComboBoxViewAdapter;




public class HeuristicViewAdapterFactory {
    private static final List<ViewAdapterDescriptor> _descriptors = new ArrayList<ViewAdapterDescriptor> ();
    
    static {
        _descriptors.add (new JTextComponentAdapterDescriptor ());
        _descriptors.add (new JButtonAdapterDescriptor ());
        _descriptors.add (new JCheckboxAdapterDescriptor ());
        _descriptors.add (new JComboBoxAdapterDescriptor ());
    }
    
    public static void register (ViewAdapterDescriptor desc) {
        _descriptors.add (0, desc); // am Anfang, um Spezialisierungen für "eingebaute" Adapter zu erlauben
    }
    
    public static ViewAdapter createAdapter (Object viewElement) {
        for (ViewAdapterDescriptor desc: _descriptors)
            if (desc.canHandle (viewElement))
                return desc.createInstance (viewElement);
        
        throw new IllegalArgumentException ("Kein ViewAdapter für Elementtyp " + viewElement.getClass ().getName () + " gefunden.");
    }

}
