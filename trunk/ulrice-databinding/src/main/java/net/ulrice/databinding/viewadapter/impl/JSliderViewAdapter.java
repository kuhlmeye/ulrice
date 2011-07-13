package net.ulrice.databinding.viewadapter.impl;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;

/**
 * Gui accessor for sliders
 * 
 * @author andre
 *
 * @param <U>
 */
public class JSliderViewAdapter extends AbstractViewAdapter implements ChangeListener {
    
    private JSlider slider;

    public JSliderViewAdapter(JSlider slider) {
        super(Integer.class);
        this.slider = slider;
        this.slider.addChangeListener(this);
    }

	@Override
	public void updateBinding(IFBinding binding) {
		if(!isInNotification()) {
            slider.removeChangeListener(this);
            slider.setValue((Integer)binding.getCurrentValue());
            slider.addChangeListener(this);
		}
		if(getTooltipHandler() != null) {
			getTooltipHandler().updateTooltip(binding, slider);
		}
		if(getStateMarker() != null) {
			getStateMarker().updateState(binding, slider);
		}
	}

	@Override
	public Object getValue() {
		return slider.getValue();
	}

	@Override
	public JComponent getComponent() {
		return slider;
	}

	@Override
	public void setEnabled(boolean enabled) {
		slider.setEnabled(enabled);
	}

	@Override
	public boolean isEnabled() {
		return slider.isEnabled();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		fireViewChange();
	}

}
