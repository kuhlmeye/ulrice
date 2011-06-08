package net.ulrice.databinding.impl.ga;

import javax.swing.JComponent;

import net.ulrice.databinding.DataState;
import net.ulrice.databinding.IFAttributeModel;
import net.ulrice.databinding.IFAttributeModelEventListener;
import net.ulrice.databinding.IFConverter;
import net.ulrice.databinding.IFGuiAccessor;
import net.ulrice.databinding.IFStateMarker;
import net.ulrice.databinding.IFTooltipHandler;
import net.ulrice.databinding.impl.ga.tooltip.DetailedTooltipHandler;

/**
 * An abstract gui accessor.
 * 
 * @author christof
 */
public abstract class AbstractGA<T extends JComponent, U, V> implements IFGuiAccessor<T, IFAttributeModel<U>>,
        IFAttributeModelEventListener<U> {

    /** The identifier for this gui accessor. */
    private String id;

    /** The gui component. */
    private T component;

    private IFConverter<U, V> guiConverter;

    /** The connected attribute model. */
    private IFAttributeModel<U> attributeModel;

    /** The class marking the current state at the component. */
    private IFStateMarker stateMarker;

    /** The tooltip handler of the component. */
    private IFTooltipHandler tooltipHandler;

    public AbstractGA(String id, T component, IFConverter<U, V> guiConverter) {
        this.id = id;
        this.guiConverter = guiConverter;
        this.component = component;

        BorderStateMarker border = new BorderStateMarker();
        this.component.setBorder(border);
        setStateMarker(border);
        
        setTooltipHandler(new DetailedTooltipHandler());
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#getComponent()
     */
    @Override
    public T getComponent() {
        return component;
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#getAttributeModel()
     */
    @Override
    public IFAttributeModel<U> getAttributeModel() {
        return attributeModel;
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#setAttributeModel(net.ulrice.databinding.IFAttributeModel)
     */
    @Override
    public void setAttributeModel(IFAttributeModel<U> attributeModel) {
        this.attributeModel = attributeModel;
        this.attributeModel.addAttributeModelEventListener(this);
    }

    public void setAttributeValue(U value) {
        if (getAttributeModel() != null) {
            getAttributeModel().gaChanged(this, value);
        }
    }

    /**
     * @see net.ulrice.databinding.IFAttributeModelEventListener#dataChanged(net.ulrice.databinding.IFGuiAccessor,
     *      net.ulrice.databinding.IFAttributeModel, java.lang.Object,
     *      java.lang.Object, net.ulrice.databinding.DataState)
     */
    @Override
    public void dataChanged(IFGuiAccessor<?, ?> gaSource, IFAttributeModel<U> amSource, U oldValue,
            U newValue, DataState state) {
        
        if(getTooltipHandler() != null) {
            getTooltipHandler().updateTooltip(amSource, this, component);
        }
        dataChangedIntern(gaSource, oldValue, newValue);
    }
    
    /**
     * 
     * @param gaSource The gui accessor responsible for the change or null if the change is coming for somewhere else
     * @param oldValue The old value
     * @param newValue The new value
     */
    protected abstract void dataChangedIntern(IFGuiAccessor<?, ?> gaSource, U oldValue,
                    U newValue);

    /**
     * @see net.ulrice.databinding.IFAttributeModelEventListener#stateChanged(net.ulrice.databinding.IFGuiAccessor,
     *      net.ulrice.databinding.IFAttributeModel,
     *      net.ulrice.databinding.DataState, net.ulrice.databinding.DataState)
     */
    @Override
    public void stateChanged(IFGuiAccessor<?, ?> gaSource, IFAttributeModel<U> amSource, DataState oldState,
            DataState newState) {
        if (getStateMarker() != null) {
            stateMarker.paintState(getComponent(), newState);
        }
        if(getTooltipHandler() != null) {
            getTooltipHandler().updateTooltip(amSource, this, component);
        }
    }

    /**
     * @return the guiConverter
     */
    public IFConverter<U, V> getGuiConverter() {
        return guiConverter;
    }

    /**
     * @param guiConverter the guiConverter to set
     */
    public void setGuiConverter(IFConverter<U, V> guiConverter) {
        this.guiConverter = guiConverter;
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#getStateMarker()
     */
    public IFStateMarker getStateMarker() {
        return stateMarker;
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#setStateMarker(net.ulrice.databinding.IFStateMarker)
     */
    public void setStateMarker(IFStateMarker stateMarker) {
        this.stateMarker = stateMarker;
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#getTooltipHandler()
     */
    public IFTooltipHandler getTooltipHandler() {
        return tooltipHandler;
    }

    /**
     * @see net.ulrice.databinding.IFGuiAccessor#setTooltipHandler(net.ulrice.databinding.IFTooltipHandler)
     */
    public void setTooltipHandler(IFTooltipHandler tooltipHandler) {
        this.tooltipHandler = tooltipHandler;
    }
}
