package net.ulrice.databinding.columnchooser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * @author EXSTHUB
 */
public class ColumnChooserSaver {

    public static void savePrefs(String tableUniqueKey, List<String> columnsToHide) {
        getPreferences().put(tableUniqueKey, listToString(columnsToHide));
    }

    public static List<String> loadPrefs(String tableUniqueKey) {
        return listFromString(getPreferences().get(tableUniqueKey, null));
    }

    private static Preferences getPreferences() {
        return Preferences.userNodeForPackage(ColumnChooserSaver.class);
    }

    private static String listToString(List<String> columnsToHide) {
        StringBuilder s = new StringBuilder();
        for (String col : columnsToHide) {
            s = s.append(col);
            s = s.append(";");
        }
        String st = s.toString();
        if (st.endsWith(";")) {
            st = st.substring(0, st.length() - 1);
        }
        return st;
    }

    private static List<String> listFromString(String listString) {
        List<String> columnsToHide = new ArrayList<>();

        if (listString != null) {
            Collections.addAll(columnsToHide, listString.split(";"));
        }

        return columnsToHide;
    }

}
