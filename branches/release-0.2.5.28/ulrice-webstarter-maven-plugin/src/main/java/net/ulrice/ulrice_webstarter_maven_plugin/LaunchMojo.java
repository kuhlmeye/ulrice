package net.ulrice.ulrice_webstarter_maven_plugin;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.ulrice.webstarter.ApplicationDescription;
import net.ulrice.webstarter.IFProcessEventListener;
import net.ulrice.webstarter.ProcessThread;
import net.ulrice.webstarter.XMLDescriptionReader;
import net.ulrice.webstarter.tasks.IFTask;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.xml.sax.SAXException;

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
     * The Maven project object
     * 
     * @parameter expression="${project}"
     * @readonly
     */
    private MavenProject project;

    /**
     * The plugin dependencies.
     * 
     * @parameter expression="${plugin.artifacts}"
     * @readonly
     */
    private List<Artifact> pluginArtifacts;

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
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
            throw new MojoFailureException(
                "URL is missing. Either provide a <configuration><url>...</url></configuration> or a -Dulrice-webstarter.launch.url=...");
        }

        getLog().debug("Using URL: " + url);

        XMLDescriptionReader reader;
        try {
            reader = new XMLDescriptionReader(url.openStream(), null);
        }
        catch (IOException e1) {
            throw new MojoFailureException("Failed to read application descriptor from " + url);
        }
        ApplicationDescription appDescription = new ApplicationDescription();

        appDescription.setId(url.toString());
        try {
            reader.parseXML(appDescription);
        }
        catch (SAXException e) {
            throw new MojoExecutionException("Failed to parse xml", e);
        }
        catch (IOException e) {
            throw new MojoFailureException("Failed to read application descriptor from " + url);
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
    public void taskProgressed(ProcessThread thread, IFTask task, int progress, String shortMessage,
        String longMessage) {

        if (progress >= nextProgress) {
            getLog()
                .info(
                    String.format("  %s [%d%%]", getProgressMessage(task.getName(), shortMessage, longMessage),
                        progress));
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
