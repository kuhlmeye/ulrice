package net.ulrice.databinding.viewadapter.utable;

import java.awt.Dimension;

import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class UTableViewport extends JViewport implements ChangeListener {

	private static final long serialVersionUID = -2957487815567117335L;
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() instanceof UTableViewport) {
			UTableViewport otherViewport = (UTableViewport) e.getSource();
			removeChangeListener(otherViewport);
			
			scrollRectToVisible(otherViewport.getVisibleRect());
			
			addChangeListener(otherViewport);
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
		Dimension preferredSize = super.getPreferredSize();
		return new Dimension(getView().getPreferredSize().width, preferredSize.height);
	}

}
