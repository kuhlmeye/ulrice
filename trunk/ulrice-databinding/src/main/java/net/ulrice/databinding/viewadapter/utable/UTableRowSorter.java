package net.ulrice.databinding.viewadapter.utable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultRowSorter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;

public class UTableRowSorter extends DefaultRowSorter<UTableViewAdapter, String> {

    private UTableModelRowSorter scrollTableRS;
    private UTableModelRowSorter staticTableRS;
    private List<RowSorter.SortKey> staticSortKeys;
    private List<RowSorter.SortKey> scrollSortKeys;
    private List<RowSorter.SortKey> mandatorySortKeys;
    private int fixedColumns;
    private UTableViewAdapter model;

    public UTableRowSorter(final UTableViewAdapter model, int fixedColumns, UTableModel staticTableModel,
        UTableModel scrollTableModel) {
        this.staticSortKeys = Collections.emptyList();
        this.scrollSortKeys = Collections.emptyList();
        this.mandatorySortKeys = Collections.emptyList();
        this.staticTableRS = new UTableModelRowSorter(false, staticTableModel);
        this.scrollTableRS = new UTableModelRowSorter(true, scrollTableModel);
        this.fixedColumns = fixedColumns;
        this.model = model;

        setModelWrapper(new ModelWrapper<UTableViewAdapter, String>() {

            @Override
            public UTableViewAdapter getModel() {
                return model;
            }

            @Override
            public int getColumnCount() {
                return model.getColumnCount();
            }

            @Override
            public int getRowCount() {
                return model.getRowCount();
            }

            @Override
            public Object getValueAt(int row, int column) {
                return model.getValueAt(row, column);
            }

            @Override
            public String getIdentifier(int row) {
                return model.getElementAtUsingModelIndex(row).getUniqueId();
            }
        });
    }

    @Override
    public List< ? extends RowSorter.SortKey> getSortKeys() {
        // for testing
        List< ? extends RowSorter.SortKey> keys = super.getSortKeys();
        // System.out.println("get sortkeys");
        // for(SortKey key : keys){
        // System.out.println(" "+key.getColumn()+" "+key.getSortOrder());
        // }
        return keys;
    }

    public RowSorter<UTableModel> getStaticTableRowSorter() {
        return staticTableRS;
    }

    public RowSorter<UTableModel> getScrollTableRowSorter() {
        return scrollTableRS;
    }

    public void updateGlobalSortKeys() {
        List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        for (RowSorter.SortKey key : staticSortKeys) {
            sortKeys.add(new RowSorter.SortKey(key.getColumn(), key.getSortOrder()));
        }
        for (RowSorter.SortKey key : scrollSortKeys) {
            sortKeys.add(new RowSorter.SortKey(key.getColumn() + fixedColumns, key.getSortOrder()));
        }
        UTableRowSorter.this.setSortKeys(sortKeys);
        UTableRowSorter.this.model.getComponent().invalidate();
        UTableRowSorter.this.model.getComponent().repaint();
    }

    /**
     * allows to set the sorting keys do not use setSortKeys, because the sort keys of staticTableRS and scrollTableRS
     * are not refreshed the right way
     * 
     * @param sortKeys
     */
    public void setGlobalSortKeys(List<RowSorter.SortKey> sortKeys) {
        List<RowSorter.SortKey> newStaticSortKeys = new ArrayList<RowSorter.SortKey>();
        List<RowSorter.SortKey> newScrollSortKeys = new ArrayList<RowSorter.SortKey>();

        boolean hasMandatorySortKeys = mandatorySortKeys != null && !mandatorySortKeys.isEmpty();
        if(hasMandatorySortKeys){
            for (RowSorter.SortKey key : sortKeys) {
                if (key.getColumn() >= fixedColumns) {
                    newScrollSortKeys.add(new RowSorter.SortKey(key.getColumn() - fixedColumns, key.getSortOrder()));
                }
                else {
                    newStaticSortKeys.add(new RowSorter.SortKey(key.getColumn(), key.getSortOrder()));
                }
            }
        }
            
        for (RowSorter.SortKey key : sortKeys) {
            if (!containsSortKey(mandatorySortKeys, key.getColumn())) {
                if (key.getColumn() >= fixedColumns) {
                    newScrollSortKeys.add(new RowSorter.SortKey(key.getColumn() - fixedColumns, key.getSortOrder()));
                }
                else {
                    newStaticSortKeys.add(new RowSorter.SortKey(key.getColumn(), key.getSortOrder()));
                }
            }
        }
        
        this.staticSortKeys = newStaticSortKeys;
        this.scrollSortKeys = newScrollSortKeys;
        // updateGlobalSortKeys();
        UTableRowSorter.this.setSortKeys(sortKeys);
        UTableRowSorter.this.model.getComponent().invalidate();
        UTableRowSorter.this.model.getComponent().repaint();
        UTableRowSorter.this.sort();
        fireSortOrderChanged();
    }

