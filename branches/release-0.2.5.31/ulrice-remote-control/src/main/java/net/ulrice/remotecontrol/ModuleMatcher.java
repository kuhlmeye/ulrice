package net.ulrice.remotecontrol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import net.ulrice.module.IFModule;
import net.ulrice.module.IFModuleTitleProvider;
import net.ulrice.remotecontrol.util.RegularMatcher;
import net.ulrice.remotecontrol.util.RemoteControlUtils;

/**
 * Matcher for the {@link ModuleRemoteControl}
 * 
 * @author Manfred HANTSCHEL
 */
public abstract class ModuleMatcher implements Serializable {

    private static final long serialVersionUID = 848205798323514220L;

    /**
     * Matches all modules
     * 
     * @return the matcher
     */
    public static ModuleMatcher all() {
        return new ModuleMatcher() {

            private static final long serialVersionUID = -2190252870425650444L;

            @Override
            public Collection<IFModule<?>> match(Collection<IFModule<?>> controllers) throws RemoteControlException {
                return controllers;
            }

            @Override
            public String toString() {
                return "all";
            }

        };
    }

    /**
     * The result of the matcher is the intersection of the results of all specified matchers.
     * 
     * @param matchers the matchers
     * @return the matcher
     */
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
            public Collection<IFModule<?>> match(Collection<IFModule<?>> modules) throws RemoteControlException {
                Collection<IFModule<?>> results = new LinkedHashSet<IFModule<?>>(modules);

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

    /**
     * The result is the union of the results of all specified matchers.
     * 
     * @param matchers the matchers
     * @return the matcher
     */
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
            public Collection<IFModule<?>> match(Collection<IFModule<?>> modules) throws RemoteControlException {
                Collection<IFModule<?>> results = new LinkedHashSet<IFModule<?>>();

                for (ModuleMatcher matcher : matchers) {
                    results.addAll(matcher.match(new ArrayList<IFModule<?>>(modules)));
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
     * The result is the inversion of the result of the specified matcher.
     * 
     * @param matcher the matcher
     * @return the matcher
     */
    public static ModuleMatcher not(final ModuleMatcher matcher) {
        return new ModuleMatcher() {

            private static final long serialVersionUID = 497264784972003925L;

            @Override
            public Collection<IFModule<?>> match(Collection<IFModule<?>> modules) throws RemoteControlException {
                modules.removeAll(matcher.match(new ArrayList<IFModule<?>>(modules)));

                return modules;
            }

            @Override
            public String toString() {
                return "not[" + matcher + "]";
            }
        };
    }

    /**
     * The result of the matcher are all modules if a unique id, that matches the specified regular expression.
     * 
     * @param regex the regular expression
     * @return the matcher
     * @throws RemoteControlException on occasion
     */
    public static ModuleMatcher withId(final String regex) throws RemoteControlException {
        final RegularMatcher matcher = RemoteControlUtils.toMatcher(regex);

        return new ModuleMatcher() {

            private static final long serialVersionUID = -245398486090616818L;

            @Override
            public Collection<IFModule<?>> match(Collection<IFModule<?>> modules) throws RemoteControlException {
                Iterator<IFModule<?>> it = modules.iterator();

                while (it.hasNext()) {
                    IFModule<?> module = it.next();

                    if ((module.getUniqueId() == null) || (!matcher.matches(module.getUniqueId()))) {
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

    /**
     * The result of the matcher are all modules with a title (of any usage) that matches the specified reguar
     * expression.
     * 
     * @param regex the regular expression
     * @return the matcher
     * @throws RemoteControlException on occasion
     */
    public static ModuleMatcher titeled(final String regex) throws RemoteControlException {
        final RegularMatcher matcher = RemoteControlUtils.toMatcher(regex);

        return new ModuleMatcher() {

            private static final long serialVersionUID = 5918807697935735273L;

            @Override
            public Collection<IFModule<?>> match(Collection<IFModule<?>> modules) throws RemoteControlException {
                Iterator<IFModule<?>> it = modules.iterator();

                moduleLoop: while (it.hasNext()) {
                    IFModule<?> module = it.next();

                    for (IFModuleTitleProvider.Usage usage : IFModuleTitleProvider.Usage.values()) {
                        String title = module.getModuleTitle(usage);

                        if ((title != null) && (matcher.matches(title))) {
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

    /**
     * The result of the matcher are all modules, with an unique id or a title (of any usage) that matches the
     * specified reguar expression
     * 
     * @param regex the regular expression
     * @return the matcher
     * @throws RemoteControlException on occasion
     */
    public static ModuleMatcher like(final String regex) throws RemoteControlException {
        final RegularMatcher matcher = RemoteControlUtils.toMatcher(regex);

        return new ModuleMatcher() {

            private static final long serialVersionUID = 5918807697935735273L;

            @Override
            public Collection<IFModule<?>> match(Collection<IFModule<?>> modules) throws RemoteControlException {
                Iterator<IFModule<?>> it = modules.iterator();

                moduleLoop: while (it.hasNext()) {
                    IFModule<?> module = it.next();

                    if (matcher.matches(module.getUniqueId())) {
                        continue;
                    }

                    for (IFModuleTitleProvider.Usage usage : IFModuleTitleProvider.Usage.values()) {
                        String title = module.getModuleTitle(usage);

                        if ((title != null) && (matcher.matches(title))) {
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

    /**
     * Returns a collection that contains all the modules that match. The returned collection may or may not be the
     * module parameters, as well as the modules parameter may or may not be altered.
     * 
     * @param modules the modules
     * @return the matching modules
     * @throws RemoteControlException on occasion
     */
    public abstract Collection<IFModule<?>> match(Collection<IFModule<?>> modules) throws RemoteControlException;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String toString();

}
