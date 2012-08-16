package net.ulrice.sample.simpledatabindingsample;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.ulrice.databinding.directbinding.ModelBinding;
import net.ulrice.databinding.directbinding.table.ExpressionColumnSpec;
import net.ulrice.databinding.directbinding.table.TypedEditableColumnTableModel;




public class TableExample {
    private final JFrame frame = new JFrame ("Binding-Demo");
    private final PersonList model = new PersonList ();

    private int counter = 0;
    
    public TableExample () {
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

        frame.setBounds (50, 50, 800, 600);
        frame.setLayout (new BorderLayout ());

//         final JTable table = new JTable ();
         final JTable table = new JTable (new TypedEditableColumnTableModel ());
        frame.add (new JScrollPane (table), BorderLayout.CENTER);

        final JButton button = new JButton ();
        frame.add (button, BorderLayout.SOUTH);
        
        final JTextField text = new JTextField ();
        frame.add (text, BorderLayout.NORTH);

        model.addPerson (0, new PersonDTO ("Arno",   "Haase", 99, true));
        model.addPerson (1, new PersonDTO ("Fred",   "Haase", 59, false));
        model.addPerson (2, new PersonDTO ("Martin", "Haase", 79, true));

        //-----------------------------------------------------------

        final ModelBinding binding = new ModelBinding (model);        

//         binding.registerSingleListTable (table.getModel (), "personen", "vorname", "nachname", "name", "hatAuto", "zahl");
        binding.registerSingleListTable (table.getModel (), "personen", 
                new ExpressionColumnSpec ("vorname", String.class), 
                new ExpressionColumnSpec ("nachname", String.class),
                new ExpressionColumnSpec ("name",String.class), 
                new ExpressionColumnSpec ("hatAuto",Boolean.class), 
                new ExpressionColumnSpec ("zahl", Integer.class));

        binding.register (button, "\"Button von \" + personen[0].name", String.class, "personen[0].hatAuto");
        binding.register (text, "personen[0].vorname", String.class);

        //-----------------------------------------------------------

        frame.setVisible (true);

        for (int i=0; i<10; i++)
            addPerson ();
        
//        startAdderThread ();
    }

    @SuppressWarnings ("unused")
    private void startAdderThread () {
        new Thread () {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep (500);
                    } catch (InterruptedException exc) {
                    }
                    
                    addPerson ();
                }
            }
        }.start ();
    }
    
    private void addPerson () {
        final PersonDTO person = new PersonDTO ("vorname-" + counter, "nachname-" + counter, counter, counter % 3 == 0);
        counter++;
        SwingUtilities.invokeLater (new Runnable () {
            public void run () {
                model.addPerson (1, person);
            }
        });
    }

    public static void main (String[] args) throws Exception {
        SwingUtilities.invokeLater (new Runnable () {
            public void run () {
                new TableExample ();
            }
        });
    }
}

