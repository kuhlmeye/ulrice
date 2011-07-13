/**
 * 
 */
package net.ulrice.databinding.viewadapter.impl;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * @author christof
 *
 */
public class JTableVADefaultRenderer extends DefaultTableCellRenderer {

    
    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

}
