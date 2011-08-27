package net.ulrice.databinding.viewadapter.impl;

import java.util.List;

import javax.swing.JComponent;

import net.ulrice.databinding.bufferedbinding.impl.Element;
import net.ulrice.databinding.viewadapter.IFTooltipHandler;

/**
 * Displays the detailed state of an attribute model as a tooltip in the
 * component.
 * 
 * @author christof
 */
public class DetailedCellTooltipHandler implements IFTooltipHandler<Element> {

	/**
	 * @see net.ulrice.databinding.viewadapter.IFTooltipHandler#updateTooltip(net.ulrice.databinding.IFAttributeModel,
	 *      net.ulrice.databinding.IFGuiAccessor, javax.swing.JComponent)
	 */
	@Override
	public void updateTooltip(Element element, JComponent component) {


		if (!element.isValid()) {
			StringBuffer buffer = new StringBuffer();
			// TODO Add to UI class
			buffer.append("<html>State: Invalid");
			List<String> validationFailures = element.getValidationFailures();
			if (validationFailures != null) {
				for (String message : validationFailures) {
					buffer.append("<br>");
					buffer.append(message);
				}
			}
			buffer.append("</html>");
			component.setToolTipText(buffer.toString());
		} else {
			if (element.isDirty()) {
				component.setToolTipText("<html>State: Changed<br>Old value: " + element.getOriginalValue() + "<br>New value: "
						+ element.getCurrentValue() + "</html>");
			} else {
				component.setToolTipText("State: Not changed");
			}
		}
	}
}
