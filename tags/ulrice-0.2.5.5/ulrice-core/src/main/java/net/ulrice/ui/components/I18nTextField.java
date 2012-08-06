package net.ulrice.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class I18nTextField extends I18nTextComponent {

	private static final long serialVersionUID = 1L;

	public I18nTextField() {
		super(new JTextField() {
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
		});
	
		getTextComponent().setBorder(BorderFactory.createEmptyBorder());
		getTextComponent().setOpaque(false);
		add(getLocaleSelector(), BorderLayout.WEST);
		add(getTextComponent(), BorderLayout.CENTER);
	}
}
