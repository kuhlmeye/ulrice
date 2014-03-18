package net.ulrice.databinding.bufferedbinding.impl;

import java.util.EventListener;

public interface ColumnDefinitionChangedListener extends EventListener {

    void valueRangeChanged(ColumnDefinition<?> colDef);
 
    void filterModeChanged(ColumnDefinition<?> colDef);
}
