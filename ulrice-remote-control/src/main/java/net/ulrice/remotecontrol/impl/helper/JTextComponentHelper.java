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

    @Override
    public boolean isActive(JTextComponent component) throws RemoteControlException {
        return component.isEditable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean enter(Robot robot, final JTextComponent component, final String text)
        throws RemoteControlException {

        try {
            RemoteControlUtils.invokeInSwing(new Runnable() {

                @Override
                public void run() {
                    component.requestFocusInWindow();
                    component.setText(text);
                    component.transferFocus();
                }
            });
        }
        catch (RemoteControlException e) {
            throw new RemoteControlException(String.format("Failed to enter %s into the text component", text), e);
        }

        return true;
    }

}
