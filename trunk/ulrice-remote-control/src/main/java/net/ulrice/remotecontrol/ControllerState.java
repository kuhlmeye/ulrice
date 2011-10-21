package net.ulrice.remotecontrol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModule;

public class ControllerState extends ModuleState {

    private static final long serialVersionUID = -3305582414039507341L;

    public static ControllerState inspect(IFController controller) {
        if (controller == null) {
            return null;
        }

        IFModule module = Ulrice.getModuleManager().getModule(controller);

        if (module == null) {
            return null;
        }

        return new ControllerState(module, controller);
    }

    public static Collection<ControllerState> inspect(Collection<IFController> controllers) {
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

    protected ControllerState(IFModule module, IFController controller) {
        super(module);

        this.controller = controller;
        
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
