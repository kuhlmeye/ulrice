package net.ulrice.sample.simpledatabindingsample;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class PersonPanel extends JPanel {
    final JTextField _vornameTF = new JTextField ();
    final JTextField _vorname2TF = new JTextField ();
    final JTextField _nachnameTF = new JTextField ();
    final JTextField _zahlTF = new JTextField ();
    
    final JTextField _nameTF = new JTextField ();
    
    final JCheckBox _hatAutoCB = new JCheckBox ();
    
    final JComboBox _anredeCombo = new JComboBox();
    
    final JButton _saveButton = new JButton ("Speichern");
    final JButton _otherButton = new JButton ("Anderer Button");
    
    public PersonPanel () {
        setLayout (null);
        
        createLabel ("Vorname", 0);
        createLabel ("Nachname", 1);
        createLabel ("Zahl", 2);
        createLabel ("Name", 3);
        createLabel ("Auto", 4);
        createLabel ("Anrede", 5);
        
        _vornameTF. setBounds (200, 20, 200, 21);
        _vorname2TF. setBounds (420, 20, 200, 21);
        _nachnameTF.setBounds (200, 45, 200, 21);
        _zahlTF.   setBounds (200, 70, 200, 21);
        
        add (_vornameTF);
        add (_vorname2TF);
        add (_nachnameTF);
        add (_zahlTF);
        
        _nameTF.setBounds (200, 95, 200, 21);
        _nameTF.setEnabled (false);
        add (_nameTF);
       
        _hatAutoCB.setBounds (200, 120, 200, 21);
        add (_hatAutoCB);
        
        _anredeCombo.setBounds (200, 145, 200, 21);
        add (_anredeCombo);
        
        _saveButton. setBounds ( 20, 250, 120, 25);
        _otherButton.setBounds (150, 250, 220, 25);
        add (_saveButton);
        add (_otherButton);
    }
    
    private JLabel createLabel (String text, int line) {
        final JLabel result = new JLabel (text);
        result.setBounds (20, 20 + 25*line, 150, 20);
        add (result);
        return result;
    }
}
