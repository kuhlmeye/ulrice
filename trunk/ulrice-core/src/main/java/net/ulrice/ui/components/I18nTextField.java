package net.ulrice.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class I18nTextField extends JPanel {

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
	
	private JTextField textField;
	
	private Map<Locale, String> valueMap;

	public I18nTextField() {
		super(new BorderLayout(2, 2));
	
		localeSelector.setActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				updateTextField();
			}
		});
		
		textField = new JTextField() {
			private static final long serialVersionUID = 5152597750522473770L;

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
			};
		};
		textField.getDocument().addDocumentListener(documentListener);

		textField.setBorder(BorderFactory.createEmptyBorder());
		textField.setOpaque(false);
		add(localeSelector, BorderLayout.WEST);
		add(textField, BorderLayout.CENTER);
	}

	private void updateTextField() {
		try {
			textField.getDocument().removeDocumentListener(documentListener);
			Locale locale = getSelectedLocale();
			if(valueMap != null && valueMap.containsKey(locale)) {
				String text = valueMap.get(locale);
				textField.setText(text);
				textField.setCaretPosition(text == null ? 0 : text.length());
			} else {
				textField.setText(null);
			}
		} finally {
			textField.getDocument().addDocumentListener(documentListener);
		}
	}
	
	private void updateTextMap() {
		Locale locale = getSelectedLocale();
		if(locale != null) {
			valueMap.put(locale, textField.getText());
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
	
	private class LocaleSelector extends JLabel {

	    private static final long serialVersionUID = 1L;

	    private JPopupMenu dropDownMenu = new JPopupMenu();    
	    private Locale selectedLocale = null;
	    
	    private ActionListener listener = null;	    	    

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
            	setToolTipText(localeItem.getText());
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
	}
}
