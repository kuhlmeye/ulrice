package net.ulrice.sample.module.profiledmodulesample;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.ulrice.module.impl.AbstractController;
import net.ulrice.module.impl.IFClosing;

public class ProfiledModuleSampleController extends AbstractController {

	private JPanel view = new JPanel(new BorderLayout());
	private JLabel label = new JLabel("Default Value");
	
	public ProfiledModuleSampleController() {
		view.add(label, BorderLayout.NORTH);
	}
	
	@Override
	public JComponent getView() {
		return view;
	}

	@Override
	public void onClose(IFClosing closing) {
		closing.doClose();
	}

	public void setTextValue(String textValue) {
		label.setText(textValue);
	}
}
