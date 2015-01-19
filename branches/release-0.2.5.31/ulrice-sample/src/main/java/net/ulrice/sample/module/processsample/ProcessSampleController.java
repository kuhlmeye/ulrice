package net.ulrice.sample.module.processsample;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.ulrice.module.ModuleParam;
import net.ulrice.module.impl.AbstractController;
import net.ulrice.module.impl.IFClosing;
import net.ulrice.process.CtrlProcessExecutor;

/**
 * Controller of the sample module 1
 * 
 * @author christof
 */
public class ProcessSampleController extends AbstractController {

	private CtrlProcessExecutor processExecutor;
	
	private final JPanel view = new JPanel();

	public ProcessSampleController()  {
		processExecutor = new CtrlProcessExecutor(2);
		
	    view.setLayout(new BorderLayout());
	    String descriptionText = "Background Process Sample\n\n" +
	    		"Sample module executing several processes with the ulrice process manager.\n" +
	    		"The process manager is restricted to 2 processes. Watch the process progress" +
	    		"window and see how the processes are executed one by one.";
	    
		JTextArea textArea = new JTextArea(descriptionText);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		
		view.add(new JScrollPane(textArea), BorderLayout.CENTER);
	    view.add(new JButton(new AbstractAction("Execute additional process") {
			
			private static final long serialVersionUID = 5538981988308739742L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				processExecutor.executeProcess(new SampleProcess(ProcessSampleController.this));
			}
		}), BorderLayout.SOUTH);
	}
	
	/**
	 * @see net.ulrice.module.IFController#postCreationEvent(net.ulrice.module.IFModule)
	 */
	@Override
	public void postCreate() {
		SampleProcess process = new SampleProcess(this);
		processExecutor.executeProcess(process);
		processExecutor.executeProcess(new SampleProcess(this), process);
		processExecutor.executeProcess(new SampleProcess(this), process);
		processExecutor.executeProcess(new SampleProcess(this), process);
		processExecutor.executeProcess(new SampleProcess(this), process);
		processExecutor.executeProcess(new SampleProcess(this), process);
		postInfoMessage("Sample controller 1 successfully initialized.");
	}

	@Override
	public JComponent getView() {
	    return view;
	}
    
	@Override
    public void onClose(IFClosing closing) {       
        closing.doClose();
    }
}
