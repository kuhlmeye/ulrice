package net.ulrice.sample.module.masktextfield;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.ulrice.module.impl.AbstractController;
import net.ulrice.module.impl.IFClosing;
import net.ulrice.ui.components.MaskTextField;

/**
 * Controller of the sample module 2
 * 
 * @author christof
 */
public class MaskTextFieldController extends AbstractController {

	private JPanel view = new JPanel();
	private MaskTextField maskTF = new MaskTextField();

	public MaskTextFieldController() {
		maskTF.setMask("#### - **** '# (**)");
		view.setLayout(new BorderLayout());
		view.add(new JLabel("Sample module for MaskTextField"), BorderLayout.CENTER);
		view.add(maskTF, BorderLayout.NORTH);
	}

	public JComponent getView() {
		return view;
	}

	@Override
	public void onClose(IFClosing closing) {
		closing.doClose();
	}
}
