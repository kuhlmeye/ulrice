package net.ulrice.remotecontrol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

import net.ulrice.module.IFModule;
import net.ulrice.module.IFModuleTitleProvider;
import net.ulrice.remotecontrol.impl.ComponentUtils;

public abstract class ModuleMatcher implements Serializable {

    private static final long serialVersionUID = 848205798323514220L;

    public static ModuleMatcher all() {
        return new ModuleMatcher() {

            private static final long serialVersionUID = -2190252870425650444L;

            @Override
            public Collection<IFModule> match(Collection<IFModule> controllers) throws RemoteControlException {
                return controllers;
            }

            @Override
            public String toString() {
                return "all";
            }

        };
    }

    public static ModuleMatcher and(final ModuleMatcher... matchers) {
        if ((matchers == null) || (matchers.length == 0)) {
            return all();
        }

        if (matchers.length == 1) {
            return matchers[0];
        }

        return new ModuleMatcher() {

            private static final long serialVersionUID = -6209456642504973328L;

            @Override
            public Collection<IFModule> match(Collection<IFModule> modules) throws RemoteControlException {
                Collection<IFModule> results = new LinkedHashSet<IFModule>(modules);

                for (ModuleMatcher matcher : matchers) {
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

    public static ModuleMatcher or(final ModuleMatcher... matchers) {
        if ((matchers == null) || (matchers.length == 0)) {
            return all();
        }

        if (matchers.length == 1) {
            return matchers[0];
        }

        return new ModuleMatcher() {

            private static final long serialVersionUID = -6533779666059405536L;

            @Override
            public Collection<IFModule> match(Collection<IFModule> modules) throws RemoteControlException {
                Collection<IFModule> results = new LinkedHashSet<IFModule>();

                for (ModuleMatcher matcher : matchers) {
                    results.addAll(matcher.match(new ArrayList<IFModule>(modules)));
                }

                return results;
            }

            @Override
            public String toString() {
                return "or" + Arrays.toString(matchers);
            }

        };
    }

    public static ModuleMatcher not(final ModuleMatcher matcher) {
        return new ModuleMatcher() {

            private static final long serialVersionUID = 497264784972003925L;

            @Override
            public Collection<IFModule> match(Collection<IFModule> modules) throws RemoteControlException {
                modules.removeAll(matcher.match(new ArrayList<IFModule>(modules)));

                return modules;
            }

            @Override
            public String toString() {
                return "not[" + matcher + "]";
            }
        };
    }

    public static ModuleMatcher withId(final String regex) throws RemoteControlException {
        final Pattern pattern = ComponentUtils.toPattern(regex);

        return new ModuleMatcher() {

            private static final long serialVersionUID = -245398486090616818L;

            @Override
            public Collection<IFModule> match(Collection<IFModule> modules) throws RemoteControlException {
                Iterator<IFModule> it = modules.iterator();

                while (it.hasNext()) {
                    IFModule module = it.next();

                    if ((module.getUniqueId() == null) || (!pattern.matcher(module.getUniqueId()).matches())) {
                        it.remove();
                    }
                }

                return modules;
            }

            @Override
            public String toString() {
                return "withId[" + regex + "]";
            }
        };
    }

    public static ModuleMatcher titeled(final String regex) throws RemoteControlException {
        final Pattern pattern = ComponentUtils.toPattern(regex);

        return new ModuleMatcher() {

            private static final long serialVersionUID = 5918807697935735273L;

            @Override
            public Collection<IFModule> match(Collection<IFModule> modules) throws RemoteControlException {
                Iterator<IFModule> it = modules.iterator();

                moduleLoop: while (it.hasNext()) {
                    IFModule module = it.next();

                    for (IFModuleTitleProvider.Usage usage : IFModuleTitleProvider.Usage.values()) {
                        String title = module.getModuleTitle(usage);

                        if ((title != null) && (pattern.matcher(title).matches())) {
                            continue moduleLoop;
                        }
                    }

                    it.remove();
                }

                return modules;
            }

            @Override
            public String toString() {
                return "titeled[" + regex + "]";
            }
        };
    }

    public static ModuleMatcher like(final String regex) throws RemoteControlException {
        final Pattern pattern = ComponentUtils.toPattern(regex);

        return new ModuleMatcher() {

            private static final long serialVersionUID = 5918807697935735273L;

            @Override
            public Collection<IFModule> match(Collection<IFModule> modules) throws RemoteControlException {
                Iterator<IFModule> it = modules.iterator();

                moduleLoop: while (it.hasNext()) {
                    IFModule module = it.next();

                    if (pattern.matcher(module.getUniqueId()).matches()) {
                        continue;
                    }

                    for (IFModuleTitleProvider.Usage usage : IFModuleTitleProvider.Usage.values()) {
                        String title = module.getModuleTitle(usage);

                        if ((title != null) && (pattern.matcher(title).matches())) {
                            continue moduleLoop;
                        }
                    }

                    it.remove();
                }

                return modules;
            }

            @Override
            public String toString() {
                return "like[" + regex + "]";
            }
        };
    }

    public abstract Collection<IFModule> match(Collection<IFModule> modules) throws RemoteControlException;

    @Override
    public abstract String toString();

}
