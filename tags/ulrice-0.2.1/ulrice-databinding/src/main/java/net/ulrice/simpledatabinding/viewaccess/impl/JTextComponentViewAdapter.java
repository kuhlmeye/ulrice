package net.ulrice.simpledatabinding.viewaccess.impl;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import net.ulrice.simpledatabinding.viewaccess.AbstractViewAdapter;



public class JTextComponentViewAdapter extends AbstractViewAdapter {
    private final JTextComponent _textComponent;
    private Color _rememberedColor;
    private String _rememberedTooltip;
    
    private boolean _isValid = true;
    
    public JTextComponentViewAdapter (JTextComponent textComponent) {
        super (String.class, false);
        _textComponent = textComponent;
        
        _textComponent.getDocument ().addDocumentListener (new DocumentListener() {
            public void removeUpdate (DocumentEvent e) {
                fireViewChange ();
            }
            
            public void insertUpdate (DocumentEvent e) {
                fireViewChange ();
            }
            
            public void changedUpdate (DocumentEvent e) {
                fireViewChange ();
            }
        });
    }

    @Override
    public Object getValue () {
        return _textComponent.getText ();
    }

    @Override
    public void setValue (Object value) {
        _textComponent.setText ((String) value);
    }

    @Override
    public void setValidationFailures (List<String> messages) {
        if (messages.isEmpty ()) {
            if (_isValid)
                return;
            
            _isValid = true;
            _textComponent.setBackground (_rememberedColor);
            _textComponent.setToolTipText (_rememberedTooltip);
        }
        else {
            if (_isValid) {
                _rememberedColor = _textComponent.getBackground ();
                _rememberedTooltip = _textComponent.getToolTipText ();
            }
            _isValid = false;
            
            _textComponent.setBackground (Color.RED);
            
            final StringBuilder tt = new StringBuilder ();
            final Iterator<String> it = messages.iterator ();
            while (it.hasNext ()) {
                tt.append (it.next ());
                if (it.hasNext ())
                    tt.append ("\n");
            }
            _textComponent.setToolTipText (tt.toString ());
        }
    }

    public void setEnabled (boolean enabled) {
        _textComponent.setEnabled (enabled);
    }
}
