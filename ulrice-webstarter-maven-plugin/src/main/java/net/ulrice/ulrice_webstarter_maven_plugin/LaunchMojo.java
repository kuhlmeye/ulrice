package net.ulrice.ulrice_webstarter_maven_plugin;

import net.ulrice.webstarter.*;
import net.ulrice.webstarter.tasks.IFTask;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Launch ulrice application mojo
 *
 * @author HAM
 * @goal launch
 */
public class LaunchMojo extends AbstractMojo implements IFProcessEventListener {

    private final Object semaphore = new Object();

    private int nextProgress = 0;

    /**
     * @parameter expression="${ulrice-webstarter.launch.url}"
     */
    private URL url;

    /**
     * @parameter expression="${ulrice-webstarter.launch.baseUrl}"
     */
    private URL baseUrl;

    /**
     * @parameter expression="${ulrice-webstarter.launch.tamUrl}"
     */
    private URL tamUrl;

    /**
     * @parameter expression="${ulrice-webstarter.launch.temppath}"
     */
    private String temppath;

    /**
     * @parameter expression="${ulrice-webstarter.launch.authType}"
     */
    private String authType;

    /**
     * The Maven project object
     *
     * @parameter expression="${project}"
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter expression="${ulrice-webstarter.launch.tamUser}"
     */
    private String tamUser;

    /**
     * @parameter expression="${ulrice-webstarter.launch.tamPW}"
     */
    private String tamPW;

    /**
     * The plugin dependencies.
     *
     * @parameter expression="${plugin.artifacts}"
     * @readonly
     */
    private List<Artifact> pluginArtifacts;

    public URL getTamUrl() {
        return tamUrl;
    }

