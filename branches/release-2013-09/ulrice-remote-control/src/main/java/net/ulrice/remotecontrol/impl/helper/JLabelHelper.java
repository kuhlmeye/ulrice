package net.ulrice.remotecontrol.impl.helper;

import java.awt.Component;

import javax.swing.JLabel;

import net.ulrice.remotecontrol.RemoteControlException;

public class JLabelHelper extends AbstractJComponentHelper<JLabel>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<JLabel> getType()
	{
		return JLabel.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getLabelFor(JLabel component) throws RemoteControlException
	{
		return component.getLabelFor();
	}

}
