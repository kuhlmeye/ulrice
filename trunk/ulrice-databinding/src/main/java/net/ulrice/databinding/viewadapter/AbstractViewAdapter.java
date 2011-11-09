package net.ulrice.databinding.viewadapter;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.converter.impl.DoNothingConverter;

public abstract class AbstractViewAdapter<M, V> implements IFViewAdapter<M, V> {

    private final EventListenerList listenerList = new EventListenerList();

    private boolean inNotification = false;

    private Class<V> viewType;

    private IFTooltipHandler<IFBinding> tooltipHandler;
    private IFStateMarker stateMarker;
    private IFValueConverter<M, V> valueConverter;
    private boolean bindWithoutValue;
    private boolean useAutoValueConverter = true;

    private boolean editable = true;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public AbstractViewAdapter(Class viewType) {
        this.viewType = viewType;
        setValueConverter(null);
    }

    protected void fireViewChange() {
        inNotification = true;
        if (isEditable()) {
            
            if(SwingUtilities.isEventDispatchThread()) {
                fireViewChangeInternal();
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    
                    @Override
                    public void run() {
                        fireViewChangeInternal();
                    }
                });
            }
        }
        inNotification = false;
    }

    private void fireViewChangeInternal() {
        IFViewChangeListener[] listeners = listenerList.getListeners(IFViewChangeListener.class);
        if(listeners != null) {
            for (IFViewChangeListener l : listeners) {
                l.viewValueChanged(this);
            }
        }
    }

    public void addBindingListener(ViewAdapterBindingListener l) {
        listenerList.add(ViewAdapterBindingListener.class, l);
    }
    
    public void removeBindingListener(ViewAdapterBindingListener l) {
        listenerList.remove(ViewAdapterBindingListener.class, l);
    }
    
    


    protected void fireAttributeModelBound(IFBinding binding) {
        ViewAdapterBindingListener[] listeners = listenerList.getListeners(ViewAdapterBindingListener.class);
        for (ViewAdapterBindingListener listener : listeners) {
            listener.attributeModelBound(this, binding);
        }
    }

    protected void fireAttributeModelDetached(IFBinding binding) {
        ViewAdapterBindingListener[] listeners = listenerList.getListeners(ViewAdapterBindingListener.class);
        for (ViewAdapterBindingListener listener : listeners) {
            listener.attributeModelDetached(this, binding);
        }
    }

    @Override
    public void bind(IFBinding binding) {
        fireAttributeModelBound(binding);
        updateFromBinding(binding);
    }
    
    @Override
    public void detach(IFBinding binding) {
        fireAttributeModelDetached(binding);
    }
    
    @Override
    public void updateFromBinding(IFBinding binding) {
        if (!isInNotification()) {
            removeComponentListener();
            setValue((M) binding.getCurrentValue());

            if(binding.isReadOnly() && isComponentEnabled()) {
                setComponentEnabled(false);
            }
            if(!binding.isReadOnly() && !isComponentEnabled()) {
                setComponentEnabled(true);
            }
            
            addComponentListener();
        }
        if (getTooltipHandler() != null) {
            getTooltipHandler().updateTooltip(binding, getComponent());
        }
        if (getStateMarker() != null) {
            getStateMarker().updateState(binding.isReadOnly() && isEditable(), binding.isDirty(), binding.isValid(), getComponent());
        }
    }

    @Override
    public final boolean isEditable() {
        return editable;
    }
    
    @Override
    public final void setEditable(boolean editable) {
        this.editable = editable;        
        fireViewChange();
    }
    
    
    
    protected abstract void addComponentListener();

    protected abstract void setValue(M value);

    protected abstract void removeComponentListener();

    
    @Override
    public void addViewChangeListener(IFViewChangeListener l) {
        listenerList.add(IFViewChangeListener.class, l);
    }

    @Override
    public void removeViewChangeListener(IFViewChangeListener l) {
        listenerList.remove(IFViewChangeListener.class, l);
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
