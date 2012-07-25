package net.ulrice.ui.accordionpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class AccordionPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 5734171488424370652L;

    private final List<AccordionContentPanel> foldables;
    private final JPanel topPanel;
    private final GridBagConstraints constraints;

    private JComponent content;

    private boolean justOneOpen = false;

    public AccordionPanel(boolean justOneOpen) {
        super(new BorderLayout());

        this.justOneOpen = justOneOpen;

        foldables = new ArrayList<AccordionContentPanel>();

        topPanel = new JPanel(new GridBagLayout());

        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        
        add(topPanel, BorderLayout.NORTH);

    }

    public AccordionPanel() {
        this(false);
    }

    /**
     * Add a foldable with scrollbars
     */
    public AccordionContentPanel addFoldableWithScrollBar(String title, JComponent content) {
        JScrollPane scrollPaneWithContent = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPaneWithContent.setBorder(BorderFactory.createEmptyBorder());
        
        return addFoldable(title, scrollPaneWithContent);
    }
    
    /**
     * Default foldable with light blue color
     */
    public AccordionContentPanel addFoldable(String title, JComponent content) {
        return addFoldable(title, content, new Color(0xecf4fb));
    }

    /**
     * Add a foldable with specific seperator color
     */
    public AccordionContentPanel addFoldable(String title, JComponent content, Color seperatorColor) {
        AccordionContentPanel panel = new AccordionContentPanel(title, content, seperatorColor);

        panel.addActionListener(this);
        panel.setFolded(foldables.size() > 0);

        foldables.add(panel);

        topPanel.add(panel, constraints);
        constraints.gridy += 1;

        return panel;
    }

    public void removeFoldable(AccordionContentPanel panel) {
        foldables.remove(panel);
        topPanel.remove(panel);

    }

    public AccordionPanel setContent(JComponent content) {
        if (this.content != null) {
            remove(this.content);
        }

        this.content = content;
        add(content, BorderLayout.CENTER);

        return this;
    }

    public void togglePanel(Object panel) {
        invalidate();
        if (foldables.contains(panel)) {
            for (AccordionContentPanel foldable : foldables) {
                if (foldable == panel) {
                    foldable.setFolded(!foldable.isFolded());
                }
                else {
                    if (justOneOpen) {
                        foldable.setFolded(true);
                    }
                }
            }
        }
        validate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        togglePanel(source);
    }
}
