package net.ulrice.databinding.viewadapter;

import java.util.ArrayList;
import java.util.List;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.converter.impl.DoNothingConverter;

public abstract class AbstractViewAdapter<M, V> implements IFViewAdapter<M, V> {

    private final List<IFViewChangeListener> _listeners = new ArrayList<IFViewChangeListener>();

    private boolean inNotification = false;

    private Class<V> viewType;

    private IFTooltipHandler<IFBinding> tooltipHandler;
    private IFStateMarker stateMarker;
    private IFValueConverter<M, V> valueConverter;
    private boolean bindWithoutValue;
    private boolean useAutoValueConverter = true;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public AbstractViewAdapter(Class viewType) {
        this.viewType = viewType;
        setValueConverter(null);
    }

    protected void fireViewChange() {
        inNotification = true;
        if (isEnabled()) {
            for (IFViewChangeListener l : _listeners) {
                l.viewValueChanged(this);
            }
        }
        inNotification = false;
    }

    @Override
    public void updateFromBinding(IFBinding binding) {
        if (!isInNotification()) {
            removeComponentListener();
            setValue((M) binding.getCurrentValue());
            addComponentListener();
        }
        if (getTooltipHandler() != null) {
            getTooltipHandler().updateTooltip(binding, getComponent());
        }
        if (getStateMarker() != null) {
            getStateMarker().updateState(binding.isDirty(), binding.isValid(), getComponent());
        }
    }

    protected abstract void addComponentListener();

    protected abstract void setValue(M value);

    protected abstract void removeComponentListener();

    @Override
    public void addViewChangeListener(IFViewChangeListener l) {
        _listeners.add(l);
    }

    @Override
    public void removeViewChangeListener(IFViewChangeListener l) {
        _listeners.remove(l);
    }

    protected boolean isInNotification() {
        return inNotification;
    }

    @Override
    public Class<V> getViewType() {
        return viewType;
    }

    @Override
    public void setTooltipHandler(IFTooltipHandler<IFBinding> tooltipHandler) {
        this.tooltipHandler = tooltipHandler;
    }

    @Override
    public void setStateMarker(IFStateMarker stateMarker) {
        this.stateMarker = stateMarker;
        if (this.stateMarker != null) {
            this.stateMarker.initialize(getComponent());
        }
    }

    protected IFTooltipHandler<IFBinding> getTooltipHandler() {
        return tooltipHandler;
    }

    protected IFStateMarker getStateMarker() {
        return stateMarker;
    }

    public boolean isBindWithoutValue() {
        return bindWithoutValue;
    }

    public void setBindWithoutValue(boolean bindWithoutValue) {
        this.bindWithoutValue = bindWithoutValue;
    }

    @SuppressWarnings("unchecked")
    public void setValueConverter(IFValueConverter<M, V> valueConverter) {
        this.valueConverter = valueConverter;
        if (this.valueConverter == null) {
            this.valueConverter = DoNothingConverter.INSTANCE;
        }
    }

    @Override
    public IFValueConverter<M, V> getValueConverter() {
        return valueConverter;
    }

    protected M viewToModel(V object) {
        return getValueConverter().viewToModel(object);
    }

    protected V modelToView(M object) {
        return getValueConverter().modelToView(object);
    }

    @Override
    public boolean isUseAutoValueConverter() {
        return useAutoValueConverter;
    }

    public void setUseAutoValueConverter(boolean useAutoValueConverter) {
        this.useAutoValueConverter = useAutoValueConverter;
    }
}
