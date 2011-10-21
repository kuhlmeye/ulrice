package net.ulrice.remotecontrol;

import java.awt.Component;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

import net.ulrice.remotecontrol.impl.ComponentRegistry;
import net.ulrice.remotecontrol.impl.ComponentUtils;
import net.ulrice.remotecontrol.impl.helper.ComponentHelper;
import net.ulrice.remotecontrol.impl.helper.ComponentHelperRegistry;

public abstract class ComponentMatcher implements Serializable {

    private static final long serialVersionUID = 1L;

    public static ComponentMatcher all() {
        return new ComponentMatcher() {

            private static final long serialVersionUID = -7645112022497959861L;

            @Override
            public Collection<Component> match(Collection<Component> controllers) throws RemoteControlException {
                return controllers;
            }

            @Override
            public String toString() {
                return "all";
            }

        };
    }

    public static ComponentMatcher and(final ComponentMatcher... matchers) {
        if ((matchers == null) || (matchers.length == 0)) {
            return all();
        }
        
        if (matchers.length == 1) {
            return matchers[0];
        }

        return new ComponentMatcher() {

            private static final long serialVersionUID = 4731283638595311247L;

            @Override
            public Collection<Component> match(Collection<Component> components) throws RemoteControlException {
                Collection<Component> results = new LinkedHashSet<Component>(components);

                for (ComponentMatcher matcher : matchers) {
                    results = matcher.match(results);
                }

                return results;
            }

            @Override
            public String toString() {
                return "and" + Arrays.toString(matchers);
            }

        };
    }

    public static ComponentMatcher or(final ComponentMatcher... matchers) {
        if ((matchers == null) || (matchers.length == 0)) {
            return all();
        }
        
        if (matchers.length == 1) {
            return matchers[0];
        }

        return new ComponentMatcher() {

            private static final long serialVersionUID = -6057489938390614259L;

            @Override
            public Collection<Component> match(Collection<Component> components) throws RemoteControlException {
                Collection<Component> results = new LinkedHashSet<Component>();

                for (ComponentMatcher matcher : matchers) {
                    results.addAll(matcher.match(new ArrayList<Component>(components)));
                }

                return results;
            }

            @Override
            public String toString() {
                return "or" + Arrays.toString(matchers);
            }

        };
    }

    public static ComponentMatcher not(final ComponentMatcher matcher) {
        return new ComponentMatcher() {

            private static final long serialVersionUID = 6488388571036316763L;

            @Override
            public Collection<Component> match(Collection<Component> components) throws RemoteControlException {
                components.removeAll(matcher.match(new ArrayList<Component>(components)));

                return components;
            }

            @Override
            public String toString() {
                return "not[" + matcher + "]";
            }

        };
    }

    public static ComponentMatcher withId(final Long uniqueId) {
        return new ComponentMatcher() {

            private static final long serialVersionUID = 7366001726950768210L;

            @Override
            public Collection<Component> match(Collection<Component> components) throws RemoteControlException {
                Iterator<Component> it = components.iterator();

                while (it.hasNext()) {
                    if (!uniqueId.equals(ComponentRegistry.getUnqiueId(it.next()))) {
                        it.remove();
                    }
                }

                return components;
            }

            @Override
            public String toString() {
                return "withId[" + uniqueId + "]";
            }

        };
    }

    public static ComponentMatcher ofType(final Class< ? extends Component> type) {
        return new ComponentMatcher() {

            private static final long serialVersionUID = 7366001726950768210L;

            @Override
            public Collection<Component> match(Collection<Component> components) throws RemoteControlException {
                Iterator<Component> it = components.iterator();

                while (it.hasNext()) {
                    if (!type.isInstance(it.next())) {
                        it.remove();
                    }
                }

                return components;
            }

            @Override
            public String toString() {
                return "ofType[" + type + "]";
            }

        };
    }

    public static ComponentMatcher like(final String regex) {
        return new ComponentMatcher() {

            private static final long serialVersionUID = -1328715034660944023L;

            @Override
            public Collection<Component> match(Collection<Component> components) throws RemoteControlException {
                Pattern pattern = ComponentUtils.toPattern(regex);
                Iterator<Component> it = components.iterator();

                while (it.hasNext()) {
                    Component component = it.next();
                    String name = component.getName();

                    if ((name != null) && (pattern.matcher(name).matches())) {
                        continue;
                    }

                    ComponentHelper<Component> helper = ComponentHelperRegistry.get(component.getClass());
                    String text = helper.getText(component);

                    if ((text != null) && (pattern.matcher(text).matches())) {
                        continue;
                    }

                    String title = helper.getTitle(component);

                    if ((title != null) && (pattern.matcher(title).matches())) {
                        continue;
                    }

                    it.remove();
                }

                return components;
            }

            @Override
            public String toString() {
                return "called[" + regex + "]";
            }

        };
    }

