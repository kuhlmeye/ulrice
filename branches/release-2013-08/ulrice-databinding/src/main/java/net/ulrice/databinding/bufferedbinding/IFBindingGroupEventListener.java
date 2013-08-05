package net.ulrice.databinding.bufferedbinding;

import java.util.EventListener;

/**
 * 
 * @author apunahassaphemapetilon@hotmail.com
 *
 */
public interface IFBindingGroupEventListener extends EventListener {
	
	void bindingGroupChanged(IFBindingGroup bindingGroup);
	
	void bindingGroupDataChanged(IFBindingGroup bindingGroup, String attributeModelId);

}
