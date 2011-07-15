/**
 * 
 */
package net.ulrice.databinding.viewadapter.impl;

import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;
import net.ulrice.databinding.viewadapter.IFStateMarker;
import net.ulrice.databinding.viewadapter.IFTooltipHandler;

/**
 * Gui accessor for text fields.
 * 
 * @author christof
 */
public class JTextComponentViewAdapter extends AbstractViewAdapter implements DocumentListener {

	
	private JTextComponent textComponent;


	public JTextComponentViewAdapter(JTextComponent textComponent) {
		super(String.class);
		this.textComponent = textComponent;
		textComponent.getDocument().addDocumentListener(this);
	}
	
	@Override
	public Object getValue() {
		return viewToModel(textComponent.getText());
	}

	@Override
	protected void setValue(Object value) {
		textComponent.setText((String)modelToView(value));
	}

	@Override
	public JTextComponent getComponent() {
		return textComponent;
	}

	@Override
	public void setEnabled(boolean enabled) {
		textComponent.setEnabled(enabled);
	}


    @Override
    public void changedUpdate(DocumentEvent e) {
    	fireViewChange();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
    	fireViewChange();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
    	fireViewChange();
    }

	@Override
	public boolean isEnabled() {
		return textComponent.isEnabled();
	}

	@Override
	protected void addComponentListener() {
		textComponent.getDocument().removeDocumentListener(this);
	}

	@Override
	protected void removeComponentListener() {
		textComponent.getDocument().addDocumentListener(this);	
	}

}
