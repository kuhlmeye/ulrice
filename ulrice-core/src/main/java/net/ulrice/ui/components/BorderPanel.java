package net.ulrice.ui.components;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

/**
 * Panel with empty border and 7px insets
 *
 * @author EXSTHUB
 */
public class BorderPanel extends JPanel {
    private static final long serialVersionUID = 4952054987230225000L;

    private final JPanel contentPane;

    public BorderPanel(Component component) {
        this(component, false);
    }

    public BorderPanel(Component component, boolean scolling) {
        this(component, BorderFactory.createEmptyBorder(5, 5, 5, 5), scolling);
    }

    public BorderPanel(Component component, Border border) {
        this(component, border, false);
    }

    public BorderPanel(Component component, Border border, boolean scrolling) {
        super(new BorderLayout());

        if (scrolling) {
            setOpaque(true);

            contentPane = new JPanel(new BorderLayout());

            JScrollPane scrollPane = new JScrollPane(contentPane);

            scrollPane.setBorder(BorderFactory.createEmptyBorder());

            super.add(scrollPane, BorderLayout.CENTER);
        }
        else {
            contentPane = this;
        }

        contentPane.setOpaque(false);
        contentPane.setBorder(border);

        if (component != null) {
            contentPane.add(component, BorderLayout.CENTER);
        }
    }

    public JPanel getContentPane() {
        return contentPane;
    }

    public BorderPanel opaque() {
        getContentPane().setOpaque(true);

        return this;
    }
}
