package net.ulrice.databinding.viewadapter.utable;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class UTableViewport extends JViewport implements ChangeListener {

    private static final long serialVersionUID = -2957487815567117335L;

    public static final String BACKGROUND_COLOR = "UTableViewport.Background";
    
    private boolean isInEventProcessing = false;

    public UTableViewport() {
        super();

        Color background = (Color) UIManager.get(BACKGROUND_COLOR);

        if (background != null) {
            setBackground(background);
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() instanceof UTableViewport) {            
            UTableViewport otherViewport = (UTableViewport) e.getSource();
            isInEventProcessing = true;
            try {
                setViewPosition(new Point(getViewPosition().x, otherViewport.getViewPosition().y));
            } finally {
                isInEventProcessing = false;
            }
        }
    }

    @Override
    public void scrollRectToVisible(Rectangle arg0) {
        if(isInEventProcessing) {
            return;
        }
        super.scrollRectToVisible(arg0);
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension preferredSize = super.getPreferredSize();
        return new Dimension(getView().getPreferredSize().width, preferredSize.height);
    }

}
