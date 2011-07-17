package net.ulrice.sample.module.moviedb;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.ulrice.databinding.viewadapter.impl.JTableViewAdapter;
import net.ulrice.module.IFView;

public class VMovieDB implements IFView {

	private CMovieDB ctrl;
	private JPanel panel;

	public VMovieDB(CMovieDB ctrl) {
		this.ctrl = ctrl;
	}

	@Override
	public void initialize() {
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		JTable table = new JTable();
		JTableViewAdapter movieTableVA = new JTableViewAdapter(table);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		panel.add(new JScrollPane(movieTableVA.getComponent()), BorderLayout.CENTER);
		
		ctrl.getDataGroup().addGA("MovieList", movieTableVA);
	}

	@Override
	public JComponent getView() {
		return panel;
	}

}
