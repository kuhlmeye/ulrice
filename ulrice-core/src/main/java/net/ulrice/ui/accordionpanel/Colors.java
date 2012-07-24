package net.ulrice.ui.accordionpanel;

import java.awt.Color;

public class Colors
{

    public static Color darker(Color color, double factor)
    {
        return new Color(Math.max((int) (color.getRed() - (color.getRed() * factor)), 0), Math.max(
            (int) (color.getGreen() - (color.getGreen() * factor)), 0), Math.max(
            (int) (color.getBlue() - (color.getBlue() * factor)), 0), color.getAlpha());
    }

    public static Color brighter(Color color, double factor)
    {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int alpha = color.getAlpha();

        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
        int i = (int) (1.0 / (1.0 - factor));
        if ((r == 0) && (g == 0) && (b == 0))
        {
            return new Color(i, i, i, alpha);
        }
        if ((r > 0) && (r < i))
        {
            r = i;
        }
        if ((g > 0) && (g < i))
        {
            g = i;
        }
        if ((b > 0) && (b < i))
        {
            b = i;
        }

        return new Color(Math.min((int) (r / factor), 255), Math.min((int) (g / factor), 255), Math.min(
            (int) (b / factor), 255), alpha);
    }

    public static Color translucent(Color color, double factor)
    {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * factor));
    }

    public static Color blend(Color color1, Color color2, double factor)
    {
        return new Color((int) (color1.getRed() + (color2.getRed() - color1.getRed()) * (1-factor)),
            (int) (color1.getGreen() + (color2.getGreen() - color1.getGreen()) * (1-factor)),
            (int) (color1.getBlue() + (color2.getBlue() - color1.getBlue()) * (1-factor)),
            (int) (color1.getAlpha() + (color2.getAlpha() - color1.getAlpha()) * (1-factor)));
    }

}
