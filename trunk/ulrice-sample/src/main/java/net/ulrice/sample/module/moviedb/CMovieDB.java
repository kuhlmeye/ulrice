package net.ulrice.sample.module.moviedb;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.ulrice.databinding.bufferedbinding.impl.BindingGroup;
import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition.ColumnType;
import net.ulrice.databinding.bufferedbinding.impl.Element;
import net.ulrice.databinding.validation.impl.StringLengthValidator;
import net.ulrice.module.impl.AbstractController;
import net.ulrice.module.impl.IFClosing;
import net.ulrice.module.impl.ModuleActionState;
import net.ulrice.module.impl.action.ActionType;
import net.ulrice.module.impl.action.UlriceAction;
import net.ulrice.sample.SingleListTableModel;
import net.ulrice.sample.SingleObjectModel;
import net.ulrice.sample.TableAMBuilder;
import net.ulrice.sample.module.moviedb.Movie.Actor;


public class CMovieDB extends AbstractController implements ListSelectionListener {
    private final SingleListTableModel<Movie> overviewModel = new SingleListTableModel<Movie>(Movie.class) {{
        addColumn("name");
        addColumn("director");
        addColumn("year");
        addColumn("actors", new ActorValueConverter());
        addDerivedColumn ("description", "The film {0} directed by {1}.", "name", "director");

        getColumn("name").setValidator(new StringLengthValidator(1, 255));
        getColumn("actors").setColumnType(ColumnType.ReadOnly);
    }};
    
    private final SingleObjectModel<Movie> detailModel = new SingleObjectModel<Movie>(Movie.class) {{
        setAttributeModel("data.actors", new TableAMBuilder(this, "data.actors", Actor.class)
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
		
		detailGroup.bind(detailModel.getAttributeModel("data.name"), view.getTitleVA());
		detailGroup.bind(detailModel.getAttributeModel("data.year"), view.getYearVA());
		detailGroup.bind(detailModel.getAttributeModel("data.director"), view.getDirectorVA());
		detailGroup.bind(detailModel.getAttributeModel("data.actors"), view.getActorTableVA());

		overviewGroup.read();


		view.getMovieTableAdapter().getComponent().addListSelectionListener(this);
		view.getMovieTableAdapter().getComponent().sizeColumns(false);

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
					view.getMovieTableAdapter().delSelectedRows();
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
					view.getActorTableVA().delSelectedRows();
					actorTable.getSelectionModel().addListSelectionListener(CMovieDB.this);
				}
			}
		};

		return Arrays.asList(new ModuleActionState(true, addMovieAction), new ModuleActionState(true, removeMovieAction),
				new ModuleActionState(true, addActorAction), new ModuleActionState(true, removeActorAction) );
	}

	private void detailToOverview() {
        detailGroup.write();
        overviewModel.getAttributeModel().getElementById(detailMovieId).setCurrentValue(detailModel.getData());
	}
	
	private void overviewToDetail() {
	    final int selectedRow = view.getMovieTableAdapter().getSelectedRowModelIndex();
        final Element selElement = overviewModel.getAttributeModel().getElementAt(selectedRow);
        detailMovieId = selElement.getUniqueId();
        detailModel.setData((Movie) selElement.getCurrentValue());
        detailGroup.read();
        view.getActorTableVA().getComponent().sizeColumns(true);
	}
	
	private void clearDetail() {
        detailMovieId = null;
        detailModel.setData(null);
        detailGroup.read();
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
	    if (detailMovieId != null && detailGroup.isDirty()) {
	        detailToOverview();
	    }

	    if (view.getMovieTableAdapter().getSelectedRowModelIndex() > -1) {
	        overviewToDetail();
	    } 
	    else {
	        clearDetail();
	    }
	}
    
	@Override
    public void onClose(IFClosing closing) {
        
        closing.doClose();
    }
}
