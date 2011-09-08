package net.ulrice.sample.module.sample1;

import javax.swing.JComponent;

import net.ulrice.module.impl.AbstractController;
import net.ulrice.module.impl.IFClosing;
import net.ulrice.process.CtrlProcessExecutor;

/**
 * Controller of the sample module 1
 * 
 * @author christof
 */
public class CSample1 extends AbstractController {

	private CtrlProcessExecutor processExecutor;
	private final VSample1 v = new VSample1();

	public CSample1()  {
		processExecutor = new CtrlProcessExecutor(2);
	}
	
	/**
	 * @see net.ulrice.module.IFController#postCreationEvent(net.ulrice.module.IFModule)
	 */
	@Override
	public void postCreate() {
		PSample1 process = new PSample1(this);
		processExecutor.executeProcess(process);
		processExecutor.executeProcess(new PSample1(this), process);
		processExecutor.executeProcess(new PSample1(this), process);
		processExecutor.executeProcess(new PSample1(this), process);
		processExecutor.executeProcess(new PSample1(this), process);
		processExecutor.executeProcess(new PSample1(this), process);
		postInfoMessage("Sample controller 1 successfully initialized.");
	}

	@Override
	public JComponent getView() {
	    return v.getView();
	}
    
	@Override
    public void onClose(IFClosing closing) {
        
        closing.doClose();
    }
}
