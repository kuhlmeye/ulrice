package net.ulrice.databinding.directbinding;

import java.util.List;

import net.ulrice.databinding.directbinding.table.TableModelAdapter;



class TableBinding {
    private final TableModelAdapter tableViewAdapter;
    private final List<IndexedBinding> columnBindings;
//TODO    private final Predicate _enabledPredicate;
    
    public TableBinding (TableModelAdapter tableViewAdapter, List<IndexedBinding> columnBindings) {
    	this.tableViewAdapter = tableViewAdapter;
        this.columnBindings = columnBindings;
    }

    public TableModelAdapter getTableViewAdapter () {
        return tableViewAdapter;
    }
    
    public List<IndexedBinding> getColumnBindings () {
        return columnBindings;
    }
}

