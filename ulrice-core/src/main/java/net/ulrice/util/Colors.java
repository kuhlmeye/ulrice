package net.ulrice.util;

import java.awt.Color;

public class Colors {

    public static float[] colorToRgb(Color color) {
        return new float[] { color.getRed() / 256f, color.getGreen() / 256f, color.getBlue() / 256f };
    }

    public static float[] colorToYuv(Color color) {
        return rgbToYuv(new float[] { color.getRed() / 256f, color.getGreen() / 256f, color.getBlue() / 256f });
    }

    public static Color rgbToColor(float[] rgb) {
        return new Color(bound(rgb[0], 0, 1), bound(rgb[1], 0, 1), bound(rgb[2], 0, 1));
    }

    public static Color yuvToColor(float[] yuv, float alpha) {
        return rgbToColor(yuvToRgb(yuv), alpha);
    }

    public static Color rgbToColor(float[] rgb, float alpha) {
        return new Color(bound(rgb[0], 0, 1), bound(rgb[1], 0, 1), bound(rgb[2], 0, 1), bound(alpha, 0, 1));
    }

    public static float[] rgbToYuv(float[] rgb) {
        return new float[] { //
        (0.299f * rgb[0]) + (0.587f * rgb[1]) + (0.144f * rgb[2]), //
            (-0.14713f * rgb[0]) + (-0.28886f * rgb[1]) + (0.436f * rgb[2]), //
            (0.615f * rgb[0]) + (-0.51499f * rgb[1]) + (-0.10001f * rgb[2]) //
        };
    }

    public static float[] yuvToRgb(float[] yuv) {
        return new float[] { //
        (1f * yuv[0]) + (0f * yuv[1]) + (1.13983f * yuv[2]), //
            (1f * yuv[0]) + (-0.39465f * yuv[1]) + (-0.58060f * yuv[2]), //
            (1f * yuv[0]) + (2.03211f * yuv[1]) + (0f * yuv[2]) //
        };
    }

    /**
     * Makes the color darker by the factor (-1 <= factor <= 1). If the factor is negative, the color gets brighter
     * 
     * @param color the color
     * @param factor the factor
     * @return the color
     */
    public static Color darker(Color color, double factor) {
        return brighter(color, -factor);
    }

    /**
     * Makes the color brighter by the factor (-1 <= factor <= 1). If the factor is negative, the color gets darker
     * 
     * @param color the color
     * @param factor the factor
     * @return the color
     */
    public static Color brighter(Color color, double factor) {
        float[] yuv = colorToYuv(color);

        yuv[0] += factor;

        return yuvToColor(yuv, color.getAlpha() / 256f);
    }

    public static Color transparent(Color color, double factor) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), bound((int) (255 - (factor * 255)), 0, 255));
    }

    /**
     * Semantically wrong
     * 
     * @param color
     * @param factor
     * @return
     */
    @Deprecated
    public static Color translucent(Color color, double factor) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), bound((int) (color.getAlpha() * factor), 0, 255));
    }

    public static Color blend(Color color1, Color color2, double factor) {
        int red = (int) (color1.getRed() + ((color2.getRed() - color1.getRed()) * factor));
        int green = (int) (color1.getGreen() + ((color2.getGreen() - color1.getGreen()) * factor));
        int blue = (int) (color1.getBlue() + ((color2.getBlue() - color1.getBlue()) * factor));
        int alpha = (int) (color1.getAlpha() + ((color2.getAlpha() - color1.getAlpha()) * factor));
        
        return new Color(bound(red, 0, 255), bound(green, 0, 255), bound(blue, 0, 255), bound(alpha, 0, 255));
    }
    
    private static float bound(float value, float minimum, float maximum) {
        return Math.min(maximum, Math.max(value, minimum));
    }

    private static int bound(int value, int minimum, int maximum) {
        return Math.min(maximum, Math.max(value, minimum));
    }
}
