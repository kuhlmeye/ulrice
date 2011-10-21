package net.ulrice.remotecontrol.impl.helper;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

public class ComponentHelperRegistry
{

	private static final Map<Class<?>, ComponentHelper<Component>> HELPERS =
	    new HashMap<Class<?>, ComponentHelper<Component>>();

	static
	{
		add(new DefaultComponentHelper());
		add(new DefaultJComponentHelper());
		add(new JComboBoxHelper());
		add(new JLabelHelper());
		add(new JListHelper());
		add(new JProgressBarHelper());
		add(new JTabbedPaneHelper());
		add(new JTableHelper());
		add(new JTextComponentHelper());
		add(new UTableComponentHelper());
		add(new WindowHelper());
	}

	@SuppressWarnings("unchecked")
	public static void add(ComponentHelper<? extends Component> helper)
	{
		HELPERS.put(helper.getType(), (ComponentHelper<Component>) helper);
	}

	@SuppressWarnings("unchecked")
	public static ComponentHelper<Component> get(Class<? extends Component> type)
	{
		Class<?> currentType = type;

		while (currentType != null)
		{
			ComponentHelper<?> result = HELPERS.get(currentType);

			if (result != null)
			{
				return (ComponentHelper<Component>) result;
			}

			currentType = currentType.getSuperclass();
		}

		for (Class<?> currentInterface : type.getInterfaces())
		{
			ComponentHelper<?> result = HELPERS.get(currentInterface);

			if (result != null)
			{
				return (ComponentHelper<Component>) result;
			}
		}

		return null;
	}

}
