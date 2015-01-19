package net.ulrice.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class I18nTextArea extends I18nTextComponent {

	private static final long serialVersionUID = 1L;

	public I18nTextArea() {
		super(new JTextArea() {
			private static final long serialVersionUID = 5152597750522473770L;

			{
			    setLineWrap(true);
			    setWrapStyleWord(true);
			}
			
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
		getLocaleSelector().setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
		getTextComponent().setBorder(BorderFactory.createEmptyBorder(1, 3, 1, 3));
		getTextComponent().setOpaque(false);
		
		setBorder(BorderFactory.createEmptyBorder());
		
		add(getLocaleSelector(), BorderLayout.NORTH);
		
		JScrollPane scrollPane = new JScrollPane(getTextComponent());
		
		scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
		
        add(scrollPane, BorderLayout.CENTER);
	}
	
	public void setMaxLength(int maxLength) {
	    if (getTextComponent().getDocument().getClass() == I18nTextComponent.I18nPlainDocument.class) {
	        ((I18nTextComponent.I18nPlainDocument) getTextComponent().getDocument()).setMaxLength(maxLength); 
	    }
	}
}
