package net.ulrice.databinding.viewadapter.utable;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * UTableVACellRenderer to render any numeric type and simple type as a text.
 * 
 * @author apunahassaphemapetilon
 *
 */
public class UTableVANumericCellRenderer extends UTableVACellRenderer implements
		TableCellRenderer {
	
	private DefaultTableCellRenderer renderer;
	
	public UTableVANumericCellRenderer() {
		super();
		this.renderer = new DefaultTableCellRenderer();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		// TODO Auto-generated method stub
		
		renderer.setHorizontalAlignment(SwingConstants.RIGHT);
    	if(value == null) {
    		renderer.setText("");
    	} else {
    		renderer.setText(value.toString());
    	}
    	return renderer;
	}

	
	
}
