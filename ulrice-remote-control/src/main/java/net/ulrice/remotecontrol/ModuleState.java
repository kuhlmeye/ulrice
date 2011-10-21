package net.ulrice.remotecontrol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.ulrice.module.IFModule;
import net.ulrice.module.IFModuleTitleProvider;

public class ModuleState implements Serializable {

    private static final long serialVersionUID = -7582126644063949125L;

    public static ModuleState inspect(IFModule module) {
        if (module == null) {
            return null;
        }

        return new ModuleState(module);
    }

    public static Collection<ModuleState> inspect(Collection<IFModule> modules) {
        Collection<ModuleState> results = new ArrayList<ModuleState>();

        for (IFModule module : modules) {
            ModuleState state = inspect(module);

            if (state != null) {
                results.add(state);
            }
        }

        return results;
    }

    private final transient IFModule module;

    private final String uniqueId;
    private final Map<IFModuleTitleProvider.Usage, String> titles;

    protected ModuleState(IFModule module) {
        super();

        this.module = module;

        uniqueId = module.getUniqueId();
        titles = new HashMap<IFModuleTitleProvider.Usage, String>();

        for (IFModuleTitleProvider.Usage usage : IFModuleTitleProvider.Usage.values()) {
            titles.put(usage, module.getModuleTitle(usage));
        }
    }

    public IFModule getModule() {
        return module;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public Map<IFModuleTitleProvider.Usage, String> getTitles() {
        return titles;
    }

    @Override
    public String toString() {
        return "ModuleState [uniqueId=" + uniqueId + ", titles=" + titles + "]";
    }

}
