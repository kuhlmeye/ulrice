package net.ulrice.process;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.EventListenerList;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;

/**
 * ABstract job implementation.
 * 
 * @author DL10KUH
 * @param <T> The type of the provisional result.
 * @param <V> The result.
 */
public abstract class AbstractProcess<T, V> extends SwingWorker<T, V> implements IFBackgroundProcess {

    private IFController owner;
    private ProcessState state;
    private EventListenerList listenerList;
    private boolean blocksWorkarea = false;
    private String progressMessage;
    private Long startNanos;
    private Long endNanos;

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
    protected final T doInBackground() throws Exception {
        this.state = ProcessState.Started;
        this.startNanos = System.nanoTime();
        fireStateChanged();
        return work();
    }

    @Override
    protected final void done() {
        endNanos = System.nanoTime();
        
        super.done();

        this.state = ProcessState.Done;
        fireStateChanged();

        try {
            finished(get());
        }
        catch (CancellationException ex) {
            this.state = ProcessState.Cancelled;
        }
        catch (InterruptedException ex) {
            Ulrice.getMessageHandler().handleException(ex);
        }
        catch (ExecutionException ex) {
            failed(ex.getCause());
        }
        finally {
            this.state = ProcessState.Done;
        }
    }

    @Override
    protected void process(List<V> arg0) {
        super.process(arg0);
    }

    public void fireStateChanged() {
        if (SwingUtilities.isEventDispatchThread()) {
            IFProcessListener[] listeners = listenerList.getListeners(IFProcessListener.class);
            if (listeners != null) {
                for (IFProcessListener listener : listeners) {
                    listener.stateChanged(AbstractProcess.this);
                }
            }
        }
        else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        IFProcessListener[] listeners = listenerList.getListeners(IFProcessListener.class);
                        if (listeners != null) {
                            for (IFProcessListener listener : listeners) {
                                listener.stateChanged(AbstractProcess.this);
                            }
                        }
                    }
                });
            }
            catch (InterruptedException e) {
                Ulrice.getMessageHandler().handleException(getOwningController(), e);
            }
            catch (InvocationTargetException e) {
                Ulrice.getMessageHandler().handleException(getOwningController(), e);
            }
        }
    }

    public void fireProgressChanged() {
    	if(listenerList.getListenerCount(IFProcessListener.class) == 0) {
    		return;
    	}
    	
        if (SwingUtilities.isEventDispatchThread()) {
            IFProcessListener[] listeners = listenerList.getListeners(IFProcessListener.class);
            if (listeners != null) {
                for (IFProcessListener listener : listeners) {
                    listener.progressChanged(AbstractProcess.this);
                }
            }
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
    
                @Override
                public void run() {
                    IFProcessListener[] listeners = listenerList.getListeners(IFProcessListener.class);
                    if (listeners != null) {
                        for (IFProcessListener listener : listeners) {
                            listener.progressChanged(AbstractProcess.this);
                        }
                    }
                }
            });
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
    
    protected abstract void failed(Throwable t);

    public boolean blocksWorkarea() {
        return blocksWorkarea;
    }

    @Override
    public long getDurationNanos() {
        if (startNanos == null) {
            return 0;
        }
        
        if (endNanos == null) {
            return System.nanoTime() - startNanos;
        }
        
        return endNanos - startNanos;
    }
    
    
}
