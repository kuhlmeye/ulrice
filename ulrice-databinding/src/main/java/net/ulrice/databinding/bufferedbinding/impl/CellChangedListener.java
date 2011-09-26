package net.ulrice.databinding.bufferedbinding.impl;

import java.util.EventListener;

public interface CellChangedListener extends EventListener {
    
    void cellValueChanged(Element element, String columnId);

}