    /**
     * return the sortkeys in one list, the column represents the column in the "global" table
     */
    public List<RowSorter.SortKey> getGlobalSortKeys() {
        List<RowSorter.SortKey> globalSortKeys = new ArrayList<RowSorter.SortKey>();
        if (mandatorySortKeys != null && !mandatorySortKeys.isEmpty()) {
            for (RowSorter.SortKey key : mandatorySortKeys) {
                globalSortKeys.add(key);
            }
        }
        for (RowSorter.SortKey key : staticSortKeys) {
            if (!containsSortKey(mandatorySortKeys, key.getColumn())) {
                globalSortKeys.add(key);
            }
        }

        for (RowSorter.SortKey key : scrollSortKeys) {
            if (!containsSortKey(mandatorySortKeys, key.getColumn())) {
                globalSortKeys.add(new RowSorter.SortKey(key.getColumn() + fixedColumns, key.getSortOrder()));
            }
        }
        return globalSortKeys;
    }

    protected class UTableModelRowSorter extends RowSorter<UTableModel> {
        private UTableModel model;
        private boolean scrollable;

        public UTableModelRowSorter(boolean scrollable, UTableModel model) {
            this.model = model;
            this.scrollable = scrollable;
        }

        @Override
        public UTableModel getModel() {
            return model;
        }

        @Override
        public void toggleSortOrder(int column) {
            List< ? extends RowSorter.SortKey> sortKeys = getSortKeys();
            RowSorter.SortKey sortKey = null;
            for (RowSorter.SortKey key : sortKeys) {
                if (key.getColumn() == column) {
                    sortKey = key;
                }
            }

            if (sortKey != null) {
                switch (sortKey.getSortOrder()) {
                    case ASCENDING:
                        sortKey = new RowSorter.SortKey(column, SortOrder.DESCENDING);
                        break;
                    case DESCENDING:
                        sortKey = new RowSorter.SortKey(column, SortOrder.UNSORTED);
                        break;
                    case UNSORTED:
                        sortKey = new RowSorter.SortKey(column, SortOrder.ASCENDING);
                        break;
                }
            }
            else {
                sortKey = new RowSorter.SortKey(column, SortOrder.ASCENDING);
            }
            List<RowSorter.SortKey> newSortKeys = new ArrayList<RowSorter.SortKey>();
            newSortKeys.add(sortKey);
            setSortKeys(newSortKeys);

        }

        @Override
        public List< ? extends RowSorter.SortKey> getSortKeys() {
            return scrollable ? scrollSortKeys : staticSortKeys;
        }

        @Override
        public void setSortKeys(List< ? extends RowSorter.SortKey> keys) {
            if (scrollable) {
                staticSortKeys.clear();
                if (mandatorySortKeys != null && !mandatorySortKeys.isEmpty()) {
                    scrollSortKeys = new ArrayList<RowSorter.SortKey>(mandatorySortKeys);
                    scrollSortKeys.addAll(keys);
                }
                else {
                    scrollSortKeys = new ArrayList<RowSorter.SortKey>(keys);
                }
                updateGlobalSortKeys();
                UTableRowSorter.this.sort();
                fireSortOrderChanged();
                staticTableRS.fireSortOrderChanged();
            }
            else {
                scrollSortKeys.clear();
                if (mandatorySortKeys != null && !mandatorySortKeys.isEmpty()) {
                    staticSortKeys = new ArrayList<RowSorter.SortKey>(mandatorySortKeys);
                    staticSortKeys.addAll(keys);
                }
                else {
                    staticSortKeys = new ArrayList<RowSorter.SortKey>(keys);
                }
                updateGlobalSortKeys();
                UTableRowSorter.this.sort();
                fireSortOrderChanged();
                scrollTableRS.fireSortOrderChanged();
            }
        }

        @Override
        public int convertRowIndexToModel(int index) {
            return UTableRowSorter.this.convertRowIndexToModel(index);
        }

        @Override
        public int convertRowIndexToView(int index) {
            return UTableRowSorter.this.convertRowIndexToView(index);
        }

        @Override
        public int getViewRowCount() {
            return UTableRowSorter.this.getViewRowCount();
        }

        @Override
        public int getModelRowCount() {
            return UTableRowSorter.this.getModelRowCount();
        }

        @Override
        public void modelStructureChanged() {
            UTableRowSorter.this.modelStructureChanged();
        }

        @Override
        public void allRowsChanged() {
            UTableRowSorter.this.allRowsChanged();
        }

        @Override
        public void rowsInserted(int firstRow, int endRow) {
            UTableRowSorter.this.rowsInserted(firstRow, endRow);
        }

        @Override
        public void rowsDeleted(int firstRow, int endRow) {
            UTableRowSorter.this.rowsDeleted(firstRow, endRow);
        }

        @Override
        public void rowsUpdated(int firstRow, int endRow) {
            UTableRowSorter.this.rowsUpdated(firstRow, endRow);
        }

        @Override
        public void rowsUpdated(int firstRow, int endRow, int column) {
            UTableRowSorter.this.rowsUpdated(firstRow, endRow, column);
        }
    }

    public List< ? extends RowSorter.SortKey> getMandatorySortKeys() {
        return mandatorySortKeys;
    }

    public void setMandatorySortKeys(List<RowSorter.SortKey> mandatorySortKeys) {
        this.mandatorySortKeys = mandatorySortKeys;
    }

    public static boolean containsSortKey(List< ? extends SortKey> sortKeys, int column) {
        for (SortKey key : sortKeys) {
            if (key.getColumn() == column) {
                return true;
            }
        }
        return false;
    }
}
