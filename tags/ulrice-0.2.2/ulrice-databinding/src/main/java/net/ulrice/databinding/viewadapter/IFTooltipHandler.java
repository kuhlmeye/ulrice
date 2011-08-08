package net.ulrice.databinding.viewadapter;

import javax.swing.JComponent;

import net.ulrice.databinding.IFBinding;

/**
 * Class defining the tooltip of the component.
 * 
 * @author christof
 */
public interface IFTooltipHandler {
    
    /**
     * Returns the tooltip, that should be set in the component.
     * 
     * @param attributeModel The attribute model.
     * @param guiAccessor The gui accessor.
     * @param component The component.
     */
    void updateTooltip(IFBinding binding, JComponent component);

}
