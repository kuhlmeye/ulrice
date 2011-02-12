package net.ulrice.databinding.impl.ga.tooltip;

import javax.swing.JComponent;

import net.ulrice.databinding.IFAttributeModel;
import net.ulrice.databinding.IFGuiAccessor;
import net.ulrice.databinding.IFTooltipHandler;
import net.ulrice.databinding.impl.validation.ValidationError;
import net.ulrice.databinding.impl.validation.ValidationErrors;

/**
 * Displays the detailed state of an attribute model as a tooltip in the
 * component.
 * 
 * @author christof
 */
public class DetailedTooltipHandler implements IFTooltipHandler {

    /**
     * @see net.ulrice.databinding.IFTooltipHandler#updateTooltip(net.ulrice.databinding.IFAttributeModel,
     *      net.ulrice.databinding.IFGuiAccessor, javax.swing.JComponent)
     */
    @Override
    public void updateTooltip(IFAttributeModel<?> attributeModel, IFGuiAccessor<?, ?> guiAccessor, JComponent component) {
        switch (attributeModel.getState()) {
            case NotInitialized:
                // TODO Add to UI class
                component.setToolTipText("State: Not initalized");
                break;
            case NotChanged:
                // TODO Add to UI class
                component.setToolTipText("State: Not changed");
                break;
            case Changed:
                // TODO Add to UI class
                component.setToolTipText("<html>State: Changed<br>Old value: " + attributeModel.getOriginalValue()
                        + "<br>New value: " + attributeModel.getCurrentValue() + "</html>");
                break;
            case Invalid:
                StringBuffer buffer = new StringBuffer();
                // TODO Add to UI class
                buffer.append("<html>State: Invalid");
                ValidationErrors validationErrors = attributeModel.getValidationErrors();
                if(validationErrors != null && validationErrors.getValidationErrors() != null) {
                    for(ValidationError validationError: validationErrors.getValidationErrors()) {
                        buffer.append("<br>");
                        buffer.append(validationError.getMessage());
                    }
                }
                buffer.append("</html>");
                component.setToolTipText(buffer.toString());
                break;
        }
    }

}
