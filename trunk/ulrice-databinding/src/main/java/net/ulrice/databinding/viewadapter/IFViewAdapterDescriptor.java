package net.ulrice.databinding.viewadapter;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;




public interface IFViewAdapterDescriptor {
    boolean canHandle (Object viewElement);
    IFViewAdapter createInstance (Object viewElement, IFAttributeInfo attributeInfo);
}
