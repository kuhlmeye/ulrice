/**
 * 
 */
package net.ulrice.databinding.viewadapter.utable;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;

/**
 * @author christof
 */
public class UTableVADefaultRenderer extends AbstractUTableRenderer {

    private static final long serialVersionUID = -5400969133715647268L;


    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object,
     *      boolean, boolean, int, int)
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
        int row, int column) {

        JComponent component = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        return adaptComponent(table, isSelected, row, column, component);
    }

}
