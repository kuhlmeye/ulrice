package net.ulrice.sample.module.sample1;

import javax.swing.SwingWorker;

import net.ulrice.module.IFController;
import net.ulrice.process.IFBackgroundProcess;
import net.ulrice.process.IFProcessListener;

public class PSample1 extends SwingWorker<Void, Void> implements IFBackgroundProcess {

	int percent = 0;
	private CSample1 owner;
	
	public PSample1(CSample1 owner) {
		this.owner = owner;
	}
	
	@Override
	public ProcessState getProcessState() {
		return null;
	}

	@Override
	public double getProcessProgress() {
		return percent;
	}

	@Override
	public String getProcessProgressMessage() {
		return percent + "%";
	}

	@Override
	public String getProcessName() {
		return "Counter-Process";
	}

	@Override
	protected Void doInBackground() throws Exception {

		for(percent = 0; percent < 100; percent++) {
			Thread.sleep(100);
			System.out.println(percent + "%");
		}

		return null;
	}

	@Override
	public IFController getOwningController() {
		return owner;
	}

	@Override
	public void addProcessListener(IFProcessListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeProcessListener(IFProcessListener listener) {
		// TODO Auto-generated method stub
		
	}

}
