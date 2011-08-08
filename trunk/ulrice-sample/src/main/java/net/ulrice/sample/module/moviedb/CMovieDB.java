package net.ulrice.sample.module.moviedb;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.ulrice.databinding.bufferedbinding.impl.BindingGroup;
import net.ulrice.databinding.bufferedbinding.impl.Element;
import net.ulrice.module.impl.AbstractController;
import net.ulrice.module.impl.ModuleActionState;
import net.ulrice.module.impl.action.Action;
import net.ulrice.module.impl.action.ActionType;

public class CMovieDB extends AbstractController<MMovieDB> implements ListSelectionListener {

	private BindingGroup overviewGroup = new BindingGroup();
	private BindingGroup detailGroup = new BindingGroup();
	private String detailMovieId;

	@Override
	protected void postEventInitialization() {
		super.postEventInitialization();

		getModel().setMovieList(MovieData.generateData());

		overviewGroup.bind(getModel().getMovieListAM(), v.getMovieTableAdapter());

		detailGroup.bind(getModel().getTitleAM(), v.getTitleVA());
		detailGroup.bind(getModel().getYearAM(), v.getYearVA());
		detailGroup.bind(getModel().getDirectorAM(), v.getDirectorVA());
		detailGroup.bind(getModel().getActorListAM(), v.getActorTableVA());

		overviewGroup.read();

		// FIXME Static column support
		v.getMovieTableAdapter().getComponent().getScrollTable().getSelectionModel().addListSelectionListener(this);
		v.getMovieTableAdapter().sizeColumns(false);

	}

	@Override
	protected ModuleActionState[] getHandledActions() {

		Action addMovieAction = new Action("_ADD_MOVIE", "Add Movie", true, ActionType.ModuleAction, null) {
			@Override
			public void actionPerformed(ActionEvent e) {		
				// FIXME Static column support
				JTable movieTable = v.getMovieTableAdapter().getComponent().getScrollTable();
				movieTable.getSelectionModel().removeListSelectionListener(CMovieDB.this);
				v.getMovieTableAdapter().addRow();
				movieTable.getSelectionModel().addListSelectionListener(CMovieDB.this);
			}
		};

		Action removeMovieAction = new Action("_DEL_MOVIE", "Del Movie", true, ActionType.ModuleAction, null) {
			@Override
			public void actionPerformed(ActionEvent e) {		
				// FIXME Static column support
				int selectedRow = v.getMovieTableAdapter().getComponent().getScrollTable().getSelectedRow();
				if(selectedRow >= 0) {
					JTable movieTable = v.getMovieTableAdapter().getComponent().getScrollTable();
					movieTable.getSelectionModel().removeListSelectionListener(CMovieDB.this);
					v.getMovieTableAdapter().delRow(selectedRow);
					movieTable.getSelectionModel().addListSelectionListener(CMovieDB.this);
				}
			}
		};

		Action addActorAction = new Action("_ADD_ACTOR", "Add Actor", true, ActionType.ModuleAction, null) {
			@Override
			public void actionPerformed(ActionEvent e) {		
				// FIXME Static column support
				JTable actorTable = v.getActorTableVA().getComponent().getScrollTable();
				actorTable.getSelectionModel().removeListSelectionListener(CMovieDB.this);
				v.getActorTableVA().addRow();
				actorTable.getSelectionModel().addListSelectionListener(CMovieDB.this);
			}
		};

		Action removeActorAction = new Action("_DEL_ACTOR", "Del Actor", true, ActionType.ModuleAction, null) {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = v.getActorTableVA().getComponent().getScrollTable().getSelectedRow();
				if(selectedRow >= 0) {		
					// FIXME Static column support
					JTable actorTable = v.getActorTableVA().getComponent().getScrollTable();
					actorTable.getSelectionModel().removeListSelectionListener(CMovieDB.this);
					v.getActorTableVA().delRow(selectedRow);
					actorTable.getSelectionModel().addListSelectionListener(CMovieDB.this);
				}
			}
		};

		return new ModuleActionState[] { new ModuleActionState(true, this, addMovieAction), new ModuleActionState(true, this, removeMovieAction),
				new ModuleActionState(true, this, addActorAction), new ModuleActionState(true, this, removeActorAction) };

	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {		
			// FIXME Static column support
			JTable movieTable = v.getMovieTableAdapter().getComponent().getScrollTable();
			movieTable.getSelectionModel().removeListSelectionListener(this);
			int selectedRow = movieTable.getSelectedRow();

			if (detailMovieId != null && detailGroup.isDirty()) {
				detailGroup.write();
				Movie movie = getModel().getMovie();
				getModel().getMovieListAM().getElementById(detailMovieId).setCurrentValue(movie);
			}

			if (selectedRow > -1) {
				Element selElement = getModel().getMovieListAM().getElementAt(selectedRow);
				detailMovieId = selElement.getUniqueId();
				Movie movie = (Movie) selElement.getCurrentValue();
				getModel().setMovie(movie);
				detailGroup.read();
				v.getActorTableVA().sizeColumns(true);
			} else {
				detailMovieId = null;
				getModel().setMovie(null);
				detailGroup.read();
			}

			movieTable.getSelectionModel().addListSelectionListener(this);
		}
	}

	@Override
	protected MMovieDB instantiateModel() {
		return new MMovieDB();
	}

	private final VMovieDB v = new VMovieDB();
	@Override
	protected JComponent instantiateView() {
	    return v.getView();
	}
}
