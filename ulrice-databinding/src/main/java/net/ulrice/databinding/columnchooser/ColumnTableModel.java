package net.ulrice.databinding.columnchooser;

import net.ulrice.Ulrice;
import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;
import net.ulrice.message.TranslationProvider;
import net.ulrice.message.TranslationUsage;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author EXSTHUB
 */
public class ColumnTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 833212348466712833L;

    private List<Row> rows = new ArrayList<>();

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return 3; // isUniqueColumn, translatedName, showColumn
    }

    @Override
    public String getColumnName(int columnIndex) {
        TranslationProvider tp = Ulrice.getTranslationProvider();

        switch (columnIndex) {
            case 0:
                return tp.getUlriceTranslation(TranslationUsage.TableColumn, "Required").getText();
            case 1:
                return tp.getUlriceTranslation(TranslationUsage.TableColumn, "ColumnName").getText();
            case 2:
                return tp.getUlriceTranslation(TranslationUsage.TableColumn, "ShowColumn").getText();
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Boolean.class;
            case 1:
                return String.class;
            case 2:
                return Boolean.class;
            default:
                return null;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Row row = rows.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return row.isUniqueKeyColumn();
            case 1:
                return row.getTranslatedName();
            case 2:
                return row.isShowColumn();
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        Row row = rows.get(rowIndex);

        switch (columnIndex) {
            case 2:
                return !row.isUniqueKeyColumn();
            default:
                return false;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Row row = rows.get(rowIndex);

        switch (columnIndex) {
            case 2:
                if (row.isUniqueKeyColumn()) {
                    // cannot be changed!
                    return;
                }

                row.setShowColumn((Boolean) aValue);
                fireTableDataChanged();
                break;
        }
    }

    public void addRow(String columnId, String translatedName, boolean isUniqueKeyColumn, boolean showColumn) {
        rows.add(new Row(columnId, translatedName, isUniqueKeyColumn, showColumn));
    }

    public boolean isRowUnique(int row) {
        return rows.get(row).isUniqueKeyColumn();
    }

    private class Row {

        private String columnId;
        private String translatedName;
        private boolean isUniqueKeyColumn;

        private boolean showColumn;

        public Row(String columnId, String translatedName, boolean isUniqueKeyColumn, boolean showColumn) {
            this.columnId = columnId;
            this.translatedName = translatedName;
            this.isUniqueKeyColumn = isUniqueKeyColumn;
            this.showColumn = showColumn;
        }

        public String getColumnId() {
            return columnId;
        }

        public String getTranslatedName() {
            return translatedName;
        }

        public boolean isUniqueKeyColumn() {
            return isUniqueKeyColumn;
        }

        public boolean isShowColumn() {
            return showColumn;
        }

        public void setShowColumn(boolean showColumn) {
            this.showColumn = showColumn;
        }
    }

    public List<String> getColumnsToHide() {
        List<String> result = new ArrayList<>();
        for (Row row : rows) {
            if (!row.isShowColumn()) {
                result.add(row.getColumnId());
            }
        }
        return result;
    }

}
