package net.ulrice.remotecontrol.impl.helper;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;

import net.ulrice.remotecontrol.RemoteControlException;

public interface ComponentHelper<TYPE extends Component>
{

	Class<TYPE> getType();

	String getText(TYPE component) throws RemoteControlException;

	String getTitle(TYPE component) throws RemoteControlException;

	String getToolTipText(TYPE component) throws RemoteControlException;

	Component getLabelFor(TYPE component) throws RemoteControlException;

	boolean isSelected(TYPE component) throws RemoteControlException;
	
	boolean isActive(TYPE component) throws RemoteControlException;

	Object getData(TYPE component) throws RemoteControlException;

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

    boolean selectNone(Robot robot, TYPE component) throws RemoteControlException;

	boolean select(Robot robot, TYPE component, int start, int end) throws RemoteControlException;

}
