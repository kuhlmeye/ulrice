package net.ulrice.webstarter.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.ulrice.webstarter.EncryptionUtils;
import net.ulrice.webstarter.ProcessThread;
import net.ulrice.webstarter.ProvidedJRE;
import net.ulrice.webstarter.util.WebstarterUtils;

/**
 * Starts a java application
 * 
 * @author christof
 */
public class StartApplication extends AbstractTask {

    private static final String JRE_TYPE_PREFER_LOCAL = "preferLocal";

	public enum OSType {
    	Linux, Windows
	}

	/** The logger used by this class. */
    private static final Logger LOG = Logger.getLogger(StartApplication.class.getName());

    /** Parameter containing the main class value. */
    private static final String PARAM_MAIN_CLASS = "mainClass";

    /** Optional parameter containing a location jre path. */
    private static final String PARAM_LOCAL_JRE = "localJre";

    /** Optional parameter containing options for the virtual machine, e.g. Xmx512m */
    private static final String VM_OPTIONS = "vmOptions";
        
    private static final String OS = "os";

    private static final String JRE_TYPE = "jreType";
    private static final String MIN_VERSION = "minVersion";
    private static final String MAX_VERSION = "maxVersion";
    // TODO Add check for local JRE
    
    @Override
    public boolean doTask(ProcessThread thread) {
    	// Check, if this task is relevant for the current operating system.
    	Object osParam = getParameter(OS);
    	OSType osType = determineOS();
    	
    	if(osParam != null && !osParam.equals(osType.name())) {
    		// Operating system does not match. skipping this task.
    		return true;
    	}    	    	

    	String jreCommand = getJreStartCmd(osType, thread);
    	LOG.info("Starting Application with " + jreCommand);
    	if(jreCommand == null) {
    		// No jre found.
    		return false;
    	}
        StringBuffer commandBuffer = new StringBuffer();
        if (osType == OSType.Windows) {
            commandBuffer.append("\"").append(jreCommand).append("\"").append(" ");
        } else
        {
            commandBuffer.append(jreCommand).append(" ");
        }
        
    	
        String vmOptions = getParameterAsString(VM_OPTIONS);
        if (vmOptions != null) {
            StringTokenizer st = new StringTokenizer(vmOptions, ",");
            while (st.hasMoreTokens()) {
                String vmOption = st.nextToken();
                commandBuffer.append("-");
                commandBuffer.append(vmOption);
                commandBuffer.append(" ");
            }
        }

        List<String> classPath = thread.getContext().getClassPath();
        commandBuffer.append("-cp ");
        for (String element : classPath) {
            commandBuffer.append(element).append(File.pathSeparator);
        }
        commandBuffer.append(" ");

        String mainClass = getParameterAsString(PARAM_MAIN_CLASS);
        if (mainClass != null) {
            commandBuffer.append(mainClass);
        }

        List<String> appParameters = thread.getAppDescription().getAppParameters();
        if (appParameters != null) {
            for (String appParameter : appParameters) {

                String param = replacePlaceholders(thread, appParameter);
                if (param != null) {
                    commandBuffer.append(" ").append(param);
                }
            }
        }

        try {
            LOG.log(Level.INFO, commandBuffer.toString());
            // Start application
    		String localDir = WebstarterUtils.resolvePlaceholders(thread.getAppDescription().getLocalDir());
            Process process = Runtime.getRuntime().exec(commandBuffer.toString(), null, new File(localDir));
            StreamGobbler isGobbler = new StreamGobbler("OUT", process.getInputStream(), System.out);
            StreamGobbler esGobbler = new StreamGobbler("ERR", process.getErrorStream(), System.out);
            isGobbler.start();
            esGobbler.start();
        }
        catch (IOException e) {
            thread.handleError(this, "Error starting application.", "Error starting application: " + e.getMessage());
            LOG.log(Level.SEVERE, "IOException during application startup", e);
        }

        return true;
    }

	private boolean isLocalVersionOK(String minJavaVersion, String maxJavaVersion) {
		String[] splittedMinJavaVersion = new String[]{"0", "0", "0"};
		if(minJavaVersion != null) {		
			splittedMinJavaVersion = minJavaVersion.split("\\.|_");
		}
		String[] splittedMaxJavaVersion = new String[]{"999", "999", "999"};
		if(minJavaVersion != null) {		
			splittedMaxJavaVersion = maxJavaVersion.split("\\.|_");
		}		
		String[] splittedLocalJavaVersion = System.getProperty("java.version").split("\\.|_");
		
		LOG.info("Java Version System: " +  System.getProperty("java.version"));
		
		int minLength = Math.min(splittedLocalJavaVersion.length, Math.min(splittedMaxJavaVersion.length, splittedMinJavaVersion.length));
		boolean versionOK = true;
		for(int i = 0; i < minLength && versionOK; i++) {
			try {
				int local = Integer.valueOf(splittedLocalJavaVersion[i]);
				int max = Integer.valueOf(splittedMaxJavaVersion[i]);
				int min = Integer.valueOf(splittedMinJavaVersion[i]);
				versionOK &= (local >= min && local <= max); 			
			} catch(NumberFormatException e) {
				return false;
			}
			
		}
		LOG.info("Use local java version: " + versionOK);
		return versionOK;		
	}
    
