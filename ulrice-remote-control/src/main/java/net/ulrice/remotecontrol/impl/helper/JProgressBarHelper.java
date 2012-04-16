package net.ulrice.remotecontrol.impl.helper;

import javax.swing.JProgressBar;

import net.ulrice.remotecontrol.RemoteControlException;

public class JProgressBarHelper extends AbstractJComponentHelper<JProgressBar>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<JProgressBar> getType()
	{
		return JProgressBar.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(JProgressBar component) throws RemoteControlException
	{
		return component.getString();
	}

}
