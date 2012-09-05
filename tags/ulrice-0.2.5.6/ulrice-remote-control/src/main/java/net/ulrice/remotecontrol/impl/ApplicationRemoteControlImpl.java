package net.ulrice.remotecontrol.impl;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

import javax.imageio.ImageIO;

import net.ulrice.remotecontrol.ApplicationRemoteControl;
import net.ulrice.remotecontrol.ComponentMatcher;
import net.ulrice.remotecontrol.ComponentState;
import net.ulrice.remotecontrol.ControllerMatcher;
import net.ulrice.remotecontrol.ControllerState;
import net.ulrice.remotecontrol.RemoteControlCenter;
import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.ComponentUtils;
import net.ulrice.remotecontrol.util.RemoteControlUtils;
import net.ulrice.remotecontrol.util.Result;
import net.ulrice.remotecontrol.util.ResultClosure;

/**
 * Implementation of the {@link ApplicationRemoteControl}
 * 
 * @author Manfred HANTSCHEL
 */
public class ApplicationRemoteControlImpl implements ApplicationRemoteControl {

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ApplicationRemoteControl#ping()
     */
    @Override
    public boolean ping() {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ApplicationRemoteControl#shutdown()
     */
    @Override
    public void shutdown() {
        System.exit(0);
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ApplicationRemoteControl#screenshot()
     */
    @Override
    public byte[] screenshot() throws RemoteControlException {
        Window[] windows = Window.getWindows();
        Rectangle rectangle = null;

        for (Window window : windows) {
            if (window.isVisible()) {
                Rectangle bounds = window.getBounds();

                rectangle = (rectangle != null) ? bounds.union(rectangle) : bounds;

                ComponentUtils.toFront(window);
            }
        }

        Robot robot;

        try {
            robot = new Robot();
        }
        catch (AWTException e) {
            throw new RemoteControlException("Failed to tell robot to capture screenshot", e);
        }

        BufferedImage screenshot = robot.createScreenCapture(rectangle);

        ByteArrayOutputStream out;

        try {
            out = new ByteArrayOutputStream();

            try {
                ImageIO.write(screenshot, "PNG", out);

            }
            finally {
                out.close();
            }
        }
        catch (IOException e) {
            throw new RemoteControlException("Failed to write screenshot", e);
        }

        return out.toByteArray();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ApplicationRemoteControl#overrideSpeedFactor(double)
     */
    @Override
    public void overrideSpeedFactor(double speedFactor) {
        RemoteControlUtils.overrideSpeedFactor(speedFactor);
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ApplicationRemoteControl#combinedWaitFor(double,
     *      net.ulrice.remotecontrol.ComponentMatcher, net.ulrice.remotecontrol.ControllerMatcher)
     */
    @Override
    public boolean combinedWaitFor(double timeoutInSeconds, final ComponentMatcher componentMatcher,
        final ControllerMatcher controllerMatcher) throws RemoteControlException {

        try {
            return RemoteControlUtils.repeatInThread(timeoutInSeconds, new ResultClosure<Boolean>() {

                @Override
                public void invoke(Result<Boolean> result) throws RemoteControlException {
                    Collection<ComponentState> componentStates =
                            RemoteControlCenter.componentRC().statesOf(componentMatcher);
                    Collection<ControllerState> controllerStates =
                            RemoteControlCenter.controllerRC().statesOf(controllerMatcher);

                    if (((componentStates != null) && (componentStates.size() > 0))
                        || ((controllerStates != null) && (controllerStates.size() > 0))) {
                        result.fireResult(true);
                    }
                }

            });
        }
        catch (RemoteControlException e) {
            throw new RemoteControlException(String.format("Failed to wait %,.1f s for all components or controllers: %s or %s",
                timeoutInSeconds, componentMatcher, controllerMatcher), e);
        }
    }

}
