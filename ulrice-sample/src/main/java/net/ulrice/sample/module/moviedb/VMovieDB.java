package net.ulrice.sample.module.moviedb;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.ulrice.databinding.viewadapter.impl.JTextComponentViewAdapter;
import net.ulrice.databinding.viewadapter.impl.factory.ViewAdapterFactory;
import net.ulrice.databinding.viewadapter.utable.UTableViewAdapter;

public class VMovieDB {

	private final JPanel mainPanel = new JPanel();
	private final JPanel detailPanel = new JPanel();
	private final JPanel overviewPanel = new JPanel();

	private final UTableViewAdapter movieTableVA = ViewAdapterFactory.createUTableViewAdapter(1);
	private final JTextComponentViewAdapter titleVA = ViewAdapterFactory.createTextFieldAdapter();
	private final JTextComponentViewAdapter yearVA = ViewAdapterFactory.createTextFieldAdapter();
	private final JTextComponentViewAdapter directorVA = ViewAdapterFactory.createTextFieldAdapter();
	private final UTableViewAdapter actorTableVA = ViewAdapterFactory.createUTableViewAdapter(0);

	public VMovieDB() {					
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
		
		detailPanel.setLayout(new BorderLayout());
		detailPanel.add(movieAttributePanel, BorderLayout.NORTH);
		detailPanel.add(actorTableVA.getComponent(), BorderLayout.CENTER);
		
		mainPanel.setLayout(new GridLayout(2, 1));
		mainPanel.add(overviewPanel);
		mainPanel.add(detailPanel);		
	}

	public JComponent getMainPanel() {
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
