package net.ulrice.databinding.viewadapter.utable;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.Painter;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Extended table header, adding the preferred height of the layout to its preferred height and revalidates when the
 * dragged column is released.
 */
public class UTableVAHeader extends JTableHeader {

    /** Default generated serial version uid. */
    private static final long serialVersionUID = 9040213273578605389L;
    
    public static final String UI_NAME = UTableVAHeader.class.getSimpleName();
    public static final String UI_BACKGROUND_PAINTER = UI_NAME + ".backgroundPainter";

    private static final UTableVAHeaderPainter UI_BACKGROUND_PAINTER_DEFAULT = new UTableVAHeaderPainter();

    /**
     * if the components (serach filter should extend the heigt of the table header
     */
    private boolean extendInHeight = true;

    private Painter<UTableVAHeader> painter;

    /**
     * Create a new table header with space for possible filter boxes.
     * 
     * @param cm The table column model.
     */
    public UTableVAHeader(TableColumnModel cm, Insets margin) {
        super(cm);

        // Set the layout component.
        setLayout(new TableGAHeaderLayout(margin));

        // Set the alignment of the label renderer to the top.
        TableCellRenderer renderer = getDefaultRenderer();
        setDefaultRenderer(new UTableVAHeaderRenderer(renderer));
        if (renderer instanceof JLabel) {
            ((JLabel) renderer).setVerticalAlignment(SwingConstants.TOP);
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();

        @SuppressWarnings("unchecked")
        Painter<UTableVAHeader> painter = (Painter<UTableVAHeader>) UIManager.get(UI_BACKGROUND_PAINTER);

        this.painter = (painter != null) ? painter : UI_BACKGROUND_PAINTER_DEFAULT;
    }

    /**
     * @see javax.swing.table.JTableHeader#columnMoved(javax.swing.event.TableColumnModelEvent)
     */
    @Override
    public void columnMoved(TableColumnModelEvent e) {
        super.columnMoved(e);

        // Repaint the column
        if (getDraggedColumn() != null) {
            revalidate();
            repaint();
        }
    }

    /**
     * @see javax.swing.JComponent#processKeyBinding(javax.swing.KeyStroke, java.awt.event.KeyEvent, int, boolean)
     */
    @Override
    protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
        if (ks.getKeyCode() == KeyEvent.VK_SPACE && ks.getKeyEventType() == KeyEvent.KEY_PRESSED) {
            return true; // BUG: 2896 catch space event for preventing sort toggle
        }
        return super.processKeyBinding(ks, e, condition, pressed);
    }

    /**
     * @see javax.swing.table.JTableHeader#setDraggedColumn(javax.swing.table.TableColumn)
     */
    @Override
    public void setDraggedColumn(TableColumn column) {
        super.setDraggedColumn(column);

        // Repaint the column
        if (column == null) {
            revalidate();
            repaint();
        }
    }

    /**
     * @see javax.swing.JComponent#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize() {
        // Extend the height of the header.
        final Dimension size = super.getPreferredSize();
        final LayoutManager layout = getLayout();
        if (layout != null) {
            if (isExtendInHeight()) {
                size.height += layout.preferredLayoutSize(this).height;
            }
            else {
                size.height = Math.max(layout.preferredLayoutSize(this).height, size.height);
            }

        }
        return size;
    }

    public boolean isExtendInHeight() {
        return extendInHeight;
    }

    public void setExtendInHeight(boolean extendInHeight) {
        this.extendInHeight = extendInHeight;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        painter.paint((Graphics2D) g.create(), this, getWidth(), getHeight());
    }

    /**
     * Layout of the table header of the tablega-table component. This layout allows the creation of jcomponents
     * below/above the header label. This is normally used for the filter components.
     * 
     * @author christof
     */
    private final class TableGAHeaderLayout implements LayoutManager2, Serializable {

        /** Default generated serial version. */
        private static final long serialVersionUID = 4892824934320290149L;

        /** Map containing the components for each column with the id as a key. */
        private Map<String, Component> componentMap = new HashMap<String, Component>();

        /** The margin between the filter fields and the rest. */
        private Insets margin;

        /**
         * @param margin
         */
        public TableGAHeaderLayout(Insets margin) {
            this.margin = margin;
        }

        /**
         * @see java.awt.LayoutManager2#addLayoutComponent(java.awt.Component, java.lang.Object)
         */
        @Override
        public void addLayoutComponent(Component comp, Object constraints) {
            if (constraints == null) {
                throw new IllegalArgumentException("Layout constraint must not be null.");
            }

            String columnId = constraints.toString();
            componentMap.put(columnId, comp);
        }

        /**
         * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String, java.awt.Component)
         */
        @Override
        public void addLayoutComponent(String name, Component comp) {
            addLayoutComponent(comp, name);
        }

        /**
         * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
         */
        @Override
        public void removeLayoutComponent(Component comp) {
            if (componentMap.containsValue(comp)) {
                for (Entry<String, Component> entry : componentMap.entrySet()) {
                    if (entry.getValue().equals(comp)) {
                        componentMap.remove(entry.getKey());
                        return;
                    }
                }
            }
        }

        /**
         * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
         */
        @Override
        public void layoutContainer(Container parent) {
            JTableHeader header = (JTableHeader) parent;
            TableColumnModel columnModel = header.getColumnModel();
            for (int i = 0; i < columnModel.getColumnCount(); i++) {
                TableColumn column = columnModel.getColumn(i);
                Object columnId = column.getIdentifier();
                if (componentMap.containsKey(columnId)) {
                    Component filterComponent = componentMap.get(columnId);
                    Rectangle rect = header.getHeaderRect(i);
                    final Dimension size = filterComponent.getPreferredSize();
                    rect.x += margin.left;
                    rect.y += margin.top;
                    rect.width -= margin.left + margin.right;
                    rect.height -= margin.top + margin.bottom;
                    if (rect.height > size.height) {
                        rect.y += rect.height - size.height;
                        rect.height = size.height;
                    }
                    if (!isExtendInHeight()) {
                        rect.y = margin.top;
                        rect.x = rect.x + rect.width-size.width;
                    }
                    filterComponent.setBounds(rect);
                }
            }
        }

        /**
         * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
         */
        @Override
        public Dimension preferredLayoutSize(Container parent) {
            Collection<Component> values = componentMap.values();
            int h = 0;
            if (values != null) {
                for (Component component : values) {
                    h = Math.max(h, component.getPreferredSize().height);
                }
            }
            return new Dimension(0, margin.top + margin.bottom + h);
        }

        /**
         * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
         */
        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension();
        }

        /**
         * @see java.awt.LayoutManager2#maximumLayoutSize(java.awt.Container)
         */
        @Override
        public Dimension maximumLayoutSize(Container target) {
            return new Dimension();
        }

        /**
         * @see java.awt.LayoutManager2#getLayoutAlignmentX(java.awt.Container)
         */
        @Override
        public float getLayoutAlignmentX(Container target) {
            return 0.5f;
        }

        /**
         * @see java.awt.LayoutManager2#getLayoutAlignmentY(java.awt.Container)
         */
        @Override
        public float getLayoutAlignmentY(Container target) {
            return 0.5f;
        }

        /**
         * @see java.awt.LayoutManager2#invalidateLayout(java.awt.Container)
         */
        @Override
        public void invalidateLayout(Container target) {
        }
    }
}
