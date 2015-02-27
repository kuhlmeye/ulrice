package net.ulrice.remotecontrol;

import java.io.Serializable;
import java.util.*;

import net.ulrice.module.IFModule;
import net.ulrice.module.IFModuleTitleProvider;

/**
 * Represents the state of a module
 * 
 * @author Manfred HANTSCHEL
 */
public class ModuleState implements Serializable {

    private static final long serialVersionUID = -7582126644063949125L;

    /**
     * Creates a module state, null if specified module is null
     * 
     * @param module the module
     * @return the state
     */
    public static ModuleState inspect(IFModule<?> module) {
        if (module == null) {
            return null;
        }

        return new ModuleState(module);
    }

    /**
     * Returns a collection of states of all specified modules. Null entries are ignored.
     * 
     * @param modules the modules
     * @return a collection of module states
     */
    public static Collection<ModuleState> inspectModules(Collection<IFModule<?>> modules) {
        Collection<ModuleState> results = new ArrayList<>();

        for (IFModule<?> module : modules) {
            ModuleState state = inspect(module);

            if (state != null) {
                results.add(state);
            }
        }

        return results;
    }

    private final transient IFModule<?> module;

    private final String uniqueId;
    private final Map<IFModuleTitleProvider.Usage, String> titles;

    protected ModuleState(IFModule<?> module) {
        super();

        this.module = module;

        uniqueId = module.getUniqueId();
        titles = new EnumMap<>(IFModuleTitleProvider.Usage.class);

        for (IFModuleTitleProvider.Usage usage : IFModuleTitleProvider.Usage.values()) {
            titles.put(usage, module.getModuleTitle(usage));
        }
    }

    public IFModule<?> getModule() {
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
        return "ModuleState [uniqueId=" + uniqueId + ", titles=" + titles + ']';
    }

}
