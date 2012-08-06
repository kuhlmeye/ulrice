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
		
		getLocaleSelector().setShowTextAndIcon(true);
		getTextComponent().setBorder(BorderFactory.createEmptyBorder());
		getTextComponent().setOpaque(false);
		add(getLocaleSelector(), BorderLayout.NORTH);
		add(new JScrollPane(getTextComponent()), BorderLayout.CENTER);
	}
}
