package net.ulrice.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.html.HTMLDocument;

import net.ulrice.ui.components.JPopupMenuTriggerListener;
import net.ulrice.ui.components.JPopupMenuTriggerListener.TriggerType;
import net.ulrice.ui.components.LocaleSelectorItem;

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

    private JTextPane textPane;

    private Map<Locale, String> valueMap = new HashMap<Locale, String>();

    public I18nTextPane(JTextPane textPane) {
        super(new BorderLayout(0, 2));
        this.textPane = textPane;

        localeSelector.setActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                updateTextField();
            }
        });

        textPane.setDocument(new HTMLDocument());
        textPane.getDocument().addDocumentListener(documentListener);
        textPane.setOpaque(true);
        setOpaque(false);
    }

    /* method is public now to be able to reach the text pane for BDD tests */
    public JTextPane getTextPane() {
        return textPane;
    }

    protected LocaleSelector getLocaleSelector() {
        return localeSelector;
    }

    private void updateTextField() {
        try {
            textPane.getDocument().removeDocumentListener(documentListener);
            Locale locale = getSelectedLocale();
            if ((valueMap != null) && valueMap.containsKey(locale)) {
                String text = valueMap.get(locale);
                textPane.setText(text);
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

    public void setData(Map<Locale, String> valueMap) {
        this.valueMap = valueMap;
        updateTextField();
    }

    public Map<Locale, String> getData() {
        updateTextMap();
        return valueMap;
    }

    protected class LocaleSelector extends JLabel {
        private static final long serialVersionUID = 1L;
        private final JPopupMenu dropDownMenu = new JPopupMenu();
        private Locale selectedLocale = null;
        private ActionListener listener = null;
        private boolean showTextAndIcon = false;

        public LocaleSelector() {
            super();
            setBorder(new EmptyBorder(1, 1, 1, 4));
            setOpaque(true);
            addMouseListener(new JPopupMenuTriggerListener(dropDownMenu, TriggerType.ALL_MOUSE_BUTTONS));
        }

        public void setSelectedLocale(LocaleSelectorItem localeItem) {
            selectedLocale = localeItem.getLocale();
            if (localeItem.getIcon() != null) {
                setIcon(localeItem.getIcon());
                if (showTextAndIcon) {
                    setText(localeItem.getText());
                }
                else {
                    setToolTipText(localeItem.getText());
                }
            }
            else {
                setText(localeItem.getText());
            }
        }

        public void setActionListener(ActionListener listener) {
            this.listener = listener;
        }

        public Locale getSelectedLocale() {
            return selectedLocale;
        }

        /**
         * Add a search provider to the list of selectable search providers
         */
        public void addLocale(final LocaleSelectorItem localeItem) {
            final JMenuItem menuItem = new JMenuItem(localeItem.getText(), localeItem.getIcon());
            menuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    setSelectedLocale(localeItem);
                    listener.actionPerformed(e);
                }
            });
            dropDownMenu.add(menuItem);
        }

        public void setShowTextAndIcon(boolean showTextAndIcon) {
            this.showTextAndIcon = showTextAndIcon;
        }
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

    protected class I18nPlainDocument extends PlainDocument {
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
