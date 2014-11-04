package net.ulrice.databinding.viewadapter.utable;

import javax.swing.ListSelectionModel;
import javax.swing.tree.DefaultTreeSelectionModel;
 
public class TreeTableSelectionModel extends DefaultTreeSelectionModel {
     
    public TreeTableSelectionModel() {
        super();     
    }
     
    ListSelectionModel getListSelectionModel() {
        return listSelectionModel;
    }
}