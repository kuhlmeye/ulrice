package net.ulrice.options.modules.hotkey;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;

public class HotkeyButton extends JToggleButton {

	private static final long serialVersionUID = -7162424449314377024L;
	private boolean alreadyInUse;

	public HotkeyButton(String key, ButtonGroup bg, boolean alreadyInUse) {
		super();
		this.alreadyInUse = alreadyInUse;
		setText(key);
		bg.add(this);		
	}
	
	@Override
	public boolean isSelected() {
		if(alreadyInUse) {
			return true;
		} else {
			return super.isSelected();
		}
	}
	
	public boolean isAlreadyInUse() {
		return alreadyInUse;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		if(isSelected()) {
			setBackground(Color.red);
		} else {
			setBackground(Color.green);
		}

		super.paintComponent(g);
	}

}
