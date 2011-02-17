package net.ulrice.webstarter.tasks;

import java.util.LinkedList;

import net.ulrice.webstarter.ProcessContext;
import net.ulrice.webstarter.ProcessThread;

public class StartApplication extends AbstractTask {

	@Override
	public boolean doTask(ProcessThread thread) {

		LinkedList<String> classPath = thread.getContext().getValue(ProcessContext.CLASSPATH, new LinkedList<String>());
		
		StringBuffer commandBuffer = new StringBuffer();
		commandBuffer.append("");
		
		commandBuffer.append("-cp ");
		for(String element : classPath) {
			commandBuffer.append(element).append(";");
		}
		commandBuffer.append(" ");

		System.out.println(commandBuffer);
		
		return true;
	}

}
