package net.ulrice.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

/**
 * The ulrice look and feel class.
 * 
 * @author ckuhlmeyer
 */
public class UI implements UIConstants {

	public static void applyDefaultUI() {
		UIManager.put(CHANGEOVER_DIALOG_SIZE, new Dimension(300, 150));
		UIManager.put(CHANGEOVER_ICON_PANEL_BORDER, BorderFactory.createBevelBorder(BevelBorder.RAISED));
		UIManager.put(CHANGEOVER_MARKED_BORDER, BorderFactory.createCompoundBorder(BorderFactory
				.createLineBorder(Color.BLACK), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
		UIManager.put(CHANGEOVER_NONMARKED_BORDER, BorderFactory.createEmptyBorder(9, 9, 9, 9));
		UIManager.put(CHANGEOVER_ICON_INSETS, new Insets(5, 5, 5, 5));
		UIManager.put(CHANGEOVER_ICONS_PER_ROW, Integer.valueOf(4));

		Font labelFont = UIManager.getFont("Label.font");
		UIManager.put(STATUSBAR_CLOCKRENDERER_DATE_FONT, labelFont.deriveFont(8.0f).deriveFont(Font.PLAIN));
		UIManager.put(STATUSBAR_CLOCKRENDERER_TIME_FONT, labelFont.deriveFont(10.0f));
	}
}
