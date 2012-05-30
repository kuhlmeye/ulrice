package net.ulrice.remotecontrol.impl.helper;

import java.awt.Component;

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
			Component tabComponentAt = component.getTabComponentAt(i);
			
            result.addEntry((tabComponentAt != null) ? tabComponentAt.getClass().getName() : Void.class, component.getSelectedIndex() == i);
		}

		return result;
	}

}
