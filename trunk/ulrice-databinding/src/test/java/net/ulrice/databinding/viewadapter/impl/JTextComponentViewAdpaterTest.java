package net.ulrice.databinding.viewadapter.impl;

import javax.swing.JTextField;

import junit.framework.TestCase;

public class JTextComponentViewAdpaterTest extends TestCase {
    
    public void testSetComponentEnabled() {
        JTextField textField = new JTextField();
        JTextComponentViewAdapter textVA = new JTextComponentViewAdapter(textField, null);
        
        textVA.setEditable(false);
        
        assertTrue(textField.isEnabled());
        assertFalse(textField.isEditable());
    }
}
