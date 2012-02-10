package net.ulrice.databinding.viewadapter.utable;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
 
public class TreeTableSelectionModel extends DefaultTreeSelectionModel {
     
    public TreeTableSelectionModel() {
        super();
 
        getListSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                 
            }
        });
    }
     
    ListSelectionModel getListSelectionModel() {
        return listSelectionModel;
    }
}