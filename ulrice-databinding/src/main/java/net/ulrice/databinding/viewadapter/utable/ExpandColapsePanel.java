package net.ulrice.databinding.viewadapter.utable;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ExpandColapsePanel extends JPanel {

    public ExpandColapsePanel(final UTreeTableComponent treeTable) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JButton btnExpand = new JButton("+");
        add(btnExpand);
        btnExpand.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                treeTable.expandAll();

            }
        });

        JButton btnCollapse = new JButton("-");
        btnCollapse.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                treeTable.collapseAll();
            }
        });
        add(btnCollapse);
    }

}
