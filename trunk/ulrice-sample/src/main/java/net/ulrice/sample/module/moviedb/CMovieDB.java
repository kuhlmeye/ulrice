package net.ulrice.sample.module.moviedb;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;

import net.ulrice.databinding.bufferedbinding.impl.BindingGroup;
import net.ulrice.module.impl.AbstractController;

public class CMovieDB extends AbstractController<MMovieDB, VMovieDB> implements ListSelectionListener {

	private BindingGroup overviewGroup = new BindingGroup();
	private BindingGroup detailGroup = new BindingGroup();
	private int lastSelectedRow = -1;

	@Override
	protected void postEventInitialization() {
		super.postEventInitialization();
		
		getModel().setMovieList(MovieData.generateData());
		
		overviewGroup.bind(getModel().getMovieListAM(), getView().getMovieTableAdapter());
		
		detailGroup.bind(getModel().getTitleAM(), getView().getTitleVA());
		detailGroup.bind(getModel().getYearAM(), getView().getYearVA());
		detailGroup.bind(getModel().getDirectorAM(), getView().getDirectorVA());
		detailGroup.bind(getModel().getActorListAM(), getView().getActorTableVA());
		
		overviewGroup.read();

		getView().getMovieTableAdapter().getComponent().getSelectionModel().addListSelectionListener(this);		
		getView().getMovieTableAdapter().sizeColumns(false);				
	}
	

	
	@Override	
	protected MMovieDB instanciateModel() {
		return new MMovieDB();
	}

	@Override
	protected VMovieDB instanciateView() {
		return new VMovieDB();
	}



	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(!e.getValueIsAdjusting()) {
			
			JTable movieTable = getView().getMovieTableAdapter().getComponent();
			movieTable.getSelectionModel().removeListSelectionListener(this);
			int selectedRow = movieTable.getSelectedRow();
			
			if(lastSelectedRow > -1) {
				detailGroup.write();
				Movie movie = getModel().getMovie();
				getModel().getMovieListAM().getElementAt(lastSelectedRow).setCurrentValue(movie);
			}

			if(selectedRow > -1) {				
				Movie movie = (Movie)getModel().getMovieListAM().getElementAt(selectedRow).getCurrentValue();
				getModel().setMovie(movie);
				detailGroup.read();
				getView().getActorTableVA().sizeColumns(true);
			}
			
			lastSelectedRow = selectedRow;
			movieTable.getSelectionModel().addListSelectionListener(this);
		}
	}
}
