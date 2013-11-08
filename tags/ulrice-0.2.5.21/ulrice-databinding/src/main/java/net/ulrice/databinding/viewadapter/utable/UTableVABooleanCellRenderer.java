package net.ulrice.databinding.viewadapter.utable;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 * UTableVABooleanCellRenderer to boolean and Boolean within the UTable as a JCheckBox.
 * 
 * @author apunahassaphemapetilon
 */
public class UTableVABooleanCellRenderer extends UTableVADefaultRenderer {
    private static final long serialVersionUID = -2732626862848774169L;

    private JCheckBox renderer;
    private JPanel emptyRenderer;

    public UTableVABooleanCellRenderer() {
        super();
        this.renderer = new JCheckBox();
        this.renderer.setOpaque(true);
        this.emptyRenderer = new JPanel();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        if (value != null && (Boolean.class.equals(value.getClass()) || Boolean.TYPE.equals(value.getClass()))) {
            renderer.setSelected(((Boolean) value).booleanValue());
            renderer.setEnabled(table.isCellEditable(row, column));
            return adaptComponent(table, isSelected, row, column, renderer);
        }
        else {
            if (table.isCellEditable(row, column)) {
                renderer.setSelected(false);
                renderer.setEnabled(table.isCellEditable(row, column));
                return adaptComponent(table, isSelected, row, column, renderer);
            }
            else {
                emptyRenderer.setBorder(BorderFactory.createEmptyBorder());
                return adaptComponent(table, isSelected, row, column, emptyRenderer);
            }
        }
    }

}
