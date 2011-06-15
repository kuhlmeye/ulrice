package net.ulrice.simpledatabinding.viewaccess.impl;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JComboBox;

import net.ulrice.simpledatabinding.util.ObjectWithPresentation;
import net.ulrice.simpledatabinding.viewaccess.AbstractViewAdapter;


public class JComboBoxViewAdapter extends AbstractViewAdapter {
    private final JComboBox _combo;
//    private Color _rememberedColor;
//    private String _rememberedTooltip;
//    
//    private boolean _isValid = true;
    
    public JComboBoxViewAdapter (JComboBox combo) {
        super (String.class, false);
        _combo = combo;
        
        _combo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				fireViewChange();
			}
		});
    }

    @Override
    public Object getValue () {
        return ((ObjectWithPresentation) _combo.getSelectedItem()).getValue();
    }

    @Override
    public void setValue (Object value) {
    	_combo.setSelectedItem (new ObjectWithPresentation(value, ""));
    }

    @Override
    public void setValidationFailures (List<String> messages) {
//        if (messages.isEmpty ()) {
//            if (_isValid)
//                return;
//            
//            _isValid = true;
//            _combo.setBackground (_rememberedColor);
//            _combo.setToolTipText (_rememberedTooltip);
//        }
//        else {
//            if (_isValid) {
//                _rememberedColor = _combo.getBackground ();
//                _rememberedTooltip = _combo.getToolTipText ();
//            }
//            _isValid = false;
//            
//            _combo.setBackground (Color.RED);
//            
//            final StringBuilder tt = new StringBuilder ();
//            final Iterator<String> it = messages.iterator ();
//            while (it.hasNext ()) {
//                tt.append (it.next ());
//                if (it.hasNext ())
//                    tt.append ("\n");
//            }
//            _combo.setToolTipText (tt.toString ());
//        }
    }

    public void setEnabled (boolean enabled) {
        _combo.setEnabled (enabled);
    }
}
