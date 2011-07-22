package net.ulrice.sample.module.moviedb;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.ulrice.databinding.viewadapter.impl.BackgroundStateMarker;
import net.ulrice.databinding.viewadapter.impl.DetailedTooltipHandler;
import net.ulrice.databinding.viewadapter.impl.JTableViewAdapter;
import net.ulrice.module.IFView;

public class VMovieDB implements IFView<CMovieDB> {

	private JPanel panel;
	private JTableViewAdapter movieTableVA;

	@Override
	public void initialize(CMovieDB controller) {		
		movieTableVA = new JTableViewAdapter();
		movieTableVA.setStateMarker(new BackgroundStateMarker());
		movieTableVA.setTooltipHandler(new DetailedTooltipHandler());
		movieTableVA.getComponent().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		controller.getDataGroup().addViewAdapter("MMovieDB.movieList", movieTableVA);

		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(new JScrollPane(movieTableVA.getComponent()), BorderLayout.CENTER);
	}

	@Override
	public JComponent getView() {
		return panel;
	}

	public JTableViewAdapter getTableAdapter() {
		return movieTableVA;
	}
}
