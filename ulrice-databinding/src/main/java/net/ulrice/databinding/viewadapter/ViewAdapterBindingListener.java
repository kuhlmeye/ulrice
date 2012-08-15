package net.ulrice.databinding.viewadapter;

import java.util.EventListener;

import net.ulrice.databinding.IFBinding;

public interface ViewAdapterBindingListener extends EventListener {

    void attributeModelBound(IFViewAdapter viewAdapter, IFBinding binding);

    void attributeModelDetached(IFViewAdapter viewAdapter, IFBinding binding);

}
