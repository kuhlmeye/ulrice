package net.ulrice.ui.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

public class JPopupMenuTriggerListener extends MouseAdapter {
    
    private final JPopupMenu popupMenu;
    private final TriggerType type;
    
    public static enum TriggerType {
        /**
         * All mouse buttons triggers the visibility of the popup menu. 
         */
        ALL_MOUSE_BUTTONS,
        
        /**
         * Only the popup trigger mouse button (usually the right mouse button) triggers the visibility of the popup menu. 
         */
        POPUP_TRIGGER_MOUSE_BUTTON;
    }
    
    public JPopupMenuTriggerListener(final JPopupMenu popupMenu, final TriggerType type) {
        this.popupMenu = popupMenu;
        this.type = type;
    }
    
    @Override
    public void mousePressed(final MouseEvent e) {
        openPopup(e);
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        openPopup(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        openPopup(e);
    }

    private void openPopup(final MouseEvent e) {
        final JComponent source = (JComponent) e.getSource();
        final int x = 0;
        final int y = source.getHeight();
        
        switch (type) {

            case POPUP_TRIGGER_MOUSE_BUTTON:
                if (e.isPopupTrigger()) {
                    popupMenu.show(source, x, y);
                }
                break;
                
            case ALL_MOUSE_BUTTONS:
                popupMenu.show(source, x, y);
                break;
        }
    }
}