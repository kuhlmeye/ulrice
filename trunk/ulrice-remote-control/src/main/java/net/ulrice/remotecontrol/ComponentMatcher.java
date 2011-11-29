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
import net.ulrice.remotecontrol.impl.helper.ComponentHelper;
import net.ulrice.remotecontrol.impl.helper.ComponentHelperRegistry;
import net.ulrice.remotecontrol.util.ComponentUtils;
import net.ulrice.remotecontrol.util.RemoteControlUtils;

/**
 * Matchers for the {@link ComponentRemoteControl}.
 * 
 * @author Manfred HANTSCHEL
 */
public abstract class ComponentMatcher implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Matches all components.
     * 
     * @return the matcher
     */
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

    /**
     * Each component must match each matcher. The result is the intersection of the result of all specified matchers.
     * 
     * @param matchers the matchers
     * @return the matcher
     */
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

    /**
     * Each component matches at least one of the specified matchers. The result is the union of the results of all
     * specified matchers.
     * 
     * @param matchers the matchers
     * @return the matcher
     */
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

    /**
     * Keeps those components, that do not match the specified matcher. The result is the inversion of the result of
     * the specified matcher.
     * 
     * @param matcher the matcher
     * @return the matcher
     */
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

    /**
     * Matches components with the specified id. The unique id is generated when a {@link ComponentState} is created.
     * 
     * @param uniqueId the unique id
     * @return the matcher
     */
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

    /**
     * Matches all components of the specified type
     * 
     * @param type the type
     * @return the matcher
     */
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

    /**
     * Matches all components with a name, text or title that matches the regular expression.
     * 
     * @param regex the regular expression
     * @return the matcher
     * @throws RemoteControlException on occasion
     */
    public static ComponentMatcher like(final String regex) throws RemoteControlException {
        final Pattern pattern = RemoteControlUtils.toPattern(regex);

        return new ComponentMatcher() {

            private static final long serialVersionUID = -1328715034660944023L;

            @Override
            public Collection<Component> match(Collection<Component> components) throws RemoteControlException {
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

    /**
     * Matches all components with a name that matches the regular expression
     * 
     * @param regex the regular expression
     * @return the matcher
     * @throws RemoteControlException on occasion
     */
    public static ComponentMatcher named(final String regex) throws RemoteControlException {
        final Pattern pattern = RemoteControlUtils.toPattern(regex);

        return new ComponentMatcher() {

            private static final long serialVersionUID = -8552962762441427284L;

            @Override
            public Collection<Component> match(Collection<Component> components) throws RemoteControlException {
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

    /**
     * Matches all components with a title that matches the regular expression. If the component has no title, it, of
     * course, does not match.
     * 
     * @param regex the regular expression
     * @return the matcher
     * @throws RemoteControlException on occasion
     */
    public static ComponentMatcher titeled(final String regex) throws RemoteControlException {
        final Pattern pattern = RemoteControlUtils.toPattern(regex);

        return new ComponentMatcher() {

            private static final long serialVersionUID = 3323697224579645088L;

            @Override
            public Collection<Component> match(Collection<Component> components) throws RemoteControlException {
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

    /**
     * Matches all components with a text, that matches the specified regular expression. If the component has no
     * text, it never matches this matcher.
     * 
     * @param regex the regular expression
     * @return the matcher
     * @throws RemoteControlException on occasion
     */
    public static ComponentMatcher texted(final String regex) throws RemoteControlException {
        final Pattern pattern = RemoteControlUtils.toPattern(regex);

        return new ComponentMatcher() {

            private static final long serialVersionUID = 3323697224579645088L;

            @Override
            public Collection<Component> match(Collection<Component> components) throws RemoteControlException {
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

    /**
     * Matches a component, that is referenced by a label with the specified name or text as label-for. E.g. if you
     * have a text field and a label with the text "Name". The property labelFor of the label is set to text field. If
     * you call this matcher with "Name", then it matches the text field.
     * 
     * @param regex the regurlar expression
     * @return the matcher
     * @throws RemoteControlException on occasion
     */
    public static ComponentMatcher labeled(final String regex) throws RemoteControlException {
        final Pattern pattern = RemoteControlUtils.toPattern(regex);

        return new ComponentMatcher() {

            private static final long serialVersionUID = -8552962762441427284L;

            @Override
            public Collection<Component> match(Collection<Component> components) throws RemoteControlException {
                Collection<Component> results = new LinkedHashSet<Component>();

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

    /**
     * Matches all components that are enabled
     * 
     * @return the matcher
     */
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

    /**
     * Matches all components that are visible
     * 
     * @return the matcher
     */
    public static ComponentMatcher visible() {
        return new ComponentMatcher() {

            private static final long serialVersionUID = -7181604678594727946L;

            @Override
            public Collection<Component> match(Collection<Component> components) throws RemoteControlException {
                Iterator<Component> it = components.iterator();

                while (it.hasNext()) {
                    Component component = it.next();

                    if (!component.isVisible()) {
                        it.remove();
                    }
                }

                return components;
            }

            @Override
            public String toString() {
                return "visible";
            }

        };
    }

    /**
     * Matches the one component that is the focus owner
     * 
     * @return the matcher
     */
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

    /**
     * Matches all components which are childs of components that match the specified matchers.
     * 
     * @param matchers the matchers, concatinated by and
     * @return the matcher
     */
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

    /**
     * Returns a collection that contains all the components that match. The returned collection may or may not be the
     * components parameters, as well as the actions parameter may or may not be altered.
     * 
     * @param components the components
     * @return the matching components
     * @throws RemoteControlException on occasion
     */
    public abstract Collection<Component> match(Collection<Component> components) throws RemoteControlException;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String toString();

}
