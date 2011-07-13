package net.ulrice.databinding.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.EventListenerList;

import net.ulrice.databinding.DataState;
import net.ulrice.databinding.IFAttributeModel;
import net.ulrice.databinding.IFAttributeModelEventListener;
import net.ulrice.databinding.IFDataGroup;
import net.ulrice.databinding.viewadapter.IFViewAdapter;

/**
 * A data group is a set of attribute model gui accessor pairs.
 * 
 * @author christof
 */
@SuppressWarnings("unchecked")
public class DataGroup implements IFDataGroup, IFAttributeModelEventListener {

    /** The list of all gui accessors contained in this data group. */
    private Map<String, List<IFViewAdapter>> vaMap = new HashMap<String, List<IFViewAdapter>>();

    /** The list of all attribute models contained in this data group. */
    private Map<String, IFAttributeModel> amMap = new HashMap<String, IFAttributeModel>();

    /** The set of all changed attribute models. */
    private Set<String> changedSet = new HashSet<String>();

    /** The set of all invalid attribute models. */
    private Set<String> invalidSet = new HashSet<String>();

    /** The state of this connector group. */
    private DataState state = DataState.NotInitialized;

    /**
     * Add an attribute model to this data group.
     * 
     * @param am The attribute model.
     */
    public void addAM(IFAttributeModel<?> am) {
        if (am == null) {
            throw new IllegalArgumentException("Could not add null attribute model.");
        }

        String id = am.getId();
        if (id == null) {
            throw new IllegalStateException("Id of an attribute model must not be null.");
        }

        List<IFViewAdapter> gaList = vaMap.get(id);
        if (gaList != null) {
            for (IFViewAdapter va : gaList) {
            	am.addViewAdapter(va);
            }
        }
        am.addAttributeModelEventListener(this);
        amMap.put(id, am);
    }

    /**
     * Add a gui accessor to this data group.
     * 
     * @param va The gui accessor.
     */
    public void addGA(String id, IFViewAdapter va) {
        if (va == null) {
            throw new IllegalArgumentException("Could not add null gui accessor.");
        }

        if (id == null) {
            throw new IllegalStateException("Id of an attribute model must not be null.");
        }

        IFAttributeModel am = amMap.get(id);
        if (am != null) {
        	am.addViewAdapter(va);
        }

        List<IFViewAdapter> gaList = vaMap.get(id);
        if (gaList == null) {
            gaList = new LinkedList<IFViewAdapter>();
            vaMap.put(id, gaList);
        }
        gaList.add(va);
    }

    /**
     * Executes read on all attribute models contained in this data group.
     */
    public void read() {
        if (amMap != null && !amMap.isEmpty()) {
            state = DataState.NotChanged;
            for (IFAttributeModel<?> am : amMap.values()) {
                am.read();
            }
        }
    }

    /**
     * Executes write on all attribute models contained in this data group.
     */
    public void write() {
        if (amMap != null && !amMap.isEmpty()) {
            for (IFAttributeModel<?> am : amMap.values()) {
                am.write();
            }
        }
    }

    /**
     * @see net.ulrice.databinding.IFAttributeModelEventListener#stateChanged(net.ulrice.databinding.IFGuiAccessor,
     *      net.ulrice.databinding.IFAttributeModel,
     *      net.ulrice.databinding.DataState, net.ulrice.databinding.DataState)
     */
    @Override
    public void stateChanged(IFViewAdapter gaSource, IFAttributeModel amSource, DataState oldState, DataState newState) {
        String id = amSource.getId();
        changedSet.remove(id);
        invalidSet.remove(id);
        switch (newState) {
            case Invalid:
                invalidSet.add(id);
                break;
            case Changed:
                changedSet.add(id);
                break;
            default:
                break;
        }

        if (!invalidSet.isEmpty()) {
            state = DataState.Invalid;
        } else if (!changedSet.isEmpty()) {
            state = DataState.Changed;
        } else {
            state = DataState.NotChanged;
        }
    }

    /**
     * @see net.ulrice.databinding.IFAttributeModelEventListener#dataChanged(net.ulrice.databinding.IFGuiAccessor,
     *      net.ulrice.databinding.IFAttributeModel, java.lang.Object,
     *      java.lang.Object, net.ulrice.databinding.DataState)
     */
    @Override
    public void dataChanged(IFViewAdapter gaSource, IFAttributeModel amSource, Object oldValue, Object newValue,
            DataState state) {
        // Ignore these events.
    }

    /**
     * Return the list of all gui accessors contained in this data group.
     * 
     * @return The list of all gui accessors.
     */
    public List<IFViewAdapter> getGuiAccessors() {
        List<IFViewAdapter> result = new LinkedList<IFViewAdapter>();
        if (vaMap != null && !vaMap.isEmpty()) {
            for (List<IFViewAdapter> gaList : vaMap.values()) {
                result.addAll(gaList);
            }
        }
        return result;
    }

    /**
     * Return a list of all gui accessors for a specific id.
     * 
     * @param id The identifier.
     * @return The list of all gui accessors.
     */
    public List<IFViewAdapter> getGuiAccessors(String id) {
        List<IFViewAdapter> result = new LinkedList<IFViewAdapter>();
        if (vaMap != null && !vaMap.isEmpty()) {
            List<IFViewAdapter> gaList = vaMap.get(id);
            result.addAll(gaList);
        }
        return result;
    }

    /**
     * Return the first gui accessor for a specific id.
     * 
     * @param id The identifier.
     * @return The first gui accessor.
     */
    public IFViewAdapter getFirstGuiAccessor(String id) {
        if (vaMap != null && !vaMap.isEmpty()) {
            List<IFViewAdapter> gaList = vaMap.get(id);
            return gaList != null && !gaList.isEmpty() ? gaList.get(0) : null;
        }
        return null;
    }

    /**
     * Return a list of all attribute models contained in this data group.
     * 
     * @return The list of all attribute models.
     */
    public List<IFAttributeModel> getAttributeModels() {
        List<IFAttributeModel> result = new LinkedList<IFAttributeModel>();
        if (amMap != null && !amMap.isEmpty()) {
            result.addAll(amMap.values());
        }
        return result;
    }

    /**
     * Return the attribute model for a specific id.
     * 
     * @param id The identifier.
     * @return The attribute model.
     */
    public IFAttributeModel getAttributeModel(String id) {
        return amMap != null ? amMap.get(id) : null;
    }
    
    /**
     * Return the state of this connector group.
     * 
     * @return The state.
     */
    public DataState getState() {
        return state;
    }
}
