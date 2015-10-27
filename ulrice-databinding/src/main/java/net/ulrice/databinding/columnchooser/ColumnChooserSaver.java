package net.ulrice.databinding.columnchooser;

import net.ulrice.Ulrice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author EXSTHUB
 */
public class ColumnChooserSaver {

    public static void saveColVisiblePrefs(String tableUniqueKey, List<String> columnsToHide) {
        getPreferences().put(tableUniqueKey, listToString(columnsToHide));
    }

    public static List<String> loadColVisiblePrefs(String tableUniqueKey) {
        return listFromString(getPreferences().get(tableUniqueKey, null));
    }

    public static void saveColPosPrefs(String tableUniqueKey, Map<String, Integer> columnOrderMap) {
        getPreferences().put("T" + tableUniqueKey, mapToString(columnOrderMap));
    }

    public static Map<String, Integer> loadColPosPrefs(String tableUniqueKey) {
        return mapFromString(getPreferences().get("T" + tableUniqueKey, null));
    }

    public static void clearAllPrefs() {
        Preferences prefs = Preferences.userNodeForPackage(ColumnChooserSaver.class);
        try {
            prefs.removeNode();
        }
        catch (BackingStoreException e) {
            Ulrice.getMessageHandler().handleException(e);
        }
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

    private static String mapToString(Map<String, Integer> columnOrderMap) {

        if(columnOrderMap == null){
            return "";
        }

        StringBuilder s = new StringBuilder();
        for (String col : columnOrderMap.keySet()) {
            s = s.append(col + "=" + columnOrderMap.get(col));
            s = s.append(";");
        }
        String st = s.toString();
        if (st.endsWith(";")) {
            st = st.substring(0, st.length() - 1);
        }
        return st;
    }

    private static Map<String, Integer> mapFromString(String columnOrderString) {
        Map<String, Integer> map = new HashMap<>();

        if(columnOrderString == null || columnOrderString.trim().isEmpty()){
            return map;
        }

        if (columnOrderString != null) {
            String[] mapEntries = columnOrderString.split(";");

            for (String mapEntry : mapEntries) {
                String[] splitEntry = mapEntry.split("=");
                map.put(splitEntry[0], Integer.valueOf(splitEntry[1]));
            }
        }
        return map;
    }
}
