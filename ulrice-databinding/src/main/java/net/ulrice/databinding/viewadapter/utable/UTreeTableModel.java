package net.ulrice.databinding.viewadapter.utable;

import net.ulrice.databinding.bufferedbinding.impl.Element;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;

/**
 * Model for using the TreeTableModel with the TableAM
 * TODO: eventuell überlegen ob der UTableViewAdapter nicht besser wäre...
 *
 * @author rad
 *
 */
public class UTreeTableModel extends AbstractTreeTableModel {

    
    public UTreeTableModel(TableAM tableAM) {
        super(tableAM);  
        this.tableAM = tableAM;
    }

    private TableAM tableAM;
    
    @Override
    public int getColumnCount() {
       return tableAM.getColumnCount();
    }

    @Override
    public String getColumnName(int column) {
        return tableAM.getColumnName(column);
    }

    @Override
    public Class< ?> getColumnClass(int column) {
        return tableAM.getColumnClass(column);
    }

    @Override
    public Object getValueAt(Object node, int column) {
        if (node instanceof Element) {
            Element element = (Element) node;
            return element.getValueAt(column);
        }
        return null;
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
        if(column == 0){
            return true;
        }
       
        Element element = (Element) node;
        return !tableAM.isReadOnly() && element != null && !element.isReadOnly(column) && !element.isRemoved();
        
    }

    @Override
    public void setValueAt(Object aValue, Object node, int column) {
        if (node instanceof Element) {
            Element element = (Element) node;
            
            tableAM.activateMassEditMode();
            try {
                element.setValueAt(column, aValue);
            }
            finally {
                tableAM.deactivateMassEditModeAndUpdate();
            }
            
        }
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent instanceof Element) {
            Element element = (Element) parent; 
            return element.getChild(index);
            
        }else if(parent instanceof TableAM) {
            TableAM am = (TableAM) parent;
            return am.getElementAt(index);
        }
        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof Element) {
            Element element = (Element) parent;
            return element.getChildCount();
            
        }else if(parent instanceof TableAM) {
            TableAM am = (TableAM) parent;
            return am.getElements().size();
        }
        return 0;
    }
}
