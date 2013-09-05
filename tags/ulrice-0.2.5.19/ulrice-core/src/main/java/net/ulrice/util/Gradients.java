package net.ulrice.util;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;

public class Gradients {

    public static LinearGradientPaint curved(Color color, int width, int height, double curvedFactor, double glossyness) {
        return new LinearGradientPaint(new Point2D.Double(0, 0), new Point2D.Double(width, height), //
            new float[] { 0.0f, 0.15f, (float) (0.3 + (0.19 * glossyness)), (float) (0.7 - (0.19 * glossyness)), 0.85f, 1.0f }, //
            new Color[] { //
            Colors.brighter(color, 0.25 * curvedFactor), //
                Colors.brighter(color, 0.1 * curvedFactor), //
                Colors.brighter(color, 0.025 * curvedFactor), //
                Colors.darker(color, 0.025 * curvedFactor), //
                Colors.darker(color, 0.1 * curvedFactor), //
                Colors.darker(color, 0.5 * curvedFactor) //
            });
    }

    public static LinearGradientPaint pressed(Color color, int width, int height, double curvedFactor, double glossyness) {
        return new LinearGradientPaint(new Point2D.Double(0, 0), new Point2D.Double(width, height), //
            new float[] { 0.0f, 0.1f, (float) (0.3 + (0.19 * glossyness)), (float) (0.7 - (0.19 * glossyness)), 0.9f, 1.0f }, //
            new Color[] { //
            Colors.brighter(color, 0.25 * curvedFactor), //
                Colors.darker(color, 0.1 * curvedFactor), //
                Colors.darker(color, 0.025 * curvedFactor), //
                Colors.brighter(color, 0.025 * curvedFactor), //
                Colors.brighter(color, 0.1 * curvedFactor), //
                Colors.darker(color, 0.25 * curvedFactor) //
            });
    }

    public static LinearGradientPaint shadow(Color color, int width, int height, int shadowLength) {
        double shadowStrength = 0.25;
        double length = Math.sqrt((width * width) + (height * height));
        float fraction = (float) (shadowLength / length);

        if (fraction >= 1) {
            return new LinearGradientPaint(new Point2D.Double(0, 0), new Point2D.Double(width, height), //
                new float[] { 0.0f, 1.0f }, //
                new Color[] { //
                Colors.blend(color, Color.BLACK, shadowStrength), //
                    Colors.blend(color, Color.BLACK, shadowStrength - ((shadowStrength * length) / shadowLength)), //
                    color, //
                    color //
                });
        }

        return new LinearGradientPaint(new Point2D.Double(0, 0), new Point2D.Double(width, height), //
            new float[] { 0.0f, fraction, 1.0f }, //
            new Color[] { //
            Colors.blend(color, Color.BLACK, shadowStrength), //
                color, //
                color //
            });
    }

}
