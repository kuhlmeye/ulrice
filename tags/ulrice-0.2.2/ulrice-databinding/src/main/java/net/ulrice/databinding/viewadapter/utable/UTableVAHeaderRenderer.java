/**
 * 
 */
package net.ulrice.databinding.viewadapter.utable;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;
import net.ulrice.databinding.viewadapter.impl.JTableViewAdapter;

/**
 * @author christof
 * 
 */
public class UTableVAHeaderRenderer implements TableCellRenderer {

    private TableCellRenderer labelRenderer;

    /**
     * @param tableGA
     * @param labelRenderer
     */
    public UTableVAHeaderRenderer(JTableViewAdapter tableGA, TableCellRenderer labelRenderer) {
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
