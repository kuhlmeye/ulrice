package net.ulrice.dashboard;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import net.ulrice.dashboard.gui.RoundedBorder;

/**
 * 
 * The abstract class for dashbord components
 *
 * @author dv20jac
 *
 */
public abstract class DashboardComponent {
    
    private Dimension dashboardDefaultSize = new Dimension(200, 150);
    
    /**
     * Return the default size for the dashboard component
     * 
     * @return The component size
     */
    public Dimension getDashboardSize() {
        return dashboardDefaultSize;
    }
    
    /**
     * Returns the preview icon of the dashboard component
     * itself.
     * 
     * @param percent The scale value
     * @return An preview icon of the component
     */
    public Icon getPreviewIcon(double percent) {
        getDashboardComponent().setSize(getDashboardSize());
        Dimension size = getDashboardComponent().getSize();
        BufferedImage bufferedImage = new BufferedImage(size.width, size.height,
            BufferedImage.TYPE_INT_ARGB);
        
        Graphics bufferedGraphics =  bufferedImage.createGraphics();
        getDashboardComponent().paint(bufferedGraphics);
        bufferedGraphics.drawImage(bufferedImage, 0, 0, null);

        double scaledWidth = size.getWidth() * (percent / 100);
        double scaledHeight = size.getHeight() * (percent / 100);

        Image scaled = bufferedImage.getScaledInstance((int) scaledWidth, 
            (int) scaledHeight, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaled); 
        return icon;
    }
    
    /**
     * Returns the preview icon of the dashboard component
     * itself.
     * 
     * @param width The requested width
     * @param height The requested height
     * @return An preview icon of the component
     */
    public Icon getPreviewIcon(double width, double height) {
        getDashboardComponent().setSize(getDashboardSize());
        Dimension size = getDashboardComponent().getSize();
        BufferedImage bufferedImage = new BufferedImage(size.width, size.height,
            BufferedImage.TYPE_INT_ARGB);
        
        Graphics bufferedGraphics =  bufferedImage.createGraphics();
        getDashboardComponent().paint(bufferedGraphics);
        bufferedGraphics.drawImage(bufferedImage, 0, 0, null);

        Image scaled = bufferedImage.getScaledInstance((int) width, 
            (int) height, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaled); 
        return icon;
    }
    
    /**
     * Returns the name of the component. It can be used in combination
     * with the preview icon.
     * 
     * @return The name of the dashboard component
     */
    public abstract String getPreviewName();
    
    /**
     * Returns the dashboard component. It must be a separate instance of an JCombonent
     * and not equal to the component that will be return by <code>IFView</code>
     * 
     * @return The dashboard component.
     */
    public abstract JComponent getDashboardComponent();
    
    /**
     * Returns a unique id to identify the DashboardComponent.
     * 
     * @return Unique Id of the DashboardComponent.
     */
    public abstract String getUniqueId();
    
    /**
     * Enable or disable the resizing icon
     * 
     * @param enabled Enable or disable the icon
     */
    public void resizingEnabled(boolean enabled) {
        if (getDashboardComponent() != null 
                && getDashboardComponent().getBorder() instanceof RoundedBorder) {
            ((RoundedBorder) getDashboardComponent().getBorder()).setResizingEnabled(enabled);
            getDashboardComponent().repaint();
        }
    }
    
    /**
     * Enable or disable the closing icon.
     * 
     * @param enabled Enable or disable the icon
     */
    public void closingEnabled(boolean enabled) {
        if (getDashboardComponent() != null 
                && getDashboardComponent().getBorder() instanceof RoundedBorder) {
            ((RoundedBorder) getDashboardComponent().getBorder()).setClosingEnabled(enabled);
            getDashboardComponent().repaint();
        }
    }
    
    /**
     * Override this method if necessary to update the dashboard component view
     */
    public void update(){
    }
}
