package net.ulrice.databinding.bufferedbinding;

import net.ulrice.databinding.viewadapter.IFViewAdapter;

@SuppressWarnings("rawtypes")
public class ModelEventAdapter<T> implements IFAttributeModelEventListener<T> {

    @Override
    public void dataChanged(IFViewAdapter viewAdapter, IFAttributeModel<T> amSource) {
    }

    @Override
    public void stateChanged(IFViewAdapter viewAdapter, IFAttributeModel<T> amSource) {
    }

}
