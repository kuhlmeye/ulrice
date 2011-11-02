package net.ulrice.sample.module.sample2;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.ulrice.ui.components.MaskTextField;

/**
 * View of the sample module 2
 * 
 * @author christof
 */
public class VSample2 {

	private JPanel view = new JPanel();
	private MaskTextField maskTF = new MaskTextField();
	
	
	public VSample2 () {
	    maskTF.setMask("#### - **** '# (**)");
	    view.setLayout(new BorderLayout());
	    view.add(new JLabel("Sample 2 module."), BorderLayout.CENTER);
	    view.add(maskTF, BorderLayout.NORTH);	    
	}

	public JComponent getView() {
		return view;
	}
}
