package net.ulrice.remotecontrol.impl;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.ulrice.remotecontrol.ApplicationRemoteControl;
import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.ComponentUtils;
import net.ulrice.remotecontrol.util.RemoteControlUtils;

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

}
