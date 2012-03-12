package net.ulrice.databinding.viewadapter.utable;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

import net.ulrice.databinding.bufferedbinding.impl.Element;
 
/**
 * Adapter for the TreeModel to the TableModel
 *
 * @author rad
 *
 */
public class TreeTableModelAdapter extends AbstractTableModel {
     
    private static final long serialVersionUID = -1l;
    
    private JTree tree;
    private AbstractTreeTableModel treeTableModel;
 
    public TreeTableModelAdapter(AbstractTreeTableModel treeTableModel, final JTree tree) {
        this.tree = tree;
        this.treeTableModel = treeTableModel;
 
//        tree.addTreeExpansionListener(new TreeExpansionListener() {
//            
//            public void treeExpanded(TreeExpansionEvent event) {
//                int rowForPath = tree.getRowForPath(event.getPath());
//                Element elementForRow = getElementForRow(rowForPath);
//                fireTableRowsInserted(rowForPath, rowForPath + elementForRow.getChildCount());
//            }
// 
//            public void treeCollapsed(TreeExpansionEvent event) {
//                int rowForPath = tree.getRowForPath(event.getPath());
//                Element elementForRow = getElementForRow(rowForPath);
//                fireTableRowsDeleted(rowForPath, rowForPath + elementForRow.getChildCount());
//            }
//        });
        
        tree.addTreeExpansionListener(new TreeExpansionListener() {
            public void treeExpanded(TreeExpansionEvent event) {
                fireTableDataChanged();
            }
 
            public void treeCollapsed(TreeExpansionEvent event) {
                fireTableDataChanged();
            }
        });
    } 
     
    public int getColumnCount() {
        return treeTableModel.getColumnCount();
    }
 
    public String getColumnName(int column) {
        return treeTableModel.getColumnName(column);
    }
 
    public Class<?> getColumnClass(int column) {
        return treeTableModel.getColumnClass(column);
    }
 
    public int getRowCount() {
        return tree.getRowCount();
    }
 
    protected Element getElementForRow(int row) {
        return (Element) nodeForRow(row);
    }
    
    protected Object nodeForRow(int row) {
        TreePath treePath = tree.getPathForRow(row);     
        if(treePath == null){
            return null;
        }
        return treePath.getLastPathComponent();
    }
 
    public Object getValueAt(int row, int column) {
        return treeTableModel.getValueAt(nodeForRow(row), column);
    }
 
    public boolean isCellEditable(int row, int column) {
        return treeTableModel.isCellEditable(nodeForRow(row), column);
    }
 
    public void setValueAt(Object value, int row, int column) {
        treeTableModel.setValueAt(value, nodeForRow(row), column);
        fireTableDataChanged();
    }
}