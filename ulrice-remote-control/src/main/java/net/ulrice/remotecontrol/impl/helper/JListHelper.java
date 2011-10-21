package net.ulrice.remotecontrol.impl.helper;

import java.awt.Robot;

import javax.swing.JList;
import javax.swing.ListModel;

import net.ulrice.remotecontrol.ComponentListData;
import net.ulrice.remotecontrol.RemoteControlException;

public class JListHelper extends AbstractJComponentHelper<JList>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<JList> getType()
	{
		return JList.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getData(JList component)
	{
		ComponentListData data = new ComponentListData();
		ListModel model = component.getModel();

		for (int i = 0; i < model.getSize(); i += 1)
		{
			data.setEntry(i, model.getElementAt(i), component.isSelectedIndex(i));
		}

		return data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean click(Robot robot, JList component, int index) throws RemoteControlException
	{
		if (index < 0)
		{
			index = component.getModel().getSize() + index;
		}

		return click(robot, component, component.getCellBounds(index, index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean click(Robot robot, JList component, String text) throws RemoteControlException
	{
		ListModel model = component.getModel();

		for (int i = 0; i < model.getSize(); i += 1)
		{
			if (text.equals(model.getElementAt(i)))
			{
				return click(robot, component, i);
			}
		}

		return false;
	}

}
