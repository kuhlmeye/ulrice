package net.ulrice.ui.components;

import net.ulrice.ui.components.JPopupMenuTriggerListener.TriggerType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class I18nTextPane extends JPanel {

    private static final long serialVersionUID = 1L;

    private final DocumentListener documentListener = new DocumentListener() {

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateTextMap();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateTextMap();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateTextMap();
        }
    };

    private final LocaleSelector localeSelector = new LocaleSelector();

    private final JTextPane textPane;

    private Map<Locale, String> valueMap = new HashMap<Locale, String>();

    public I18nTextPane(JTextPane textPane) {
        super(new BorderLayout(0, 2));
        this.textPane = textPane;

        localeSelector.setOpaque(false);
        localeSelector.setActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                updateTextField();
            }
        });

        textPane.setDocument(new I18nHTMLDocument());
        textPane.getDocument().addDocumentListener(documentListener);
        textPane.setOpaque(true);
        setOpaque(false);
    }

    /* method is public now to be able to reach the text pane for BDD tests */
    public JTextPane getTextPane() {
        return textPane;
    }

    public LocaleSelector getLocaleSelector() {
        return localeSelector;
    }

    private void updateTextField() {
        try {
            textPane.getDocument().removeDocumentListener(documentListener);
            Locale locale = getSelectedLocale();
            if ((valueMap != null) && valueMap.containsKey(locale)) {
                String text = valueMap.get(locale);
                textPane.setText(text);
//                textPane.setCaretPosition(text == null ? 0 : text.length());
            }
            else {
                textPane.setText(null);
            }
        }
        finally {
            textPane.getDocument().addDocumentListener(documentListener);
        }
    }

    private void updateTextMap() {
        Locale locale = getSelectedLocale();
        if (locale != null) {
            String text = textPane.getText();
            if ((text == null) || "".equals(text)) {
                valueMap.put(locale, null);
            }
            else {
                valueMap.put(locale, text);
            }
        }
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);

        if (localeSelector != null) {
            localeSelector.setBackground(bg);
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setOpaque(true);

        Color sfBackground = UIManager.getColor("I18nTextField.background");
        if (sfBackground != null) {
            setBackground(sfBackground);
        }

        Color sfForeground = UIManager.getColor("I18nTextField.foreground");
        if (sfForeground != null) {
            setForeground(sfForeground);
        }
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(getForeground()), new EmptyBorder(0, 0, 0, 0)));
    }

    public void setAvailableLocales(LocaleSelectorItem... localeItems) {
        if (localeItems != null) {
            if (localeItems.length > 0) {
                localeSelector.setSelectedLocale(localeItems[0]);
                updateTextField();
            }
            for (LocaleSelectorItem localeItem : localeItems) {
                localeSelector.addLocale(localeItem);
            }
        }
    }

    public Locale getSelectedLocale() {
        return localeSelector.getSelectedLocale();
    }

    public List<Locale> getAvailableLocales() {
        return localeSelector.getAvailableLocales();
    }

    public void setData(Map<Locale, String> valueMap) {
        this.valueMap = valueMap;
        updateTextField();
    }

    public Map<Locale, String> getData() {
        updateTextMap();
        return valueMap;
    }

    public void addDocumentListener(DocumentListener documentListener) {
        getTextPane().getDocument().addDocumentListener(documentListener);
    }

    public void removeDocumentListener(DocumentListener documentListener) {
        getTextPane().getDocument().removeDocumentListener(documentListener);
    }

    public void setEditable(boolean editable) {
        getTextPane().setEditable(editable);
    }

    public boolean isEditable() {
        return getTextPane().isEditable();
    }

    public class I18nHTMLDocument extends HTMLDocument {
        private static final long serialVersionUID = 1988349089916816464L;
        private int maxLength = Integer.MAX_VALUE;

        public void setMaxLength(int maxLength) {
            this.maxLength = maxLength;
        }

        @Override
        public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
            if ((getLength() + str.length()) > maxLength) {
                return;
            }
            super.insertString(offset, str, a);
        }
    }
}
