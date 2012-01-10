package net.ulrice.databinding.viewadapter.utable;

import java.awt.Component;
import java.awt.Graphics;
 
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeModel;
 
 
/**
 * TreeTableCellRenderer renders the tree in the first column of the tree table
 *
 * @author rad
 *
 */
public class TreeTableCellRenderer extends JTree implements TableCellRenderer {

    protected int visibleRow;
     
    private JTable table;
     
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
    public void setRowHeight(int rowHeight) {
        if (rowHeight > 0) {
            super.setRowHeight(rowHeight);
            if (table != null && table.getRowHeight() != rowHeight) {
                table.setRowHeight(getRowHeight());
            }
        }
    }
 
    /**
     * Tree must have the same height as the table
     */
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, 0, w, table.getHeight());
    }
 
    /**
     * indent of the folders
     */
    public void paint(Graphics g) {
        g.translate(0, -visibleRow * getRowHeight());
         
        super.paint(g);
    }
     
    /**
     * Liefert den Renderer mit der passenden Hintergrundfarbe zurueck.
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//        if (isSelected)
//            setBackground(table.getSelectionBackground());
//        else
//            setBackground(table.getBackground());
 
        visibleRow = row;
        return this;
    }
}