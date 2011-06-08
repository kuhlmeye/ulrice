package net.ulrice.databinding;

import javax.swing.JComponent;

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
    void updateTooltip(IFAttributeModel<?> attributeModel, IFGuiAccessor<?, ?> guiAccessor, JComponent component);

}
