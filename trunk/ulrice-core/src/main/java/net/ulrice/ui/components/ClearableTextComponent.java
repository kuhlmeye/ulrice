package net.ulrice.ui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

public class ClearableTextComponent<TEXT_COMPONENT_TYPE extends JTextComponent> extends ToolTextComponent<TEXT_COMPONENT_TYPE> {

    private static final long serialVersionUID = -2300597920805371966L;

    private static final ImageIcon ERASE_ICON = new ImageIcon(ClearableTextComponent.class.getResource("erase.png"));

    private final JButton eraseButton;

    public ClearableTextComponent(TEXT_COMPONENT_TYPE textComponent) {
        super(textComponent);

        textComponent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                getTextComponent().selectAll();
            }
        });
        
        // TODO add translation
        eraseButton = addTool("Erase", ERASE_ICON, "Clears the text", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getTextComponent().setText(null);
                getTextComponent().requestFocusInWindow();
            }
        });

        getTextComponent().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateButtonState();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateButtonState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateButtonState();
            }
        });

        updateButtonState();
    }

    protected void updateButtonState() {
        String text = getTextComponent().getText();

        eraseButton.setVisible((text != null) && (text.trim().length() > 0));
        
        updateToolBarState();
    }
}
