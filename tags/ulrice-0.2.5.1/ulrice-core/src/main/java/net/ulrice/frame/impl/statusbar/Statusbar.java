/**
 * 
 */
package net.ulrice.frame.impl.statusbar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.ulrice.Ulrice;
import net.ulrice.frame.IFMainFrameComponent;
import net.ulrice.message.IFMessageEventListener;
import net.ulrice.message.Message;
import net.ulrice.message.MessageHandler;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModuleManager;
import net.ulrice.module.event.IFModuleEventListener;

/**
 * The default statusbar of ulrice.
 * 
 * @author christof
 */
public class Statusbar extends JPanel implements IFMainFrameComponent, IFMessageEventListener, IFModuleEventListener, ActionListener {

	private static final String SHOW_PROCESS_LIST = "SHOW_PROCESS_LIST";

	/** Default generated serial version uid. */
	private static final long serialVersionUID = -4976672386681791073L;

	/** The renderer displaying the current message. */
	private IFStatusbarMessageRenderer messageRenderer = new StatusbarMessageRenderer();

	/** The renderer displaying the current clock. */
	private IFStatusbarClockRenderer clockRenderer = new StatusbarClockRenderer();
	
	/** The current render component for the messages. */
	private JPanel messageRenderPanel = null;
	
	/** The panel containing the clock rendering component. */
	private JPanel clockRenderPanel = null;	

	/** The ulrice message handler. */
	private MessageHandler messageHandler;

	/** The module manager of ulrice. */
	private IFModuleManager moduleManager;

	/** The current active controller. */
	private IFController activeController;

	/** The list of current messages. */
	private SortedSet<Message> messageList;
	
	private JButton processButton;
	
	/** Panel in which the process state is displayed. */
	private JPanel processPanel;


	public Statusbar() {
	    
		messageHandler = Ulrice.getMessageHandler();
		messageHandler.addMessageEventListener(this);

		moduleManager = Ulrice.getModuleManager();
		moduleManager.addModuleEventListener(this);

		messageList = new TreeSet<Message>();
		
		messageRenderPanel = new JPanel();
		messageRenderPanel.setLayout(new BorderLayout());
		
		clockRenderPanel = new JPanel();
		clockRenderPanel.setLayout(new BorderLayout());

		Timer clockTimer = new Timer();
		ClockTimerTask clockTimerTask = new ClockTimerTask();
		clockTimerTask.run();
		clockTimer.schedule(clockTimerTask, 60000);
		
		processButton = new JButton(new ImageIcon(getClass().getResource("showprocesslist.png")));
		processButton.setActionCommand(SHOW_PROCESS_LIST);
		processButton.setFocusPainted(false);
		processButton.addActionListener(this);
		processButton.setBorderPainted(false);
		processButton.setOpaque(false);
		processButton.setBackground(new Color(0, 0, 0, Color.TRANSLUCENT));
		
		processPanel = new JPanel();
		processPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		processPanel.setLayout(new BorderLayout());
		processPanel.add(new JScrollPane(new ProcessList()), BorderLayout.CENTER);
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(messageRenderPanel);		
		add(Box.createHorizontalGlue());
		add(processButton);
		add(clockRenderPanel);
		
		
	}

	/**
	 * @see net.ulrice.frame.IFMainFrameComponent#getComponentId()
	 */
	@Override
	public String getComponentId() {
		return getClass().getName();
	}

	/**
	 * @see net.ulrice.frame.IFMainFrameComponent#getView()
	 */
	@Override
	public JComponent getView() {
		return this;
	}

	/**
	 * The task setting the clock in the statusbar.
	 * 
	 * @author christof
	 */
	class ClockTimerTask extends TimerTask {
		
