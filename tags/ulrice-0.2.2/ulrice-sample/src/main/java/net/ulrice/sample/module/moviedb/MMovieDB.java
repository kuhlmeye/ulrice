package net.ulrice.sample.module.moviedb;

import java.util.List;

import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;
import net.ulrice.databinding.bufferedbinding.impl.GenericAM;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;
import net.ulrice.databinding.modelaccess.impl.DynamicReflectionMVA;
import net.ulrice.databinding.modelaccess.impl.IndexedReflectionMVA;
import net.ulrice.databinding.modelaccess.impl.ReflectionMVA;
import net.ulrice.databinding.validation.impl.StringLengthValidator;
import net.ulrice.module.IFModel;
import net.ulrice.sample.module.moviedb.Movie.Actor;

public class MMovieDB implements IFModel<CMovieDB> {
	
	private List<Movie> movieList;
	private Movie movie;

	private TableAM movieListAM;
	private GenericAM<?> titleAM;
	private GenericAM<?> yearAM;
	private GenericAM<?> directorAM;
	private TableAM actorListAM;

	@Override
	public void initialize(CMovieDB controller) {

		movieListAM = new TableAM(new IndexedReflectionMVA(this, "movieList"));
		ColumnDefinition<String> nameColumn = new ColumnDefinition<String>(new DynamicReflectionMVA(Movie.class, "name"), String.class);
		nameColumn.setValidator(new StringLengthValidator(1, 255));
		movieListAM.addColumn(nameColumn);		
		movieListAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(Movie.class, "director"), String.class));		
		movieListAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(Movie.class, "year"), String.class));		
		ColumnDefinition<String> actorColumn = new ColumnDefinition<String>(new DynamicReflectionMVA(Movie.class, "actors"), String.class);
		actorColumn.setValueConverter(new ActorValueConverter());
		actorColumn.setReadOnly(true);
		movieListAM.addColumn(actorColumn);		
		
		titleAM = new GenericAM<String>(new ReflectionMVA(this, "movie.name"));
		yearAM = new GenericAM<Integer>(new ReflectionMVA(this, "movie.year"));
		directorAM = new GenericAM<String>(new ReflectionMVA(this, "movie.director"));				
		
		actorListAM = new TableAM(new IndexedReflectionMVA(this, "movie.actors"));
		actorListAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(Actor.class, "lastname"), String.class));		
		actorListAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(Actor.class, "firstname"), String.class));				
	}

	public List<Movie> getMovieList() {
		return movieList;
	}

	public void setMovieList(List<Movie> movieList) {
		this.movieList = movieList;
	}
	
	public Movie getMovie() {
		return movie;
	}
	public void setMovie(Movie movie) {
		this.movie = movie;
	}
	
	public TableAM getMovieListAM() {
		return movieListAM;
	}
	
	public GenericAM<?> getTitleAM() {
		return titleAM;
	}
	
	public GenericAM<?> getYearAM() {
		return yearAM;
	}
	
	public GenericAM<?> getDirectorAM() {
		return directorAM;
	}
	
	public TableAM getActorListAM() {
		return actorListAM;
	}
}
