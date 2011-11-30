package net.ulrice.databinding.viewadapter.impl;

import java.awt.Color;

import javax.swing.JComponent;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.ui.BindingUI;
import net.ulrice.databinding.viewadapter.IFStateMarker;

public class BackgroundStateMarker implements IFStateMarker {

	private static final Color INVALID_BG_COLOR = BindingUI.getColor(BindingUI.BACKGROUND_STATE_MARKER_INVALID, new Color(200, 130, 130));
	private static final Color CHANGED_BG_COLOR = BindingUI.getColor(BindingUI.BACKGROUND_STATE_MARKER_CHANGED, new Color(230, 230, 130));
	private Color normalBGColor;

	
	@Override
	public void updateState(Object value, boolean editable, boolean dirty, boolean valid, JComponent component) {
		if(!valid) {
			component.setBackground(INVALID_BG_COLOR);
		} else {
			if(dirty) {
				component.setBackground(CHANGED_BG_COLOR);
			} else {
				component.setBackground(normalBGColor);
			}
		}
	}

	@Override
	public void initialize(JComponent component) {
		this.normalBGColor = component.getBackground();
	}

}
