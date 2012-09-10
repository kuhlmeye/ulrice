package net.ulrice.remotecontrol.impl.helper;

import java.awt.Robot;

import javax.swing.JCheckBox;

import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.RemoteControlUtils;

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
            if (!component.isEnabled()) {
                throw new RemoteControlException("Failed to select/deselect checkbox. Checkbox is disabled");
            }
            
            try {
                RemoteControlUtils.invokeInSwing(new Runnable() {
                    @Override
                    public void run() {
                        while (component.isSelected() != valueToSet) {
                            component.doClick(5);
                        }
//                        component.setSelected(valueToSet);
                    }
                });
            }
            catch (RemoteControlException e) {
                throw new RemoteControlException("Failed to select/deselect checkbox", e);
            }

            return true;
        }

        return true;
    }

    @Override
    public String getText(JCheckBox component) throws RemoteControlException {
        return String.valueOf(component.isSelected());
    }

}
