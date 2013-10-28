package net.ulrice.util;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;

public class Gradients {

    private static final Color TOP_COLOR = Colors.brighter(Color.BLUE, 0.5);// Colors.blend(Color.BLUE, Color.WHITE,
                                                                            // 0.9);
    private static final Color BOTTOM_COLOR = Colors.darker(Color.ORANGE, 0.5);// Colors.blend(Color.RED, Color.BLACK,
                                                                               // 0.8);
    private static final Color SHADOW_COLOR = Color.BLACK;

    public static LinearGradientPaint curved(Color color, int width, int height, double curvedFactor, double glossyness) {
        return curved(color, 0, 0, width, height, curvedFactor, glossyness);
    }

    public static LinearGradientPaint curved(Color color, int x, int y, int width, int height, double curvedFactor, double glossyness) {
        return new LinearGradientPaint(new Point2D.Double(x, y), new Point2D.Double(width, height), //
            new float[] { 0.0f, 0.15f, 0.32f, 0.49f, 0.50f, 0.675f, 0.85f, 1.0f }, new Color[] { //
            Colors.blend(Colors.brighter(color, curvedFactor * 0.375), Colors.brighter(TOP_COLOR, 0.00), glossyness * 0.125), //
                Colors.blend(Colors.brighter(color, curvedFactor * 0.25), Colors.brighter(TOP_COLOR, 0.25), glossyness * 0.125), //
                Colors.blend(Colors.brighter(color, curvedFactor * 0.125), Colors.brighter(TOP_COLOR, 0.40), glossyness * 0.125), //
                Colors.blend(Colors.brighter(color, 0), Colors.brighter(TOP_COLOR, 0.50), glossyness * 0.125), //
                Colors.blend(Colors.darker(color, 0), Colors.darker(BOTTOM_COLOR, 0.00), glossyness * 0.125), //
                Colors.blend(Colors.darker(color, curvedFactor * 0.125), Colors.darker(BOTTOM_COLOR, 0.25), glossyness * 0.125), //
                Colors.blend(Colors.darker(color, curvedFactor * 0.25), Colors.darker(BOTTOM_COLOR, 0.40), glossyness * 0.125), //
                Colors.blend(Colors.darker(color, curvedFactor * 0.375), Colors.darker(BOTTOM_COLOR, 0.50), glossyness * 0.125) //
            });
    }

    public static LinearGradientPaint pressed(Color color, int width, int height, double curvedFactor, double glossyness) {
        return pressed(color, 0, 0, width, height, curvedFactor, glossyness);
    }

    public static LinearGradientPaint pressed(Color color, int x, int y, int width, int height, double curvedFactor, double glossyness) {
        return new LinearGradientPaint(new Point2D.Double(x, y), new Point2D.Double(width, height), //
            new float[] { 0.0f, 0.10f, 0.11f, 0.32f, 0.49f, 0.5f, 0.675f, 0.85f, 0.86f, 1.0f }, new Color[] { //
            Colors.blend(Colors.brighter(color, curvedFactor * 0.375), Colors.brighter(TOP_COLOR, 0.25), glossyness * 0.125), //
                Colors.blend(Colors.darker(color, curvedFactor * 0.375), Colors.darker(BOTTOM_COLOR, 0.50), glossyness * 0.125), //
                Colors.blend(Colors.darker(color, curvedFactor * 0.25), Colors.darker(BOTTOM_COLOR, 0.40), glossyness * 0.125), //
                Colors.blend(Colors.darker(color, curvedFactor * 0.125), Colors.darker(BOTTOM_COLOR, 0.25), glossyness * 0.125), //
                Colors.blend(Colors.darker(color, 0), Colors.darker(BOTTOM_COLOR, 0.00), glossyness * 0.125), //
                Colors.blend(Colors.brighter(color, 0), Colors.brighter(TOP_COLOR, 0.50), glossyness * 0.25), //
                Colors.blend(Colors.brighter(color, curvedFactor * 0.125), Colors.brighter(TOP_COLOR, 0.40), glossyness * 0.125), //
                Colors.blend(Colors.brighter(color, curvedFactor * 0.25), Colors.brighter(TOP_COLOR, 0.25), glossyness * 0.125), //
                Colors.blend(Colors.brighter(color, curvedFactor * 0.375), Colors.brighter(TOP_COLOR, 0.00), glossyness * 0.125), //
                Colors.blend(Colors.darker(color, curvedFactor * 0.375), Colors.darker(BOTTOM_COLOR, 0.40), glossyness * 0.125) //
            });
    }

    public static LinearGradientPaint shadow(Color color, int width, int height, double shadowStrength, int shadowLength) {
        return shadow(color, 0, 0, width, height, shadowStrength, shadowLength);
    }

    public static LinearGradientPaint shadow(Color color, int x, int y, int width, int height, double shadowStrength, int shadowLength) {
        double length = Math.sqrt((width * width) + (height * height));
        float fraction = (float) (shadowLength / length);

        return new LinearGradientPaint(new Point2D.Double(x, y), new Point2D.Double(width, height), //
            new float[] { 0.0f, Math.min(fraction / 4, 0.97f), Math.min(fraction / 2, 0.98f), Math.min(fraction, 0.99f), 1.0f }, //
            new Color[] { //
            Colors.blend(color, SHADOW_COLOR, shadowStrength), //
                Colors.blend(color, SHADOW_COLOR, shadowStrength / 2), //
                Colors.blend(color, SHADOW_COLOR, shadowStrength / 4), //
                color, //
                color //
            });
    }

    public static LinearGradientPaint glow(Color color, int width, int height, double strength) {
        return glow(color, 0, 0, width, height, strength);
    }

    public static LinearGradientPaint glow(Color color, int x, int y, int width, int height, double strength) {
        return new LinearGradientPaint(new Point2D.Double(x, y), new Point2D.Double(width, height), //
            new float[] { 0.0f, 0.375f, 0.5f, 0.625f, 1.0f }, //
            new Color[] { //
            Colors.transparent(color, 1), //
                Colors.transparent(color, 1 - (strength * 0.5)), //
                Colors.transparent(color, 1 - strength), //
                Colors.transparent(color, 1 - (strength * 0.5)), //
                Colors.transparent(color, 1) //
            });
    }
}
