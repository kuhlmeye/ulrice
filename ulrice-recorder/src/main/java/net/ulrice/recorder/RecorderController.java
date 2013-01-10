package net.ulrice.recorder;

import javax.swing.JComponent;

import net.ulrice.module.impl.AbstractController;
import net.ulrice.module.impl.IFClosing;

/**
 * Controller of the screen recorder
 * 
 * @author christof
 */
public class RecorderController extends AbstractController {

	@Override
	public JComponent getView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onClose(IFClosing closing) {
		closing.doClose();
	}
}
