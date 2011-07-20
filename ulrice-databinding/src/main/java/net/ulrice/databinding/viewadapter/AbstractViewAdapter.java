package net.ulrice.databinding.viewadapter;

import java.util.ArrayList;
import java.util.List;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.converter.IFValueConverter;




public abstract class AbstractViewAdapter implements IFViewAdapter {

    private final List<IFViewChangeListener> _listeners = new ArrayList<IFViewChangeListener> ();

    private boolean inNotification = false;

	private Class<?> viewType;
	
	private IFTooltipHandler tooltipHandler;
	private IFStateMarker stateMarker;
	private IFValueConverter valueConverter;
	private boolean bindWithoutValue;

    public AbstractViewAdapter(Class<?> viewType) {
		this.viewType = viewType;
	}

	protected void fireViewChange () {
    	inNotification = true;
        for (IFViewChangeListener l: _listeners) {
            l.viewValueChanged (this);
        }
    	inNotification = false;
    }
	
	@Override
	public void updateFromBinding(IFBinding binding) {
		if(!isInNotification()) {
			removeComponentListener();
			setValue(binding.getCurrentValue());
			addComponentListener();
		}
		if(getTooltipHandler() != null) {
			getTooltipHandler().updateTooltip(binding, getComponent());
		}
		if(getStateMarker() != null) {
			getStateMarker().updateState(binding, getComponent());
		}
	}
    
    protected abstract void addComponentListener();

	protected abstract void setValue(Object value);

	protected abstract void removeComponentListener();

	@Override
    public void addViewChangeListener (IFViewChangeListener l) {
        _listeners.add (l);
    }


    @Override
    public void removeViewChangeListener (IFViewChangeListener l) {
        _listeners.remove (l);
    }
    
    protected boolean isInNotification() {
    	return inNotification;
    }
    

	@Override
	public Class<?> getViewType() {
		return viewType;
	}

	@Override
	public void setTooltipHandler(IFTooltipHandler tooltipHandler) {
		this.tooltipHandler = tooltipHandler;		
	}

	@Override
	public void setStateMarker(IFStateMarker stateMarker) {
		this.stateMarker = stateMarker;
		this.stateMarker.initialize(getComponent());
	}

	protected IFTooltipHandler getTooltipHandler() {
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

	public void setValueConverter(IFValueConverter valueConverter) {
		this.valueConverter = valueConverter;
	}
	
	public IFValueConverter getValueConverter() {
		return valueConverter;
	}

    
	protected Object viewToModel(Object object) {
		if(getValueConverter() != null) {
			return getValueConverter().viewToModel(object);
		}
		return object;
	}

    protected Object modelToView(Object object) {
		if(getValueConverter() != null) {
			return getValueConverter().modelToView(object);
		}
		return object;
	}
}
