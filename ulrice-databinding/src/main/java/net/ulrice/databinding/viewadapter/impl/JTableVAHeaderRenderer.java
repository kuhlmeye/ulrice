/**
 * 
 */
package net.ulrice.databinding.viewadapter.impl;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import net.ulrice.databinding.impl.am.ColumnDefinition;

/**
 * @author christof
 * 
 */
public class JTableVAHeaderRenderer implements TableCellRenderer {

    private TableCellRenderer labelRenderer;

    /**
     * @param tableGA
     * @param labelRenderer
     */
    public JTableVAHeaderRenderer(JTableViewAdapter tableGA, TableCellRenderer labelRenderer) {
        this.labelRenderer = labelRenderer;
    }

    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
     *      java.lang.Object, boolean, boolean, int, int)
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {

        Object renderValue = value;
        if (value instanceof ColumnDefinition<?>) {
            renderValue = ((ColumnDefinition<?>) value).getColumnName();
        }

        return labelRenderer.getTableCellRendererComponent(table, renderValue, isSelected, hasFocus, row, column);
    }
}
