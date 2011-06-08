package net.ulrice.databinding;

import javax.swing.JComponent;

/**
 * Interface of a gui accessor.
 * 
 * @author christof
 */
public interface IFGuiAccessor<T extends JComponent, U extends IFAttributeModel<?>> {		
	
	/**
	 * Return the identifier of this gui accessor.
	 * 
	 * @return The id as a string.
	 */
	String getId();
	
	/**
	 * Return the component accessed by this accessor.
	 * 
	 * @return The gui component.
	 */
	T getComponent();
	
	/**
	 * Set the connected attribute model. 
	 * 
	 * @param attributeModel The connected attribute model.
	 */
	void setAttributeModel(U attributeModel);
	
	/**
	 * Return the connected attribute model.
	 * 
	 * @return The connected attribute model.
	 */
	U getAttributeModel();
	
	/**
	 * Sets the tooltip handler.
	 * 
	 * @param tooltipHandler The tooltip handler.
	 */
	void setTooltipHandler(IFTooltipHandler tooltipHandler);
	
	/**
	 * Returns the current tooltip handler.
	 * 
	 * @return The tooltip handler
	 */
	IFTooltipHandler getTooltipHandler();
	
	/**
	 * Sets the state marker of this gui accessor.
	 * 
	 * @param stateMarker The state marker.
	 */
	void setStateMarker(IFStateMarker stateMarker);
	
	/**
	 * Returns the state marker of this gui accessor.
	 * 
	 * @return The state marker.
	 */
	IFStateMarker getStateMarker();
}
