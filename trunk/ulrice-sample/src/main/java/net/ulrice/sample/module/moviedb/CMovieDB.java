package net.ulrice.sample.module.moviedb;

import net.ulrice.databinding.bufferedbinding.impl.BindingGroup;
import net.ulrice.module.impl.AbstractController;

public class CMovieDB extends AbstractController<MMovieDB, VMovieDB> {

	private BindingGroup dataGroup;

	public CMovieDB() {
		this.dataGroup = new BindingGroup();
	}
	
	@Override
	protected void postEventInitialization() {
		super.postEventInitialization();
		
		getModel().setMovieList(MovieData.generateData());

		dataGroup.read();

		getView().getTableAdapter().sizeColumns(false);				
	}
	
	@Override	
	protected MMovieDB instanciateModel() {
		return new MMovieDB();
	}

	@Override
	protected VMovieDB instanciateView() {
		return new VMovieDB();
	}
	
	public BindingGroup getDataGroup() {
		return dataGroup;
	}




}
