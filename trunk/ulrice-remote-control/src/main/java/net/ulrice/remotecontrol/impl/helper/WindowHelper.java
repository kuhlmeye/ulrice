package net.ulrice.remotecontrol.impl.helper;

import java.awt.Point;
import java.awt.Robot;
import java.awt.Window;

import net.ulrice.remotecontrol.RemoteControlException;

public class WindowHelper extends AbstractComponentHelper<Window>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<Window> getType()
	{
		return Window.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isActive(Window component) throws RemoteControlException
	{
		return component.isActive();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public boolean click(Robot robot, Window component) throws RemoteControlException
    {
		final Point location = new Point(component.getWidth() / 2, 10);

		return click(robot, component, location);
    }
	
}
