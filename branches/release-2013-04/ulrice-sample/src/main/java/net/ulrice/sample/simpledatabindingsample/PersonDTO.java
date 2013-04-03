package net.ulrice.sample.simpledatabindingsample;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


public class PersonDTO {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport (this);
    
    private String vorname;
    private String nachname;
    private int zahl;
    private boolean hatAuto;
    private String anrede;

    public PersonDTO () {}
    public PersonDTO (String vorname, String nachname, int zahl, boolean hatAuto) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.zahl = zahl;
        this.hatAuto = hatAuto;
    }
    
    public void addPropertyChangeListener (PropertyChangeListener l) {
        pcs.addPropertyChangeListener (l);
    }

    public void removePropertyChangeListener (PropertyChangeListener l) {
        pcs.removePropertyChangeListener (l);
    }
    
    public String getVorname () {
        return vorname;
    }
    public void setVorname (String vorname) {
        String old = this.vorname;
        this.vorname = vorname;
        pcs.firePropertyChange ("vorname", old, vorname);
    }
    public String getNachname () {
        return nachname;
    }
    public void setNachname (String nachname) {
        String old = this.nachname;
        this.nachname = nachname;
        pcs.firePropertyChange ("nachname", old, nachname);
    }
    
    public String getName () {
        return getVorname () + " " + getNachname ();
    }
    
    public int getZahl () {
        return zahl;
    }
    public void setZahl (int zahl) {
        int old = this.zahl;
        this.zahl = zahl;
        pcs.firePropertyChange ("zahl", old, zahl);
    }
    
    public boolean getHatAuto () {
        return hatAuto;
    }
    public void setHatAuto (boolean hatAuto) {
        boolean old = this.hatAuto;
        this.hatAuto = hatAuto;
        pcs.firePropertyChange ("hatAuto", old, hatAuto);
    }
	public void setAnrede(String anrede) {
		pcs.firePropertyChange("anrede", this.anrede, this.anrede = anrede);
	}
	public String getAnrede() {
		return anrede;
	}
}


