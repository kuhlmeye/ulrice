package net.ulrice.databinding.ui;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import net.ulrice.databinding.viewadapter.impl.BorderStateMarker;

public class BindingUI implements BindingUIConstants {

	public static void applyDefaultUI() {
		UIManager.put(BORDER_STATE_MARKER_INSETS, new Insets(2, 3, 2, 3));
		UIManager.put(BORDER_STATE_MARKER_NORMAL_INNER_BORDER, Color.DARK_GRAY);
		UIManager.put(BORDER_STATE_MARKER_NORMAL_OUTER_BORDER, Color.LIGHT_GRAY);
		UIManager.put(BORDER_STATE_MARKER_CHANGED_INNER_BORDER, new Color(130, 130, 30));
		UIManager.put(BORDER_STATE_MARKER_CHANGED_OUTER_BORDER, Color.LIGHT_GRAY);
		UIManager.put(BORDER_STATE_MARKER_INVALID_INNER_BORDER, new Color(100, 30, 30));
		UIManager.put(BORDER_STATE_MARKER_INVALID_OUTER_BORDER, Color.LIGHT_GRAY);
		UIManager.put(BORDER_STATE_MARKER_CHANGED_IMAGE, new ImageIcon(BorderStateMarker.class.getResource("attention.png")));
		UIManager.put(BORDER_STATE_MARKER_INVALID_IMAGE, new ImageIcon(BorderStateMarker.class.getResource("cross.png")));
	}
	
	public static Color getColor(String key, Color defaultColor) {
		Color color = UIManager.getColor(key);
		return color == null ? defaultColor : color;
	}

	public static Boolean getBoolean(String key, Boolean defaultBoolean) {
	    Boolean result = UIManager.getBoolean(key);
	    return result == null ? defaultBoolean : result;
	}
	
	
}
