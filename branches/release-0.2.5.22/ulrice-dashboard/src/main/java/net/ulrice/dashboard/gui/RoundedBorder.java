/**
 * 
 */
package net.ulrice.dashboard.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.border.AbstractBorder;

/**
 * Rounded border for dashboard components
 * 
 * @author dv20jac
 */
public class RoundedBorder extends AbstractBorder {
    /**
     * Generated id
     */
    private static final long serialVersionUID = 7875311495759374472L;
    
    /**The insets */
    private Insets insets;  
    /**The stroke */
    private Stroke stroke;  
    /**The color */
    private Color strokeColor;  
    /**The roundness */
    private int arc;  
    /**The stroke width */
    private float strokeWidth;  
    /**Resizing flag */
    private boolean resizingEnabled;
    /**Closing flag */
    private boolean closingEnabled;
  
    /**
     * Create a new rounded border
     * 
     * @param arc The roundness of the edges
     */
    public RoundedBorder(int arc) {  
        this.arc = arc;  
        int i = (int) (arc / Math.PI) / 2;  
        insets = new Insets(i, i, i, i);  
    }  
    /** 
    * Rounded border with an outline 
    * @param arc The roundness of the edges
    * @param strokeWidth Width of the outline 
    * @param color color of the outline     
    */  
    public RoundedBorder(int arc, float strokeWidth, Color color) {
        this.arc = arc;  
        int i = (int) ((arc / Math.PI) + ((strokeWidth * 2) / (Math.PI)));  
        insets = new Insets(i, i, i, i);  
        this.stroke = new BasicStroke(strokeWidth);  
        this.strokeColor = color;  
        this.strokeWidth = strokeWidth;  
    }  

    /**
     * 
     * {@inheritDoc}
     * @see javax.swing.border.AbstractBorder#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int)
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {  
        Graphics2D g2 = (Graphics2D) g.create();  
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  
                         RenderingHints.VALUE_ANTIALIAS_ON);  
 
            // In real code optimize by preserving the rect between calls  
        if (stroke != null) {  
            int i = (int) strokeWidth / 2;  
            RoundRectangle2D.Float rect =  new RoundRectangle2D.Float(i, i, width - strokeWidth,  
                                      height - strokeWidth, arc, arc);  
            g2.translate(x, y);  
            g2.setColor(c.getBackground());  
            //g2.fill(rect);  
            g2.setColor(strokeColor);  
            g2.setStroke(stroke);  
            g2.draw(rect);
        } 
        else {
            RoundRectangle2D.Float rect =  
                 new RoundRectangle2D.Float(0, 0, width, height, arc, arc);     
            g2.translate(x, y);  
            g2.setColor(c.getBackground());  
            g2.fill(rect);  
        }
        if (resizingEnabled) {
            Polygon triangle = new Polygon();
            
            triangle.addPoint(width - 4, height - 4);   
            triangle.addPoint(width - 6, height - 2);            
            triangle.addPoint(width - 20, height - 2);
            triangle.addPoint(width - 2, height - 20);
            triangle.addPoint(width - 2, height - 6);
            
            g2.fillPolygon(triangle);
        }
        if (closingEnabled) {
            Shape shape = new Ellipse2D.Float(width - 16, 3, 12, 12);
            g2.setColor(strokeColor);
            g2.fill(shape);  
              
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND,  
                BasicStroke.JOIN_ROUND));  
            g2.draw(shape);
            
            g2.setColor(Color.WHITE);
            g2.drawLine(width - 13, 6, width - 7 , 13); 
            g2.drawLine(width - 7, 6, width - 13 , 13); 
        }
    }
    
    /**
     * Sets the value to enable or disable the resizing button
     * 
     * @param enabled If true then is the resizing button visible, otherwise not
     */
    public void setResizingEnabled(boolean enabled) {
        this.resizingEnabled = enabled;
    }
    
    /**
     * Sets the value to enable or disable the closing button
     * 
     * @param enabled If true then is the colsing button visible, otherwise not
     */
    public void setClosingEnabled(boolean enabled) {
        this.closingEnabled = enabled;
    }
}

