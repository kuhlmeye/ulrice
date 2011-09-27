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

public class ViewAdapterFactoryCallbackImpl implements ViewAdapterFactoryCallback {

    @Override
    public void setDefaultStateMarker(IFViewAdapter< ?, ?> viewAdapter) {
        if (viewAdapter instanceof JTableViewAdapter) {
            viewAdapter.setStateMarker(new BackgroundStateMarker());
        }
        else if (viewAdapter instanceof UTableViewAdapter) {
            ((UTableViewAdapter) viewAdapter).setCellStateMarker(new BackgroundStateMarker());
        }
        else {
            BorderStateMarker stateMarker = new BorderStateMarker();
            viewAdapter.setStateMarker(stateMarker);
            viewAdapter.getComponent().setBorder(stateMarker);
        }
    }

    @Override
    public void setDefaultTooltipHandler(IFViewAdapter< ?, ?> viewAdapter) {
        if (viewAdapter instanceof UTableViewAdapter) {
            ((UTableViewAdapter) viewAdapter).setCellTooltipHandler(new DetailedCellTooltipHandler());
        }
        else {
            viewAdapter.setTooltipHandler(new DetailedTooltipHandler());
        }
    }

    @Override
    public JTextComponentViewAdapter createTextFieldAdapter() {
        JTextComponentViewAdapter viewAdapter = new JTextComponentViewAdapter();
        setDefaultStateMarker(viewAdapter);
        setDefaultTooltipHandler(viewAdapter);
        return viewAdapter;
    }

    @Override
    public JTableViewAdapter createTableViewAdapter() {
        JTableViewAdapter viewAdapter = new JTableViewAdapter();
        viewAdapter.getComponent().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setDefaultStateMarker(viewAdapter);
        setDefaultTooltipHandler(viewAdapter);
        return viewAdapter;
    }

    @Override
    public UTableViewAdapter createUTableViewAdapter(int staticColumns) {
        UTableViewAdapter viewAdapter = new UTableViewAdapter(staticColumns);
        setDefaultStateMarker(viewAdapter);
        setDefaultTooltipHandler(viewAdapter);
        return viewAdapter;
    }

    @Override
    public <M> JComboBoxViewAdapter createComboBoxAdapter(PresentationProvider<M> presentationProvider) {
        JComboBoxViewAdapter<M> viewAdapter = new JComboBoxViewAdapter<M>(presentationProvider);
        setDefaultStateMarker(viewAdapter);
        setDefaultTooltipHandler(viewAdapter);
        return viewAdapter;
    }

    @Override
    public JCheckBoxViewAdapter createCheckBoxAdapter() {
        JCheckBoxViewAdapter viewAdapter = new JCheckBoxViewAdapter();
        setDefaultStateMarker(viewAdapter);
        setDefaultTooltipHandler(viewAdapter);
        return viewAdapter;
    }
}
