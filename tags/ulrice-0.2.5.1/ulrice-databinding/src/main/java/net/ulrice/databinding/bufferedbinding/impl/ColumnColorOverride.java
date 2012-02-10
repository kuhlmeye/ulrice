package net.ulrice.databinding.bufferedbinding.impl;

import java.awt.Color;

/**
 * Object containing the colors for overriding the standard background in the {@link ColumnDefinition}
 *
 * @author rad
 *
 */
public class ColumnColorOverride {
    
    private Color evenNormalColor;
    private Color oddNormalColor;
    private Color evenReadOnlyColor;
    private Color oddReadOnlyColor;
    
    public ColumnColorOverride(Color evenNormalColor, Color oddNormalColor, Color evenReadOnlyColor,
        Color oddReadOnlyColor) {
        super();
        this.evenNormalColor = evenNormalColor;
        this.oddNormalColor = oddNormalColor;
        this.evenReadOnlyColor = evenReadOnlyColor;
        this.oddReadOnlyColor = oddReadOnlyColor;
    }
    
    public Color getEvenNormalColor() {
        return evenNormalColor;
    }
    public void setEvenNormalColor(Color evenNormalColor) {
        this.evenNormalColor = evenNormalColor;
    }
    public Color getOddNormalColor() {
        return oddNormalColor;
    }
    public void setOddNormalColor(Color oddNormalColor) {
        this.oddNormalColor = oddNormalColor;
    }
    public Color getEvenReadOnlyColor() {
        return evenReadOnlyColor;
    }
    public void setEvenReadOnlyColor(Color evenReadOnlyColor) {
        this.evenReadOnlyColor = evenReadOnlyColor;
    }
    public Color getOddReadOnlyColor() {
        return oddReadOnlyColor;
    }
    public void setOddReadOnlyColor(Color oddReadOnlyColor) {
        this.oddReadOnlyColor = oddReadOnlyColor;
    }
}
