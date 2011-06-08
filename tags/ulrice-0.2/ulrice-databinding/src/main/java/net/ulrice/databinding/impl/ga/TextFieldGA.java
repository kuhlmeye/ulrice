/**
 * 
 */
package net.ulrice.databinding.impl.ga;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.ulrice.databinding.DataState;
import net.ulrice.databinding.IFAttributeModel;
import net.ulrice.databinding.IFConverter;
import net.ulrice.databinding.IFGuiAccessor;

/**
 * Gui accessor for text fields.
 * 
 * @author christof
 */
public class TextFieldGA<U> extends AbstractGA<JTextField, U, String> implements DocumentListener {

    /**
     * Creates a new textfield gui accessor.
     * 
     * @param id The id of this accessor.
     */
    public TextFieldGA(String id, IFConverter<U, String> guiConverter) {
        super(id, new JTextField(), guiConverter);
        getComponent().getDocument().addDocumentListener(this);
    }

    /**
     * @see net.ulrice.databinding.impl.ga.AbstractGA#dataChangedIntern(net.ulrice.databinding.IFGuiAccessor,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public void dataChangedIntern(IFGuiAccessor<?, ?> gaSource, U oldValue, U newValue) {
        if (!this.equals(gaSource)) {
            getComponent().getDocument().removeDocumentListener(this);
            getComponent().setText(getGuiConverter().mapToTarget(newValue));
            getComponent().getDocument().addDocumentListener(this);
        }
    }

    /**
     * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
     */
    @Override
    public void changedUpdate(DocumentEvent e) {
        guiChanged();
    }

    /**
     * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
     */
    @Override
    public void insertUpdate(DocumentEvent e) {
        guiChanged();
    }

    /**
     * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
     */
    @Override
    public void removeUpdate(DocumentEvent e) {
        guiChanged();
    }

    /**
     * Called, if the gui has changed.
     */
    private void guiChanged() {
        setAttributeValue(getGuiConverter().mapToSource(getComponent().getText()));
    }
}
