package net.ulrice.databinding.ui;

import java.awt.Color;

import javax.swing.UIManager;

public class BindingUI {

	public static Color getColor(String key, Color defaultColor) {
		Color color = UIManager.getColor(key);
		return color == null ? defaultColor : color;
	}

	
}
