package net.ulrice.databinding.viewadapter.utable;

import java.awt.Dimension;

import javax.swing.JTable;
 /**
  * TreeTable Component according to
  * http://www.hameister.org/JavaSwingTreeTable.html
  *
  */
public class TreeTable extends JTable {
 
    private TreeTableCellRenderer tree;
     
     
    public TreeTable(AbstractTreeTableModel treeTableModel) {
        super();
 
        tree = new TreeTableCellRenderer(this, treeTableModel);
        setModel(new TreeTableModelAdapter(treeTableModel, tree));
         
        // Gleichzeitiges Selektieren fuer Tree und Table.
        TreeTableSelectionModel selectionModel = new TreeTableSelectionModel();
        tree.setSelectionModel(selectionModel); //For the tree
        setSelectionModel(selectionModel.getListSelectionModel()); //For the table
         
        // Renderer fuer den Tree.
        setDefaultRenderer(TreeTableModel.class, tree);
        // Editor fuer die TreeTable
        setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor(tree, this));
         
        // Kein Grid anzeigen.
        setShowGrid(false);
 
        // Keine Abstaende.
        setIntercellSpacing(new Dimension(2, 2));
 
    }
}