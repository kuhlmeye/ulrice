package net.ulrice.databinding.viewadapter.utable;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellEditor;
 
/**
 * TreeTableCellEditor helps to react on the click on the tree of the tble
 *
 * @author rad
 *
 */
public class TreeTableCellEditor extends AbstractCellEditor implements TableCellEditor {
 
    private static final long serialVersionUID = 1L;
    
    private JTree tree;
    private JTable table;
 
    public TreeTableCellEditor(JTree tree, JTable table) {
        this.tree = tree;
        this.table = table;
    }
 
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c) {
        return tree;
    }
 
    public boolean isCellEditable(EventObject e) {
        if (e instanceof MouseEvent) {
            int colunm1 = 0;
            MouseEvent me = (MouseEvent) e;
            int doubleClick = 2;
            MouseEvent newME = new MouseEvent(tree, me.getID(), me.getWhen(), me.getModifiers(), me.getX() - table.getCellRect(0, colunm1, true).x, me.getY(), doubleClick, me.isPopupTrigger());
            tree.dispatchEvent(newME);
        }
        return false;
    }
 
    @Override
    public Object getCellEditorValue() {
        return null;
    }
 
}