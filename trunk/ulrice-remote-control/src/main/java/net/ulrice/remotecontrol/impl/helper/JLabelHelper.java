package net.ulrice.remotecontrol.impl.helper;

import java.awt.Component;

import javax.swing.JLabel;

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
	public Component getLabelFor(JLabel component)
	{
		return component.getLabelFor();
	}

}
