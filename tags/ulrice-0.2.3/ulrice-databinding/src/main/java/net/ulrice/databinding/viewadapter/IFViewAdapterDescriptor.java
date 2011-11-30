package net.ulrice.databinding.viewadapter;




public interface IFViewAdapterDescriptor {
    boolean canHandle (Object viewElement);
    IFViewAdapter createInstance (Object viewElement);
}
