package net.ulrice.ui.components;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class DefaultTubeTabRenderer implements TubeTabRenderer {

	private JLabel renderer;

	public DefaultTubeTabRenderer(String name) {
		this.renderer = new JLabel(name);
	}

	@Override
	public JComponent getComponent(boolean selected) {
		return renderer;
	}

}
