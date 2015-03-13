package net.ulrice.databinding.viewadapter.utable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeModel;

import net.ulrice.databinding.ui.BindingUI;

/**
 * TreeTableCellRenderer renders the tree in the first column of the tree table
 *
 * @author rad
 */
public class TreeTableCellRenderer extends JTree implements TableCellRenderer {

    /**
     * TODO: description
     */
    private static final long serialVersionUID = 3544864181158317610L;
    private static final Color NORMAL_BG_COLOR = BindingUI.getColor(BindingUI.BACKGROUND_STATE_MARKER_NORMAL,
        BindingUI.getColor(BindingUI.BACKGROUND_STATE_MARKER_NORMAL_EVEN, new Color(230, 230, 230)));
    private static final Color SELECTED_BG_COLOR = BindingUI.getColor(BindingUI.BACKGROUND_STATE_MARKER_SELECTED, new Color(200, 200, 255));

    protected int visibleRow;

    private final JTable table;

    public TreeTableCellRenderer(JTable table, TreeModel model) {
        super(model);

        this.table = table;

        // Setzen der Zeilenhoehe fuer die JTable
        // Muss explizit aufgerufen werden, weil treeTable noch
        // null ist, wenn super(model) setRowHeight aufruft!
        setRowHeight(getRowHeight());
    }

    /**
     * Tree ande Table have to have the same height
     */
    @Override
    public void setRowHeight(int rowHeight) {
        if (rowHeight > 0) {
            super.setRowHeight(rowHeight);
            if ((table != null) && (table.getRowHeight() != rowHeight)) {
                table.setRowHeight(getRowHeight());
            }
        }
    }

    /**
     * Tree must have the same height as the table
     */
    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, 0, w, table.getHeight());
    }

    /**
     * indent of the folders
     */
    @Override
    public void paint(Graphics g) {
        g.translate(0, -visibleRow * getRowHeight());

        super.paint(g);
    }

    /**
     * Liefert den Renderer mit der passenden Hintergrundfarbe zurueck.
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Color background = table.getBackground();

        if (isSelected) {
            background = table.getSelectionBackground();
        }

        setBackground(background);

        visibleRow = row;
        return this;
    }
}