    private String getJreStartCmd(OSType osType, ProcessThread thread) {
    	Set<ProvidedJRE> providedJRESet = thread.getAppDescription().getProvidedJRESet();
    	ProvidedJRE providedJRE = null;
    	if(osType != null && providedJRESet != null) {
	    	for(ProvidedJRE item : providedJRESet) {
	    		if(item.getOs().equals(osType.name())) {
	    			providedJRE = item;
	    			break;
	    		}
	    	}
    	}
    	String localDirString = WebstarterUtils.resolvePlaceholders(thread.getAppDescription().getLocalDir());
    	String jreType = getParameterAsString(JRE_TYPE, JRE_TYPE_PREFER_LOCAL);
        if(JRE_TYPE_PREFER_LOCAL.equalsIgnoreCase(jreType) && isLocalVersionOK(getParameterAsString(MIN_VERSION), getParameterAsString(MAX_VERSION))) {
            StringBuilder javaExecStringBuilder = new StringBuilder(); 
            javaExecStringBuilder.append(System.getProperty("java.home")).append("/bin/java");
            if (osType == OSType.Windows) {
                javaExecStringBuilder.append(".exe");
            }
            File javaExec = new File(javaExecStringBuilder.toString());
            LOG.info("Local JRE match preconditions. Local JRE absolute Path: " + javaExec.getAbsolutePath());
            LOG.info("Java is File: " + javaExec.isFile() + " Java could be executed: " + javaExec.canExecute());
            if(javaExec.isFile() && javaExec.canExecute()) {
                return javaExec.getAbsolutePath();
            }
        }
 
    	if(providedJRE != null) {
    		try {
				DownloadFile downloadTask = providedJRE.getDownloadTask();
				boolean downloaded = downloadTask.downloadFile(thread);
				
				String jreExecutable = thread.getContext().getPersistentProperties().getProperty(providedJRE.getFilename() + "_start");
				
				if(downloaded || jreExecutable == null) {
					String fileName = UnzipJRE.extractFilename(this, thread, downloadTask.getUrl());
					jreExecutable = UnzipJRE.unzipJre(this, thread, downloadTask.getUrl(), localDirString, fileName);
					thread.getContext().getPersistentProperties().setProperty(providedJRE.getFilename() + "_start", jreExecutable);
				}
				LOG.info("Provided JRE absolute Path: " + jreExecutable);
				return jreExecutable;
			} catch (IOException e) {
	            LOG.log(Level.SEVERE, "IO exception during file download.", e);
	        }
	        catch (InstantiationException e) {
	            LOG.log(Level.SEVERE, "Instanciation exception during file download.", e);
	        }
	        catch (IllegalAccessException e) {
	            LOG.log(Level.SEVERE, "Access exception during file download.", e);
	        }
    	}

        thread.handleError(this, "No Java found.", "Could not found a java runtime. Version must be min: " + getParameterAsString(MIN_VERSION) + " and Max: " + getParameterAsString(MAX_VERSION));
    	
    	return null;
	}

	private OSType determineOS() {
    	if(System.getProperty("os.name").contains("Windows")) {
    		return OSType.Windows;
    	} else if(System.getProperty("os.name").contains("Linux")) {
    		return OSType.Linux;
    	}
		LOG.severe("Could not determine OS Type for " + System.getProperty("os.name"));

		return null;
	}

	protected String replacePlaceholders(ProcessThread thread, String appParameter) {

        String userId = thread.getContext().getUserId();
        String proxyHost = thread.getContext().getAppSettings().getProperty("http.proxyHost");
        String proxyPort = thread.getContext().getAppSettings().getProperty("http.proxyPort");
        String proxyUser = thread.getContext().getAppSettings().getProperty("http.proxyUser");
        String proxyPass = thread.getContext().getAppSettings().getProperty("http.proxyPassword");

        if ((userId != null) && !"".equals(userId)) {
            appParameter = appParameter.replace("${USERID}", userId);
        }
        else {
            appParameter = appParameter.replace("${USERID}", "");
        }
        if ((proxyHost != null) && !"".equals(proxyHost)) {
            appParameter = appParameter.replace("${PROXY_HOST}", proxyHost);
        }
        if ((proxyPort != null) && !"".equals(proxyPort)) {
            appParameter = appParameter.replace("${PROXY_PORT}", proxyPort);
        }
        if ((proxyUser != null) && !"".equals(proxyUser)) {
            appParameter = appParameter.replace("${PROXY_USER}", proxyUser);
        }
        if ((proxyPass != null) && !"".equals(proxyPass)) {
            appParameter = appParameter.replace("${PROXY_PASS}", EncryptionUtils.decrypt(proxyPass));
        }

        int cookieIdxStart = appParameter.indexOf("${COOKIE=");
        int cookieIdxEnd = appParameter.indexOf("}", cookieIdxStart);
        if ((cookieIdxStart >= 0) && (cookieIdxEnd >= 0)) {
            String key = appParameter.substring(cookieIdxStart, cookieIdxEnd + 1);
            String cookiePlaceholder = appParameter.substring(cookieIdxStart + "${COOKIE=".length(), cookieIdxEnd);
            String replacement = thread.getContext().getCookieMap().get(cookiePlaceholder);
            if (replacement == null) {
                return null;
            }
            appParameter = appParameter.replace(key, replacement);
        }
        return appParameter;
    }

    class StreamGobbler extends Thread {

        InputStream is;
        OutputStream os;
        String name;

        public StreamGobbler(String name, InputStream is, OutputStream os) {
            this.is = is;
            this.os = os;
            this.name = name;
        }

        @Override
        public void run() {

            PrintWriter pw = new PrintWriter(os);
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                try {
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        pw.println(name + " > " + line);
                        System.out.println(line);
                    }
                }
                finally {
                    br.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace(pw);
            }
        }
    }
}
