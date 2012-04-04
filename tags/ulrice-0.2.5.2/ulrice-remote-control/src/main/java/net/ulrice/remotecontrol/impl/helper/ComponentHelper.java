package net.ulrice.remotecontrol.impl.helper;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;

import net.ulrice.remotecontrol.RemoteControlException;

public interface ComponentHelper<TYPE extends Component>
{

	Class<TYPE> getType();

	String getText(TYPE component);

	String getTitle(TYPE component);

	String getToolTipText(TYPE component);

	Component getLabelFor(TYPE component);

	boolean isSelected(TYPE component);
	
	boolean isActive(TYPE component);

	Object getData(TYPE component);

	boolean click(Robot robot, TYPE component) throws RemoteControlException;

	boolean click(Robot robot, TYPE component, int index) throws RemoteControlException;

	boolean click(Robot robot, TYPE component, String text) throws RemoteControlException;

	boolean click(Robot robot, TYPE component, int row, int column) throws RemoteControlException;

	boolean click(Robot robot, TYPE component, final Rectangle rectangle) throws RemoteControlException;

	boolean click(Robot robot, TYPE component, final Point location) throws RemoteControlException;

	boolean enter(Robot robot, TYPE component, String text) throws RemoteControlException;

	boolean enter(Robot robot, TYPE component, String text, int index) throws RemoteControlException;

	boolean enter(Robot robot, TYPE component, String text, int row, int column) throws RemoteControlException;

	boolean focus(Robot robot, TYPE component) throws RemoteControlException;

	boolean selectAll(Robot robot, TYPE component) throws RemoteControlException;


}
