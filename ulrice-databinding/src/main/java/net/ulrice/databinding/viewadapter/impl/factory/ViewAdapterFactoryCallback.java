package net.ulrice.databinding.viewadapter.impl.factory;

import net.ulrice.databinding.viewadapter.IFViewAdapter;
import net.ulrice.databinding.viewadapter.impl.JCheckBoxViewAdapter;
import net.ulrice.databinding.viewadapter.impl.JComboBoxViewAdapter;
import net.ulrice.databinding.viewadapter.impl.JTableViewAdapter;
import net.ulrice.databinding.viewadapter.impl.JTextComponentViewAdapter;
import net.ulrice.databinding.viewadapter.impl.PresentationProvider;

public interface ViewAdapterFactoryCallback {

    void setDefaultStateMarker(IFViewAdapter< ?, ?> viewAdapter);

    void setDefaultTooltipHandler(IFViewAdapter< ?, ?> viewAdapter);

    JTextComponentViewAdapter createTextFieldAdapter();

    JTableViewAdapter createTableViewAdapter();

    <M> JComboBoxViewAdapter createComboBoxAdapter(PresentationProvider<M> presentationProvider);
    
    JCheckBoxViewAdapter createCheckBoxAdapter();

}
