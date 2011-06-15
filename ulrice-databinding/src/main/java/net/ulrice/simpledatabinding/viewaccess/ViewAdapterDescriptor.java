package net.ulrice.simpledatabinding.viewaccess;



public interface ViewAdapterDescriptor {
    boolean canHandle (Object viewElement);
    ViewAdapter createInstance (Object viewElement);
}
