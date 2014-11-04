package net.ulrice.remotecontrol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents list data from e.g. a JList.
 * 
 * @author Manfred HANTSCHEL
 */
public class ComponentListData implements Serializable {

    private static final long serialVersionUID = 1472470097458284640L;

    private final List<ComponentListDataEntry> entries;

    public ComponentListData() {
        super();

        entries = new ArrayList<ComponentListDataEntry>();
    }

    public int size() {
        return entries.size();
    }

    public ComponentListDataEntry getEntry(int index) {
        return (index < entries.size()) ? entries.get(index) : null;
    }

    public ComponentListData addEntry(Serializable value, boolean selected) {
        addEntry(new ComponentListDataEntry(value, selected));

        return this;
    }

    public ComponentListData addEntry(ComponentListDataEntry entry) {
        entries.add(entry);

        return this;
    }

    public ComponentListData setEntry(int index, Object value, boolean selected) {
        setEntry(index, new ComponentListDataEntry(value, selected));

        return this;
    }

    public ComponentListData setEntry(int index, ComponentListDataEntry entry) {
        while (index >= entries.size()) {
            entries.add(null);
        }

        entries.set(index, entry);

        return this;
    }

    public Serializable getValue(int index) {
        ComponentListDataEntry entry = getEntry(index);

        return (entry != null) ? entry.getValue() : null;
    }

    public ComponentListData addValue(Serializable value) {
        entries.add(new ComponentListDataEntry(value, false));

        return this;
    }

    public ComponentListData setValue(int index, Serializable value) {
        ComponentListDataEntry entry = getEntry(index);

        if (entry == null) {
            setEntry(index, new ComponentListDataEntry(value, false));
        }
        else {
            entry.setValue(value);
        }

        return this;
    }

    public boolean isSelected(int index) {
        ComponentListDataEntry entry = getEntry(index);

        return (entry != null) && (entry.isSelected());
    }

    public ComponentListData setSelected(int index, boolean selected) {
        ComponentListDataEntry entry = getEntry(index);

        if (entry == null) {
            setEntry(index, new ComponentListDataEntry(null, selected));
        }
        else {
            entry.setSelected(selected);
        }

        return this;
    }

    public String toString(int index) {
        ComponentListDataEntry entry = getEntry(index);

        return (entry != null) ? entry.toString() : "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        int width = 1;

        for (int index = 0; index < size(); index += 1) {
            width = Math.max(width, toString(index).length());
        }

        builder.append(String.format("IDX | %-" + width + "s |", "Value"));
        builder.append("\n    +");

        for (int i = -2; i < width; i += 1) {
            builder.append("-");
        }
        
        builder.append("+");

        for (int index = 0; index < size(); index += 1) {
            if (isSelected(index)) {
                builder.append(String.format("\n%3d*|[%-" + width + "s]|", index, toString(index)));
            }
            else {
                builder.append(String.format("\n%3d | %-" + width + "s |", index, toString(index)));
            }
        }

        return builder.toString();
    }

}
