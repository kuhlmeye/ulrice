package net.ulrice.webstarter.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.ulrice.webstarter.EncryptionUtils;
import net.ulrice.webstarter.ProcessThread;
import net.ulrice.webstarter.util.WebstarterUtils;

/**
 * Starts a java application
 * 
 * @author christof
 */
public class StartApplication extends AbstractTask {

    /** The logger used by this class. */
    private static final Logger LOG = Logger.getLogger(StartApplication.class.getName());

    /** Parameter containing the main class value. */
    private static final String PARAM_MAIN_CLASS = "mainClass";

    /** Optional parameter containing a location jre path. */
    private static final String PARAM_LOCAL_JRE = "localJre";

    /** Optional parameter containing options for the virtual machine, e.g. Xmx512m */
    private static final String VM_OPTIONS = "vmOptions";

    @Override
    public boolean doTask(ProcessThread thread) {

        List<String> classPath = thread.getContext().getClassPath();

        StringBuffer commandBuffer = new StringBuffer();
        String localDir = WebstarterUtils.resolvePlaceholders(thread.getAppDescription().getLocalDir());
        String localJre = getParameterAsString(PARAM_LOCAL_JRE);
        if (localJre != null) {
            commandBuffer.append(localDir).append(localJre).append(File.separator).append("bin").append(File.separator);
        }
        commandBuffer.append("java ");

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
