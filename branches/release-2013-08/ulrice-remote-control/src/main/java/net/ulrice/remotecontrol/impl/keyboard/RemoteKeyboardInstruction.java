package net.ulrice.remotecontrol.impl.keyboard;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.ulrice.remotecontrol.util.RemoteControlUtils;

public abstract class RemoteKeyboardInstruction implements Serializable
{

	private static final long serialVersionUID = -6344407099257923978L;

	/**
	 * Creates a press key instrcution for the specified key code
	 * 
	 * @param keyCode the key code
	 * @return the instruction
	 */
	public static RemoteKeyboardInstruction press(final int keyCode)
	{
		return new RemoteKeyboardInstruction()
		{

			private static final long serialVersionUID = -8390291700058361647L;

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void execute(Robot robot)
			{
				robot.keyPress(keyCode);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public double duration()
			{
				return RemoteControlUtils.getRobotDelay();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString()
			{
				return String.format("press(%s)", getKeyCodeConstant(keyCode));
			}
		};
	}

	/**
	 * Creates a release key instruction for the specified key code
	 * 
	 * @param keyCode the key code
	 * @return the instruction
	 */
	public static RemoteKeyboardInstruction release(final int keyCode)
	{
		return new RemoteKeyboardInstruction()
		{

			private static final long serialVersionUID = 9023709842609555138L;

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void execute(Robot robot)
			{
				robot.keyRelease(keyCode);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public double duration()
			{
				return RemoteControlUtils.getRobotDelay();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString()
			{
				return String.format("release(%s)", getKeyCodeConstant(keyCode));
			}
		};
	}

	/**
	 * Creates a key instruction for a pause of the specified amount of seconds
	 * 
	 * @param seconds the seconds
	 * @return the instruction
	 */
	public static RemoteKeyboardInstruction pause(final double seconds)
	{
		if (seconds <= 0)
		{
			throw new IllegalArgumentException("Seconds <= 0");
		}

		return new RemoteKeyboardInstruction()
		{

			private static final long serialVersionUID = -6587370039130426033L;

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void execute(Robot robot)
			{
				RemoteControlUtils.pause(seconds);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public double duration()
			{
				return seconds * RemoteControlUtils.speedFactor();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString()
			{
				return String.format("pause(%,.1f)", seconds);
			}
		};
	}

	/**
	 * Tries to find the key constant in the KeyEvent class
	 * 
	 * @param keyCode the key code
	 * @return the key constant, if found
	 */
	public static String getKeyCodeConstant(int keyCode)
	{
		for (Field field : KeyEvent.class.getFields())
		{
			if (field.getType() != int.class)
			{
				continue;
			}

			if (!Modifier.isPublic(field.getModifiers()))
			{
				continue;
			}

			if (!Modifier.isStatic(field.getModifiers()))
			{
				continue;
			}

			if (!field.getName().startsWith("VK_"))
			{
				continue;
			}

			try
			{
				if (field.getInt(null) == keyCode)
				{
					return field.getName();
				}
			}
			catch (IllegalArgumentException e)
			{
				// ignore
			}
			catch (IllegalAccessException e)
			{
				// ignore
			}
		}

		return null;
	}

	public abstract void execute(Robot robot);

	public abstract double duration();

}
