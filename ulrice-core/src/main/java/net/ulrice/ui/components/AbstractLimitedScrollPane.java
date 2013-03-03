package net.ulrice.ui.components;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

/**
 * Abstract base class for the {@link HorizontalScrollPane} and {@link VerticalScrollPane}
 * 
 * @author Manfred Hantschel
 */
public abstract class AbstractLimitedScrollPane extends JScrollPane {

    private static final long serialVersionUID = -6785608117387588377L;

    private class Viewport extends JViewport {

        private static final long serialVersionUID = 3025239163141101382L;

        public Viewport() {
            super();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setViewSize(Dimension newSize) {
            final JScrollPane scrollPane = (JScrollPane) getParent();
            Dimension currentPreferredSize = scrollPane.getPreferredSize();
            final Dimension expectedPreferredSize = getLayout().preferredLayoutSize(this);

            if (scrollPane.getHorizontalScrollBar().isVisible()) {
                expectedPreferredSize.height += scrollPane.getHorizontalScrollBar().getHeight();
            }

            if (scrollPane.getVerticalScrollBar().isVisible()) {
                expectedPreferredSize.width += scrollPane.getVerticalScrollBar().getWidth();
            }

            if (isUpdatePreferredSizeNeeded(currentPreferredSize, expectedPreferredSize)) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        scrollPane.setPreferredSize(expectedPreferredSize);
                        scrollPane.validate();
                    }
                });
            }

            super.setViewSize(newSize);
        }

    }

    public AbstractLimitedScrollPane() {
        super();
    }

    public AbstractLimitedScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
        super(view, vsbPolicy, hsbPolicy);
    }

    public AbstractLimitedScrollPane(Component view) {
        super(view);
    }

    public AbstractLimitedScrollPane(int vsbPolicy, int hsbPolicy) {
        super(vsbPolicy, hsbPolicy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JViewport createViewport() {
        return new Viewport();
    }

    protected abstract boolean isUpdatePreferredSizeNeeded(Dimension currentPreferredSize,
        Dimension expectedPreferredSize);

}
