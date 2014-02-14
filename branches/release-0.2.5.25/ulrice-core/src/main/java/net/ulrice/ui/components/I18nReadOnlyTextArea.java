package net.ulrice.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;

public class I18nReadOnlyTextArea extends I18nTextPane {

    private static final long serialVersionUID = 1409769455638118225L;
    
    private static final String TEXT_HTML = "text/html";

    public I18nReadOnlyTextArea() {
        super(new JTextPane() {
            private static final long serialVersionUID = -3993932026960195138L;

            @Override
            public void updateUI() {
                super.updateUI();
                setOpaque(false);

                Color sfBackground = UIManager.getColor("I18nTextField.background");
                if (sfBackground != null) {
                    setBackground(sfBackground);
                }

                Color sfForeground = UIManager.getColor("I18nTextField.foreground");
                if (sfForeground != null) {
                    setForeground(sfForeground);
                }

                setBorder(BorderFactory.createEmptyBorder());

                int sfWidth = UIManager.getInt("I18nTextField.fieldWidth");
                if (sfWidth > 0) {
                    setPreferredSize(new Dimension(sfWidth, getHeight()));
                }
            }
        });

        getLocaleSelector().setShowTextAndIcon(true);
        getLocaleSelector().setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        getLocaleSelector().setVisible(true);
        getTextPane().setBorder(BorderFactory.createEmptyBorder(1, 3, 1, 3));
        getTextPane().setOpaque(false);
        getTextPane().setContentType(TEXT_HTML);

        setBorder(BorderFactory.createEmptyBorder());

        add(getLocaleSelector(), BorderLayout.NORTH);
        add(new JScrollPane(getTextPane()), BorderLayout.CENTER);
    }

    public void setMaxLength(int maxLength) {
        if (getTextPane().getDocument().getClass().equals(I18nTextPane.I18nHTMLDocument.class)) {
            ((I18nTextPane.I18nHTMLDocument) getTextPane().getDocument()).setMaxLength(maxLength);
        }
        if (getTextPane().getDocument().getClass().equals( HTMLDocument.class)) {
            System.out.println(getTextPane().getDocument().getLength());
        }
    }
}
