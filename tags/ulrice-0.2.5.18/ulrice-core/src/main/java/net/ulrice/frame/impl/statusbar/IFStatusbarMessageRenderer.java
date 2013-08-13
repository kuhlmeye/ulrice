/**
 * 
 */
package net.ulrice.frame.impl.statusbar;

import java.util.SortedSet;

import javax.swing.JComponent;

import net.ulrice.message.Message;

/**
 * @author christof
 *
 */
public interface IFStatusbarMessageRenderer {

	JComponent getMessageRenderingComponent(Message message, SortedSet<Message> messageList);
	
}
