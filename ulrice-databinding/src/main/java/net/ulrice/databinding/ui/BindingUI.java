package net.ulrice.databinding.ui;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import net.ulrice.databinding.viewadapter.impl.BorderStateMarker;

public class BindingUI implements BindingUIConstants {

    public static void applyDefaultUI() {
		UIManager.put(BORDER_STATE_MARKER_INSETS, new Insets(2, 3, 2, 3));
		UIManager.put(BORDER_STATE_MARKER_CHANGED_BORDER, new Color(0x2cbb00, false));
		UIManager.put(BORDER_STATE_MARKER_INVALID_BORDER, new Color(0xbb0000, false));
		UIManager.put(BACKGROUND_STATE_MARKER_REMOVED_BG, new Color(150, 150, 150));
		UIManager.put(BACKGROUND_STATE_MARKER_REMOVED_FG, new Color(80, 80, 80));
		
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
