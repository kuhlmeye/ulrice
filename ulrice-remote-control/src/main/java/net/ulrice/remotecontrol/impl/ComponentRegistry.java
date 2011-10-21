package net.ulrice.remotecontrol.impl;

import java.awt.Component;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import net.ulrice.remotecontrol.ComponentState;

public class ComponentRegistry
{

	private static final Map<Component, Long> UNIQUE_IDS =
	    Collections.synchronizedMap(new WeakHashMap<Component, Long>());

	private static long currentId = (long) (Math.random() * Long.MAX_VALUE);

	public static Long register(Component component)
	{
		synchronized (UNIQUE_IDS)
		{
			Long result = UNIQUE_IDS.get(component);

			if (result != null)
			{
				return result;
			}

			result = Long.valueOf(currentId++);

			UNIQUE_IDS.put(component, result);

			return result;
		}
	}

	public static ComponentState register(ComponentState state)
	{
		state.setId(register(state.getComponent()));

		if (state.getLabelFor() != null)
		{
			register(state.getLabelFor());
		}
		
		register(state.getChilds());

		return state;
	}

	public static Collection<ComponentState> register(Collection<ComponentState> states)
	{
		for (ComponentState state : states)
		{
			register(state);
		}

		return states;
	}

	public static Long getUnqiueId(Component component)
	{
		return UNIQUE_IDS.get(component);
	}

	public static Component get(Long uniqueId)
	{
		synchronized (UNIQUE_IDS)
		{
			Iterator<Entry<Component, Long>> it = UNIQUE_IDS.entrySet().iterator();

			while (it.hasNext())
			{
				Entry<Component, Long> entry = it.next();

				if (uniqueId.equals(entry.getValue()))
				{
					return entry.getKey();
				}
			}
		}

		return null;
	}

}
