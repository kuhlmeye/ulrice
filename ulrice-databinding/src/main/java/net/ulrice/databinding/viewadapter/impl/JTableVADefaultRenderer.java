/**
 * 
 */
package net.ulrice.databinding.viewadapter.impl;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import net.ulrice.databinding.bufferedbinding.AbstractTableAM;
import net.ulrice.databinding.bufferedbinding.ColumnDefinition;

/**
 * @author christof
 *
 */
public class JTableVADefaultRenderer extends DefaultTableCellRenderer {

	public JTableViewAdapter tableVA;
	
	private Color evenNormalBackground = new Color(230, 230, 230);	
	private Color oddNormalBackground = new Color(200, 200, 200);
	private Color evenReadOnlyBackground = new Color(200, 230, 200);
	private Color oddReadOnlyBackground = new Color(170, 200, 170);
	
	public JTableVADefaultRenderer(JTableViewAdapter tableVA) {
		this.tableVA = tableVA;
	}
    
    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
    	        
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        AbstractTableAM tableAM = tableVA.getAttributeModel();
        boolean readOnly = false;
        
        if(tableAM != null) {
        	ColumnDefinition<? extends Object> columnDefinition = tableAM.getColumns().get(column);
        	readOnly = columnDefinition.isReadOnly();
        }
        
        if(readOnly) {
        	component.setBackground(row % 2 == 0 ? evenReadOnlyBackground : oddReadOnlyBackground);
        } else {
        	component.setBackground(row % 2 == 0 ? evenNormalBackground : oddNormalBackground);
        }
        
        
		return component;
    }

}
