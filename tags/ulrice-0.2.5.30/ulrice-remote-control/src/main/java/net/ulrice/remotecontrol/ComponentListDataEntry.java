package net.ulrice.remotecontrol;

import java.io.Serializable;

import net.ulrice.remotecontrol.util.RemoteControlUtils;

/**
 * One entry in the {@link ComponentListData}
 * 
 * @author Manfred HANTSCHEL
 */
public class ComponentListDataEntry implements Serializable {

    private static final long serialVersionUID = 2523568134135579008L;

    private Serializable value;
    private boolean selected;

    public ComponentListDataEntry(Object value, boolean selected) {
        super();
        
        this.value = RemoteControlUtils.ensureSerializable(value);
        this.selected = selected;
    }

    public Serializable getValue() {
        return value;
    }

    public void setValue(Serializable value) {
        this.value = value;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return (value != null) ? value.toString() : "";
    }

}