		/**
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {			
			clockRenderPanel.removeAll();
			clockRenderPanel.add(getClockRenderer().getClockRenderer(), BorderLayout.EAST);
			clockRenderPanel.invalidate();
		}
	}

	/**
	 * @see net.ulrice.message.IFMessageEventListener#messageOccurred(net.ulrice.message.Message)
	 */
	@Override
	public void messageOccurred(Message message) {
		updateMessageList();
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#activateModule(net.ulrice.module.IFController)
	 */
	@Override
	public void activateModule(IFController activeController) {
		this.activeController = activeController;
		updateMessageList();
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#closeController(net.ulrice.module.IFController)
	 */
	@Override
	public void closeController(IFController activeController) {
		if (this.activeController != null && this.activeController.equals(activeController)) {
			this.activeController = null;
		}
		resetMessageList();
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#deactivateModule(net.ulrice.module.IFController)
	 */
	@Override
	public void deactivateModule(IFController activeController) {
		if (activeController.equals(this.activeController)) {
			this.activeController = null;
		}
		resetMessageList();
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#openModule(net.ulrice.module.IFController)
	 */
	@Override
	public void openModule(IFController activeController) {
		this.activeController = activeController;
		updateMessageList();
	}

	/**
	 * Updates the messages with the messages of the current active controller.
	 */
	private void updateMessageList() {
		List<Message> messages = messageHandler.getMessages(activeController);
		if (messages != null) {
			messageList.addAll(messages);
		}
		List<Message> globalMessages = messageHandler.getGlobalMessages();
		if (globalMessages != null) {
			messageList.addAll(globalMessages);
		}

		// Update message component
		if (messageList != null && messageList.size() > 0) {
			Message firstMessage = messageList.first();
			showMessage(firstMessage);
		} else {
			showMessage(null);
		}
	}

	/**
	 * Displays a message.
	 * 
	 * @param message The message.
	 */
	private void showMessage(Message message) {
		if(getMessageRenderer() != null) {
			messageRenderPanel.removeAll();
			messageRenderPanel.add(getMessageRenderer().getMessageRenderingComponent(message, messageList), BorderLayout.WEST);
			messageRenderPanel.invalidate();
		}
	}

	/**
	 * Resets the list of messages.
	 */
	private void resetMessageList() {
		messageList.clear();
	}

	/**
	 * @return the messageRenderer
	 */
	public IFStatusbarMessageRenderer getMessageRenderer() {
		return messageRenderer;
	}

	/**
	 * @param messageRenderer the messageRenderer to set
	 */
	public void setMessageRenderer(IFStatusbarMessageRenderer messageRenderer) {
		this.messageRenderer = messageRenderer;
	}

	/**
	 * @return the clockRenderer
	 */
	public IFStatusbarClockRenderer getClockRenderer() {
		return clockRenderer;
	}

	/**
	 * @param clockRenderer the clockRenderer to set
	 */
	public void setClockRenderer(IFStatusbarClockRenderer clockRenderer) {
		this.clockRenderer = clockRenderer;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(SHOW_PROCESS_LIST.equals(e.getActionCommand())) {			
			JFrame frame = Ulrice.getMainFrame().getFrame();
			JLayeredPane layeredPane = frame.getLayeredPane();
			if(layeredPane.isAncestorOf(processPanel)) {				
				layeredPane.remove(processPanel);
			} else {
				Point buttonLoc = processButton.getLocation();
				SwingUtilities.convertPointToScreen(buttonLoc, processButton);
				SwingUtilities.convertPointFromScreen(buttonLoc, layeredPane);
				int y = buttonLoc.y;
				int w = 250;
				int h = 100;
				processPanel.setBounds(processButton.getX(), y - h, w, h);								
				layeredPane.add(processPanel, JLayeredPane.POPUP_LAYER);
			}
			
			processPanel.doLayout();
			processPanel.repaint();
			layeredPane.doLayout();
			layeredPane.repaint();
		}
	}

	@Override
	public void moduleBlocked(IFController controller, Object blocker) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moduleUnblocked(IFController controller, Object blocker) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public void messagesChanged() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void nameChanged(IFController controller) {
        // TODO Auto-generated method stub
        
    }
}
