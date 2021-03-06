package net.ulrice.remotecontrol.impl.helper;

import javax.swing.JComponent;

import net.ulrice.remotecontrol.RemoteControlException;

public abstract class AbstractJComponentHelper<TYPE extends JComponent> extends AbstractComponentHelper<TYPE>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getToolTipText(TYPE component) throws RemoteControlException
	{
		return component.getToolTipText();
	}

}
