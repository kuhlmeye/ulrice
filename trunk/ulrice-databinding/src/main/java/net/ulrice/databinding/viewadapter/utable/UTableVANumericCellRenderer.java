package net.ulrice.databinding.viewadapter.utable;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 * UTableVACellRenderer to render any numeric type and simple type as a text.
 * 
 * @author apunahassaphemapetilon
 *
 */
public class UTableVANumericCellRenderer extends UTableVADefaultRenderer {
	
	
	public UTableVANumericCellRenderer() {
		super();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel renderer = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		renderer.setHorizontalAlignment(SwingConstants.RIGHT);
    	if(value == null) {
    		renderer.setText("");
    	} else {
    		renderer.setText(value.toString());
    	}
    	return renderer;
	}

	
	
}
