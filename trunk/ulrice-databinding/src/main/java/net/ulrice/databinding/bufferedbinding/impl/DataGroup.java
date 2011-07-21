package net.ulrice.databinding.bufferedbinding.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ulrice.databinding.bufferedbinding.IFBufferedBinding;
import net.ulrice.databinding.bufferedbinding.IFBufferedBindingEventListener;
import net.ulrice.databinding.bufferedbinding.IFDataGroup;
import net.ulrice.databinding.viewadapter.IFViewAdapter;

/**
 * A data group is a set of attribute model gui accessor pairs.
 * 
 * @author christof
 */
@SuppressWarnings("unchecked")
public class DataGroup implements IFDataGroup, IFBufferedBindingEventListener {

    /** The list of all gui accessors contained in this data group. */
    private Map<String, List<IFViewAdapter>> vaMap = new HashMap<String, List<IFViewAdapter>>();

    /** The list of all attribute models contained in this data group. */
    private Map<String, IFBufferedBinding> amMap = new HashMap<String, IFBufferedBinding>();

    /** The set of all changed attribute models. */
    private Set<String> changedSet = new HashSet<String>();

    /** The set of all invalid attribute models. */
    private Set<String> invalidSet = new HashSet<String>();

    boolean dirty;
    boolean initialized;
    boolean valid;

    public DataGroup() {
        initialized = false;
        valid = true;
        dirty = false;
    }
    /**
     * Add an attribute model to this data group.
     * 
     * @param am The attribute model.
     */
    public void addAM(IFBufferedBinding<?> am) {
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

        IFBufferedBinding am = amMap.get(id);
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
            initialized = true;
            valid = true;
            dirty = false;
            for (IFBufferedBinding<?> am : amMap.values()) {
                am.read();
            }
        }
    }

    /**
     * Executes write on all attribute models contained in this data group.
     */
    public void write() {
        if (amMap != null && !amMap.isEmpty()) {
            for (IFBufferedBinding<?> am : amMap.values()) {
                am.write();
            }
        }
    }

    /**
     * @see net.ulrice.databinding.bufferedbinding.IFBufferedBindingEventListener#stateChanged(net.ulrice.databinding.IFGuiAccessor,
     *      net.ulrice.databinding.bufferedbinding.IFAttributeModel,
     *      net.ulrice.databinding.DataState, net.ulrice.databinding.DataState)
     */
    @Override
    public void stateChanged(IFViewAdapter gaSource, IFBufferedBinding amSource) {
        String id = amSource.getId();
        changedSet.remove(id);
        invalidSet.remove(id);
        
        if(!amSource.isValid()) {
        	invalidSet.add(id);
        }
        
        if(amSource.isDirty()) {
        	changedSet.add(id);
        }

        dirty = !changedSet.isEmpty();
        valid = invalidSet.isEmpty();
    }

    /**
     * @see net.ulrice.databinding.bufferedbinding.IFBufferedBindingEventListener#dataChanged(net.ulrice.databinding.IFGuiAccessor,
     *      net.ulrice.databinding.bufferedbinding.IFAttributeModel, java.lang.Object,
     *      java.lang.Object, net.ulrice.databinding.DataState)
     */
    @Override
    public void dataChanged(IFViewAdapter gaSource, IFBufferedBinding amSource) {
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
    public List<IFBufferedBinding> getAttributeModels() {
        List<IFBufferedBinding> result = new LinkedList<IFBufferedBinding>();
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
    public IFBufferedBinding getAttributeModel(String id) {
        return amMap != null ? amMap.get(id) : null;
    }
    
    public boolean isValid() {
		return valid;
	}
    
    public boolean isDirty() {
		return dirty;
	}
    
    public boolean isInitialized() {
		return initialized;
	}
}
