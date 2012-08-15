package net.ulrice.sample.simpledatabindingsample;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class PersonPanel extends JPanel {
    final JTextField vornameTF = new JTextField ();
    final JTextField vorname2TF = new JTextField ();
    final JTextField nachnameTF = new JTextField ();
    final JTextField zahlTF = new JTextField ();
    
    final JTextField nameTF = new JTextField ();
    
    final JCheckBox hatAutoCB = new JCheckBox ();
    
    final JComboBox anredeCombo = new JComboBox();
    
    final JButton saveButton = new JButton ("Speichern");
    final JButton otherButton = new JButton ("Anderer Button");
    
    public PersonPanel () {
        setLayout (null);
        
        createLabel ("Vorname", 0);
        createLabel ("Nachname", 1);
        createLabel ("Zahl", 2);
        createLabel ("Name", 3);
        createLabel ("Auto", 4);
        createLabel ("Anrede", 5);
        
        vornameTF. setBounds (200, 20, 200, 21);
        vorname2TF. setBounds (420, 20, 200, 21);
        nachnameTF.setBounds (200, 45, 200, 21);
        zahlTF.   setBounds (200, 70, 200, 21);
        
        add (vornameTF);
        add (vorname2TF);
        add (nachnameTF);
        add (zahlTF);
        
        nameTF.setBounds (200, 95, 200, 21);
        nameTF.setEnabled (false);
        add (nameTF);
       
        hatAutoCB.setBounds (200, 120, 200, 21);
        add (hatAutoCB);
        
        anredeCombo.setBounds (200, 145, 200, 21);
        add (anredeCombo);
        
        saveButton. setBounds ( 20, 250, 120, 25);
        otherButton.setBounds (150, 250, 220, 25);
        add (saveButton);
        add (otherButton);
    }
    
    private JLabel createLabel (String text, int line) {
        final JLabel result = new JLabel (text);
        result.setBounds (20, 20 + 25*line, 150, 20);
        add (result);
        return result;
    }
}
