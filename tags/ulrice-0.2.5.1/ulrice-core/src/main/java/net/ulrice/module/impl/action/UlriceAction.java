package net.ulrice.module.impl.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 * Action that is handled by the ulrice action manager and delegated to the ulrice modules.
 * 
 * @author christof
 */
public abstract class UlriceAction extends AbstractAction {

	/** Default generated serial version uid. */
	private static final long serialVersionUID = -8961513296023817932L;
	
	/** The unique id of this action. */
	private String uniqueId;
	
	/** Initiale state. */
	private boolean initiallyEnabled;

	private ActionType type;

	private Icon icon;
	
	private String name;


	/**
	 * Creates a new action handled by the module.
	 * 
	 * @param uniqueId The unique id of this action.
	 * @param name The name of this action.
	 * @param icon The icon of this action.
	 */
	public UlriceAction(String uniqueId, String name, boolean enabled, ActionType type, Icon icon) {
		super();
		this.type = type;
		this.initiallyEnabled = enabled;
		this.uniqueId = uniqueId;
		this.icon = icon;
		this.name = name;
		
		setEnabled(enabled);
		
		putValue(SMALL_ICON, icon);
		putValue(NAME, name);
		putValue(SHORT_DESCRIPTION, name);
	}
	
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public abstract void actionPerformed(ActionEvent e);

	/**
	 * @return the uniqueId
	 */
	public String getUniqueId() {
		return uniqueId;
	}

	/**
	 * @return the initiallyEnabled
	 */
	public boolean isInitiallyEnabled() {
		return initiallyEnabled;
	}

	/**
	 * @return the type
	 */
	public ActionType getType() {
		return type;
	}
	
	/**
	 * @return the icon
	 */
	public Icon getIcon() {
	    return icon;
	}
	
	/**
	 * @return the name
	 */
	public String getName()  {
	    return name;
	}

    public KeyStroke getHotkey() {
        return (KeyStroke)getValue(Action.ACCELERATOR_KEY);
    }
    
    public void setHotkey(KeyStroke keyStroke) {
        putValue(Action.ACCELERATOR_KEY, keyStroke);
    }

}
