package net.ulrice.databinding;

import java.util.HashMap;
import java.util.Map;

import net.ulrice.databinding.bufferedbinding.IFAttributeModel;
import net.ulrice.databinding.bufferedbinding.impl.GenericAM;
import net.ulrice.databinding.modelaccess.impl.ReflectionMVA;
import net.ulrice.databinding.modelaccess.impl.ReflectionUtils;


/**
 * This is a convenience class for models that contain a single object, e.g. a Transfer Object.
 * 
 * @author arno
 */
public class SingleObjectModel<T> {
    private final Class<T> dataClass;
    private T data;
    @SuppressWarnings("rawtypes")
    private final Map<String, IFAttributeModel> attributeModels = new HashMap<String, IFAttributeModel>();
    
    public SingleObjectModel(Class<T> dataClass) {
        this.dataClass = dataClass;
    }
    public SingleObjectModel(T data, Class<T> dataClass) {
        this (dataClass);
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
        if (attributeModels.get(path) == null) {
            attributeModels.put(path, new GenericAM<T>(new ReflectionMVA(ReflectionMVA.createID(this, path), this, "data." + path, false, ReflectionUtils.getFieldType(dataClass, path))));
        }
        return attributeModels.get(path);
    }
    
    @SuppressWarnings("rawtypes")
    public void setAttributeModel(String path, IFAttributeModel am) {
        attributeModels.put(path, am);
    }
}
