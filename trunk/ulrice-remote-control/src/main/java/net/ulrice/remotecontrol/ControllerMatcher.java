package net.ulrice.remotecontrol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModule;
import net.ulrice.module.IFModuleManager;
import net.ulrice.module.IFModuleTitleProvider;
import net.ulrice.remotecontrol.util.RegularMatcher;
import net.ulrice.remotecontrol.util.RemoteControlUtils;

/**
 * Provides matchers for the {@link ControllerRemoteControl}.
 * 
 * @author Manfred HANTSCHEL
 */
public abstract class ControllerMatcher implements Serializable {

    private static final long serialVersionUID = -6107920562275444097L;

    /**
     * Matches all controllers
     * 
     * @return the matcher
     */
    public static ControllerMatcher all() {
        return new ControllerMatcher() {

            private static final long serialVersionUID = -1509396165864537141L;

            @Override
            public Collection<IFController> match(Collection<IFController> controllers) throws RemoteControlException {
                return controllers;
            }

            @Override
            public String toString() {
                return "all";
            }

        };
    }

    /**
     * Each controller must match each matcher.
     * 
     * @param matchers the matchers
     * @return the matcher
     */
    public static ControllerMatcher and(final ControllerMatcher... matchers) {
        if ((matchers == null) || (matchers.length == 0)) {
            return all();
        }

        if (matchers.length == 1) {
            return matchers[0];
        }

        return new ControllerMatcher() {

            private static final long serialVersionUID = 4538844438211264465L;

            @Override
            public Collection<IFController> match(Collection<IFController> controllers) throws RemoteControlException {
                Collection<IFController> results = new LinkedHashSet<IFController>(controllers);

                for (ControllerMatcher matcher : matchers) {
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
     * Each controller matches at least one of the specified matchers.
     * 
     * @param matchers the matchers
     * @return the matcher
     */
    public static ControllerMatcher or(final ControllerMatcher... matchers) {
        if ((matchers == null) || (matchers.length == 0)) {
            return all();
        }

        if (matchers.length == 1) {
            return matchers[0];
        }

        return new ControllerMatcher() {

            private static final long serialVersionUID = 8514888712058502392L;

            @Override
            public Collection<IFController> match(Collection<IFController> controllers) throws RemoteControlException {
                Collection<IFController> results = new LinkedHashSet<IFController>();

                for (ControllerMatcher matcher : matchers) {
                    results.addAll(matcher.match(new ArrayList<IFController>(controllers)));
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
     * Keeps those controllers, that do not match the specified matcher
     * 
     * @param matcher the matcher
     * @return the matcher
     */
    public static ControllerMatcher not(final ControllerMatcher matcher) {
        return new ControllerMatcher() {

            private static final long serialVersionUID = 1173413643827448825L;

            @Override
            public Collection<IFController> match(Collection<IFController> controllers) throws RemoteControlException {
                Collection<IFController> matched = matcher.match(new ArrayList<IFController>(controllers));
                
                controllers.removeAll(matched);

                return controllers;
            }

            @Override
            public String toString() {
                return "not[" + matcher + "]";
            }
        };
    }

    /**
     * Matches all controllers with an id that matches the specified regular expression.
     * 
     * @param regex the regular expression
     * @return the matcher
     * @throws RemoteControlException on occasion
     */
    public static ControllerMatcher withId(final String regex) throws RemoteControlException {
        final RegularMatcher matcher = RemoteControlUtils.toMatcher(regex);

        return new ControllerMatcher() {

            private static final long serialVersionUID = -6262316713001635250L;

            @Override
            public Collection<IFController> match(Collection<IFController> controllers) throws RemoteControlException {
                IFModuleManager manager = Ulrice.getModuleManager();
                Iterator<IFController> it = controllers.iterator();

                while (it.hasNext()) {
                    IFController controller = it.next();
                    IFModule<?> module = manager.getModule(controller);

                    if ((module != null) && (module.getUniqueId() != null)
                        && (matcher.matches(module.getUniqueId()))) {
                        continue;
                    }

                    it.remove();
                }

                return controllers;
            }

            @Override
            public String toString() {
                return "withId[" + regex + "]";
            }
        };
    }

    /**
     * Matches all controllers with a title (for any usage) that matches the specified regular expression.
     * 
     * @param regex the regular expression
     * @return the matcher
     * @throws RemoteControlException on occasion
     */
    public static ControllerMatcher titeled(final String regex) throws RemoteControlException {
        final RegularMatcher matcher = RemoteControlUtils.toMatcher(regex);

        return new ControllerMatcher() {

            private static final long serialVersionUID = -4297216619461241561L;

            @Override
            public Collection<IFController> match(Collection<IFController> controllers) throws RemoteControlException {
                Iterator<IFController> it = controllers.iterator();

                controllerLoop: while (it.hasNext()) {
                    IFController controller = it.next();

                    for (IFModuleTitleProvider.Usage usage : IFModuleTitleProvider.Usage.values()) {
                        String title = controller.getTitleProvider().getModuleTitle(usage);

                        if ((title != null) && (matcher.matches(title))) {
                            continue controllerLoop;
                        }
                    }

                    it.remove();
                }

                return controllers;
            }

            @Override
            public String toString() {
                return "titeled[" + regex + "]";
            }
        };
    }

    /**
     * Matches all controllers with an id or a title (any usage) that matches the regular expression.
     * 
     * @param regex the regular expression
     * @return the matcher
     * @throws RemoteControlException on occasion
     */
    public static ControllerMatcher like(final String regex) throws RemoteControlException {
        final RegularMatcher matcher = RemoteControlUtils.toMatcher(regex);

        return new ControllerMatcher() {

            private static final long serialVersionUID = -4297216619461241561L;

            @Override
            public Collection<IFController> match(Collection<IFController> controllers) throws RemoteControlException {
                IFModuleManager manager = Ulrice.getModuleManager();
                Iterator<IFController> it = controllers.iterator();

                controllerLoop: while (it.hasNext()) {
                    IFController controller = it.next();
                    IFModule<?> module = manager.getModule(controller);

                    if (module != null) {
                        if ((module.getUniqueId() != null) && (matcher.matches(module.getUniqueId()))) {
                            continue;
                        }

                        for (IFModuleTitleProvider.Usage usage : IFModuleTitleProvider.Usage.values()) {
                            String title = module.getModuleTitle(usage);

                            if ((title != null) && (matcher.matches(title))) {
                                continue controllerLoop;
                            }
                        }
                    }

                    it.remove();
                }

                return controllers;
            }

            @Override
            public String toString() {
                return "like[" + regex + "]";
            }
        };
    }

    /**
     * Matches the controller that is mark as current one
     * 
     * @return the matcher
     */
    public static ControllerMatcher current() {
        return new ControllerMatcher() {

            private static final long serialVersionUID = 2516732398045378023L;

            @Override
            public Collection<IFController> match(Collection<IFController> controllers) throws RemoteControlException {
                IFController currentController = Ulrice.getModuleManager().getCurrentController();
                Collection<IFController> result = new LinkedHashSet<IFController>();

                if ((currentController != null) && (controllers.contains(currentController))) {
                    result.add(currentController);
                }

                return result;
            }

            @Override
            public String toString() {
                return "current";
            }

        };
    }

    /**
     * Matches all controllers that are blocked
     * 
     * @return the matcher
     */
    public static ControllerMatcher blocked() {
        return new ControllerMatcher() {

            private static final long serialVersionUID = 2592992524282214062L;

            @Override
            public Collection<IFController> match(Collection<IFController> controllers) throws RemoteControlException {
                Iterator<IFController> it = controllers.iterator();
                while (it.hasNext()) {
                    if (!Ulrice.getModuleManager().isBlocked(it.next())) {
                        it.remove();
                    }
                }
                return controllers;
            }

            @Override
            public String toString() {
                return "blocked";
            }

        };
    }

    /**
     * Returns a collection that contains all the controllers that match. The returned collection may or may not be
     * the controllers parameters, as well as the controllers parameter may or may not be altered.
     * 
     * @param controllers the controllers
     * @return the matching controllers
     * @throws RemoteControlException on occasion
     */
    public abstract Collection<IFController> match(Collection<IFController> controllers)
        throws RemoteControlException;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String toString();

}
