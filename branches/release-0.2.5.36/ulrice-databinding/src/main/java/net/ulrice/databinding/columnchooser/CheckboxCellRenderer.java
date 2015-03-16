package net.ulrice.databinding.columnchooser;

import sun.swing.DefaultLookup;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class CheckboxCellRenderer extends JCheckBox implements TableCellRenderer {
    private static final long serialVersionUID = 1L;
    private ColumnTableModel tableModel;
    private boolean isUniqueCol;

    public CheckboxCellRenderer(ColumnTableModel tableModel, boolean isUniqueCol) {
        this.tableModel = tableModel;
        this.isUniqueCol = isUniqueCol;
        setHorizontalAlignment(CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        if (isUniqueCol) {
            setEnabled(false);
        }
        else {
            setEnabled(!tableModel.isRowUnique(row));
        }

        setOpaque(true);

        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        }
        else {
            Color background = table.getBackground();
            if (background == null || background instanceof javax.swing.plaf.UIResource) {
                Color alternateColor = DefaultLookup.getColor(this, ui, "Table.alternateRowColor");
                if (alternateColor != null && row % 2 == 0) {
                    background = alternateColor;
                }
                else {
                    background = Color.WHITE;
                }
            }
            setForeground(table.getForeground());
            setBackground(background);
        }
        setSelected((value != null && ((Boolean) value).booleanValue()));
        return this;
    }

}
