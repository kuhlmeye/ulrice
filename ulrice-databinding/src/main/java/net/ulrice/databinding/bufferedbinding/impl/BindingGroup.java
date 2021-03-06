package net.ulrice.databinding.bufferedbinding.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.bufferedbinding.IFAttributeModel;
import net.ulrice.databinding.viewadapter.IFViewAdapter;

/**
 * A data group is a set of attribute model gui accessor pairs.
 *
 * @author christof
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class BindingGroup extends AbstractBindingGroup {

    /** The list of all gui accessors contained in this data group. */
    private final Map<String, List<IFViewAdapter>> vaMap = new HashMap<String, List<IFViewAdapter>>();

    /** The list of all attribute models contained in this data group. */
    private final Map<String, IFAttributeModel> amMap = new HashMap<String, IFAttributeModel>();

    /** The set of all changed attribute models. */
    private Set<String> changedSet = new HashSet<String>();

    /** The set of all invalid attribute models. */
    private Set<String> invalidSet = new HashSet<String>();

    protected boolean dirty;
    protected boolean initialized;
    protected boolean valid;

    public BindingGroup() {
        initialized = false;
        valid = true;
        dirty = false;
    }


    public void bind(IFAttributeModel<?> attributeModel, IFViewAdapter viewAdapter) {

    	if (!amMap.containsValue(attributeModel)) {
    		addAttributeModel(attributeModel);
    	}

    	if (!vaMap.containsValue(viewAdapter)) {
    		addViewAdapter(attributeModel.getId(), viewAdapter);
    	}
    }

    public void unbind(IFAttributeModel<?> attributeModel, IFViewAdapter viewAdapter){
        if (amMap.containsValue(attributeModel)) {
            removeAttributeModel(attributeModel);
        }

        final List<IFViewAdapter> viewAdapters = vaMap.get(attributeModel.getId());
        if (viewAdapters != null && viewAdapters.contains(viewAdapter)) {
            removeViewAdapter(attributeModel.getId(), viewAdapter);
        }
    }

    public void unbind(IFAttributeModel<?> attributeModel){
        if (amMap.containsValue(attributeModel)) {
            removeAttributeModel(attributeModel);
        }

        final List<IFViewAdapter> viewAdapters = new ArrayList<IFViewAdapter>(vaMap.get(attributeModel.getId()));
        for (IFViewAdapter viewAdapter : viewAdapters) {
            removeViewAdapter(attributeModel.getId(), viewAdapter);
        }
    }

    public void removeAttributeModel(IFAttributeModel<?> am){
        if (am == null) {
            throw new IllegalArgumentException("Could not remove null attribute model.");
        }

        String id = am.getId();
        if (id == null) {
            throw new IllegalStateException("Id of an attribute model must not be null.");
        }

        List<IFViewAdapter> gaList = vaMap.get(id);
        if (gaList != null) {
            for (IFViewAdapter va : gaList) {
                am.removeViewAdapter(va);
            }
        }

        changedSet.remove(id);
        invalidSet.remove(id);
        checkDirtyAndValidStateAndWriteIt();

        am.removeAttributeModelEventListener(this);
        amMap.remove(id);
    }

    /**
     * Add an attribute model to this data group.
     *
     * @param am The attribute model.
     */
    public void addAttributeModel(IFAttributeModel<?> am) {
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

        if(am.isDirty()) {
            changedSet.add(am.getId());
        }

        if(!am.isValid()) {
            invalidSet.add(am.getId());
        }
        checkDirtyAndValidStateAndWriteIt();

        am.addAttributeModelEventListener(this);
        amMap.put(id, am);
    }

    /**
     * Add a gui accessor to this data group.
     *
     * @param va The gui accessor.
     */
    public void addViewAdapter(String id, IFViewAdapter va) {
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

    public void removeViewAdapter(String id, IFViewAdapter va) {
        if (va == null) {
            throw new IllegalArgumentException("Could not add null gui accessor.");
        }

        if (id == null) {
            throw new IllegalStateException("Id of an attribute model must not be null.");
        }

        IFAttributeModel am = amMap.get(id);
        if (am != null) {
            am.removeViewAdapter(va);
        }

        final List<IFViewAdapter> viewAdapters = vaMap.get(id);
        if (viewAdapters != null) {
            viewAdapters.remove(va);
        }
    }

    /**
     * Executes read on all attribute models contained in this data group.
     */
    @Override
    public void read() {
        initialized = true;
        valid = true;
        invalidSet.clear();
        dirty = false;
        changedSet.clear();
        // remove initialization before read
        for (IFAttributeModel<?> am : amMap.values()) {
            if (am instanceof GenericAM) {
                ((GenericAM< ?>) am).setInitialized(false);
            }
        }
        for (IFAttributeModel< ?> am : amMap.values()) {
            am.read();
        }
    }

    /**
     * Executes write on all attribute models contained in this data group.
     */
    @Override
    public void write() {
        for (IFAttributeModel<?> am : amMap.values()) {
            am.write();
        }
    }

    @Override
	protected void stateChangedInternal(IFViewAdapter viewAdapter,
			IFAttributeModel amSource) {
    	String id = amSource.getId();
        changedSet.remove(id);
        invalidSet.remove(id);

        if(!amSource.isValid()) {
        	invalidSet.add(id);
        }

        if(amSource.isDirty()) {
        	changedSet.add(id);
        }

        checkDirtyAndValidStateAndWriteIt();
	}

    private void checkDirtyAndValidStateAndWriteIt() {
        dirty = !changedSet.isEmpty();
        valid = invalidSet.isEmpty();
    }

    @Override
    protected void dataChangedInternal(IFViewAdapter viewAdapter, IFAttributeModel amSource) {
        // ignore
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
            if (null != gaList && !gaList.isEmpty()) {
                result.addAll(gaList);
            }
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

    public IFAttributeInfo getAttributeInfo(String id) {
        IFAttributeModel attributeModel = getAttributeModel(id);
        return attributeModel != null ? attributeModel.getAttributeInfo() : null;
    }

    @Override
    public boolean isValid() {
		return valid;
	}

    @Override
    public boolean isDirty() {
		return dirty;
	}

    public boolean isInitialized() {
		return initialized;
	}

    public void removeAttributeModel(String id) {
        amMap.remove(id);
    }

	public void clear() {
		amMap.clear();
		vaMap.clear();
		changedSet.clear();
		invalidSet.clear();

        initialized = false;
        valid = true;
        dirty = false;
	}
}
