package net.ulrice.databinding.directbinding;

import java.util.List;

import net.ulrice.databinding.directbinding.table.TableModelAdapter;



class TableBinding {
    private final TableModelAdapter _tableViewAdapter;
    private final List<IndexedBinding> _columnBindings;
//TODO    private final Predicate _enabledPredicate;
    
    public TableBinding (TableModelAdapter tableViewAdapter, List<IndexedBinding> columnBindings) {
        _tableViewAdapter = tableViewAdapter;
        _columnBindings = columnBindings;
    }

    public TableModelAdapter getTableViewAdapter () {
        return _tableViewAdapter;
    }
    
    public List<IndexedBinding> getColumnBindings () {
        return _columnBindings;
    }
}

