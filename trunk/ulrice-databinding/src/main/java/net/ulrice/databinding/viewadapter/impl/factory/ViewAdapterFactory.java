package net.ulrice.databinding.viewadapter.impl.factory;

import javax.swing.JTable;

import net.ulrice.databinding.viewadapter.IFViewAdapter;
import net.ulrice.databinding.viewadapter.impl.BackgroundStateMarker;
import net.ulrice.databinding.viewadapter.impl.BorderStateMarker;
import net.ulrice.databinding.viewadapter.impl.DetailedCellTooltipHandler;
import net.ulrice.databinding.viewadapter.impl.DetailedTooltipHandler;
import net.ulrice.databinding.viewadapter.impl.JCheckBoxViewAdapter;
import net.ulrice.databinding.viewadapter.impl.JComboBoxViewAdapter;
import net.ulrice.databinding.viewadapter.impl.JTableViewAdapter;
import net.ulrice.databinding.viewadapter.impl.JTextComponentViewAdapter;
import net.ulrice.databinding.viewadapter.impl.PresentationProvider;
import net.ulrice.databinding.viewadapter.utable.UTableViewAdapter;

public class ViewAdapterFactory {

    private static ViewAdapterFactoryCallback callback;
    
    public static void setViewAdapterFactoryCallback(ViewAdapterFactoryCallback callback) {
        ViewAdapterFactory.callback = callback;
    }    
    
    public static void setDefaultStateMarker(IFViewAdapter< ?, ?> viewAdapter) {        
        callback.setDefaultStateMarker(viewAdapter);
    }

    public static void setDefaultTooltipHandler(IFViewAdapter< ?, ?> viewAdapter) {
        callback.setDefaultTooltipHandler(viewAdapter);
    }

    public static JTextComponentViewAdapter createTextFieldAdapter() {
        return callback.createTextFieldAdapter();
    }

    public static JTableViewAdapter createTableViewAdapter() {
        return callback.createTableViewAdapter();
    }

    public static UTableViewAdapter createUTableViewAdapter(int staticColumns) {
        return callback.createUTableViewAdapter(staticColumns);
    }

    public static <M> JComboBoxViewAdapter createComboBoxAdapter(PresentationProvider<M> presentationProvider) {
        return callback.createComboBoxAdapter(presentationProvider);
    }

    public static JCheckBoxViewAdapter createCheckBoxAdapter() {
        return callback.createCheckBoxAdapter();
    }

}
