package net.ulrice.sample.module.sample1;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.process.AbstractProcess;

public class PSample1 extends AbstractProcess<Void, Void>  {


	private int progress;

	public PSample1(IFController controller) {
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
			//System.out.println(progress + "%");
		}

		return null;		
	}


	@Override
	protected void finished(Void result) {
		// Nothing to do.	
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
