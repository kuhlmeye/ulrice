package net.ulrice.databinding.viewadapter.impl;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
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

	public JSpinnerViewAdapter(JSpinner spinner, IFAttributeInfo attributeInfo) {
		super(Integer.class, attributeInfo);
		this.spinner = spinner;
		this.spinner.addChangeListener(this);
        setEditable(spinner.isEnabled());
	}
	
    @Override
    protected void setEditableInternal(boolean editable) {
        spinner.setEnabled(editable);
    }

	@Override
	public Object getValue() {
	    final Integer spinnerValue = (Integer) spinner.getValue();
		return viewToModel(spinnerValue == null ? 0 : spinnerValue);
	}

	@Override
	protected void setValue(Object value) {
	    final Integer spinnerValue = (value == null ? 0 : (Integer) value);
		spinner.setValue(modelToView(spinnerValue));
	}

	@Override
	public JSpinner getComponent() {
		return spinner;
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

    @Override
    public Object getDisplayedValue() {
        return spinner.getValue();
    }
}
