package net.ulrice.remotecontrol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ulrice.remotecontrol.util.RegularMatcher;
import net.ulrice.remotecontrol.util.RemoteControlUtils;

/**
 * Represents data of the application in form of a table, like a JTable
 * 
 * @author Manfred HANTSCHEL
 */
public class ComponentTableData implements Serializable {

    private static final long serialVersionUID = -113729012728921740L;

    private final List<String> headers;
    private final List<List<ComponentTableDataEntry>> entries;

    private int columnCount;

    public ComponentTableData() {
        super();

        headers = new ArrayList<String>();
        entries = new ArrayList<List<ComponentTableDataEntry>>();
    }

    public int getRowCount() {
        return entries.size();
    }

    public int getColumnCount() {
        return columnCount;
    }

    public String getHeader(int column) {
        if (column >= headers.size()) {
            return null;
        }

        return headers.get(column);
    }

    public void setHeader(int column, String header) {
        while (column >= headers.size()) {
            headers.add(null);
        }

        columnCount = Math.max(columnCount, column + 1);
        headers.set(column, header);
    }

    public int findHeader(String regex) throws RemoteControlException {
        RegularMatcher matcher = RemoteControlUtils.toMatcher(regex);

        for (int column = 0; column < getColumnCount(); column += 1) {
            String header = getHeader(column);

            if (header == null) {
                continue;
            }

            if (matcher.matches(header)) {
                return column;
            }
        }

        return -1;
    }

    public ComponentTableDataEntry getEntry(int row, int column) {
        if (row >= entries.size()) {
            return null;
        }

        List<ComponentTableDataEntry> list = entries.get(row);

        if ((list == null) || (column >= list.size())) {
            return null;
        }

        return list.get(column);
    }

    public void setEntry(int row, int column, Object value, boolean selected, boolean hidden) {
        setEntry(row, column, new ComponentTableDataEntry(value, selected, hidden));
    }

    public void setEntry(int row, int column, ComponentTableDataEntry entry) {
        while (row >= entries.size()) {
            entries.add(null);
        }

        List<ComponentTableDataEntry> list = entries.get(row);

        if (list == null) {
            list = new ArrayList<ComponentTableDataEntry>();
            entries.set(row, list);
        }

        while (column >= list.size()) {
            list.add(null);
        }

        columnCount = Math.max(columnCount, column + 1);

        list.set(column, entry);
    }

    public Serializable getValue(int row, int column) {
        ComponentTableDataEntry entry = getEntry(row, column);

        return (entry != null) ? entry.getValue() : null;
    }

    public void setValue(int row, int column, Serializable value) {
        ComponentTableDataEntry entry = getEntry(row, column);

        if (entry == null) {
            setEntry(row, column, new ComponentTableDataEntry(value, false, false));
        }
        else {
            entry.setValue(value);
        }
    }

    public boolean isSelected(int row, int column) {
        ComponentTableDataEntry entry = getEntry(row, column);

        return (entry != null) && (entry.isSelected());
    }

    public void setSelected(int row, int column, boolean selected) {
        ComponentTableDataEntry entry = getEntry(row, column);

        if (entry == null) {
            setEntry(row, column, new ComponentTableDataEntry(null, selected, false));
        }
        else {
            entry.setSelected(selected);
        }
    }

    public boolean isColumnSelected(int column) {
        for (int row = 0; row < getRowCount(); row += 1) {
            if (!isSelected(row, column)) {
                return false;
            }
        }

        return true;
    }

    public boolean isHidden(int row, int column) {
        ComponentTableDataEntry entry = getEntry(row, column);

        return (entry != null) && (entry.isHidden());
    }

    public void setHidden(int row, int column, boolean hidden) {
        ComponentTableDataEntry entry = getEntry(row, column);

        if (entry == null) {
            setEntry(row, column, new ComponentTableDataEntry(null, false, hidden));
        }
        else {
            entry.setHidden(hidden);
        }
    }

    public boolean isColumnHidden(int column) {
        for (int row = 0; row < getRowCount(); row += 1) {
            if (!isHidden(row, column)) {
                return false;
            }
        }

        return true;
    }

