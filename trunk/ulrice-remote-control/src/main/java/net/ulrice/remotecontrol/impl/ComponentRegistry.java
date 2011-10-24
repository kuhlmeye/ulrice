package net.ulrice.remotecontrol.impl;

import java.awt.Component;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import net.ulrice.remotecontrol.ComponentState;

/**
 * Registry for all components to create unique ids for those components. Uses a weak hash map for components
 * 
 * @author Manfred HANTSCHEL
 */
public class ComponentRegistry {

    private static final Map<Component, Long> UNIQUE_IDS = Collections
        .synchronizedMap(new WeakHashMap<Component, Long>());

    private static long currentId = (long) (Math.random() * Long.MAX_VALUE);

    /**
     * Registers a component (if it is not yet registered) and returns the unique id.
     * 
     * @param component the component
     * @return the unique id
     */
    public static Long register(Component component) {
        synchronized (UNIQUE_IDS) {
            Long result = UNIQUE_IDS.get(component);

            if (result != null) {
                return result;
            }

            result = Long.valueOf(currentId++);

            UNIQUE_IDS.put(component, result);

            return result;
        }
    }

    /**
     * Registers the component of the specified component state
     * 
     * @param state the state
     * @return the state parameter
     */
    public static ComponentState register(ComponentState state) {
        state.setUnqiueId(register(state.getComponent()));

        if (state.getLabelFor() != null) {
            register(state.getLabelFor());
        }

        register(state.getChilds());

        return state;
    }

    /**
     * Registers all components of all states
     * 
     * @param states the states
     * @return the states parameter
     */
    public static Collection<ComponentState> register(Collection<ComponentState> states) {
        for (ComponentState state : states) {
            register(state);
        }

        return states;
    }

    /**
     * Returns the unique id for the specified component or null, if it has not been registered
     * 
     * @param component the component
     * @return the unique id or null if component is not registered
     */
    public static Long getUnqiueId(Component component) {
        return UNIQUE_IDS.get(component);
    }

    /**
     * Returns the component for the specified unique id, or null if not found
     * 
     * @param uniqueId the unique id
     * @return the component, null if not found
     */
    public static Component getComponent(Long uniqueId) {
        synchronized (UNIQUE_IDS) {
            Iterator<Entry<Component, Long>> it = UNIQUE_IDS.entrySet().iterator();

            while (it.hasNext()) {
                Entry<Component, Long> entry = it.next();

                if (uniqueId.equals(entry.getValue())) {
                    return entry.getKey();
                }
            }
        }

        return null;
    }

}
