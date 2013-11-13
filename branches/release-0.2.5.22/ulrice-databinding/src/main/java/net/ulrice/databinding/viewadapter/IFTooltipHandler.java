package net.ulrice.databinding.viewadapter;

import javax.swing.JComponent;

import net.ulrice.databinding.IFBinding;

/**
 * Class defining the tooltip of the component.
 * 
 * @author christof
 */
public interface IFTooltipHandler {

    void updateTooltip(IFBinding tooltip, JComponent component);

}
