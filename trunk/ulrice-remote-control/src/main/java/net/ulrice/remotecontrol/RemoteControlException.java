package net.ulrice.remotecontrol;

public class RemoteControlException extends Exception
{

	private static final long serialVersionUID = 1L;

	public RemoteControlException(String message)
	{
		super(message);
	}

	public RemoteControlException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
