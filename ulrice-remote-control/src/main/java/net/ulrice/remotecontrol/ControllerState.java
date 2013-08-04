package net.ulrice.remotecontrol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModule;

/**
 * Represents the state of a controller
 * 
 * @author Manfred HANTSCHEL
 */
public class ControllerState extends ModuleState {

    private static final long serialVersionUID = -3305582414039507341L;

    /**
     * Creates the state of a controller. Returns null if the controller is null.
     * 
     * @param controller the controller
     * @return the state of the controller
     * @throws RemoteControlException TODO
     */
    public static ControllerState inspect(IFController controller) throws RemoteControlException {
        if (controller == null) {
            return null;
        }

        IFModule module = Ulrice.getModuleManager().getModule(controller);

        if (module == null) {
            return null;
        }

        return new ControllerState(module, controller, controller == Ulrice.getModuleManager()
            .getCurrentController());
    }

    /**
     * Creates all the states for the specified controllers. Ignores controllers that are null.
     * 
     * @param controllers the controllers
     * @return a collection of controller states
     * @throws RemoteControlException TODO
     */
    public static Collection<ControllerState> inspectControllers(Collection<IFController> controllers) throws RemoteControlException {
        Collection<ControllerState> results = new ArrayList<ControllerState>();

        for (IFController controller : controllers) {
            ControllerState state = inspect(controller);

            if (state != null) {
                results.add(state);
            }
        }

        return results;
    }

    private final transient IFController controller;

    private final Collection<ActionState> actions;
    private final ComponentState view;
    private final boolean current;

    protected ControllerState(IFModule module, IFController controller, boolean current) throws RemoteControlException {
        super(module);

        this.controller = controller;
        this.current = current;

        actions = ActionState.inspect(controller.getHandledActions());
        view = ComponentState.inspect(controller.getView());
    }

    public IFController getController() {
        return controller;
    }

    public Collection<ActionState> getActions() {
        return actions;
    }

    public ComponentState getView() {
        return view;
    }

    public boolean isCurrent() {
        return current;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.ModuleState#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("ControllerState {");

        builder.append("\n\tuniqueId: ").append(getUniqueId());
        builder.append("\n\tcurrent:  ").append(isCurrent());
        builder.append("\n\ttitles:   ").append(getTitles());

        if (!actions.isEmpty()) {
            builder.append("\n\tactions:  [");
            Iterator<ActionState> it = actions.iterator();
            while (it.hasNext()) {
                builder.append(it.next().toString().replace("\n", "\n\t          "));

                if (it.hasNext()) {
                    builder.append(", ");
                }
            }
            builder.append("]");
        }

        if (view != null) {
            builder.append("\n\tview:     ").append(view.toString().replace("\n", "\n\t          "));
        }

        builder.append("\n}");

        return builder.toString();
    }

}
