package net.ulrice.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;

public class I18nTextField extends JPanel {

	private static final long serialVersionUID = -1243667807674531792L;

	private JComboBox<Locale> localeSelector;
	private DefaultComboBoxModel<Locale> localeSelectorModel = new DefaultComboBoxModel<Locale>();
	
	private JTextField textField;

	private Map<Locale, String> valueMap;

	public I18nTextField() {
		super(new BorderLayout());
		
		localeSelector = new JComboBox<Locale>(localeSelectorModel);
		localeSelector.setUI(new BasicComboBoxUI() {
		    @Override
		    protected JButton createArrowButton() {
		        return new JButton() {
					private static final long serialVersionUID = 1L;

					@Override
	                public int getWidth() {
	                        return 0;
	                }
		        };
		    }
		});
		localeSelector.setRenderer(new ListCellRenderer<Locale>() {
			
			private JLabel label = new JLabel();

			@Override
			public Component getListCellRendererComponent(JList<? extends Locale> list, Locale value, int index, boolean isSelected, boolean cellHasFocus) {
				label.setText(value.getCountry() + " " + value.getLanguage());
				return label;
			}
			
		});
		localeSelector.addActionListener(new ActionListener() {
			
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
		textField.getDocument().addDocumentListener(new DocumentListener() {
			
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
		});

		textField.setBorder(BorderFactory.createEmptyBorder());
		textField.setOpaque(false);
		add(localeSelector, BorderLayout.WEST);
		add(textField, BorderLayout.CENTER);
	}

	private void updateTextField() {
		if(valueMap != null && valueMap.containsKey(localeSelectorModel.getSelectedItem())) {
			textField.setText(valueMap.get(localeSelectorModel.getSelectedItem()));
		} else {
			textField.setText(null);
		}
	}
	
	private void updateTextMap() {
		Locale locale = getLocale();
		if(locale != null) {
			valueMap.put(locale, textField.getText());
		}
	}

	protected JComboBox<Locale> getSelector() {
		return localeSelector;
	}

	protected JTextField getSearchField() {
		return textField;
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

	public void setAvailableLocales(Locale... locales) {
		if(locales != null) {
			for(Locale locale : locales) {
				localeSelectorModel.addElement(locale);
			}
			updateTextField();
		}
	}
	
	public void setSelectedLocale(Locale locale) {
		localeSelectorModel.setSelectedItem(locale);
		updateTextField();
	}
	
	public Locale getSelectedLocale() {
		return (Locale) localeSelectorModel.getSelectedItem();
	}
	
	public void setData(Map<Locale, String> valueMap) {
		this.valueMap = valueMap;
		updateTextField();
	};
	
	public Map<Locale, String> getData() {
		return valueMap;
	}
}
