package net.ulrice.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

public class I18nTextComponent extends JPanel {

	private static final long serialVersionUID = 1L;

	private DocumentListener documentListener = new DocumentListener() {
		
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
	
	private LocaleSelector localeSelector = new LocaleSelector();
	
	private JTextComponent textComponent;
	
	private Map<Locale, String> valueMap;

	public I18nTextComponent(JTextComponent textComponent) {
		super(new BorderLayout(2, 2));
		this.textComponent = textComponent;
	
		localeSelector.setActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				updateTextField();
			}
		});
		textComponent.getDocument().addDocumentListener(documentListener);
	}

	protected JTextComponent getTextComponent() {
		return textComponent;
	}
	
	protected LocaleSelector getLocaleSelector() {
		return localeSelector;
	}
	
	private void updateTextField() {
		try {
			textComponent.getDocument().removeDocumentListener(documentListener);
			Locale locale = getSelectedLocale();
			if(valueMap != null && valueMap.containsKey(locale)) {
				String text = valueMap.get(locale);
				textComponent.setText(text);
				textComponent.setCaretPosition(text == null ? 0 : text.length());
			} else {
				textComponent.setText(null);
			}
		} finally {
			textComponent.getDocument().addDocumentListener(documentListener);
		}
	}
	
	private void updateTextMap() {
		Locale locale = getSelectedLocale();
		if(locale != null) {
			valueMap.put(locale, textComponent.getText());
		}
	}

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
		if(localeItems != null) {
			if(localeItems.length > 0) {
				localeSelector.setSelectedLocale(localeItems[0]);
				updateTextField();
			}
			for(LocaleSelectorItem localeItem : localeItems) {
				localeSelector.addLocale(localeItem);
			}
		}
	}
	
	public Locale getSelectedLocale() {
		return (Locale) localeSelector.getSelectedLocale();
	}
	
	public void setData(Map<Locale, String> valueMap) {
		this.valueMap = valueMap;
		updateTextField();
	};
	
	public Map<Locale, String> getData() {
		return valueMap;
	}
	
	protected class LocaleSelector extends JLabel {

	    private static final long serialVersionUID = 1L;

	    private JPopupMenu dropDownMenu = new JPopupMenu();    
	    private Locale selectedLocale = null;
	    
	    private ActionListener listener = null;

		private boolean showTextAndIcon = false;  	    

	    public LocaleSelector() {
	        super();
	        
	        
	        setBorder(new EmptyBorder(1, 1, 1, 1));
	        setOpaque(false);

	        addMouseListener(new MouseAdapter() {
	            
	            @Override
	            public void mouseClicked(MouseEvent e) {
	                if (LocaleSelector.this.dropDownMenu == null) {
	                    return;
	                }
	                if (!dropDownMenu.isVisible()) {
	                    Point p = LocaleSelector.this.getLocationOnScreen();
	                    dropDownMenu.setLocation((int) p.getX(), (int) p.getY() + LocaleSelector.this.getHeight());
	                    dropDownMenu.setVisible(true);
	                }
	                else {
	                    dropDownMenu.setVisible(false);                    
	                }
	            }
	        });        
	    }

		public void setSelectedLocale(LocaleSelectorItem localeItem) {
            selectedLocale = localeItem.getLocale();
            if(localeItem.getIcon() != null) {
            	setIcon(localeItem.getIcon());
            	if(showTextAndIcon) {
                	setText(localeItem.getText());
            	} else {
                	setToolTipText(localeItem.getText());
            	}
            } else {
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

	            public void actionPerformed(ActionEvent e) {
	                dropDownMenu.setVisible(false);
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
}
