package net.ulrice.ui.accordionpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import net.ulrice.ui.components.VerticalScrollPane;

/**
 * A panel with the ability to add (vertically) foldable sections. The panel is scrollable by default, so don't add
 * any scrollpanes! The content will be automatically scaled to the height of the panent (taking the specified weights
 * into account).
 * 
 * @author HAM
 */
public class AccordionPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 5734171488424370652L;

    private final List<AccordionContentPanel> foldables;

    private Component header;
    private final JPanel content;
    private Component footer;
    private Component mainContent;

    private boolean justOneOpen = false;

    public AccordionPanel() {
        this(false);
    }

    public AccordionPanel(boolean justOneOpen) {
        super(new BorderLayout());

        this.justOneOpen = justOneOpen;

        foldables = new ArrayList<AccordionContentPanel>();
        content = new JPanel(new AccordionPanelLayout());

        VerticalScrollPane scrollPane = new VerticalScrollPane(content);

        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        super.add(scrollPane, BorderLayout.CENTER);
    }

    public JPanel getContent() {
        return content;
    }

    /**
     * Sets the the header component
     * 
     * @param component the header component
     * @return the accordion panel itself
     */
    public AccordionPanel setHeader(Component component) {
        if (header != null) {
            super.remove(header);
        }

        super.add(component, BorderLayout.NORTH);

        header = component;

        return this;
    }

    /**
     * Adds a foldable with specified title and content. The content will be surrounded by a scrollpane.
     * 
     * @param title the title
     * @param component the content
     * @param folded true if folded
     * @return the accordion panel itself
     */
    public AccordionContentPanel addFoldable(String title, Component component, boolean folded) {
        return addFoldable(content.getComponentCount(), title, component, component.getBackground(), folded);
    }

    /**
     * Adds a foldable with specified title and content. The content will be surrounded by a scrollpane.
     * 
     * @param title the title
     * @param component the content
     * @param folded true if folded
     * @return the accordion panel itself
     */
    public AccordionContentPanel addFoldable(int index, String title, Component component, boolean folded) {
        return addFoldable(index, title, component, component.getBackground(), folded);
    }

    /**
     * Adds a foldable with specified title and content. The content will be surrounded by a scrollpane.
     * 
     * @param title the title
     * @param component the content
     * @param seperatorColor the color for the separator
     * @param folded true if folded
     * @return the accordion panel itself
     */
    public AccordionContentPanel addFoldable(String title, Component component, Color seperatorColor, boolean folded) {
        return addFoldable(content.getComponentCount(), title, component, seperatorColor, folded);
    }

    /**
     * Adds a foldable with specified title and content. The content will be surrounded by a scrollpane.
     * 
     * @param title the title
     * @param component the content
     * @param seperatorColor the color for the separator
     * @param folded true if folded
     * @return the accordion panel itself
     */
    public AccordionContentPanel addFoldable(int index, String title, Component component, Color seperatorColor, boolean folded) {
        AccordionContentPanel panel = new AccordionContentPanel(title, component, seperatorColor);

        addFoldable(index, panel, folded);

        return panel;
    }

    /**
     * Adds a foldable panel
     * 
     * @param panel the panel
     * @param folded true if folded
     * @return
     */
    public AccordionPanel addFoldable(AccordionContentPanel panel, boolean folded) {
        return addFoldable(content.getComponentCount(), panel, folded);
    }

    /**
     * Adds a foldable panel
     * 
     * @param panel the panel
     * @param folded true if folded
     * @return
     */
    public AccordionPanel addFoldable(int index, AccordionContentPanel panel, boolean folded) {
        panel.addActionListener(this);
        panel.setInitialFolded(folded);

        foldables.add(panel);
        content.add(panel, Double.valueOf(0), index);

        return this;
    }

    /**
     * Removes a folable
     * 
     * @param panel the foldable
     */
    public void removeFoldable(AccordionContentPanel panel) {
        foldables.remove(panel);
        remove(panel);
    }

    /**
     * Sets the content
     * 
     * @param content
     * @return the accordion panel itself
     * @deprecated use addContent instead
     */
    @Deprecated
    public AccordionPanel setContent(Component content) {
        if (mainContent != null) {
            removeContent(mainContent);
        }

        mainContent = content;
        addContent(content);

        return this;
    }

    /**
     * Adds a content. The component will be surrounded by a scrollpane. The weight of the component will be 1
     * 
     * @param component the component
     * @return the accordion panel itself
     */
    public AccordionPanel addContent(Component component) {
        return addContent(component, 1d);
    }

    /**
     * Adds a content. The component will be surrounded by a scrollpane.
     * 
     * @param component the component
     * @param weight the weight
     * @return the accordion panel itself
     */
    public AccordionPanel addContent(Component component, double weight) {
        content.add(component, weight);

        return this;
    }

    /**
     * Removes content
     * 
     * @param component the component
     * @return the accordion panel itself
     */
    public AccordionPanel removeContent(Component component) {
        content.remove(component);

        return this;
    }

    /**
     * Sets the footer
     * 
     * @param component the footer
     * @return the accordion panel itself
     */
    public AccordionPanel setFooter(Component component) {
        if (footer != null) {
            remove(footer);
        }

        add(component, BorderLayout.SOUTH);

        footer = component;

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
