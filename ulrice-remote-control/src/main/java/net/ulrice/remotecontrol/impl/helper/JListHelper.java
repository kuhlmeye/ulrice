package net.ulrice.remotecontrol.impl.helper;

import java.awt.Robot;

import javax.swing.JList;
import javax.swing.ListModel;

import net.ulrice.remotecontrol.ComponentListData;
import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.RegularMatcher;
import net.ulrice.remotecontrol.util.RemoteControlUtils;
import net.ulrice.remotecontrol.util.Result;

@SuppressWarnings("rawtypes")
public class JListHelper extends AbstractJComponentHelper<JList> {

    public JListHelper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<JList> getType() {
        return JList.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getData(JList component) throws RemoteControlException {
        ComponentListData data = new ComponentListData();
        ListModel model = component.getModel();

        for (int i = 0; i < model.getSize(); i += 1) {
            data.setEntry(i, model.getElementAt(i), component.isSelectedIndex(i));
        }

        return data;
    }

    /**
     * {@inheritDoc}
     *
     * @see net.ulrice.remotecontrol.impl.helper.AbstractComponentHelper#enter(java.awt.Robot, java.awt.Component,
     *      java.lang.String)
     */
    @Override
    public boolean enter(Robot robot, final JList component, String text) throws RemoteControlException {
        final Result<Boolean> result = new Result<Boolean>(2);
        final RegularMatcher matcher = RemoteControlUtils.toMatcher(text);

        try {
            RemoteControlUtils.invokeInSwing(new Runnable() {

                @Override
                public void run() {
                    for (int i = 0; i < component.getModel().getSize(); i += 1) {
                        if (matcher.matches(String.valueOf(component.getModel().getElementAt(i)))) {
                            component.setSelectedIndex(i);
                            result.fireResult(true);
                            return;
                        }
                    }

                    result.fireResult(false);
                }

            });

            return result.aquireResult();
        }
        catch (RemoteControlException e) {
            throw new RemoteControlException("Entering \"" + text + "\" into JList failed", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean click(Robot robot, JList component, int index) throws RemoteControlException {
        if (index < 0) {
            index = component.getModel().getSize() + index;
        }

        return click(robot, component, component.getCellBounds(index, index));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean click(Robot robot, JList component, String text) throws RemoteControlException {
        ListModel model = component.getModel();
        final RegularMatcher matcher = RemoteControlUtils.toMatcher(text);

        for (int i = 0; i < model.getSize(); i += 1) {
            if (matcher.matches(String.valueOf(model.getElementAt(i)))) {
                return click(robot, component, i);
            }
        }

        return false;
    }

}
