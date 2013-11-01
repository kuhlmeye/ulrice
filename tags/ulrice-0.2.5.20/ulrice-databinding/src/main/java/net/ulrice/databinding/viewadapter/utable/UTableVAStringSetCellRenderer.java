package net.ulrice.databinding.viewadapter.utable;

import java.awt.Component;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

public class UTableVAStringSetCellRenderer extends AbstractUTableRenderer {
	
	
	private JLabel renderer;

    public UTableVAStringSetCellRenderer() {
		super();
        this.renderer = new JLabel();
        this.renderer.setOpaque(true);
        renderer.setHorizontalAlignment(SwingConstants.RIGHT);
    }


	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    String strValue = null; 
	    if(value instanceof Set) {
	        strValue = convertStringSetToString((Set)value);
	    } else if(value != null) {
	        strValue = value.toString();
	    }
    	if(strValue == null) {
    		renderer.setText("");
    	} else {
    		renderer.setText(strValue);
    	}
    	
    	return adaptComponent(table, isSelected, row, column, renderer);
	}

    private String convertStringSetToString(Set stringSet) {

        if (null == stringSet) {
            return null;
        }
        StringBuilder sb = new StringBuilder();

        for (Object string : stringSet) {
            sb.append(null == string ? "" : (string + " "));
        }
        return sb.toString();
    }
	
}
