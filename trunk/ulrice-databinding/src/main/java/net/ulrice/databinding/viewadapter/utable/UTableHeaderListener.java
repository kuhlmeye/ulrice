package net.ulrice.databinding.viewadapter.utable;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.plaf.basic.BasicTableHeaderUI;

/**
 * Listener for multi table sorting
 * @author rad
 *
 */
public class UTableHeaderListener implements MouseListener, MouseMotionListener {

    private MouseListener original;
    private MouseMotionListener originalMotion;
    private JTable table;
    private UTableComponent uTable;
    
    private boolean sortingDisabled = false;

    public UTableHeaderListener(BasicTableHeaderUI.MouseInputHandler original, JTable table, UTableComponent uTable) {

        this.original = original;
        this.originalMotion = original;
        this.table = table;
        this.uTable = uTable;
        
    }

    /**
     * We have to consider both tables at multiple sorting
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if(isSortingDisabled()){
            return;
        }
        boolean multi = false;
        
        //multi column sorting is activated by holding the shift key
        if(e.isShiftDown()){
            multi = true;
        }
        
        int colView = table.columnAtPoint(e.getPoint());
        if(table == uTable.getScrollTable()){
            colView = colView+uTable.getFixedColumns();
        }
        if(colView == -1){
            return;
        }
        
        int colModel = uTable.convertColumnIndexToModel(colView);
         
        UTableRowSorter sorter = uTable.getRowSorter();  table.getRowSorter();
        List< ? extends SortKey> oldKeys = sorter.getGlobalSortKeys();

        List<SortKey> newKeys = new ArrayList<SortKey>();
        boolean hasMandatorySortKeys = sorter.getMandatorySortKeys() != null && !sorter.getMandatorySortKeys().isEmpty(); 
        if(!multi && hasMandatorySortKeys){
            newKeys.addAll(sorter.getMandatorySortKeys());
        }
        boolean found = false;
        for (SortKey k : oldKeys) {
            if (k.getColumn() == colModel) {
                found = true;
                SortOrder newOrder = null;
                switch (k.getSortOrder()) {
                    case ASCENDING:
                        newOrder = SortOrder.DESCENDING;
                        break;
                    case DESCENDING:
                        if (hasMandatorySortKeys
                            && UTableRowSorter.containsSortKey(sorter.getMandatorySortKeys(), k.getColumn())) {
                            newOrder = SortOrder.DESCENDING;
                        }
                        else {
                            newOrder = SortOrder.UNSORTED;
                        }
                        break;
                    case UNSORTED:
                        newOrder = SortOrder.ASCENDING;
                        break;
                }
                newKeys.add(new SortKey(colModel, newOrder));
            }
            else {
                if(multi){
                    newKeys.add(k);
                }
            }
        }
        if(!found){
            newKeys.add(new SortKey(colModel, SortOrder.ASCENDING));
        }
        // update
        sorter.setGlobalSortKeys(newKeys);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        original.mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        original.mouseExited(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        original.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        original.mouseReleased(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        originalMotion.mouseDragged(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        originalMotion.mouseMoved(e);
    }

    public boolean isSortingDisabled() {
        return sortingDisabled;
    }

    public void setSortingDisabled(boolean sortingDisabled) {
        this.sortingDisabled = sortingDisabled;
    }
    

}
