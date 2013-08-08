package net.ulrice.remotecontrol.impl.helper;

import javax.swing.JComponent;

public class DefaultJComponentHelper extends AbstractJComponentHelper<JComponent>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<JComponent> getType()
	{
		return JComponent.class;
	}

}
