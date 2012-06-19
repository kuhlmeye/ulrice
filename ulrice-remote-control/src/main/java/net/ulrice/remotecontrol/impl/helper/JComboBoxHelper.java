package net.ulrice.remotecontrol.impl.helper;

import java.awt.Robot;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

import net.ulrice.remotecontrol.ComponentListData;
import net.ulrice.remotecontrol.ComponentMatcher;
import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.RegularMatcher;
import net.ulrice.remotecontrol.util.RemoteControlUtils;
import net.ulrice.remotecontrol.util.Result;

public class JComboBoxHelper extends AbstractJComponentHelper<JComboBox> {

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.impl.helper.ComponentHelper#getType()
     */
    @Override
    public Class<JComboBox> getType() {
        return JComboBox.class;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.impl.helper.AbstractComponentHelper#getText(java.awt.Component)
     */
    @Override
    public String getText(JComboBox component) throws RemoteControlException {
        Object selectedItem = component.getSelectedItem();

        return (selectedItem != null) ? String.valueOf(selectedItem) : null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.impl.helper.AbstractComponentHelper#getData(java.awt.Component)
     */
    @Override
    public Object getData(JComboBox component) throws RemoteControlException {
        ComponentListData data = new ComponentListData();
        ComboBoxModel model = component.getModel();

        for (int i = 0; i < model.getSize(); i += 1) {
            Object element = model.getElementAt(i);
            data.addEntry( element != null ? element.toString() : null, model.getSelectedItem() == element);
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
    public boolean enter(final Robot robot, final JComboBox component, final String text)
        throws RemoteControlException {
        final Result<Boolean> result = new Result<Boolean>(1);
        final RegularMatcher matcher = RemoteControlUtils.toMatcher(text);

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
                
                if (text.isEmpty()) {
                    component.setSelectedIndex(-1);
                    result.fireResult(true);
                    return;
                }

                result.fireResult(false);
            }

        });

        try {
            return result.aquireResult();
        }
        catch (RemoteControlException e) {
            throw new RemoteControlException("Entering \"" + text + "\" into JComboBox failed", e);
        }
    }

}
