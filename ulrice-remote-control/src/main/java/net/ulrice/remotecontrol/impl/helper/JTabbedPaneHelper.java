package net.ulrice.remotecontrol.impl.helper;

import javax.swing.JTabbedPane;

import net.ulrice.remotecontrol.ComponentListData;
import net.ulrice.remotecontrol.RemoteControlException;

public class JTabbedPaneHelper extends AbstractJComponentHelper<JTabbedPane>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<JTabbedPane> getType()
	{
		return JTabbedPane.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getData(JTabbedPane component) throws RemoteControlException
	{
		ComponentListData result = new ComponentListData();

		for (int i = 0; i < component.getTabCount(); i += 1)
		{
			result.addEntry(component.getTabComponentAt(i).getClass().getName(), component.getSelectedIndex() == i);
		}

		return result;
	}

}
