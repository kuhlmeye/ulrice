/**
 * 
 */
package net.ulrice.databinding.viewadapter.utable;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;
import net.ulrice.databinding.viewadapter.impl.JTableViewAdapter;

/**
 * @author christof
 */
public class UTableVAHeaderRenderer implements TableCellRenderer {

    private TableCellRenderer labelRenderer;

    private ExpandColapsePanel expandColapsePanel;

    /**
     * @param tableGA
     * @param labelRenderer
     */
    public UTableVAHeaderRenderer(TableCellRenderer labelRenderer) {
        this.labelRenderer = labelRenderer;
    }

    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object,
     *      boolean, boolean, int, int)
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
        int row, int column) {

        Object renderValue = value;
        String tooltipText = null;
        if (value instanceof ColumnDefinition< ?>) {
            ColumnDefinition< ?> columnDefinition = (ColumnDefinition< ?>) value;
            renderValue = columnDefinition.getColumnName();
            tooltipText = columnDefinition.getColumnTooltip();
        }

        Component component =
                labelRenderer.getTableCellRendererComponent(table, renderValue, isSelected, hasFocus, row, column);
        if (JComponent.class.isAssignableFrom(JComponent.class)) {
            ((JComponent) component).setToolTipText(tooltipText);
        }

        if (column == 0) {
            if (table instanceof UTable) {
                UTable uTable = (UTable) table;
                if (uTable.getTableComponent() instanceof UTreeTableComponent) {
                    UTreeTableComponent treeTable = (UTreeTableComponent) uTable.getTableComponent();
                    if (expandColapsePanel == null) {
                        expandColapsePanel = new ExpandColapsePanel(treeTable);
                    }
                    component = expandColapsePanel;
                }

            }
        }
        return component;
    }
}
