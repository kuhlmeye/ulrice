package net.ulrice.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

public class ToolTextComponent<TEXT_COMPONENT_TYPE extends JTextComponent> extends JComponent {

    private static final long serialVersionUID = -4459008146035175675L;

    private final JLabel label = new JLabel();
    private final TEXT_COMPONENT_TYPE textComponent;
    private final JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

    public ToolTextComponent(TEXT_COMPONENT_TYPE textComponent) {
        super();

        this.textComponent = textComponent;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLoweredBevelBorder());
        setOpaque(false);

        label.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ToolTextComponent.this.textComponent.grabFocus();
            }
        });
        
        textComponent.setBorder(BorderFactory.createEmptyBorder(1, 3, 1, 3));
        textComponent.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }
        });
        textComponent.setMargin(new Insets(2, 4, 2, 4));

        toolBar.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));
        toolBar.setOpaque(false);

        add(label, BorderLayout.WEST);
        add(textComponent, BorderLayout.CENTER);
        add(toolBar, BorderLayout.EAST);
    }

    public void setIcon(Icon icon) {
        label.setIcon(icon);
    }

    public void setLabel(String label) {
        this.label.setText(label);
    }

    public TEXT_COMPONENT_TYPE getTextComponent() {
        return textComponent;
    }

    public JButton addTool(String name, Icon icon, final String toolTipText, final ActionListener actionListener) {
        Action action = new AbstractAction(name, icon) {
            private static final long serialVersionUID = -7568921859833713045L;

            {
                if (toolTipText != null) {
                    putValue(Action.SHORT_DESCRIPTION, toolTipText);
                }
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                actionListener.actionPerformed(e);
            }
        };

        return addTool(action);
    }

    public JButton addTool(Action action) {
        return addTool(new JButton(action));
    }

    public <COMPONENT_TYPE extends JComponent> COMPONENT_TYPE addTool(COMPONENT_TYPE component) {
        component.setBorder(BorderFactory.createEmptyBorder());

        if (textComponent != null) {
            component.setBackground(textComponent.getBackground());
        }

        if (component instanceof JButton) {
            JButton button = (JButton) component;

            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setMargin(new Insets(0, 0, 0, 0));
            button.setIconTextGap(0);
            button.setHideActionText(true);
        }

        component.setFocusable(false);
        component.setOpaque(false);

        toolBar.add(component);

        return component;
    }

    @Override
    public Color getBackground() {
        if (textComponent != null) {
            return textComponent.getBackground();
        }

        return super.getBackground();
    }

    @Override
    public void setBackground(Color background) {
        if (textComponent != null) {
            textComponent.setBackground(background);
        }

        for (Component component : toolBar.getComponents()) {
            component.setBackground(background);
        }

        textComponent.setBackground(background);
    }

    public void setEditable(boolean flag) {
        if (textComponent != null) {
            textComponent.setEditable(flag);
        }

        for (Component component : toolBar.getComponents()) {
            component.setEnabled(flag);
            component.setBackground(textComponent.getBackground());
        }
    }

    @Override
    public void setToolTipText(String text) {
        textComponent.setToolTipText(text);

        super.setToolTipText(text);
    }

    @Override
    public void paint(Graphics g) {
        Insets insets = getInsets();

        g.setColor(textComponent.getBackground());
        g.fillRect(insets.left, insets.top, getWidth() - insets.left - insets.right, getHeight() - insets.top - insets.bottom);

        super.paint(g);
    }

}
