package net.ulrice.remotecontrol;

import java.io.Serializable;

import net.ulrice.remotecontrol.util.RemoteControlUtils;

/**
 * One entry in the {@link ComponentTableData}.
 * 
 * @author Manfred HANTSCHEL
 */
public class ComponentTableDataEntry implements Serializable {

    private static final long serialVersionUID = 7609497754833112047L;

    private Serializable value;
    private boolean selected;
    private boolean hidden;

    public ComponentTableDataEntry(Object value, boolean selected, boolean hidden) {
        super();
        
        this.value = RemoteControlUtils.ensureSerializable(value);
        this.selected = selected;
        this.hidden = hidden;
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

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public String toString() {
        return (value != null) ? value.toString() : "";
    }

}
