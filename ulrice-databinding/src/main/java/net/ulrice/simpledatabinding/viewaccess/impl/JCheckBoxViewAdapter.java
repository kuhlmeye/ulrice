package net.ulrice.simpledatabinding.viewaccess.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JCheckBox;

import net.ulrice.simpledatabinding.viewaccess.AbstractViewAdapter;


public class JCheckBoxViewAdapter extends AbstractViewAdapter {
    private final JCheckBox _checkBox;
    
    public JCheckBoxViewAdapter (JCheckBox checkBox) {
        super (Boolean.class, false);
        
        _checkBox = checkBox;
        
        _checkBox.addActionListener (new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                fireViewChange ();
            }
        });
    }

    public Object getValue () {
        return _checkBox.isSelected ();
    }

    public void setValue (Object value) {
        _checkBox.setSelected (Boolean.TRUE.equals (value));
    }

    public void setEnabled (boolean enabled) {
        _checkBox.setEnabled (enabled);
    }

    public void setValidationFailures (List<String> messages) {
        //TODO
    }
}
