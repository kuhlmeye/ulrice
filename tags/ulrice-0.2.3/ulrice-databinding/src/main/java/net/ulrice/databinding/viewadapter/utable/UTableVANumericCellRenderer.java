package net.ulrice.databinding.viewadapter.utable;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 * UTableVACellRenderer to render any numeric type and simple type as a text.
 * 
 * @author apunahassaphemapetilon
 *
 */
public class UTableVANumericCellRenderer extends AbstractUTableRenderer {
	
	
	private JLabel renderer;

    public UTableVANumericCellRenderer() {
		super();
        this.renderer = new JLabel();
        this.renderer.setOpaque(true);
        renderer.setHorizontalAlignment(SwingConstants.RIGHT);
    }


	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    	if(value == null) {
    		renderer.setText("");
    	} else {
    		renderer.setText(value.toString());
    	}
    	return adaptComponent(table, isSelected, row, column, renderer);
	}

	
	
}
