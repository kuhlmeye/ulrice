package net.ulrice.remotecontrol.impl.helper;

import java.awt.Robot;

import javax.swing.text.JTextComponent;

import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.RemoteControlUtils;

public class JTextComponentHelper extends AbstractJComponentHelper<JTextComponent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<JTextComponent> getType() {
        return JTextComponent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean enter(Robot robot, final JTextComponent component, final String text)
        throws RemoteControlException {
        RemoteControlUtils.invokeInSwing(new Runnable() {

            @Override
            public void run() {
                component.setText(text);
            }
        });

        return true;
    }

}
