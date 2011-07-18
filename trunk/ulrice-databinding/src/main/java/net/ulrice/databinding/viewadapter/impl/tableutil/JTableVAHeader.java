package net.ulrice.databinding.viewadapter.impl.tableutil;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Extended table header, adding the preferred height of the layout to its
 * preferred height and revalidates when the dragged column is released.
 */
public class JTableVAHeader extends JTableHeader {

    /** Default generated serial version uid. */
    private static final long serialVersionUID = 9040213273578605389L;

    /**
     * Create a new table header with space for possible filter boxes.
     * 
     * @param cm The table column model.
     */
    public JTableVAHeader(TableColumnModel cm, Insets margin) {
        super(cm);

        // Set the layout component.
        setLayout(new TableGAHeaderLayout(margin));

        // Set the alignment of the label renderer to the top.
        TableCellRenderer renderer = getDefaultRenderer();
        if (renderer instanceof JLabel) {
            ((JLabel) renderer).setVerticalAlignment(SwingConstants.TOP);
        }
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
            size.height += layout.preferredLayoutSize(this).height;
        }
        return size;
    }

    /**
     * Layout of the table header of the tablega-table component. This layout
     * allows the creation of jcomponents below/above the header label. This is
     * normally used for the filter components.
     * 
     * @author christof
     * 
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
         * @see java.awt.LayoutManager2#addLayoutComponent(java.awt.Component,
         *      java.lang.Object)
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
         * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String,
         *      java.awt.Component)
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
            if(componentMap.containsValue(comp)) {
                for(Entry<String, Component> entry : componentMap.entrySet()) {
                    if(entry.getValue().equals(comp)) {
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
            if(values != null) {
                for(Component component : values) {
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
