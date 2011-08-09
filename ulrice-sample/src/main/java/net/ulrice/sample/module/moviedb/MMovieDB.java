package net.ulrice.sample.module.moviedb;

import java.util.List;

import net.ulrice.databinding.bufferedbinding.TableAMBuilder;
import net.ulrice.databinding.bufferedbinding.impl.GenericAM;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;
import net.ulrice.databinding.modelaccess.impl.ReflectionMVA;
import net.ulrice.databinding.validation.impl.StringLengthValidator;
import net.ulrice.sample.module.moviedb.Movie.Actor;

public class MMovieDB {
	
	private List<Movie> movieList;
	private Movie movie;

	private final TableAM movieListAM;
	private GenericAM<?> titleAM;
	private GenericAM<?> yearAM;
	private GenericAM<?> directorAM;
	private final TableAM actorListAM;

	public MMovieDB() {
        final TableAMBuilder movieListBuilder = new TableAMBuilder(this, "movieList", Movie.class)
            .addColumn("name")
            .addColumn("director")
            .addColumn("year")
            .addColumn("actors", new ActorValueConverter()); 

        movieListBuilder.getColumn("name").setValidator(new StringLengthValidator(1, 255));
        movieListBuilder.getColumn("actors").setReadOnly(true);

        movieListAM = movieListBuilder.build();

        titleAM = new GenericAM<String>(new ReflectionMVA(this, "movie.name"));
		yearAM = new GenericAM<Integer>(new ReflectionMVA(this, "movie.year"));
		directorAM = new GenericAM<String>(new ReflectionMVA(this, "movie.director"));				

		actorListAM = new TableAMBuilder(this, "movie.actors", Actor.class)
		    .addColumn("lastname")
		    .addColumn("firstname")
		    .build();
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