    public void setTamUrl(URL tamUrl) {
        this.tamUrl = tamUrl;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getTamPW() {
        return tamPW;
    }

    public void setTamPW(String tamPW) {
        this.tamPW = tamPW;
    }

    public String getTamUser() {
        return tamUser;
    }

    public void setTamUser(String tamUser) {
        this.tamUser = tamUser;
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getTemppath() {
        return temppath;
    }

    public void setTemppath(String temppath) {
        this.temppath = temppath;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // if (pluginArtifacts == null) {
        // getLog().warn("Nix da");
        // }
        // else {
        // for (Object x : pluginArtifacts) {
        // getLog().warn("!!" + x. getClass());
        // getLog().warn("!!" + x);
        // }
        // }

        if (url == null) {
            throw new MojoFailureException("URL is missing. Either provide a <configuration><url>...</url></configuration> or a -Dulrice-webstarter.launch.url=...");
        }

        if (baseUrl == null) {
            throw new MojoFailureException("URL is missing. Either provide a <configuration><baseUrl>...</baseUrl></configuration> or a -Dulrice-webstarter.launch.baseUrl=...");
        }

        if (temppath == null) {
            throw new MojoFailureException("temppath missing");
        }

        System.setProperty("jarBaseUrl", baseUrl.toString() + "appstarter/importer/");

        getLog().debug("Using URL: " + url);

        ApplicationDescription appDescription = new ApplicationDescription();
        appDescription.setLocalDir(temppath);

        ArrayList<String> params = new ArrayList<String>();
        params.add("-authType=" + authType);
        params.add("-backendUrl=" + baseUrl);
        params.add("-authUrl=" + baseUrl + "OMDAuthService");
        appDescription.setAppParameters(params);

        appDescription.setId(url.toString());

        if(tamUrl != null){
            System.out.println("TAM URL: " + tamUrl);
            Class<? extends IFTask> tamLoginClass = null;
            try{
                tamLoginClass = (Class<? extends IFTask>) Class.forName("net.ulrice.webstarter.tasks.TamLogin");
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("url", tamUrl.toString());
                TaskDescription tamLoginTask = new TaskDescription(tamLoginClass, parameters);
                appDescription.addTask(tamLoginTask);
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        ProcessThread loginThread = new ProcessThread(appDescription);
        if(tamUser != null && tamUser.trim().length() > 0){
            loginThread.getContext().setUserId(tamUser);
            loginThread.getContext().setUserId(tamPW);
        }else{
            loginThread.getContext().setUserId("");
        }

        loginThread.addProcessEventListener(this);
        loginThread.startProcess();


        XMLDescriptionReader reader;
        try {
            reader = new XMLDescriptionReader(url.openStream(), null);
        }
        catch (IOException e1) {
            throw new MojoFailureException("Failed to read application descriptor from " + url);
        }

        try {
            reader.parseXML(appDescription, baseUrl.toString() + "appstarter/importer/");
        }
        catch (SAXException e) {
            throw new MojoExecutionException("Failed to parse xml", e);
        }
        catch (IOException e) {
            throw new MojoFailureException("Failed to read application descriptor from " + url);
        }

        Class<? extends IFTask> taskClass = null;
        try {
            taskClass = (Class<? extends IFTask>) Class.forName("net.ulrice.webstarter.tasks.StartApplication");


            Map<String, String> parameters = new HashMap<String, String>();
//                        parameters.put("mainClass", "vwg.omd.test.acceptance.irc.ImporterRichClientWithRC");
            parameters.put("mainClass", "vwg.omd.rc.imp.ImporterRichClient");
            parameters.put("vmOptions", "Xmx1024m");
            TaskDescription readTask = new TaskDescription(taskClass, parameters);



            appDescription.addTask(readTask);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        ProcessThread thread = new ProcessThread(appDescription);
        thread.getContext().setUserId("");

        if (pluginArtifacts != null) {
            for (Object current : pluginArtifacts) {
                Artifact artifact = (Artifact) current;

                thread.getContext().getClassPath().add(artifact.getFile().getAbsolutePath());
            }
        }

        // if (cookieString != null) {
        //
        // StringTokenizer tok = new StringTokenizer(cookieString, ";");
        // while (tok.hasMoreTokens()) {
        // String cookiePart = tok.nextToken();
        // String[] cookieParts = cookiePart.split("=");
        // String cookieKey = cookieParts[0].trim();
        // String cookieValue = null;
        // if (cookieParts.length > 1) {
        // cookieValue = cookieParts[1].trim();
        // }
        // if (!"expires".equalsIgnoreCase(cookieKey) && !"domain".equalsIgnoreCase(cookieKey)
        // && !"path".equalsIgnoreCase(cookieKey) && !"secure".equalsIgnoreCase(cookieKey)) {
        // thread.getContext().getCookieMap().put(cookieKey, cookieValue);
        // }
        // }
        // }

        thread.addProcessEventListener(this);
        thread.startProcess();

        synchronized (semaphore) {
            getLog().debug("Wait for webstarter to finish...");
            try {
                semaphore.wait();
            }
            catch (InterruptedException e) {
                // ignore
            }
        }

    }

    @Override
    public void taskStarted(ProcessThread thread, IFTask task) {
        getLog().info(task.getName() + " started...");

        nextProgress = 0;
    }

    @Override
    public void taskFinished(ProcessThread thread, IFTask task) {
        getLog().info(task.getName() + " finished.");
    }

    @Override
    public void taskProgressed(ProcessThread thread, IFTask task, int progress, String shortMessage, String longMessage) {

        if (progress >= nextProgress) {
            getLog().info(String.format("  %s [%d%%]", getProgressMessage(task.getName(), shortMessage, longMessage), progress));
            nextProgress = Math.max(nextProgress + 25, 100);
        }
    }

    @Override
    public void handleError(ProcessThread thread, IFTask task, String shortMessage, String longMessage) {
        getLog().error("Error in task " + task.getName() + ": " + shortMessage + " " + longMessage);
    }

    @Override
    public void allTasksFinished(ProcessThread processThread) {
        getLog().info("All tasks finished.");

        synchronized (semaphore) {
            semaphore.notifyAll();
        }
    }

    private static String getProgressMessage(String name, String shortMessage, String longMessage) {
        if (longMessage != null) {
            return longMessage;
        }

        if (shortMessage != null) {
            return name + " in progress: " + shortMessage;
        }

        return name + " in progress...";
    }

}
