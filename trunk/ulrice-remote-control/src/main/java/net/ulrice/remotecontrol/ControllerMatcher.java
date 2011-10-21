package net.ulrice.remotecontrol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModule;
import net.ulrice.module.IFModuleManager;
import net.ulrice.module.IFModuleTitleProvider;
import net.ulrice.remotecontrol.impl.ComponentUtils;

public abstract class ControllerMatcher implements Serializable {

    private static final long serialVersionUID = -6107920562275444097L;

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

    public static ControllerMatcher not(final ControllerMatcher matcher) {
        return new ControllerMatcher() {

            private static final long serialVersionUID = 1173413643827448825L;

            @Override
            public Collection<IFController> match(Collection<IFController> controllers) throws RemoteControlException {
                controllers.removeAll(matcher.match(new ArrayList<IFController>(controllers)));

                return controllers;
            }

            @Override
            public String toString() {
                return "not[" + matcher + "]";
            }
        };
    }

    public static ControllerMatcher withId(final String regex) throws RemoteControlException {
        final Pattern pattern = ComponentUtils.toPattern(regex);

        return new ControllerMatcher() {

            private static final long serialVersionUID = -6262316713001635250L;

            @Override
            public Collection<IFController> match(Collection<IFController> controllers) throws RemoteControlException {
                IFModuleManager manager = Ulrice.getModuleManager();
                Iterator<IFController> it = controllers.iterator();

                while (it.hasNext()) {
                    IFController controller = it.next();
                    IFModule module = manager.getModule(controller);

                    if ((module != null) && (module.getUniqueId() != null)
                        && (pattern.matcher(module.getUniqueId()).matches())) {
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

    public static ControllerMatcher titeled(final String regex) throws RemoteControlException {
        final Pattern pattern = ComponentUtils.toPattern(regex);

        return new ControllerMatcher() {

            private static final long serialVersionUID = -4297216619461241561L;

            @Override
            public Collection<IFController> match(Collection<IFController> controllers) throws RemoteControlException {
                Iterator<IFController> it = controllers.iterator();

                controllerLoop: while (it.hasNext()) {
                    IFController controller = it.next();

                    for (IFModuleTitleProvider.Usage usage : IFModuleTitleProvider.Usage.values()) {
                        String title = controller.getTitleProvider().getModuleTitle(usage);

                        if ((title != null) && (pattern.matcher(title).matches())) {
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

    public static ControllerMatcher like(final String regex) throws RemoteControlException {
        final Pattern pattern = ComponentUtils.toPattern(regex);

        return new ControllerMatcher() {

            private static final long serialVersionUID = -4297216619461241561L;

            @Override
            public Collection<IFController> match(Collection<IFController> controllers) throws RemoteControlException {
                IFModuleManager manager = Ulrice.getModuleManager();
                Iterator<IFController> it = controllers.iterator();

                controllerLoop: while (it.hasNext()) {
                    IFController controller = it.next();
                    IFModule module = manager.getModule(controller);

                    if (module != null) {
                        if ((module.getUniqueId() != null) && (pattern.matcher(module.getUniqueId()).matches())) {
                            continue;
                        }

                        for (IFModuleTitleProvider.Usage usage : IFModuleTitleProvider.Usage.values()) {
                            String title = module.getModuleTitle(usage);

                            if ((title != null) && (pattern.matcher(title).matches())) {
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

    public static ControllerMatcher current() {
        return new ControllerMatcher() {

            private static final long serialVersionUID = 2516732398045378023L;

            @Override
            public Collection<IFController> match(Collection<IFController> controllers) throws RemoteControlException {
                IFController currentController = Ulrice.getModuleManager().getCurrentController();

                if ((currentController != null) && (controllers.contains(currentController))) {
                    return Arrays.asList(currentController);
                }

                return Collections.<IFController> emptySet();
            }

            @Override
            public String toString() {
                return "current";
            }

        };
    }

    public abstract Collection<IFController> match(Collection<IFController> controllers)
        throws RemoteControlException;

    @Override
    public abstract String toString();

}
