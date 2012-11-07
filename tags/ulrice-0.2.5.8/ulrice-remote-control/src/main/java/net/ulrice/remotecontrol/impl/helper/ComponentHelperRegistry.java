package net.ulrice.remotecontrol.impl.helper;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry for all component helpers
 * 
 * @author Manfred HANTSCHEL
 */
public class ComponentHelperRegistry {

    private static final Map<Class< ?>, ComponentHelper<Component>> HELPERS =
            new HashMap<Class< ?>, ComponentHelper<Component>>();

    static {
        register(new DefaultComponentHelper());
        register(new DefaultJComponentHelper());
        register(new JCheckBoxHelper());
        register(new JComboBoxHelper());
        register(new JLabelHelper());
        register(new JListHelper());
        register(new JPanelHelper());
        register(new JProgressBarHelper());
        register(new JTabbedPaneHelper());
        register(new JTableHelper());
        register(new JTextComponentHelper());
        register(new UTableComponentHelper());
        register(new WindowHelper());
        register(new I18nTextComponentHelper());
    }

    /**
     * Adds a component helper to the registry
     * 
     * @param helper the helper
     */
    @SuppressWarnings("unchecked")
    public static void register(ComponentHelper< ? extends Component> helper) {
        HELPERS.put(helper.getType(), (ComponentHelper<Component>) helper);
    }

    /**
     * Returns the component helper for the specified type of component
     * 
     * @param type the type of component
     * @return the helper, null if not found
     */
    @SuppressWarnings("unchecked")
    public static ComponentHelper<Component> get(Class< ? extends Component> type) {
        Class< ?> currentType = type;

        while (currentType != null) {
            ComponentHelper< ?> result = HELPERS.get(currentType);

            if (result != null) {
                return (ComponentHelper<Component>) result;
            }

            currentType = currentType.getSuperclass();
        }

        for (Class< ?> currentInterface : type.getInterfaces()) {
            ComponentHelper< ?> result = HELPERS.get(currentInterface);

            if (result != null) {
                return (ComponentHelper<Component>) result;
            }
        }

        return null;
    }

}
