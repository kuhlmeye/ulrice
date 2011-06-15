package net.ulrice.sample.simpledatabindingsample;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


public class PersonDTO {
    private final PropertyChangeSupport _pcs = new PropertyChangeSupport (this);
    
    private String _vorname;
    private String _nachname;
    private int _zahl;
    private boolean _hatAuto;
    private String anrede;

    public PersonDTO () {}
    public PersonDTO (String vorname, String nachname, int zahl, boolean hatAuto) {
        _vorname = vorname;
        _nachname = nachname;
        _zahl = zahl;
        _hatAuto = hatAuto;
    }
    
    public void addPropertyChangeListener (PropertyChangeListener l) {
        _pcs.addPropertyChangeListener (l);
    }

    public void removePropertyChangeListener (PropertyChangeListener l) {
        _pcs.removePropertyChangeListener (l);
    }
    
    public String getVorname () {
        return _vorname;
    }
    public void setVorname (String vorname) {
        String old = _vorname;
        _vorname = vorname;
        _pcs.firePropertyChange ("vorname", old, vorname);
    }
    public String getNachname () {
        return _nachname;
    }
    public void setNachname (String nachname) {
        String old = _nachname;
        _nachname = nachname;
        _pcs.firePropertyChange ("nachname", old, nachname);
    }
    
    public String getName () {
        return getVorname () + " " + getNachname ();
    }
    
    public int getZahl () {
        return _zahl;
    }
    public void setZahl (int zahl) {
        int old = _zahl;
        _zahl = zahl;
        _pcs.firePropertyChange ("zahl", old, zahl);
    }
    
    public boolean getHatAuto () {
        return _hatAuto;
    }
    public void setHatAuto (boolean hatAuto) {
        boolean old = _hatAuto;
        _hatAuto = hatAuto;
        _pcs.firePropertyChange ("hatAuto", old, hatAuto);
    }
	public void setAnrede(String anrede) {
		_pcs.firePropertyChange("anrede", this.anrede, this.anrede = anrede);
	}
	public String getAnrede() {
		return anrede;
	}
}


