package net.ulrice.process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.ulrice.Ulrice;

public class CtrlProcessExecutor {

	private ExecutorService executorService;

	public CtrlProcessExecutor(int parallelExecutions) {
		executorService = Executors.newFixedThreadPool(parallelExecutions);
	}
	
	public CtrlProcessExecutor(ExecutorService executorService) {
		this.executorService = executorService;
	}
	
	public void executeProcess(IFBackgroundProcess process) {
		Ulrice.getProcessManager().registerProcess(process);
		executorService.execute(process);
	}
	
}
