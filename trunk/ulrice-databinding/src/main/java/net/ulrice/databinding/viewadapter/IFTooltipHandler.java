package net.ulrice.databinding.viewadapter;

import javax.swing.JComponent;

/**
 * Class defining the tooltip of the component.
 * 
 * @author christof
 */
public interface IFTooltipHandler<T> {

    void updateTooltip(T tooltip, JComponent component);

}
