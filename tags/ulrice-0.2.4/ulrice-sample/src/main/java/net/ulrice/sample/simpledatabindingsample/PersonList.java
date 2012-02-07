package net.ulrice.sample.simpledatabindingsample;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PersonList {
    private final PropertyChangeSupport _pcs = new PropertyChangeSupport (this);
    private final List<PersonDTO> _personen = new ArrayList<PersonDTO> ();

    private final PropertyChangeListener _childListener = new PropertyChangeListener() {
        public void propertyChange (PropertyChangeEvent evt) {
            _pcs.firePropertyChange (evt);
        }
    };
    
    public void addPropertyChangeListener (PropertyChangeListener l) {
        _pcs.addPropertyChangeListener (l);
    }

    public void removePropertyChangeListener (PropertyChangeListener l) {
        _pcs.removePropertyChangeListener (l);
    }
    
    public List<PersonDTO> getPersonen () {
        return Collections.unmodifiableList (_personen);
    }
    
    public void addPerson (int index, PersonDTO person) {
        person.addPropertyChangeListener (_childListener);
        _personen.add (index, person);
        _pcs.firePropertyChange ("personen", null, _personen);
    }
    
    public void removePerson (int index) {
        _personen.remove (index).removePropertyChangeListener (_childListener);
        _pcs.firePropertyChange ("personen", null, _personen);
    }
}
