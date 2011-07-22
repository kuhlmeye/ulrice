package net.ulrice.databinding.viewadapter.impl.factory;

import javax.swing.JTable;

import net.ulrice.databinding.viewadapter.IFViewAdapter;
import net.ulrice.databinding.viewadapter.impl.BackgroundStateMarker;
import net.ulrice.databinding.viewadapter.impl.BorderStateMarker;
import net.ulrice.databinding.viewadapter.impl.DetailedTooltipHandler;
import net.ulrice.databinding.viewadapter.impl.JTableViewAdapter;
import net.ulrice.databinding.viewadapter.impl.JTextComponentViewAdapter;

public class ViewAdapterFactory {

	public static void setDefaultStateMarker(IFViewAdapter viewAdapter) {
		if(!(viewAdapter instanceof JTableViewAdapter)) {
			BorderStateMarker stateMarker = new BorderStateMarker();
			viewAdapter.setStateMarker(stateMarker);
			viewAdapter.getComponent().setBorder(stateMarker);
		} else {
			viewAdapter.setStateMarker(new BackgroundStateMarker());
		}
	}
	
	public static void setDefaultTooltipHandler(IFViewAdapter viewAdapter) {
		viewAdapter.setTooltipHandler(new DetailedTooltipHandler());
	}
		
	public static JTextComponentViewAdapter createTextFieldAdapter() {
		JTextComponentViewAdapter viewAdapter = new JTextComponentViewAdapter();
		setDefaultStateMarker(viewAdapter);
		setDefaultTooltipHandler(viewAdapter);
		return viewAdapter;
	}

	public static JTableViewAdapter createTableViewAdapter() {
		JTableViewAdapter viewAdapter = new JTableViewAdapter();
		viewAdapter.getComponent().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setDefaultStateMarker(viewAdapter);
		setDefaultTooltipHandler(viewAdapter);

		return viewAdapter;
	}

}
