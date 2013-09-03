package net.ulrice.ui.components;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Panel with empty border and 7px insets
 * 
 * @author EXSTHUB
 */
public class BorderPanel extends JPanel {
    private static final long serialVersionUID = 4952054987230225000L;

    public BorderPanel(Component component) {
        this(component, BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    public BorderPanel(Component component, Border border) {
        super(new BorderLayout());

        setOpaque(false);
        setBorder(border);

        if (component != null) {
            add(component, BorderLayout.CENTER);
        }
    }
}
