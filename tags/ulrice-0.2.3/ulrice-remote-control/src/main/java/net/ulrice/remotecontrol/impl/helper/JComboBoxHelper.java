package net.ulrice.remotecontrol.impl.helper;

import java.awt.Robot;

import javax.swing.JComboBox;

import net.ulrice.remotecontrol.RemoteControlException;
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
    public String getText(JComboBox component) {
        Object selectedItem = component.getSelectedItem();

        return (selectedItem != null) ? String.valueOf(selectedItem) : null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.impl.helper.AbstractComponentHelper#enter(java.awt.Robot, java.awt.Component,
     *      java.lang.String)
     */
    @Override
    public boolean enter(final Robot robot, final JComboBox component, final String text) throws RemoteControlException {
        final Result<Boolean> result = new Result<Boolean>(1);
        RemoteControlUtils.invokeInSwing(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < component.getModel().getSize(); i += 1) {
                    if (text.equals(String.valueOf(component.getModel().getElementAt(i)))) {
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

}
