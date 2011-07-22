package net.ulrice.sample.module.moviedb;

import net.ulrice.databinding.bufferedbinding.impl.DataGroup;
import net.ulrice.module.impl.AbstractController;

public class CMovieDB extends AbstractController<MMovieDB, VMovieDB> {

	private DataGroup dataGroup;

	public CMovieDB() {
		this.dataGroup = new DataGroup();
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
	
	public DataGroup getDataGroup() {
		return dataGroup;
	}




}
