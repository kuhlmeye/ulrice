package net.ulrice.remotecontrol;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

import net.ulrice.remotecontrol.impl.ComponentRegistry;
import net.ulrice.remotecontrol.impl.helper.ComponentHelper;
import net.ulrice.remotecontrol.impl.helper.ComponentHelperRegistry;

/**
 * Represents the state of a component
 * 
 * @author Manfred HANTSCHEL
 */
public class ComponentState implements Serializable, Iterable<ComponentState> {

    private static final long serialVersionUID = 716896171060420478L;

    /**
     * Creates the state for the specified component. Returns null if the component is null.
     * 
     * @param component the component
     * @return the state
     * @throws RemoteControlException TODO
     */
    public static ComponentState inspect(Component component) throws RemoteControlException {
        if (component == null) {
            return null;
        }

        return new ComponentState(component);
    }

    /**
     * Creates states for all specified components. Null values are ignored
     * @param components the component
     * @return a collection of all states
     * @throws RemoteControlException TODO
     */
    public static Collection<ComponentState> inspect(Collection<Component> components) throws RemoteControlException {
        Collection<ComponentState> results = new ArrayList<ComponentState>();

        for (Component component : components) {
            ComponentState state = inspect(component);

            if (state != null) {
                results.add(state);
            }
        }

        return results;
    }

    private Long uniqueId;

    private transient Component component;

    private final String className;
    private final String name;
    private final String text;
    private final String title;
    private final boolean visible;
    private final boolean enabled;
    private final boolean selected;
    private final boolean active;
    private final boolean focusOwner;
    private final Collection<ComponentState> childs;
    private final ComponentState labelFor;
    private final String toolTipText;
    private final Rectangle bounds;
    private final Map<Class< ?>, Object> datas;

    private ComponentState(Component component) throws RemoteControlException {
        super();

        this.component = component;

        uniqueId = ComponentRegistry.register(component);
        
        ComponentHelper<Component> helper = ComponentHelperRegistry.get(component.getClass());

        className = component.getClass().getName();
        name = component.getName();
        text = helper.getText(component);
        title = helper.getTitle(component);
        visible = component.isVisible();
        enabled = component.isEnabled();
        selected = helper.isSelected(component);
        active = helper.isActive(component);
        focusOwner = component.isFocusOwner();
        childs = new LinkedHashSet<ComponentState>();
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                addChild(ComponentState.inspect(child));
            }
        }

        labelFor = ComponentState.inspect(helper.getLabelFor(component));
        toolTipText = helper.getToolTipText(component);
        bounds = component.getBounds();

        datas = new HashMap<Class< ?>, Object>();

        setData(helper.getData(component));
    }

    public Long getUniqueId() {
        return uniqueId;
    }

    public void setUnqiueId(Long uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public String getClassName() {
        return className;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isFocusOwner() {
        return focusOwner;
    }

    protected void addChild(ComponentState child) {
        childs.add(child);
    }

    public Collection<ComponentState> getChilds() {
        return Collections.unmodifiableCollection(childs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<ComponentState> iterator() {
        return childs.iterator();
    }

    public ComponentState getLabelFor() {
        return labelFor;
    }

    public String getToolTipText() {
        return toolTipText;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    @SuppressWarnings("unchecked")
    public <TYPE> TYPE getData(Class<TYPE> type) {
        return (TYPE) datas.get(type);
    }

    public ComponentState setData(Object data) {
        if (data == null) {
            return this;
        }

        datas.put(data.getClass(), data);

        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(className).append(" {");

        if (uniqueId != null) {
            builder.append("\n\t").append("id:     ").append(Long.toHexString(uniqueId.longValue()));
        }

        if ((name != null) && (name.length() > 0)) {
            builder.append("\n\t").append("name:   ").append(name);
        }

        if ((title != null) && (title.length() > 0)) {
            builder.append("\n\t").append("title:  ").append(title);
        }

        if ((text != null) && (text.length() > 0)) {
            builder.append("\n\t").append("text:   ").append(text);
        }

        builder.append("\n\t").append("state:  ").append(visible ? "visible" : "invisible");
        builder.append(", ").append(enabled ? "enabled" : "disabled");

        if (selected) {
            builder.append(", selected");
        }

        if (active) {
            builder.append(", active");
        }

        builder.append(", ").append(focusOwner ? "has focus" : "no focus");

        if ((toolTipText != null) && (toolTipText.length() > 0)) {
            builder.append("\n\t").append("tip:    ").append(text);
        }

        builder.append("\n\t").append("bounds: ").append(bounds);

        if (!datas.isEmpty()) {
            for (Object data : datas.values()) {
                builder.append("\n\t").append("data:   ").append(data.toString().replace("\n", "\n\t        "));
            }
        }

        if (labelFor != null) {
            builder.append("\n\t").append("for:    ").append(labelFor.toString().replace("\n", "\n\t        "));
        }

        if (childs.size() > 0) {
            builder.append("\n\t").append("childs: [");
            Iterator<ComponentState> it = childs.iterator();
            while (it.hasNext()) {
                builder.append(it.next().toString().replace("\n", "\n\t        "));

                if (it.hasNext()) {
                    builder.append(", ");
                }
            }
            builder.append("]");
        }

        builder.append("\n}");

        return builder.toString();
    }

}
