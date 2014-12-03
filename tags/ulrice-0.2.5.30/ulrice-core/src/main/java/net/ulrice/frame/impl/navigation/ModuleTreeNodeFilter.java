package net.ulrice.frame.impl.navigation;

import net.ulrice.module.IFModule;

public interface ModuleTreeNodeFilter {

    boolean accept(IFModule<?> module);
}
