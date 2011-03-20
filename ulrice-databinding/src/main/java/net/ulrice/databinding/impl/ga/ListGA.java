package net.ulrice.databinding.impl.ga;

import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.event.EventListenerList;

import net.ulrice.databinding.DataState;
import net.ulrice.databinding.IFAttributeModel;
import net.ulrice.databinding.IFAttributeModelEventListener;
import net.ulrice.databinding.IFGuiAccessor;
import net.ulrice.databinding.IFStateMarker;
import net.ulrice.databinding.IFTooltipHandler;
import net.ulrice.databinding.impl.am.GenericAM;

public class ListGA extends AbstractListModel implements IFGuiAccessor<JList, GenericAM<List<?>>>, IFAttributeModelEventListener<List<?>> {

	private String id;
	private JList list;
	private GenericAM<List<?>> attributeModel;
	private IFTooltipHandler tooltipHandler;
	private IFStateMarker stateMarker;
	private List<?> data;
	private EventListenerList listeners;

	public ListGA(String id, JList list) {
		this.id = id;
		this.list = list;
		this.list.setModel(this);
		this.listeners = new EventListenerList();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public JList getComponent() {
		return list;
	}

	@Override
	public void setAttributeModel(GenericAM<List<?>> attributeModel) {
		this.attributeModel = attributeModel;
		this.attributeModel.addAttributeModelEventListener(this);
	}

	@Override
	public GenericAM<List<?>> getAttributeModel() {
		return attributeModel;
	}

	@Override
	public void setTooltipHandler(IFTooltipHandler tooltipHandler) {
		this.tooltipHandler = tooltipHandler;
	}

	@Override
	public IFTooltipHandler getTooltipHandler() {
		return tooltipHandler;
	}

	@Override
	public void setStateMarker(IFStateMarker stateMarker) {
		this.stateMarker = stateMarker;
	}

	@Override
	public IFStateMarker getStateMarker() {
		return stateMarker;
	}

	@Override
	public void dataChanged(IFGuiAccessor<?, ?> gaSource, IFAttributeModel<List<?>> amSource, List<?> oldValue,
			List<?> newValue, DataState state) {

		if (getTooltipHandler() != null) {
			getTooltipHandler().updateTooltip(amSource, this, getComponent());
		}
		
		this.data = newValue;
		fireContentsChanged(this, 0, getSize());
	}

	@Override
	public void stateChanged(IFGuiAccessor<?, ?> gaSource, IFAttributeModel<List<?>> amSource, DataState oldState,
			DataState newState) {

		if (getStateMarker() != null) {
			stateMarker.paintState(getComponent(), newState);
		}
		if (getTooltipHandler() != null) {
			getTooltipHandler().updateTooltip(amSource, this, getComponent());
		}
	}

	@Override
	public int getSize() {
		return data == null ? 0 : data.size();
	}

	@Override
	public Object getElementAt(int index) {
		return data.get(index);
	}

}
