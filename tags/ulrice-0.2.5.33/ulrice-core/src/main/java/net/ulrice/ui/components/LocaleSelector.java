package net.ulrice.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class LocaleSelector extends JLabel {

        private static final long serialVersionUID = 1L;

        private final JPopupMenu dropDownMenu = new JPopupMenu();
        private final Map<Locale, LocaleSelectorItem> localeSelectorItems = new LinkedHashMap<>();
        private Locale selectedLocale = null;

        private ActionListener listener = null;

        private boolean showTextAndIcon = false;

        public LocaleSelector() {
            super();

            setBorder(new EmptyBorder(1, 1, 1, 4));

            addMouseListener(new JPopupMenuTriggerListener(dropDownMenu, JPopupMenuTriggerListener.TriggerType.ALL_MOUSE_BUTTONS));
        }

        public void setSelectedLocale(Locale locale) {
            LocaleSelectorItem item = localeSelectorItems.get(locale);

            if (item == null) {
                return;
            }

            setSelectedLocale(item);
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

        public List<Locale> getAvailableLocales() {
            return Collections.unmodifiableList(new ArrayList<>(localeSelectorItems.keySet()));
        }

        /**
         * Add a search provider to the list of selectable search providers
         */
        public void addLocale(final LocaleSelectorItem localeItem) {
            final JMenuItem menuItem = new JMenuItem(localeItem.getText(), localeItem.getIcon());
            localeSelectorItems.put(localeItem.getLocale(), localeItem);
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

        public JPopupMenu getDropDownMenu() {
            return dropDownMenu;
        }
        
    }
