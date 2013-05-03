package net.ulrice.sample;

import java.util.HashMap;
import java.util.Map;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.bufferedbinding.IFAttributeModel;
import net.ulrice.databinding.bufferedbinding.impl.AbstractBindingGroup;
import net.ulrice.databinding.bufferedbinding.impl.GenericAM;
import net.ulrice.databinding.modelaccess.impl.ReflectionMVA;
import net.ulrice.databinding.modelaccess.impl.UlriceReflectionUtils;
import net.ulrice.databinding.viewadapter.IFViewAdapter;

/**
 * This is a convenience class for models that contain a single object, e.g. a Transfer Object.
 * 
 * @author arno
 */
public class SingleObjectModel<T> extends AbstractBindingGroup<T> {
    private final Class<T> dataClass;
    private T data;    
    
    @SuppressWarnings("rawtypes")
    private final Map<String, IFAttributeModel> attributeModels = new HashMap<String, IFAttributeModel>();

    public SingleObjectModel(Class<T> dataClass) {
        this.dataClass = dataClass;
    }

    public SingleObjectModel(T data, Class<T> dataClass) {
        this(dataClass);
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @SuppressWarnings("rawtypes")
    public IFAttributeModel getAttributeModel(String path) {
        IFAttributeInfo attributeInfo = new IFAttributeInfo() {
        };
        
        if (!path.startsWith("data.")) {
            path = "data." + path;
        }

        if (attributeModels.get(path) == null) {
            attributeModels.put(path, new GenericAM<T>(new ReflectionMVA(ReflectionMVA.createID(this, path), this,
                path, false, UlriceReflectionUtils.getFieldType(dataClass, path.substring("data.".length()))), attributeInfo));
        }
        return attributeModels.get(path);
    }

    @SuppressWarnings("rawtypes")
    public void setAttributeModel(String path, IFAttributeModel am) {        
        if (!path.startsWith("data.")) {
            throw new IllegalArgumentException("all valid paths must start with 'data.'");
        }

        attributeModels.put(path, am);
    }

    @Override
    public boolean isDirty() {
        boolean dirty = false;
        for(IFAttributeModel<?> am : attributeModels.values()) {
            dirty |= am.isDirty();
        }
        return dirty;
    }
    
    @Override    
    public boolean isValid() {
        boolean valid = true;
        for(IFAttributeModel<?> am : attributeModels.values()) {
            valid &= am.isValid();
        }
        return valid;
    }
    
    @Override
    public void read()  {
        for(IFAttributeModel<?> am : attributeModels.values()) {
            am.read();
        }
    }
    
    @Override
    public void write() {
        for(IFAttributeModel<?> am : attributeModels.values()) {
            am.write();
        }
    }

	@Override
	protected void stateChangedInternal(IFViewAdapter viewAdapter,
			IFAttributeModel<T> amSource) {
	}

    @Override
    protected void dataChangedInternal(IFViewAdapter viewAdapter, IFAttributeModel<T> amSource) {
        // TODO Auto-generated method stub
        
    }

}
