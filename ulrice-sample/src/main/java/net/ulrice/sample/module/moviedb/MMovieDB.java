package net.ulrice.sample.module.moviedb;

import java.util.List;

import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;
import net.ulrice.databinding.bufferedbinding.impl.ListAM;
import net.ulrice.databinding.modelaccess.impl.DynamicReflectionMVA;
import net.ulrice.databinding.modelaccess.impl.ReflectionMVA;
import net.ulrice.databinding.validation.impl.StringLengthValidator;
import net.ulrice.module.IFModel;

public class MMovieDB implements IFModel {

	private CMovieDB ctrl;
	
	private List<Movie> movieList;

	public MMovieDB(CMovieDB ctrl) {
		this.ctrl = ctrl;
	}

	@Override
	public void initialize() {

		ListAM movieListAM = new ListAM(new ReflectionMVA(this, "movieList"));
		ColumnDefinition<String> nameColumn = new ColumnDefinition<String>(new DynamicReflectionMVA(Movie.class, "name"), String.class);
		nameColumn.setValidator(new StringLengthValidator(1, 255));
		movieListAM.addColumn(nameColumn);
		movieListAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(Movie.class, "director"), String.class));
		movieListAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(Movie.class, "year"), String.class));
		ColumnDefinition<String> actorColumn = new ColumnDefinition<String>(new DynamicReflectionMVA(Movie.class, "actors"), String.class);
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
