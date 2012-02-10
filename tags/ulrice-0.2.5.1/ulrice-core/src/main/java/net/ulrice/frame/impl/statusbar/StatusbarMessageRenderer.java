/**
 * 
 */
package net.ulrice.frame.impl.statusbar;

import java.util.SortedSet;

import javax.swing.JComponent;
import javax.swing.JLabel;

import net.ulrice.message.Message;

/**
 * @author christof
 *
 */
public class StatusbarMessageRenderer implements IFStatusbarMessageRenderer {

	private JLabel renderer;

	public StatusbarMessageRenderer() {
		renderer = new JLabel();
	}

	/**
	 * @see net.ulrice.frame.impl.statusbar.IFStatusbarMessageRenderer#getMessageRenderingComponent(net.ulrice.message.Message, java.util.SortedSet)
	 */
	@Override
	public JComponent getMessageRenderingComponent(Message message, SortedSet<Message> messageList) {

		if(message == null || message.getMessage() == null) {
			renderer.setText("");
		} else {
			renderer.setText(message.getMessage());
		}
				
		return renderer;
	}
}
