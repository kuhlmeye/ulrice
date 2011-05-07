package net.ulrice.process;

import java.util.EventListener;

public interface IFProcessListener extends EventListener {

	public void stateChanged(IFBackgroundProcess process);
	
	public void progressChanged(IFBackgroundProcess process);

}
