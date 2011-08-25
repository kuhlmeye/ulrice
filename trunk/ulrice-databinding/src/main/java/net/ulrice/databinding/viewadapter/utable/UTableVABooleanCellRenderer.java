package net.ulrice.databinding.viewadapter.utable;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

/**
 * 
 * UTableVABooleanCellRenderer to boolean and Boolean within the UTable as a JCheckBox.
 * 
 * @author apunahassaphemapetilon
 *
 */
public class UTableVABooleanCellRenderer extends UTableVACellRenderer implements
		TableCellRenderer {

	private JCheckBox renderer;

	public UTableVABooleanCellRenderer() {
		super();
		this.renderer = new JCheckBox();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		if (value != null
				&& (Boolean.class.equals(value.getClass()) || Boolean.TYPE
						.equals(value.getClass()))) {
			renderer.setSelected(((Boolean) value).booleanValue());
			renderer.setEnabled(table.isCellEditable(row, column));
		} else {
			renderer.setSelected(false);
			renderer.setEnabled(table.isCellEditable(row, column));
		}
		return renderer;
	}

}
