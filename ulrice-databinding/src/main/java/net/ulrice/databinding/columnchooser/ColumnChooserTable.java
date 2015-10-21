package net.ulrice.databinding.columnchooser;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

/**
 * @author EXSTHUB
 */
public class ColumnChooserTable extends JTable {
    private static final long serialVersionUID = 1L;

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);

        setPreferredColumnWidths(new double[] { 0.45, 0.45, 0.1 });
        setShowGrid(true);
        setGridColor(new Color(229, 236, 209));
    }

    protected void setPreferredColumnWidths(double[] percentages) {

        Dimension tableDim = getPreferredScrollableViewportSize();

        double total = 0;

        for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
            total += percentages[i];
        }

        for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
            TableColumn column = getColumnModel().getColumn(i);
            column.setPreferredWidth((int) (tableDim.width * (percentages[i] / total)));
        }
    }
}
