package net.ulrice.sample.module.moviedb;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.ulrice.databinding.SingleListTableModel;
import net.ulrice.databinding.SingleObjectModel;
import net.ulrice.databinding.bufferedbinding.TableAMBuilder;
import net.ulrice.databinding.bufferedbinding.impl.BindingGroup;
import net.ulrice.databinding.bufferedbinding.impl.Element;
import net.ulrice.databinding.validation.impl.StringLengthValidator;
import net.ulrice.module.impl.AbstractController;
import net.ulrice.module.impl.ModuleActionState;
import net.ulrice.module.impl.action.ActionType;
import net.ulrice.module.impl.action.UlriceAction;
import net.ulrice.sample.module.moviedb.Movie.Actor;

public class CMovieDB extends AbstractController implements ListSelectionListener {
    private final SingleListTableModel<Movie> overviewModel = new SingleListTableModel<Movie>(Movie.class) {{
        addColumn("name");
        addColumn("director");
        addColumn("year");
        addColumn("actors", new ActorValueConverter()); 

        getColumn("name").setValidator(new StringLengthValidator(1, 255));
        getColumn("actors").setReadOnly(true);
    }};
    
    private final SingleObjectModel<Movie> detailModel = new SingleObjectModel<Movie>(Movie.class) {{
        setAttributeModel("actors", new TableAMBuilder(this, "actors", Actor.class)
            .addColumn("lastname")
            .addColumn("firstname")
            .build());
    }};
    
    private final VMovieDB view = new VMovieDB();

    public JComponent getView() {
        return view.getMainPanel();
    }

	private final BindingGroup overviewGroup = new BindingGroup();
	private final BindingGroup detailGroup = new BindingGroup();
	private String detailMovieId;

	@Override
	public void postCreate() {
	    overviewModel.setData(MovieData.generateData());
		overviewGroup.bind(overviewModel.getAttributeModel(), view.getMovieTableAdapter());

		detailGroup.bind(detailModel.getAttributeModel("name"), view.getTitleVA());
		detailGroup.bind(detailModel.getAttributeModel("year"), view.getYearVA());
		detailGroup.bind(detailModel.getAttributeModel("director"), view.getDirectorVA());
		detailGroup.bind(detailModel.getAttributeModel("actors"), view.getActorTableVA());

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
				overviewModel.getAttributeModel().getElementById(detailMovieId).setCurrentValue(detailModel.getData());
			}

			if (selectedRow > -1) {
				Element selElement = overviewModel.getAttributeModel().getElementAt(selectedRow);
				detailMovieId = selElement.getUniqueId();
				Movie movie = (Movie) selElement.getCurrentValue();
				detailModel.setData(movie);
				detailGroup.read();
				view.getActorTableVA().sizeColumns(true);
			} else {
				detailMovieId = null;
				detailModel.setData(null);
				detailGroup.read();
			}

			movieTable.getSelectionModel().addListSelectionListener(this);
		}
	}
}
