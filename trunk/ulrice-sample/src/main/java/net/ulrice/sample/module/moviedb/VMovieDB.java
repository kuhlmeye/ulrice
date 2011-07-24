package net.ulrice.sample.module.moviedb;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.ulrice.databinding.viewadapter.impl.JTextComponentViewAdapter;
import net.ulrice.databinding.viewadapter.impl.factory.ViewAdapterFactory;
import net.ulrice.databinding.viewadapter.utable.UTableViewAdapter;
import net.ulrice.module.IFView;

public class VMovieDB implements IFView<CMovieDB> {

	private JPanel mainPanel;
	private JPanel detailPanel;
	private JPanel overviewPanel;

	private UTableViewAdapter movieTableVA = ViewAdapterFactory.createUTableViewAdapter(1);
	private JTextComponentViewAdapter titleVA = ViewAdapterFactory.createTextFieldAdapter();
	private JTextComponentViewAdapter yearVA = ViewAdapterFactory.createTextFieldAdapter();
	private JTextComponentViewAdapter directorVA = ViewAdapterFactory.createTextFieldAdapter();
	private UTableViewAdapter actorTableVA = ViewAdapterFactory.createUTableViewAdapter(0);

	@Override
	public void initialize(CMovieDB controller) {					
		overviewPanel = new JPanel();
		overviewPanel.setLayout(new BorderLayout());
		overviewPanel.add(movieTableVA.getComponent(), BorderLayout.CENTER);
		
		JPanel movieAttributePanel = new JPanel();
		movieAttributePanel.setLayout(new GridLayout(3, 2));
		movieAttributePanel.add(new JLabel("Title:"));
		movieAttributePanel.add(titleVA.getComponent());
		movieAttributePanel.add(new JLabel("Year:"));
		movieAttributePanel.add(yearVA.getComponent());
		movieAttributePanel.add(new JLabel("Director:"));
		movieAttributePanel.add(directorVA.getComponent());
		
		detailPanel = new JPanel();
		detailPanel.setLayout(new BorderLayout());
		detailPanel.add(movieAttributePanel, BorderLayout.NORTH);
		detailPanel.add(actorTableVA.getComponent(), BorderLayout.CENTER);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(2, 1));
		mainPanel.add(overviewPanel);
		mainPanel.add(detailPanel);		
	}

	@Override
	public JComponent getView() {
		return mainPanel;
	}

	public UTableViewAdapter getMovieTableAdapter() {
		return movieTableVA;
	}
	
	public JTextComponentViewAdapter getTitleVA() {
		return titleVA;
	}
	
	public JTextComponentViewAdapter getYearVA() {
		return yearVA;
	}
	
	public JTextComponentViewAdapter getDirectorVA() {
		return directorVA;
	}
	
	public UTableViewAdapter getActorTableVA() {
		return actorTableVA;
	}
}
