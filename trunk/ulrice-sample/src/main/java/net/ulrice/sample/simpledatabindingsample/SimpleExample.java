package net.ulrice.sample.simpledatabindingsample;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.ulrice.databinding.validation.impl.StringLengthValidator;
import net.ulrice.databinding.viewadapter.impl.BorderStateMarker;
import net.ulrice.databinding.viewadapter.impl.DetailedTooltipHandler;
import net.ulrice.simpledatabinding.Binding;
import net.ulrice.simpledatabinding.ModelBinding;
import net.ulrice.simpledatabinding.util.ObjectWithPresentation;



public class SimpleExample {
    public static void main (String[] args) throws Exception {
        SwingUtilities.invokeLater (new Runnable () {
            public void run () {
                final JFrame frame = new JFrame ("Binding-Demo");
                frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
                
                frame.setBounds (50, 50, 800, 600);
                frame.setLayout (new BorderLayout ());
                
                final PersonPanel panel = new PersonPanel ();
                panel._anredeCombo.addItem (new ObjectWithPresentation ("Herr", "Hr."));
                panel._anredeCombo.addItem (new ObjectWithPresentation ("Frau", "Fr."));
                
                final PersonDTO model = new PersonDTO ("Arno", "Haase", 99, true);
                
                //-----------------------------------------------------------
                
                final ModelBinding binding = new ModelBinding (model);        
                
                Binding b = binding.register (panel._vornameTF, "vorname", String.class); //TODO do not pass the type any more
                BorderStateMarker borderStateMarker = new BorderStateMarker();
				b.getViewAdapter().setStateMarker(borderStateMarker);
                panel._vornameTF.setBorder(borderStateMarker);
                b.getViewAdapter().setTooltipHandler(new DetailedTooltipHandler());
                
                b = binding.register (panel._vorname2TF, "vorname", String.class, new StringLengthValidator (2, 30)); 
                borderStateMarker = new BorderStateMarker();
				b.getViewAdapter().setStateMarker(borderStateMarker);
                panel._vorname2TF.setBorder(borderStateMarker);
                b.getViewAdapter().setTooltipHandler(new DetailedTooltipHandler());
                
                binding.register (panel._nachnameTF, "nachname", String.class); 
                binding.register (panel._zahlTF, "zahl", Integer.class);
                binding.register (panel._nameTF, "name", String.class); 
                binding.register (panel._hatAutoCB, "hatAuto", Boolean.class, "vorname.length() < 5");
                
                binding.registerWithoutData (panel._saveButton, "#isValid && vorname.startsWith ('A')");
                binding.register (panel._otherButton, "vorname + \"'s Button\"", String.class, "hatAuto && name.length() < 20");

                binding.register (panel._anredeCombo, "anrede", String.class);
                
                //-----------------------------------------------------------
                
                frame.add (panel, BorderLayout.CENTER);
                frame.setVisible (true);
            }
        });
    }
}

