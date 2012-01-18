package net.ulrice.remotecontrol.impl.helper;

import java.awt.Robot;

import javax.swing.JCheckBox;

import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.RemoteControlUtils;
import net.ulrice.remotecontrol.util.Result;

public class JCheckBoxHelper extends AbstractJComponentHelper<JCheckBox> {

    @Override
    public Class<JCheckBox> getType() {
        return JCheckBox.class;
    }

    @Override
    public boolean enter(Robot robot, final JCheckBox component, String text) throws RemoteControlException {
        final Result<Boolean> result = new Result<Boolean>(1);
        final boolean valueToSet = Boolean.parseBoolean(text);
        final boolean currentValue = component.isSelected();
        
        if (valueToSet != currentValue) {
            return click(robot, component);
        }
        
        return true;

//       
//        RemoteControlUtils.invokeInSwing(new Runnable() {
//
//            @Override
//            public void run() {
//                component.setSelected(valueToSet);
//                result.fireResult(true);
//            }
//
//        });
//
//        return result.aquireResult();
    }

    @Override
    public String getText(JCheckBox component) {
        return String.valueOf(component.isSelected());
    }

}
