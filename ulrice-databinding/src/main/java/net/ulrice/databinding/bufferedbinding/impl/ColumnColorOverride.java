package net.ulrice.databinding.bufferedbinding.impl;

import java.awt.Color;

/**
 * Object containing the colors for overriding the standard background in the {@link ColumnDefinition}
 *
 * @author rad
 *
 */
public class ColumnColorOverride {
    
    private Color color;
    
    public ColumnColorOverride(Color color) {
        super();
        
        this.color = color;
    }
    
    public Color getColor() {
        return color;
    }
    
}
