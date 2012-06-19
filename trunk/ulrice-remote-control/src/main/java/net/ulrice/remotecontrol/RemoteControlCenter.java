package net.ulrice.remotecontrol;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.swing.UIManager;

import net.ulrice.remotecontrol.impl.ActionRemoteControlImpl;
import net.ulrice.remotecontrol.impl.ApplicationRemoteControlImpl;
import net.ulrice.remotecontrol.impl.ComponentRemoteControlImpl;
import net.ulrice.remotecontrol.impl.ControllerRemoteControlImpl;
import net.ulrice.remotecontrol.impl.ModuleRemoteControlImpl;
import net.ulrice.remotecontrol.ui.RemoteControlWindow;
import net.ulrice.remotecontrol.util.RemoteControlUtils;
import net.ulrice.remotecontrol.util.StreamConsumer;

/**
 * The main class for the remote control feature of Ulrice.
 * 
 * @author Manfred HANTSCHEL
 */
public class RemoteControlCenter {

    public static final String OBJECT_NAME_PREFIX = "net.ulrice.mbeans:type=";

    private static final Map<Class< ?>, Object> BEANS = new HashMap<Class< ?>, Object>();
    private static final Object SEMAPHORE = new Object();

    private static Process process;
    private static JMXConnectorServer serverConnector;
    private static JMXConnector clientConnector;
    private static RemoteControlWindow remoteControlWindow;

    private static boolean pausing = false;
    private static boolean pauseOnError = false;
    private static boolean waiting = false;

    static {
        try {
            register(ActionRemoteControl.class, new ActionRemoteControlImpl());
            register(ApplicationRemoteControl.class, new ApplicationRemoteControlImpl());
            register(ComponentRemoteControl.class, new ComponentRemoteControlImpl());
            register(ControllerRemoteControl.class, new ControllerRemoteControlImpl());
            register(ModuleRemoteControl.class, new ModuleRemoteControlImpl());
        }
        catch (RemoteControlException e) {
            throw new ExceptionInInitializerError("Failed to register default beans");
        }

        pauseOnError = System.getProperty(RemoteControlUtils.PAUSE_ON_ERROR_PROPERTY) != null;
    }

