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
    public static final String BACKGROUND_SELECTED_TABLE_ROW = "Ulrice.Binding.JTableVARenderer.SelectedBackground";
    
    public static final String MARKABLE_DURING_DISABLED_STATE  = "Ulrice.Binding.ViewAdapter.MarkableDuringDisabledState";
    public static final String DISABLED_FOREGROUND = "Ulrice.Binding.ViewAdapter.DisabledForeground";
    public static final String DISABLED_BACKGROUND = "Ulrice.Binding.ViewAdapter.DisabledBackground";
	
	public static Color getColor(String key, Color defaultColor) {
		Color color = UIManager.getColor(key);
		return color == null ? defaultColor : color;
	}

	public static Boolean getBoolean(String key, Boolean defaultBoolean) {
	    Boolean result = UIManager.getBoolean(key);
	    return result == null ? defaultBoolean : result;
	}
	
	
}
