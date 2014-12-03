package net.ulrice.process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import net.ulrice.Ulrice;

public class CtrlProcessExecutor {

    private final ExecutorService executorService;

    public CtrlProcessExecutor(int parallelExecutions) {
		this(Executors.newFixedThreadPool(parallelExecutions, new ThreadFactory() {
	        private final AtomicInteger threadNumber = new AtomicInteger(1);

	        @Override public Thread newThread(Runnable r) {
	            final Thread result = new Thread(r);
	            result.setName("Ulrice Ctrl Process #" + threadNumber.getAndIncrement());
	            result.setDaemon(true);
	            return result;
	        }
	    }));
	}

    public CtrlProcessExecutor(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void executeProcess(IFBackgroundProcess process) {
        Ulrice.getProcessManager().registerProcess(process);
        executorService.execute(process);
    }

    public void executeProcess(IFBackgroundProcess process, IFBackgroundProcess dependsOnProcess) {
        dependsOnProcess.addProcessListener(new ChainingListener(this, process));
    }

}
