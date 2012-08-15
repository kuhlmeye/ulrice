package net.ulrice.databinding.viewadapter;

import javax.swing.JComponent;

import net.ulrice.databinding.bufferedbinding.impl.ColumnColorOverride;
import net.ulrice.databinding.bufferedbinding.impl.Element;

/**
 * Interface for all classes marking the state on elements.
 * 
 * @author christof
 */
public interface IFCellStateMarker {

	void initialize(JComponent component);
    void updateState(Element value, int row, boolean isSelected, boolean editable, boolean dirty, boolean valid, ColumnColorOverride columnColorOverride, JComponent component);    
}
