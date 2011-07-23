package net.ulrice.sample.module.moviedb;

import java.awt.event.ActionEvent;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.ulrice.databinding.bufferedbinding.impl.BindingGroup;
import net.ulrice.databinding.bufferedbinding.impl.Element;
import net.ulrice.module.impl.AbstractController;
import net.ulrice.module.impl.ModuleActionState;
import net.ulrice.module.impl.action.Action;
import net.ulrice.module.impl.action.ActionType;

public class CMovieDB extends AbstractController<MMovieDB, VMovieDB> implements ListSelectionListener {

	private BindingGroup overviewGroup = new BindingGroup();
	private BindingGroup detailGroup = new BindingGroup();
	private String detailMovieId;

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
	protected ModuleActionState[] getHandledActions() {

		Action addMovieAction = new Action("_ADD_MOVIE", "Add Movie", true, ActionType.ModuleAction, null) {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTable movieTable = getView().getMovieTableAdapter().getComponent();
				movieTable.getSelectionModel().removeListSelectionListener(CMovieDB.this);
				getView().getMovieTableAdapter().addRow();
				movieTable.getSelectionModel().addListSelectionListener(CMovieDB.this);
			}
		};

		Action removeMovieAction = new Action("_DEL_MOVIE", "Del Movie", true, ActionType.ModuleAction, null) {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = getView().getMovieTableAdapter().getComponent().getSelectedRow();
				if(selectedRow >= 0) {
					JTable movieTable = getView().getMovieTableAdapter().getComponent();
					movieTable.getSelectionModel().removeListSelectionListener(CMovieDB.this);
					getView().getMovieTableAdapter().delRow(selectedRow);
					movieTable.getSelectionModel().addListSelectionListener(CMovieDB.this);
				}
			}
		};

		Action addActorAction = new Action("_ADD_ACTOR", "Add Actor", true, ActionType.ModuleAction, null) {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTable actorTable = getView().getActorTableVA().getComponent();
				actorTable.getSelectionModel().removeListSelectionListener(CMovieDB.this);
				getView().getActorTableVA().addRow();
				actorTable.getSelectionModel().addListSelectionListener(CMovieDB.this);
			}
		};

		Action removeActorAction = new Action("_DEL_ACTOR", "Del Actor", true, ActionType.ModuleAction, null) {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = getView().getActorTableVA().getComponent().getSelectedRow();
				if(selectedRow >= 0) {
					JTable actorTable = getView().getActorTableVA().getComponent();
					actorTable.getSelectionModel().removeListSelectionListener(CMovieDB.this);
					getView().getActorTableVA().delRow(selectedRow);
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
			JTable movieTable = getView().getMovieTableAdapter().getComponent();
			movieTable.getSelectionModel().removeListSelectionListener(this);
			int selectedRow = movieTable.getSelectedRow();

			System.out.println("Dirty: " + detailGroup.isDirty() + ", Valid: " + detailGroup.isValid());
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
				getView().getActorTableVA().sizeColumns(true);
			} else {
				detailMovieId = null;
				getModel().setMovie(null);
				detailGroup.read();
			}

			movieTable.getSelectionModel().addListSelectionListener(this);
		}
	}

	@Override
	protected MMovieDB instanciateModel() {
		return new MMovieDB();
	}

	@Override
	protected VMovieDB instanciateView() {
		return new VMovieDB();
	}
}
