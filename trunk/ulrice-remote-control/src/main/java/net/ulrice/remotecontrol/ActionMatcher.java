package net.ulrice.remotecontrol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import net.ulrice.module.impl.ModuleActionState;

public abstract class ActionMatcher implements Serializable {

    private static final long serialVersionUID = 6678911828580548107L;

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

    public abstract Collection<ModuleActionState> match(Collection<ModuleActionState> actions)
        throws RemoteControlException;

    @Override
    public abstract String toString();

}