    public static ComponentMatcher named(final String regex) {
        return new ComponentMatcher() {

            private static final long serialVersionUID = -8552962762441427284L;

            @Override
            public Collection<Component> match(Collection<Component> components) throws RemoteControlException {
                Pattern pattern = ComponentUtils.toPattern(regex);
                Iterator<Component> it = components.iterator();

                while (it.hasNext()) {
                    Component component = it.next();
                    String name = component.getName();

                    if ((name != null) && (pattern.matcher(name).matches())) {
                        continue;
                    }

                    it.remove();
                }

                return components;
            }

            @Override
            public String toString() {
                return "named[" + regex + "]";
            }

        };
    }

    public static ComponentMatcher titeled(final String regex) {
        return new ComponentMatcher() {

            private static final long serialVersionUID = 3323697224579645088L;

            @Override
            public Collection<Component> match(Collection<Component> components) throws RemoteControlException {
                Pattern pattern = ComponentUtils.toPattern(regex);
                Iterator<Component> it = components.iterator();

                while (it.hasNext()) {
                    Component component = it.next();
                    ComponentHelper<Component> helper = ComponentHelperRegistry.get(component.getClass());
                    String title = helper.getTitle(component);

                    if ((title != null) && (pattern.matcher(title).matches())) {
                        continue;
                    }

                    it.remove();
                }

                return components;
            }

            @Override
            public String toString() {
                return "titeled[" + regex + "]";
            }

        };
    }

    public static ComponentMatcher texted(final String regex) {
        return new ComponentMatcher() {

            private static final long serialVersionUID = 3323697224579645088L;

            @Override
            public Collection<Component> match(Collection<Component> components) throws RemoteControlException {
                Pattern pattern = ComponentUtils.toPattern(regex);
                Iterator<Component> it = components.iterator();

                while (it.hasNext()) {
                    Component component = it.next();
                    ComponentHelper<Component> helper = ComponentHelperRegistry.get(component.getClass());
                    String text = helper.getText(component);

                    if ((text != null) && (pattern.matcher(text).matches())) {
                        continue;
                    }

                    it.remove();
                }

                return components;
            }

            @Override
            public String toString() {
                return "texted[" + regex + "]";
            }

        };
    }

    public static ComponentMatcher labeled(final String regex) {
        return new ComponentMatcher() {

            private static final long serialVersionUID = -8552962762441427284L;

            @Override
            public Collection<Component> match(Collection<Component> components) throws RemoteControlException {
                Collection<Component> results = new LinkedHashSet<Component>();
                Pattern pattern = ComponentUtils.toPattern(regex);

                for (Component component : components) {
                    ComponentHelper<Component> helper = ComponentHelperRegistry.get(component.getClass());
                    Component labelFor = helper.getLabelFor(component);

                    if (labelFor == null) {
                        continue;
                    }

                    String name = component.getName();

                    if ((name != null) && (pattern.matcher(name).matches())) {
                        results.add(labelFor);
                        continue;
                    }

                    String text = helper.getText(component);

                    if ((text != null) && (pattern.matcher(text).matches())) {
                        results.add(labelFor);
                        continue;
                    }

                    String title = helper.getTitle(component);

                    if ((title != null) && (pattern.matcher(title).matches())) {
                        results.add(labelFor);
                        continue;
                    }
                }

                return results;
            }

            @Override
            public String toString() {
                return "labeled[" + regex + "]";
            }

        };
    }

    public static ComponentMatcher enabled() {
        return new ComponentMatcher() {

            private static final long serialVersionUID = 435060729395636802L;

            @Override
            public Collection<Component> match(Collection<Component> components) throws RemoteControlException {
                Iterator<Component> it = components.iterator();

                while (it.hasNext()) {
                    Component component = it.next();

                    if (!component.isEnabled()) {
                        it.remove();
                    }
                }

                return components;
            }

            @Override
            public String toString() {
                return "enabled";
            }

        };
    }

    public static ComponentMatcher focusOwner() {
        return new ComponentMatcher() {

            private static final long serialVersionUID = 435060729395636802L;

            @Override
            public Collection<Component> match(Collection<Component> components) throws RemoteControlException {
                Iterator<Component> it = components.iterator();

                while (it.hasNext()) {
                    Component component = it.next();

                    if (!component.isFocusOwner()) {
                        it.remove();
                    }
                }

                return components;
            }

            @Override
            public String toString() {
                return "focusOwner";
            }

        };
    }

    public static ComponentMatcher within(final ComponentMatcher... matchers) {
        final ComponentMatcher matcher = and(matchers);

        return new ComponentMatcher() {

            private static final long serialVersionUID = 5109183117261814614L;

            @Override
            public Collection<Component> match(Collection<Component> components) throws RemoteControlException {
                Iterator<Component> it = components.iterator();

                while (it.hasNext()) {
                    Component component = it.next();
                    Collection<Component> parents = matcher.match(ComponentUtils.parents(component));

                    if (parents.size() == 0) {
                        it.remove();
                    }
                }

                return components;
            }

            @Override
            public String toString() {
                return "within[" + matcher + "]";
            }

        };
    }

    protected ComponentMatcher() {
        super();
    }

    public abstract Collection<Component> match(Collection<Component> components) throws RemoteControlException;

    @Override
    public abstract String toString();

}
