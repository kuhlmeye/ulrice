package net.ulrice.simpledatabinding.viewaccess.impl;

import java.util.List;

import javax.swing.JButton;

import net.ulrice.simpledatabinding.viewaccess.AbstractViewAdapter;



public class JButtonViewAdapter extends AbstractViewAdapter {
    private final JButton _button;
    
    public JButtonViewAdapter (JButton button) {
        super (String.class, true);
        _button = button;
    }

    public Object getValue () {
        return _button.getText ();
    }

    public void setValue (Object value) {
        _button.setText (String.valueOf (value));
    }

    public void setEnabled (boolean enabled) {
        _button.setEnabled (enabled);
    }

    public void setValidationFailures (List<String> messages) {
        //TODO
    }
}


