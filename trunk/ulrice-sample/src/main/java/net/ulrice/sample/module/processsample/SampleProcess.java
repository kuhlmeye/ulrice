package net.ulrice.sample.module.processsample;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.process.AbstractProcess;

public class SampleProcess extends AbstractProcess<Void, Void>  {


	private int progress;

	public SampleProcess(IFController controller) {
		super(controller, true);
        setProgressMessage("Sample Process");
	}

	@Override
	protected Void work() {

		for(progress = 0; progress < 100; progress++) {
			try {
				Thread.sleep(100);
				setProgress(progress);
				fireProgressChanged();
			} catch (InterruptedException e) {
				Ulrice.getMessageHandler().handleException(e);
			}
		}

		return null;		
	}


	@Override
	protected void finished(Void result) {
		// Nothing to do.	
	}
	
    @Override
    protected void failed(Throwable t) {
        Ulrice.getMessageHandler().handleException(getOwningController(), t);
    }

    @Override
    public boolean hasProgressInformation() {
        return false;
    }
    
    @Override
    public boolean supportsCancel() {
        return true;
    }
    
    @Override
    public void cancelProcess() {
        cancel(true);
    }
}
