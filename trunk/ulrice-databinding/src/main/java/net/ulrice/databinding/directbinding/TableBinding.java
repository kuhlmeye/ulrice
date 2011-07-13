package net.ulrice.databinding.directbinding;

import java.util.List;

import net.ulrice.databinding.directbinding.table.TableViewAdapter;



class TableBinding {
    private final TableViewAdapter _tableViewAdapter;
    private final List<IndexedBinding> _columnBindings;
//TODO    private final Predicate _enabledPredicate;
    
    public TableBinding (TableViewAdapter tableViewAdapter, List<IndexedBinding> columnBindings) {
        _tableViewAdapter = tableViewAdapter;
        _columnBindings = columnBindings;
    }

    public TableViewAdapter getTableViewAdapter () {
        return _tableViewAdapter;
    }
    
    public List<IndexedBinding> getColumnBindings () {
        return _columnBindings;
    }
}

