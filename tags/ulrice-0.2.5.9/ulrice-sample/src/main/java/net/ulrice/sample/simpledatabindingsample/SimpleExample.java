package net.ulrice.sample.simpledatabindingsample;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.ulrice.databinding.ObjectWithPresentation;
import net.ulrice.databinding.directbinding.Binding;
import net.ulrice.databinding.directbinding.ModelBinding;
import net.ulrice.databinding.validation.impl.StringLengthValidator;
import net.ulrice.databinding.viewadapter.impl.BorderStateMarker;
import net.ulrice.databinding.viewadapter.impl.DetailedTooltipHandler;

public class SimpleExample {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final JFrame frame = new JFrame("Binding-Demo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                frame.setBounds(50, 50, 800, 600);
                frame.setLayout(new BorderLayout());

                final PersonPanel panel = new PersonPanel();
                panel.anredeCombo.addItem(new ObjectWithPresentation("Herr", "Hr."));
                panel.anredeCombo.addItem(new ObjectWithPresentation("Frau", "Fr."));

                final PersonDTO model = new PersonDTO("Arno", "Haase", 99, true);

                // -----------------------------------------------------------

                final ModelBinding binding = new ModelBinding(model);

                Binding b = binding.register(panel.vornameTF, "vorname", String.class); // TODO do not pass the type
                                                                                        // any more
                BorderStateMarker borderStateMarker = new BorderStateMarker(true, false, false);
                b.getViewAdapter().setStateMarker(borderStateMarker);
                panel.vornameTF.setBorder(borderStateMarker);
                b.getViewAdapter().setTooltipHandler(new DetailedTooltipHandler());

                b = binding.register(panel.vorname2TF, "vorname", String.class, new StringLengthValidator(2, 30));
                borderStateMarker = new BorderStateMarker(true, false, false);
                b.getViewAdapter().setStateMarker(borderStateMarker);
                panel.vorname2TF.setBorder(borderStateMarker);
                b.getViewAdapter().setTooltipHandler(new DetailedTooltipHandler());

                binding.register(panel.nachnameTF, "nachname", String.class);
                binding.register(panel.zahlTF, "zahl", Integer.class);
                binding.register(panel.nameTF, "name", String.class);
                binding.register(panel.hatAutoCB, "hatAuto", Boolean.class, "vorname.length() < 5");

                binding.registerWithoutData(panel.saveButton, "#isValid && vorname.startsWith ('A')");
                binding.register(panel.otherButton, "vorname + \"'s Button\"", String.class, "hatAuto && name.length() < 20");

                binding.register(panel.anredeCombo, "anrede", String.class);

                // -----------------------------------------------------------

                frame.add(panel, BorderLayout.CENTER);
                frame.setVisible(true);
            }
        });
    }
}
