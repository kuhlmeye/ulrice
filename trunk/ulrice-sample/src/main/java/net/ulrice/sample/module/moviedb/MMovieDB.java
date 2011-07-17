package net.ulrice.sample.module.moviedb;

import java.util.List;

import net.ulrice.databinding.bufferedbinding.ColumnDefinition;
import net.ulrice.databinding.bufferedbinding.ListAM;
import net.ulrice.databinding.modelaccess.impl.ReflectionMVA;
import net.ulrice.module.IFModel;

public class MMovieDB implements IFModel {

	private CMovieDB ctrl;
	
	private List<Movie> movieList;

	public MMovieDB(CMovieDB ctrl) {
		this.ctrl = ctrl;
	}

	@Override
	public void initialize() {

		ListAM movieListAM = new ListAM("MovieList", new ReflectionMVA(this, "movieList"));
		movieListAM.addColumn(new ColumnDefinition<String>("Name", new ReflectionMVA("name"), String.class));
		movieListAM.addColumn(new ColumnDefinition<String>("Director", new ReflectionMVA("director"), String.class));
		movieListAM.addColumn(new ColumnDefinition<String>("Year", new ReflectionMVA("year"), String.class));
		ColumnDefinition<String> actorColumn = new ColumnDefinition<String>("Actors", new ReflectionMVA("actors"), String.class);
		actorColumn.setValueConverter(new ActorValueConverter());
		actorColumn.setReadOnly(true);
		movieListAM.addColumn(actorColumn);
		
		ctrl.getDataGroup().addAM(movieListAM);
	}

	public List<Movie> getMovieList() {
		return movieList;
	}

	public void setMovieList(List<Movie> movieList) {
		this.movieList = movieList;
	}
}
