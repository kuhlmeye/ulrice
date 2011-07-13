package net.ulrice.databinding.viewadapter.impl;

import java.util.List;

import javax.swing.JComponent;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.IFExtdAttributeModel;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;
import net.ulrice.databinding.viewadapter.IFTooltipHandler;

/**
 * Displays the detailed state of an attribute model as a tooltip in the
 * component.
 * 
 * @author christof
 */
public class DetailedTooltipHandler implements IFTooltipHandler {

    /**
     * @see net.ulrice.databinding.viewadapter.IFTooltipHandler#updateTooltip(net.ulrice.databinding.IFAttributeModel,
     *      net.ulrice.databinding.IFGuiAccessor, javax.swing.JComponent)
     */
    @Override
    public void updateTooltip(IFBinding binding, JComponent component) {
        switch (binding.getState()) {
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
				component.setToolTipText("<html>State: Changed<br>Old value: " + binding.getOriginalValue() + "<br>New value: "
						+ binding.getCurrentValue() + "</html>");
                break;
            case Invalid:
                StringBuffer buffer = new StringBuffer();
                // TODO Add to UI class
                buffer.append("<html>State: Invalid");
                List<String> validationFailures = binding.getValidationFailures();
                if(validationFailures != null) {
                    for(String message: validationFailures) {
                        buffer.append("<br>");
                        buffer.append(message);
                    }
                }
                buffer.append("</html>");
                component.setToolTipText(buffer.toString());
                break;
        }
    }

}