    /**
     * Launches and watches an application with the specified main class and parameters, if it is not already running
     * 
     * @return true if launched, false otherwise
     * @param vmArguments arguments for the VM, may be null
     * @param mainClass the main class
     * @param programArguments arguments for the application, may be null
     * @throws RemoteControlException on occasion
     */
    public static boolean launchApplication(List<String> vmArguments, Class< ?> mainClass,
        List<String> programArguments) throws RemoteControlException {
        synchronized (RemoteControlCenter.class) {
            if (process == null) {
                Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        RemoteControlCenter.killApplication();
                    }
                }));

                String osName = System.getProperty("os.name").toLowerCase();
                List<String> command = new ArrayList<String>();

                if (osName.contains("linux")) {
                    command.add(System.getProperty("java.home") + "/bin/java");
                }
                else {
                    command.add(System.getProperty("java.home") + "\\bin\\java.exe");
                }

                if (RemoteControlUtils.speedFactor() != 1) {
                    command.add("-D" + RemoteControlUtils.SPEED_FACTOR_PROPERTY + "="
                        + RemoteControlUtils.speedFactor());
                }

                command.add("-classpath");
                command.add(System.getProperty("java.class.path"));

                if (vmArguments != null) {
                    for (String vmArgument : vmArguments) {
                        command.add(vmArgument);
                    }
                }

                command.add(mainClass.getName());

                if (programArguments != null) {
                    for (String programArgument : programArguments) {
                        command.add(programArgument);
                    }
                }

                ProcessBuilder builder = new ProcessBuilder(command);

                try {
                    process = builder.start();
                }
                catch (IOException e) {
                    throw new RemoteControlException("Failed to start process", e);
                }

                new Thread(new StreamConsumer(process.getInputStream(), System.out), "RemoteControl Stream").start();
                new Thread(new StreamConsumer(process.getErrorStream(), System.err), "RemoteControl Stream").start();

                return true;
            }

            return false;
        }
    }

    /**
     * Kills the application, if it is running
     */
    public static void killApplication() {
        synchronized (RemoteControlCenter.class) {
            if (process != null) {
                try {
                    process.destroy();
                }
                finally {
                    process = null;
                }
            }
        }
    }

    /**
     * Tests, if the application is running
     * 
     * @return true if running
     */
    public static boolean isApplicationRunning() {
        synchronized (RemoteControlCenter.class) {
            if (process == null) {
                return false;
            }

            try {
                process.exitValue();

                return false;
            }
            catch (IllegalThreadStateException e) {
                return true;
            }
        }
    }

    /**
     * Starts the server of the Remote Control Center on the specified port.
     * 
     * @param port the port
     * @throws RemoteControlException if the server could not be started
     */
    public static void startServer(int port) throws RemoteControlException {
        synchronized (RemoteControlCenter.class) {
            if (serverConnector != null) {
                throw new RemoteControlException("Already serving");
            }

            if (clientConnector != null) {
                throw new RemoteControlException("Already connected");
            }

            try {
                LocateRegistry.createRegistry(port);
            }
            catch (RemoteException e) {
                throw new RemoteControlException("Failed to start RMI registry", e);
            }

            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            String url = "service:jmx:rmi:///jndi/rmi://localhost:" + port + "/server";

            JMXServiceURL serviceURL;
            try {
                serviceURL = new JMXServiceURL(url);
            }
            catch (MalformedURLException e) {
                throw new RemoteControlException("Invalid URL: " + url, e);
            }

            try {
                serverConnector = JMXConnectorServerFactory.newJMXConnectorServer(serviceURL, null, mBeanServer);
                serverConnector.start();
            }
            catch (IOException e) {
                serverConnector = null;
                throw new RemoteControlException("Failed to start JMX connector");
            }

            Iterator<Entry<Class< ?>, Object>> it = BEANS.entrySet().iterator();

            while (it.hasNext()) {
                Entry<Class< ?>, Object> next = it.next();

                registerMBean(next.getKey(), next.getValue());
            }
        }
    }

    /**
     * Stops the server of the Remote Control Center, if it is running.
     */
    public static void stopServer() {
        if (serverConnector != null) {
            try {
                serverConnector.stop();
            }
            catch (IOException e) {
                // ignore
            }

            serverConnector = null;
        }
    }

    /**
     * Returns true if the server of the Remote Control Center has been started
     * 
     * @return true if started
     */
    public static boolean isServing() {
        return serverConnector != null;
    }

    /**
     * Connects the Remote Control Center to the specified host and port.
     * 
     * @param host the host
     * @param port the port
     * @param timeoutInSeconds a timeout in seconds for the connect operation
     * @throws RemoteControlException if the connection could not be established
     */
    public static void connectClient(String host, int port, double timeoutInSeconds) throws RemoteControlException {
        synchronized (RemoteControlCenter.class) {
            if (serverConnector != null) {
                throw new RemoteControlException("Already serving");
            }

            if (clientConnector != null) {
                throw new RemoteControlException("Already connected");
            }

            long end = System.currentTimeMillis() + (long) (timeoutInSeconds * 1000);

            while (true) {
                String url = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/server";

                JMXServiceURL serviceURL;
                try {
                    serviceURL = new JMXServiceURL(url);
                }
                catch (MalformedURLException e) {
                    throw new RemoteControlException("Invalid URL: " + url, e);
                }

                try {
                    clientConnector = JMXConnectorFactory.connect(serviceURL, null);
                    return;
                }
                catch (IOException e) {
                    clientConnector = null;
                }

                long waitFor = end - System.currentTimeMillis();

                if (waitFor <= 0) {
                    throw new RemoteControlException("Connection failed: " + url);
                }

                try {
                    Thread.sleep(waitFor > 1000 ? 1000 : waitFor);
                }
                catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }

    /**
     * Disconnects the client from the Remote Control Center, if it is connected
     */
    public static void disconnectClient() {
        if (clientConnector != null) {
            try {
                clientConnector.close();
            }
            catch (IOException e) {
                // ignore
            }

            clientConnector = null;
        }
    }

    /**
     * Returns true if the client of the Remote Control Center has been connected
     * 
     * @return true if connected
     */
    public static boolean isClientConnected() {
        return clientConnector != null;
    }

    /**
     * Registers a bean to be served by the Remote Control Center. Convenience method for register with lesser generic
     * restrictions.
     * 
     * @param type the type of the bean
     * @param instance the implementation
     * @throws RemoteControlException on occasion
     */
    @SuppressWarnings("unchecked")
    public static <TYPE> TYPE registerNative(Class<TYPE> type, Object instance) throws RemoteControlException {
        return register(type, (TYPE) instance);
    }

    /**
     * Registers a bean to be served by the Remote Control Center.
     * 
     * @param type the type of the bean
     * @param instance the implementation
     * @throws RemoteControlException on occasion
     */
    public static <TYPE> TYPE register(Class<TYPE> type, TYPE instance) throws RemoteControlException {
        synchronized (RemoteControlCenter.class) {
            BEANS.put(type, instance);

            if (serverConnector != null) {
                registerMBean(type, instance);
            }
        }
        
        return instance;
    }

    @SuppressWarnings("unchecked")
    private static <TYPE> void registerMBean(Class<TYPE> type, Object instance) throws RemoteControlException {
        synchronized (RemoteControlCenter.class) {
            if (clientConnector != null) {
                throw new RemoteControlException("Already connected");
            }

            if (serverConnector == null) {
                throw new RemoteControlException("Server not running");
            }

            MBeanServer mBeanServer = serverConnector.getMBeanServer();
            String name = OBJECT_NAME_PREFIX + type.getSimpleName();

            try {
                mBeanServer.registerMBean(new StandardMBean((TYPE) instance, type), new ObjectName(name));
            }
            catch (InstanceAlreadyExistsException e) {
                // ignore
                // throw new RemoteControlException("Duplicate MBean: " + name, e);
            }
            catch (MBeanRegistrationException e) {
                throw new RemoteControlException("Failed to register MBean: " + name, e);
            }
            catch (NotCompliantMBeanException e) {
                throw new RemoteControlException("MBean not compilant: " + name, e);
            }
            catch (MalformedObjectNameException e) {
                throw new RemoteControlException("Invalid object name: " + name, e);
            }
            catch (NullPointerException e) {
                throw new RemoteControlException("Object missing", e);
            }
        }
    }

    /**
     * Returns a proxy for the specified bean. The Remote Control Center must either be started or connected.
     * 
     * @param type the type
     * @return the bean
     * @throws RemoteControlException if creating of the proxy fails
     */
    public static <TYPE> TYPE get(Class<TYPE> type) throws RemoteControlException {
        synchronized (RemoteControlCenter.class) {
            String name = OBJECT_NAME_PREFIX + type.getSimpleName();

            if (serverConnector != null) {
                try {
                    return MBeanServerInvocationHandler.newProxyInstance(serverConnector.getMBeanServer(),
                        new ObjectName(name), type, false);
                }
                catch (MalformedObjectNameException e) {
                    throw new RemoteControlException("Failed to get MBean: " + type, e);
                }
                catch (NullPointerException e) {
                    throw new RemoteControlException("Failed to get MBean: " + type, e);
                }
            }
            else if (clientConnector != null) {
                try {
                    return MBeanServerInvocationHandler.newProxyInstance(clientConnector.getMBeanServerConnection(),
                        new ObjectName(name), type, false);
                }
                catch (MalformedObjectNameException e) {
                    throw new RemoteControlException("Failed to get MBean: " + type, e);
                }
                catch (NullPointerException e) {
                    throw new RemoteControlException("Failed to get MBean: " + type, e);
                }
                catch (IOException e) {
                    throw new RemoteControlException("Failed to get MBean: " + type, e);
                }
            }
            else {
                throw new RemoteControlException("Not connected");
            }
        }
    }

    /**
     * Returns the {@link ApplicationRemoteControl}
     * 
     * @return the remote control
     * @throws RemoteControlException on occasion
     */
    public static ApplicationRemoteControl applicationRC() throws RemoteControlException {
        return get(ApplicationRemoteControl.class);
    }

    /**
     * Returns the {@link ActionRemoteControl}
     * 
     * @return the remote control
     * @throws RemoteControlException on occasion
     */
    public static ActionRemoteControl actionRC() throws RemoteControlException {
        return get(ActionRemoteControl.class);
    }

    /**
     * Returns the {@link ComponentRemoteControl}
     * 
     * @return the remote control
     * @throws RemoteControlException on occasion
     */
    public static ComponentRemoteControl componentRC() throws RemoteControlException {
        return get(ComponentRemoteControl.class);
    }

    /**
     * Returns the {@link ControllerRemoteControl}
     * 
     * @return the remote control
     * @throws RemoteControlException on occasion
     */
    public static ControllerRemoteControl controllerRC() throws RemoteControlException {
        return get(ControllerRemoteControl.class);
    }

    /**
     * Returns the {@link ModuleRemoteControl}
     * 
     * @return the remote control
     * @throws RemoteControlException on occasion
     */
    public static ModuleRemoteControl moduleRC() throws RemoteControlException {
        return get(ModuleRemoteControl.class);
    }

    /**
     * Activates the control window
     */
    public static void activateControlWindow() {
        if (remoteControlWindow == null) {
            if (System.getProperty(RemoteControlUtils.DISABLE_CONTROLLER_PROPERTY) == null) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
                catch (Exception e) {
                    // ignore
                }

                remoteControlWindow = new RemoteControlWindow();

                remoteControlWindow.activate();
            }
        }
    }

    public static void info(String info) {
        if (remoteControlWindow != null) {
            remoteControlWindow.info(info);
        }
    }

    public static void warning(String info) {
        if (remoteControlWindow != null) {
            remoteControlWindow.warning(info);
        }
    }

    public static void error(String info) {
        if (remoteControlWindow != null) {
            remoteControlWindow.error(info);
        }
    }

    public static void step() {
        if (remoteControlWindow != null) {
            synchronized (SEMAPHORE) {
                if (isPausing()) {
                    waiting = true;
                    remoteControlWindow.updateState();

                    try {
                        SEMAPHORE.wait();
                    }
                    catch (InterruptedException e) {
                        // intentionally left blank
                    }

                    waiting = false;
                    remoteControlWindow.updateState();
                }
            }
        }

        RemoteControlUtils.pause(0.5);
    }

    public static void nextStep() {
        synchronized (SEMAPHORE) {
            SEMAPHORE.notifyAll();
        }
    }

    public static boolean isPausing() {
        return pausing;
    }

    public static void setPausing(boolean pausing) {
        synchronized (SEMAPHORE) {
            RemoteControlCenter.pausing = pausing;

            if (remoteControlWindow != null) {
                remoteControlWindow.updateState();
            }
        }
    }

    public static boolean isPauseOnError() {
        return pauseOnError;
    }

    public static void setPauseOnError(boolean pauseOnError) {
        synchronized (SEMAPHORE) {
            RemoteControlCenter.pauseOnError = pauseOnError;

            if (remoteControlWindow != null) {
                remoteControlWindow.updateState();
            }
        }
    }

    public static boolean isWaiting() {
        return waiting;
    }

}
