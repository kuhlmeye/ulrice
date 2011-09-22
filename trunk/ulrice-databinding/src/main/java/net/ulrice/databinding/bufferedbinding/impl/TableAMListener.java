package net.ulrice.databinding.bufferedbinding.impl;

import java.util.EventListener;

public interface TableAMListener extends EventListener {

    void columnValueRangeChanged(TableAM tableAM, ColumnDefinition< ?> colDef);

    void columnFilterModeChanged(TableAM tableAM, ColumnDefinition< ?> colDef);
}
