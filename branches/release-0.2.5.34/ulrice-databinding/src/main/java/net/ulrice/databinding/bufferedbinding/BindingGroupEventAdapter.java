package net.ulrice.databinding.bufferedbinding;

/**
 * Adapter for IFBindingGroupEventListener
 * 
 * @author XHU
 */
public abstract class BindingGroupEventAdapter implements IFBindingGroupEventListener {

    @Override
    public void bindingGroupChanged(IFBindingGroup bindingGroup) {
    }

    @Override
    public void bindingGroupDataChanged(IFBindingGroup bindingGroup, String attributeModelId) {
    }

}
