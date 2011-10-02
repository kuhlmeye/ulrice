package net.ulrice.process;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.event.EventListenerList;

import net.ulrice.Ulrice;
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
	private ProcessState state;
	private EventListenerList listenerList;
	private boolean blocksWorkarea = false;
    private String progressMessage;

	public AbstractProcess(IFController owner) {
		this(owner, false);
	}

	public AbstractProcess(IFController owner, boolean blocksWorkarea) {
		this.blocksWorkarea = blocksWorkarea;
		this.owner = owner;
		this.state = ProcessState.Initialized;
		this.listenerList = new EventListenerList();
	}
	

	protected void updateProgress(int progress) {
		setProgress(progress);
		fireProgressChanged();
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
		return progressMessage;
	}
	
	public void setProgressMessage(String progressMessage) {
        this.progressMessage = progressMessage;
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
        } catch (CancellationException e) {
            this.state = ProcessState.Cancelled;
        } catch (InterruptedException e) {
            Ulrice.getMessageHandler().handleException(e);
		} catch (ExecutionException e) {
            Ulrice.getMessageHandler().handleException(e);
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
	protected abstract T work() throws Exception;

	/**
	 * Called in the awt-thread after the background process was finished.
	 * 
	 * @param result The result of the background process
	 */
	protected abstract void finished(T result);
	
	
	public boolean blocksWorkarea() {
		return blocksWorkarea;		
	}
}


