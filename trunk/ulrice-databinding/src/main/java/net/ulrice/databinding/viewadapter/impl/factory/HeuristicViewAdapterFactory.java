package net.ulrice.databinding.viewadapter.impl.factory;

import java.util.ArrayList;
import java.util.List;

import net.ulrice.databinding.viewadapter.IFViewAdapter;
import net.ulrice.databinding.viewadapter.IFViewAdapterDescriptor;




public class HeuristicViewAdapterFactory {
    private static final List<IFViewAdapterDescriptor> _descriptors = new ArrayList<IFViewAdapterDescriptor> ();
    
    static {
        _descriptors.add (new JTextComponentAdapterDescriptor ());
        _descriptors.add (new JButtonAdapterDescriptor ());
        _descriptors.add (new JCheckboxAdapterDescriptor ());
        _descriptors.add (new JComboBoxAdapterDescriptor ());
    }
    
    public static void register (IFViewAdapterDescriptor desc) {
        _descriptors.add (0, desc); // am Anfang, um Spezialisierungen für "eingebaute" Adapter zu erlauben
    }
    
    public static IFViewAdapter createAdapter (Object viewElement) {
        for (IFViewAdapterDescriptor desc: _descriptors)
            if (desc.canHandle (viewElement))
                return desc.createInstance (viewElement);
        
        throw new IllegalArgumentException ("Kein ViewAdapter für Elementtyp " + viewElement.getClass ().getName () + " gefunden.");
    }

}