    public int findSelectedRow() {
        for (int row = 0; row < getRowCount(); row += 1) {
            if (isRowSelected(row)) {
                return row;
            }
        }

        return -1;
    }

    public boolean isRowSelected(int row) {
        for (int column = 0; column < getColumnCount(); column += 1) {
            if (!isSelected(row, column)) {
                return false;
            }
        }

        return true;
    }

    public boolean isRowHidden(int row) {
        for (int column = 0; column < getColumnCount(); column += 1) {
            if (!isHidden(row, column)) {
                return false;
            }
        }

        return true;
    }

    public Map<String, Serializable> getRowAsMap(int row) {
        Map<String, Serializable> result = new HashMap<String, Serializable>();

        for (int column = 0; column < getColumnCount(); column += 1) {
            result.put(getHeader(column), getValue(row, column));
        }

        return result;
    }

    public String toString(int row, int column) {
        ComponentTableDataEntry entry = getEntry(row, column);

        return (entry != null) ? entry.toString() : "";
    }

    public int findEmptyRow() {
        for (int row = 0; row < getRowCount(); row += 1) {
            if (isRowEmpty(row)) {
                return row;
            }
        }

        return -1;
    }

    public boolean isRowEmpty(int row) {
        for (int column = 0; column < getColumnCount(); column += 1) {
            if ((!isHidden(row, column)) && (!isEmpty(getValue(row, column)))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        int widths[] = new int[columnCount];

        Arrays.fill(widths, 1);

        for (int column = 0; column < columnCount; column += 1) {
            String header = getHeader(column);
            widths[column] = Math.max(widths[column], (header != null) ? header.length() : 0);
        }

        for (int row = 0; row < getRowCount(); row += 1) {
            for (int column = 0; column < columnCount; column += 1) {
                widths[column] = Math.max(widths[column], toString(row, column).length());
            }
        }

        builder.append(" IDX |");

        for (int column = 0; column < columnCount; column += 1) {
            String header = getHeader(column);
            if (isColumnSelected(column)) {
                builder.append(String.format("[%-" + widths[column] + "s]", (header != null) ? header : ""));
            }
            else if (isColumnHidden(column)) {
                builder.append(String.format("(%-" + widths[column] + "s)", (header != null) ? header : ""));
            }
            else {
                builder.append(String.format(" %-" + widths[column] + "s ", (header != null) ? header : ""));
            }
            builder.append("|");
        }

        builder.append("\n     +");

        for (int column = 0; column < columnCount; column += 1) {
            for (int i = -2; i < widths[column]; i += 1) {
                builder.append("-");
            }
            builder.append("+");
        }

        for (int row = 0; row < getRowCount(); row += 1) {
            if (isRowSelected(row)) {
                builder.append(String.format("\n[%3d]|", row));
            }
            else if (isRowHidden(row)) {
                builder.append(String.format("\n(%3d)|", row));
            }
            else {
                builder.append(String.format("\n %3d |", row));
            }

            for (int column = 0; column < columnCount; column += 1) {
                if (isSelected(row, column)) {
                    builder.append(String.format("[%-" + widths[column] + "s]", toString(row, column)));
                }
                else if (isHidden(row, column)) {
                    builder.append(String.format("(%-" + widths[column] + "s)", toString(row, column)));
                }
                else {
                    builder.append(String.format(" %-" + widths[column] + "s ", toString(row, column)));
                }
                builder.append("|");
            }
        }

        return builder.toString();
    }

    private static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }

        if (value instanceof String) {
            String s = (String) value;

            s = s.trim();

            return s.length() == 0;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue() == 0;
        }

        if (value instanceof Boolean) {
            // ignore, what is empty?
            return true;
        }

        if (value instanceof Enum) {
            // ignore, what is empty?
            return true;
        }

        if (value instanceof Collection< ?>) {
            for (Object entry : (Collection< ?>) value) {
                if (!isEmpty(entry)) {
                    return false;
                }
            }

            return true;
        }

        if (value instanceof Map< ?, ?>) {
            return isEmpty(((Map<?, ?>)value).values());
        }
        
        return false;
    }

}
