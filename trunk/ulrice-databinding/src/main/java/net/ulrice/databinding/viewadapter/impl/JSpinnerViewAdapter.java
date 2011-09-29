package net.ulrice.databinding.viewadapter.impl;

import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.ulrice.databinding.viewadapter.AbstractViewAdapter;

/**
 * Gui accessor for a jspinner
 * 
 * @author christof
 * 
 * @param <U>
 */
public class JSpinnerViewAdapter extends AbstractViewAdapter implements ChangeListener {

	private JSpinner spinner;

	public JSpinnerViewAdapter() {
		this(new JSpinner());
	}

	public JSpinnerViewAdapter(JSpinner spinner) {
		super(Integer.class);
		this.spinner = spinner;
		this.spinner.addChangeListener(this);
	}

	@Override
	public Object getValue() {
		return viewToModel(spinner.getValue());
	}

	@Override
	protected void setValue(Object value) {
		spinner.setValue((Integer) modelToView(value));
	}

	@Override
	public JSpinner getComponent() {
		return spinner;
	}

	@Override
	public void setEnabled(boolean enabled) {
	    spinner.setEnabled(enabled);
	}

	@Override
	public boolean isEnabled() {
		return spinner.isEnabled();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		fireViewChange();
	}

	@Override
	protected void addComponentListener() {
	    spinner.addChangeListener(this);
	}

	@Override
	protected void removeComponentListener() {
	    spinner.removeChangeListener(this);
	}
}
