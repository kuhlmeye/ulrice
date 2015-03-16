package net.ulrice.util;

import java.awt.AWTKeyStroke;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

/**
 * Utilities for keys, keyStrokes, keyEvents
 *
 * @author HAM
 */
public class Keys {

    /**
     * Describes the key stroke for humans
     *
     * @param keyStroke the key stroke
     * @return the description
     */
    public static String describe(KeyStroke keyStroke) {
        if (keyStroke == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        if (keyStroke.getModifiers() != 0) {
            builder.append(KeyEvent.getKeyModifiersText(keyStroke.getModifiers())).append("+");
        }

        builder.append(KeyEvent.getKeyText(keyStroke.getKeyCode()));

        return builder.toString();
    }

    /**
     * Returns true if the key event describes the key stroke
     *
     * @param keyStroke the key stroke
     * @param keyEvent the key event
     * @return true if there's a match
     */
    public static boolean matches(KeyStroke keyStroke, KeyEvent keyEvent) {
        return AWTKeyStroke.getAWTKeyStrokeForEvent(keyEvent).equals(AWTKeyStroke.getAWTKeyStroke(keyStroke.getKeyCode(), keyStroke.getModifiers(), !keyStroke.isOnKeyRelease()));
    }
}
