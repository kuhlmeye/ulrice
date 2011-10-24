package net.ulrice.remotecontrol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import net.ulrice.module.impl.ModuleActionState;

/**
 * Matcher class for the {@link ActionRemoteControl}
 * 
 * @author Manfred HANTSCHEL
 */
public abstract class ActionMatcher implements Serializable {

    private static final long serialVersionUID = 6678911828580548107L;

    /**
     * Matches all actions
     * 
     * @return the matcher
     */
    public static ActionMatcher all() {
        return new ActionMatcher() {

            private static final long serialVersionUID = -1509396165864537141L;

            @Override
            public Collection<ModuleActionState> match(Collection<ModuleActionState> controllers)
                throws RemoteControlException {
                return controllers;
            }

            @Override
            public String toString() {
                return "all";
            }

        };
    }

    /**
     * The action must match all the specified matchers. The result of the matcher is the intersection of the results
     * of all matchers.
     * 
     * @param matchers the matchers
     * @return the matcher
     */
    public static ActionMatcher and(final ActionMatcher... matchers) {
        if ((matchers == null) || (matchers.length == 0)) {
            return all();
        }

        if (matchers.length == 1) {
            return matchers[0];
        }

        return new ActionMatcher() {

            private static final long serialVersionUID = -466776099567045553L;

            @Override
            public Collection<ModuleActionState> match(Collection<ModuleActionState> actions)
                throws RemoteControlException {
                Collection<ModuleActionState> results = new LinkedHashSet<ModuleActionState>(actions);

                for (ActionMatcher matcher : matchers) {
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
     * The action must match at least on of the specified matchers. The result of the matcher is the union of the
     * results of all matchers.
     * 
     * @param matchers the matchers
     * @return the matcher
     */
    public static ActionMatcher or(final ActionMatcher... matchers) {
        if ((matchers == null) || (matchers.length == 0)) {
            return all();
        }

        if (matchers.length == 1) {
            return matchers[0];
        }

        return new ActionMatcher() {

            private static final long serialVersionUID = -885707353343749061L;

            @Override
            public Collection<ModuleActionState> match(Collection<ModuleActionState> actions)
                throws RemoteControlException {
                Collection<ModuleActionState> results = new LinkedHashSet<ModuleActionState>();

                for (ActionMatcher matcher : matchers) {
                    results.addAll(matcher.match(new ArrayList<ModuleActionState>(actions)));
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
     * The action must not match the specified matcher. The result is the inversion of the result of the specified matcher.
     * 
     * @param matcher the matcher
     * @return the matcher
     */
    public static ActionMatcher not(final ActionMatcher matcher) {
        return new ActionMatcher() {

            private static final long serialVersionUID = -7282163835108685004L;

            @Override
            public Collection<ModuleActionState> match(Collection<ModuleActionState> actions)
                throws RemoteControlException {
                actions.removeAll(matcher.match(new ArrayList<ModuleActionState>(actions)));

                return actions;
            }

            @Override
            public String toString() {
                return "not[" + matcher + "]";
            }
        };
    }

    /**
     * The unique id of the action must match the specified one
     * 
     * @param uniqueId the unique id
     * @return the matcher
     */
    public static ActionMatcher withId(final String uniqueId) {
        return new ActionMatcher() {

            private static final long serialVersionUID = -5288846925357645955L;

            @Override
            public Collection<ModuleActionState> match(Collection<ModuleActionState> actions)
                throws RemoteControlException {
                Iterator<ModuleActionState> it = actions.iterator();

                while (it.hasNext()) {
                    if (!uniqueId.equals(it.next().getAction().getUniqueId())) {
                        it.remove();
                    }
                }

                return actions;
            }

            @Override
            public String toString() {
                return "withId[" + uniqueId + "]";
            }
        };
    }

    /**
     * The action must be enabled
     * 
     * @return the matcher
     */
    public static ActionMatcher enabled() {
        return new ActionMatcher() {

            private static final long serialVersionUID = -8177312906613211373L;

            @Override
            public Collection<ModuleActionState> match(Collection<ModuleActionState> actions)
                throws RemoteControlException {
                Iterator<ModuleActionState> it = actions.iterator();

                while (it.hasNext()) {
                    if (!it.next().isEnabled()) {
                        it.remove();
                    }
                }

                return actions;
            }

            @Override
            public String toString() {
                return "enabled";
            }
        };
    }

    /**
     * Returns a collection that contains all the actions that match. The returned collection may or may not be the
     * actions parameters, as well as the actions parameter may or may not be altered.
     * 
     * @param actions the actions
     * @return the matching actions
     * @throws RemoteControlException on occasion
     */
    public abstract Collection<ModuleActionState> match(Collection<ModuleActionState> actions)
        throws RemoteControlException;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String toString();

}
