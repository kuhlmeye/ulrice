package net.ulrice.databinding.viewadapter.impl;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
	public Object getValue() {
		return viewToModel(slider.getValue());
	}

	@Override
	protected void setValue(Object value) {
	    if(value != null) {
	        slider.setValue((Integer) modelToView(value));
	    }
	}

	@Override
	public JSlider getComponent() {
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

	@Override
	protected void addComponentListener() {
		slider.addChangeListener(this);
	}

	@Override
	protected void removeComponentListener() {
		slider.removeChangeListener(this);
	}
}
