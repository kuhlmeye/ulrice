package net.ulrice.databinding.viewadapter.utable;

import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;

public abstract class StringBasedTableCellRenderer extends AbstractUTableRenderer {

    /**
     * 
     */
    private static final long serialVersionUID = -6232584879744966205L;
    
    public abstract String getString(Object value, UTable table, ColumnDefinition columnDefinition);

}
