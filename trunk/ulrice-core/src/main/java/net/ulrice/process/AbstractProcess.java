package net.ulrice.process;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.event.EventListenerList;

import net.ulrice.module.IFController;

/**
 * ABstract job implementation.
 * 
 * @author DL10KUH
 *
 * @param <T> The type of the provisional result.
 * @param <V> The result.
 */
public abstract class AbstractProcess<T,V> extends SwingWorker<T, V> implements IFBackgroundProcess {

	private IFController owner;
	private String name;
	private ProcessState state;
	private EventListenerList listenerList;

	public AbstractProcess(IFController owner, String name) {
		this.owner = owner;
		this.name = name;		
		this.state = ProcessState.Initialized;
		this.listenerList = new EventListenerList();
	}
	
	
	@Override
	public ProcessState getProcessState() {
		return state;
	}

	@Override
	public double getProcessProgress() {
		return getProgress() / 100.0;
	}

	@Override
	public String getProcessProgressMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProcessName() {
		return name;
	}

	@Override
	public IFController getOwningController() {
		return owner;
	}

	@Override
	protected T doInBackground() throws Exception {
		this.state = ProcessState.Started;
		fireStateChanged();
		T result = work();		
		return result;		
	}
	
	@Override
	protected void done() {
		super.done();

		this.state = ProcessState.Done;
		fireStateChanged();
		
		try {
			finished(get());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.state = ProcessState.Done;
		}
	}

	@Override
	protected void process(List<V> arg0) {
		// TODO Auto-generated method stub
		super.process(arg0);
	}

	public void fireStateChanged() {
		IFProcessListener[] listeners = listenerList.getListeners(IFProcessListener.class);
		if(listeners != null)  {
			for(IFProcessListener listener : listeners) {
				listener.stateChanged(this);
			}
		}
	}
	
	public void fireProgressChanged() {
		IFProcessListener[] listeners = listenerList.getListeners(IFProcessListener.class);
		if(listeners != null)  {
			for(IFProcessListener listener : listeners) {
				listener.progressChanged(this);
			}
		}
	}
	
	@Override
	public void addProcessListener(IFProcessListener listener) {
		listenerList.add(IFProcessListener.class, listener);
	}
	
	@Override
	public void removeProcessListener(IFProcessListener listener) {
		listenerList.remove(IFProcessListener.class, listener);
	}

	/**
	 * This method is executed in background. 
	 * 
	 * @return The result of the background process
	 */
	protected abstract T work();

	/**
	 * Called in the awt-thread after the background process was finished.
	 * 
	 * @param result The result of the background process
	 */
	protected abstract void finished(T result);
}
