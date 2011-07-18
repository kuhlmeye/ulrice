package net.ulrice.databinding.ui;

import java.awt.Color;

import javax.swing.UIManager;

public class BindingUI {

	public static final String BACKGROUND_STATE_MARKER_INVALID = "Ulrice.Binding.BackgroundStateMarker.InvalidBackground";
	public static final String BACKGROUND_STATE_MARKER_CHANGED = "Ulrice.Binding.BackgroundStateMarker.ChangedBackground";

	public static final String BACKGROUND_NORMAL_ODD_TABLE_ROW = "Ulrice.Binding.JTableVARenderer.NormalOddBackground";
	public static final String BACKGROUND_NORMAL_EVEN_TABLE_ROW = "Ulrice.Binding.JTableVARenderer.NormalOddBackground";
	public static final String BACKGROUND_READONLY_ODD_TABLE_ROW = "Ulrice.Binding.JTableVARenderer.ReadonlyOddBackground";
	public static final String BACKGROUND_READONLY_EVEN_TABLE_ROW = "Ulrice.Binding.JTableVARenderer.ReadonlyOddBackground";
	
	public static Color getColor(String key, Color defaultColor) {
		Color color = UIManager.getColor(key);
		return color == null ? defaultColor : color;
	}

	
}
