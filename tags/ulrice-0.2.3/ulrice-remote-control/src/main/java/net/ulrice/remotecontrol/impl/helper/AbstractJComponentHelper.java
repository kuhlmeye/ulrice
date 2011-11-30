package net.ulrice.remotecontrol.impl.helper;

import javax.swing.JComponent;

public abstract class AbstractJComponentHelper<TYPE extends JComponent> extends AbstractComponentHelper<TYPE>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getToolTipText(TYPE component)
	{
		return component.getToolTipText();
	}

}
