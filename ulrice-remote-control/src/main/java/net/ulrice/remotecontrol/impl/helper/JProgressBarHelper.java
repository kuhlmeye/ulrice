package net.ulrice.remotecontrol.impl.helper;

import javax.swing.JProgressBar;

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
	public String getText(JProgressBar component)
	{
		return component.getString();
	}

}
