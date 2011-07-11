package net.ulrice.databinding.impl.ga;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.ulrice.databinding.IFGuiAccessor;
import net.ulrice.databinding.converter.IFValueConverter;

/**
 * Gui accessor for sliders
 * 
 * @author andre
 *
 * @param <U>
 */
public class SliderGA<U> extends AbstractGA<JSlider, U, Integer> implements ChangeListener {
    
    /**
     * 
     * TODO: description
     *
     * @param id The id of this slider
     * @param guiConverter The converter
     */
    public SliderGA(String id, IFValueConverter guiConverter) {
        super(id, new JSlider(), guiConverter);
        getComponent().addChangeListener(this);
    }

    /**
     * 
     * {@inheritDoc}
     * @see net.ulrice.databinding.impl.ga.AbstractGA#dataChangedIntern(net.ulrice.databinding.IFGuiAccessor, java.lang.Object, java.lang.Object)
     */
    @Override
    protected void dataChangedIntern(IFGuiAccessor< ?, ?> gaSource, U oldValue, U newValue) {
        if (!this.equals(gaSource)) {
            getComponent().removeChangeListener(this);
        	getComponent().setValue((Integer)getGuiConverter().modelToView(newValue));
            getComponent().addChangeListener(this);
        }
    }

    /**
     * 
     * {@inheritDoc}
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        guiChanged();
    }
    
    /**
     * Called, if the gui has changed.
     */
    private void guiChanged() {
        setAttributeValue((U)getGuiConverter().viewToModel(getComponent().getValue()));
    }

}
