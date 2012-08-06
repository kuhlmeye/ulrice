package net.ulrice.databinding.viewadapter.utable;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Panel for expanding an collapsing all tree nodes
 * It is used in the table header
 * 
 * @author rad
 *
 */
public class ExpandColapsePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public ExpandColapsePanel(final UTreeTableComponent treeTable) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JButton btnExpand = new JButton("+");
        btnExpand.setBorder(null);
        btnExpand.setPreferredSize(new Dimension(22, 22));
        btnExpand.setFont(btnExpand.getFont().deriveFont(10));
        
        add(btnExpand);
        btnExpand.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                treeTable.expandAll();

            }
        });

        JButton btnCollapse = new JButton("-");  
        btnCollapse.setBorder(null);
        btnCollapse.setPreferredSize(new Dimension(22, 22));
        btnCollapse.setFont(btnExpand.getFont().deriveFont(10));
        btnCollapse.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                treeTable.collapseAll();
            }
        });
        add(btnCollapse);
    }

}
