package net.ulrice.remotecontrol.impl;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
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

        BufferedImage screenshot;

        if (rectangle == null) {
            screenshot = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
            
            Graphics2D g = screenshot.createGraphics();
            
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            g.setColor(Color.GRAY);
            g.drawString("[process has no window]", 10, 20);
        }
        else {
            screenshot = robot.createScreenCapture(rectangle);
        }

        // if (description != null) {
        // Graphics2D g = screenshot.createGraphics();
        //
        // int h = DESCRIPTION_HEIGHT;
        // int w = screenshot.getWidth() - 32;
        // int x = 16;
        // int y = screenshot.getHeight() - h - 10;
        //
        // g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //
        // g.setColor((failure) ? new Color(0xa0ffa0a0, true) : new Color(0xa0ffffff, true));
        // g.fillRoundRect(x, y, w, h, 16, 16);
        //
        // g.setColor(new Color(0xa0000000, true));
        // g.drawRoundRect(x, y, w, h, 16, 16);
        //
        // g.clipRect(x, y, w, h);
        // y = y + 10;
        //
        // for (String d : description.split("\\n")) {
        // AttributedString s = new AttributedString(d);
        // s.addAttribute(TextAttribute.FONT, new Font(Font.MONOSPACED, Font.PLAIN, 10));
        // s.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);
        //
        // AttributedCharacterIterator iterator = s.getIterator();
        // FontRenderContext fontRenderContext = g.getFontRenderContext();
        // LineBreakMeasurer lineBreakMeasurer = new LineBreakMeasurer(iterator, fontRenderContext);
        //
        // while (lineBreakMeasurer.getPosition() < iterator.getEndIndex()) {
        // TextLayout textLayout = lineBreakMeasurer.nextLayout(w - 32);
        // y += textLayout.getAscent();
        // textLayout.draw(g, x + 16, y);
        // y += textLayout.getDescent() + textLayout.getLeading();
        // }
        // }
        // }

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
    public boolean combinedWaitFor(double timeoutInSeconds, final ComponentMatcher componentMatcher, final ControllerMatcher controllerMatcher) throws RemoteControlException {

        try {
            return RemoteControlUtils.repeatInThread(timeoutInSeconds, new ResultClosure<Boolean>() {

                @Override
                public void invoke(Result<Boolean> result) throws RemoteControlException {
                    Collection<ComponentState> componentStates = RemoteControlCenter.componentRC().statesOf(componentMatcher);
                    Collection<ControllerState> controllerStates = RemoteControlCenter.controllerRC().statesOf(controllerMatcher);

                    if (((componentStates != null) && (componentStates.size() > 0)) || ((controllerStates != null) && (controllerStates.size() > 0))) {
                        result.fireResult(true);
                    }
                }

            });
        }
        catch (RemoteControlException e) {
            throw new RemoteControlException(String.format("Failed to wait %,.1f s for all components or controllers: %s or %s", timeoutInSeconds, componentMatcher,
                controllerMatcher), e);
        }
    }

}
