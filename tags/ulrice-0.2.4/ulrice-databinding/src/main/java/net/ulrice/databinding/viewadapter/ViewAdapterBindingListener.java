package net.ulrice.databinding.viewadapter;

import java.util.EventListener;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.IFAttributeModel;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;

public interface ViewAdapterBindingListener extends EventListener {

    void attributeModelBound(IFViewAdapter viewAdapter, IFBinding binding);

    void attributeModelDetached(IFViewAdapter viewAdapter, IFBinding binding);

}
