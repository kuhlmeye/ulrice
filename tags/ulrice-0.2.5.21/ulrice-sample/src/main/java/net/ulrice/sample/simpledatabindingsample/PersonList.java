package net.ulrice.sample.simpledatabindingsample;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PersonList {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport (this);
    private final List<PersonDTO> personen = new ArrayList<PersonDTO> ();

    private final PropertyChangeListener childListener = new PropertyChangeListener() {
        public void propertyChange (PropertyChangeEvent evt) {
            pcs.firePropertyChange (evt);
        }
    };
    
    public void addPropertyChangeListener (PropertyChangeListener l) {
        pcs.addPropertyChangeListener (l);
    }

    public void removePropertyChangeListener (PropertyChangeListener l) {
        pcs.removePropertyChangeListener (l);
    }
    
    public List<PersonDTO> getPersonen () {
        return Collections.unmodifiableList (personen);
    }
    
    public void addPerson (int index, PersonDTO person) {
        person.addPropertyChangeListener (childListener);
        personen.add (index, person);
        pcs.firePropertyChange ("personen", null, personen);
    }
    
    public void removePerson (int index) {
        personen.remove (index).removePropertyChangeListener (childListener);
        pcs.firePropertyChange ("personen", null, personen);
    }
}
