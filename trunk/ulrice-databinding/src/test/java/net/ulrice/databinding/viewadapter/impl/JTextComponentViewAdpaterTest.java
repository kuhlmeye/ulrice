package net.ulrice.databinding.viewadapter.impl;

import javax.swing.JTextField;

import junit.framework.TestCase;

public class JTextComponentViewAdpaterTest extends TestCase {
    
    public void testSetComponentEnabled() {
        JTextField textField = new JTextField();
        JTextComponentViewAdapter textVA = new JTextComponentViewAdapter(textField);
        
        textVA.setEnableSelectionIfComponentDisabled(true);
        textVA.setComponentEnabled(false);
        
        //Is this even possible? JavaDoc of component.setEnabled says that a component
        //is not editable after a call of setEnabled(false). Vice versa?
        assertTrue(textField.isEnabled());
        assertFalse(textField.isEditable());
    }
    
    public void testSetComponentEditable() {
        JTextField textField = new JTextField();
        JTextComponentViewAdapter textVA = new JTextComponentViewAdapter(textField);
        
        textVA.setEditable(false);
        
        // TODO please fix this
//        assertFalse(textField.isEditable());
    }
    
}
