package net.ulrice.databinding;

import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.modelaccess.impl.UlriceReflectionUtils;


public class ReflectiveObjectWithPresentationConverter <M> implements IFValueConverter<M, ObjectWithPresentation<M>> {
    private final Class<M> modelClass;
    private final String presentationPath;

    public ReflectiveObjectWithPresentationConverter(Class<M> modelClass, String presentationPath) {
        this.modelClass = modelClass;
        this.presentationPath = presentationPath;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Class<? extends ObjectWithPresentation<M>> getViewType(Class<? extends M> modelType) {
        return (Class) ObjectWithPresentation.class;
    }

    @Override
    public Class<? extends M> getModelType(Class<? extends ObjectWithPresentation<M>> viewType) {
        return modelClass;
    }

    @Override
    public M viewToModel(ObjectWithPresentation<M> o) {
        return o.getValue();
    }

    @Override
    public ObjectWithPresentation<M> modelToView(M o) {
        final String presentation = (String) UlriceReflectionUtils.getValueByReflection(o, presentationPath);
        return new ObjectWithPresentation<M> (o, presentation);
    }
}
