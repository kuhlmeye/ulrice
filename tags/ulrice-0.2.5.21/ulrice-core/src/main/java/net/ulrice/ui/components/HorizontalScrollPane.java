package net.ulrice.ui.components;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseWheelEvent;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * A {@link JScrollPane} that only scrolls horizontally. In contrast to a default {@link JScrollPane}, it sets the
 * preferred size of the component to fit its height, instead of just hiding the vertical scrollbar. When showing the
 * horizontal scrollbar, it tries to enlarge its own size to keep the size of the viewport (this works, for example,
 * in the north or south part of a BorderLayout).
 * 
 * @author Manfred Hantschel
 */
public class HorizontalScrollPane extends AbstractLimitedScrollPane {

    private static final long serialVersionUID = 4786312248219326535L;

    public HorizontalScrollPane() {
        this(HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    public HorizontalScrollPane(Component view, int hsbPolicy) {
        super(view, VERTICAL_SCROLLBAR_NEVER, hsbPolicy);

        setWheelScrollingEnabled(false);
    }

    public HorizontalScrollPane(Component view) {
        this(view, HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    public HorizontalScrollPane(int hsbPolicy) {
        super(VERTICAL_SCROLLBAR_NEVER, hsbPolicy);

        setWheelScrollingEnabled(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isUpdatePreferredSizeNeeded(Dimension currentPreferredSize, Dimension expectedPreferredSize) {
        return currentPreferredSize.height != expectedPreferredSize.height;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.ui.components.AbstractLimitedScrollPane#fixViewSize(java.awt.Dimension)
     */
    @Override
    protected void fixViewSize(Dimension newSize) {
        newSize.height = getViewportBorderBounds().height;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.Component#processMouseWheelEvent(java.awt.event.MouseWheelEvent)
     */
    @Override
    protected void processMouseWheelEvent(MouseWheelEvent e) {
        // this is a fix for the mouse wheel event to listen to the wheelScrollingEnabled property
        if (!isWheelScrollingEnabled()) {
            if (getParent() != null) {
                getParent().dispatchEvent(SwingUtilities.convertMouseEvent(this, e, getParent()));
            }
            return;
        }

        super.processMouseWheelEvent(e);
    }
}
