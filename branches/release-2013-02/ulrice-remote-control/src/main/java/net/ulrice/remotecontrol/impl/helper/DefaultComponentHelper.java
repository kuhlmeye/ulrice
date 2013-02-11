package net.ulrice.remotecontrol.impl.helper;

import java.awt.Component;

public class DefaultComponentHelper extends AbstractComponentHelper<Component>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<Component> getType()
	{
		return Component.class;
	}

}
