package net.ulrice.sample.module.moviedb;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.ulrice.databinding.viewadapter.impl.JTableViewAdapter;
import net.ulrice.databinding.viewadapter.impl.JTextComponentViewAdapter;
import net.ulrice.databinding.viewadapter.impl.factory.ViewAdapterFactory;
import net.ulrice.module.IFView;

public class VMovieDB implements IFView<CMovieDB> {

	private JPanel mainPanel;
	private JPanel detailPanel;
	private JPanel overviewPanel;

	private JTableViewAdapter movieTableVA = ViewAdapterFactory.createTableViewAdapter();
	private JTextComponentViewAdapter titleVA = ViewAdapterFactory.createTextFieldAdapter();
	private JTextComponentViewAdapter yearVA = ViewAdapterFactory.createTextFieldAdapter();
	private JTextComponentViewAdapter directorVA = ViewAdapterFactory.createTextFieldAdapter();
	private JTableViewAdapter actorTableVA = ViewAdapterFactory.createTableViewAdapter();

	@Override
	public void initialize(CMovieDB controller) {					
		overviewPanel = new JPanel();
		overviewPanel.setLayout(new BorderLayout());
		overviewPanel.add(new JScrollPane(movieTableVA.getComponent()), BorderLayout.CENTER);
		
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
		detailPanel.add(new JScrollPane(actorTableVA.getComponent()), BorderLayout.CENTER);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(2, 1));
		mainPanel.add(overviewPanel);
		mainPanel.add(detailPanel);		
	}

	@Override
	public JComponent getView() {
		return mainPanel;
	}

	public JTableViewAdapter getMovieTableAdapter() {
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
	
	public JTableViewAdapter getActorTableVA() {
		return actorTableVA;
	}
}
