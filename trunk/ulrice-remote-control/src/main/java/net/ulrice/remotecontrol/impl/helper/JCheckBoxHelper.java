package net.ulrice.remotecontrol.impl.helper;

import java.awt.Robot;

import javax.swing.JCheckBox;

import net.ulrice.remotecontrol.RemoteControlException;

public class JCheckBoxHelper extends AbstractJComponentHelper<JCheckBox> {

    @Override
    public Class<JCheckBox> getType() {
        return JCheckBox.class;
    }

    @Override
    public boolean enter(Robot robot, final JCheckBox component, String text) throws RemoteControlException {
        final boolean valueToSet = Boolean.parseBoolean(text);
        final boolean currentValue = component.isSelected();

        if (valueToSet != currentValue) {
            return click(robot, component);
        }

        return true;
    }

    @Override
    public String getText(JCheckBox component) throws RemoteControlException {
        return String.valueOf(component.isSelected());
    }

}
