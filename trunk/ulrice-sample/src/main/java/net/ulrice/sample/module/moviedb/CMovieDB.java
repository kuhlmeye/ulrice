package net.ulrice.sample.module.moviedb;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.ulrice.databinding.bufferedbinding.impl.BindingGroup;
import net.ulrice.databinding.bufferedbinding.impl.Element;
import net.ulrice.module.impl.AbstractController;
import net.ulrice.module.impl.ModuleActionState;
import net.ulrice.module.impl.action.UlriceAction;
import net.ulrice.module.impl.action.ActionType;

public class CMovieDB extends AbstractController implements ListSelectionListener {
    private final MMovieDB model = new MMovieDB();
    private final VMovieDB view = new VMovieDB();

    public JComponent getView() {
        return view.getView();
    }

	private BindingGroup overviewGroup = new BindingGroup();
	private BindingGroup detailGroup = new BindingGroup();
	private String detailMovieId;

	@Override
	public void postCreate() {
		model.setMovieList(MovieData.generateData());

		overviewGroup.bind(model.getMovieListAM(), view.getMovieTableAdapter());

		detailGroup.bind(model.getTitleAM(), view.getTitleVA());
		detailGroup.bind(model.getYearAM(), view.getYearVA());
		detailGroup.bind(model.getDirectorAM(), view.getDirectorVA());
		detailGroup.bind(model.getActorListAM(), view.getActorTableVA());

		overviewGroup.read();

		// FIXME Static column support
		view.getMovieTableAdapter().getComponent().getScrollTable().getSelectionModel().addListSelectionListener(this);
		view.getMovieTableAdapter().sizeColumns(false);

	}

	@Override
	public List<ModuleActionState> getHandledActions() {

		UlriceAction addMovieAction = new UlriceAction("_ADD_MOVIE", "Add Movie", true, ActionType.ModuleAction, null) {
			@Override
			public void actionPerformed(ActionEvent e) {		
				// FIXME Static column support
				JTable movieTable = view.getMovieTableAdapter().getComponent().getScrollTable();
				movieTable.getSelectionModel().removeListSelectionListener(CMovieDB.this);
				view.getMovieTableAdapter().addRow();
				movieTable.getSelectionModel().addListSelectionListener(CMovieDB.this);
			}
		};

		UlriceAction removeMovieAction = new UlriceAction("_DEL_MOVIE", "Del Movie", true, ActionType.ModuleAction, null) {
			@Override
			public void actionPerformed(ActionEvent e) {		
				// FIXME Static column support
				int selectedRow = view.getMovieTableAdapter().getComponent().getScrollTable().getSelectedRow();
				if(selectedRow >= 0) {
					JTable movieTable = view.getMovieTableAdapter().getComponent().getScrollTable();
					movieTable.getSelectionModel().removeListSelectionListener(CMovieDB.this);
					view.getMovieTableAdapter().delRow(selectedRow);
					movieTable.getSelectionModel().addListSelectionListener(CMovieDB.this);
				}
			}
		};

		UlriceAction addActorAction = new UlriceAction("_ADD_ACTOR", "Add Actor", true, ActionType.ModuleAction, null) {
			@Override
			public void actionPerformed(ActionEvent e) {		
				// FIXME Static column support
				JTable actorTable = view.getActorTableVA().getComponent().getScrollTable();
				actorTable.getSelectionModel().removeListSelectionListener(CMovieDB.this);
				view.getActorTableVA().addRow();
				actorTable.getSelectionModel().addListSelectionListener(CMovieDB.this);
			}
		};

		UlriceAction removeActorAction = new UlriceAction("_DEL_ACTOR", "Del Actor", true, ActionType.ModuleAction, null) {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = view.getActorTableVA().getComponent().getScrollTable().getSelectedRow();
				if(selectedRow >= 0) {		
					// FIXME Static column support
					JTable actorTable = view.getActorTableVA().getComponent().getScrollTable();
					actorTable.getSelectionModel().removeListSelectionListener(CMovieDB.this);
					view.getActorTableVA().delRow(selectedRow);
					actorTable.getSelectionModel().addListSelectionListener(CMovieDB.this);
				}
			}
		};

		return Arrays.asList(new ModuleActionState(true, this, addMovieAction), new ModuleActionState(true, this, removeMovieAction),
				new ModuleActionState(true, this, addActorAction), new ModuleActionState(true, this, removeActorAction) );

	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {		
			// FIXME Static column support
			JTable movieTable = view.getMovieTableAdapter().getComponent().getScrollTable();
			movieTable.getSelectionModel().removeListSelectionListener(this);
			int selectedRow = movieTable.getSelectedRow();

			if (detailMovieId != null && detailGroup.isDirty()) {
				detailGroup.write();
				Movie movie = model.getMovie();
				model.getMovieListAM().getElementById(detailMovieId).setCurrentValue(movie);
			}

			if (selectedRow > -1) {
				Element selElement = model.getMovieListAM().getElementAt(selectedRow);
				detailMovieId = selElement.getUniqueId();
				Movie movie = (Movie) selElement.getCurrentValue();
				model.setMovie(movie);
				detailGroup.read();
				view.getActorTableVA().sizeColumns(true);
			} else {
				detailMovieId = null;
				model.setMovie(null);
				detailGroup.read();
			}

			movieTable.getSelectionModel().addListSelectionListener(this);
		}
	}
}
