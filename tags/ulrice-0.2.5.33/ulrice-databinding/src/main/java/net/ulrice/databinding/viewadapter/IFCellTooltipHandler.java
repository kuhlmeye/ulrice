package net.ulrice.databinding.viewadapter;

import javax.swing.JComponent;

import net.ulrice.databinding.bufferedbinding.impl.Element;

/**
 * Class defining the tooltip of the component.
 * 
 * @author christof
 */
public interface IFCellTooltipHandler {

    void updateTooltip(Element element, String columnId, JComponent component);

}
