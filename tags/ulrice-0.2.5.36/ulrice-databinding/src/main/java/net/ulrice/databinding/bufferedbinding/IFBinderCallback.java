package net.ulrice.databinding.bufferedbinding;

import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.text.JTextComponent;

import net.ulrice.databinding.bufferedbinding.impl.BindingGroup;
import net.ulrice.databinding.viewadapter.impl.JSpinnerViewAdapter;
import net.ulrice.databinding.viewadapter.impl.JTextComponentViewAdapter;
import net.ulrice.databinding.viewadapter.impl.UListViewAdapter;
import net.ulrice.databinding.viewadapter.utable.UTableComponent;
import net.ulrice.databinding.viewadapter.utable.UTableViewAdapter;

public interface IFBinderCallback {

    void adaptBinding(BindingGroup bindingGroup, JList component, UListViewAdapter viewAdapter, IFAttributeModel< ?> model);
    void adaptBinding(BindingGroup bindingGroup, UTableComponent component, UTableViewAdapter viewAdapter, IFAttributeModel< ?> model);
    void adaptBinding(BindingGroup bindingGroup, JTextComponent component, JTextComponentViewAdapter viewAdapter, IFAttributeModel< ?> model);
    void adaptBinding(BindingGroup bindingGroup, JSpinner component, JSpinnerViewAdapter viewAdapter, IFAttributeModel< ?> model);

}
