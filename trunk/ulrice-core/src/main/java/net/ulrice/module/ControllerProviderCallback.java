package net.ulrice.module;

import net.ulrice.module.exception.ModuleInstantiationException;


public interface ControllerProviderCallback {
    void onControllerReady (IFController controller);
    void onFailure (ModuleInstantiationException exc);
}
