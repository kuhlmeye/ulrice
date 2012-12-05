package net.ulrice.sample.module.moviedb;

import java.util.ArrayList;
import java.util.List;

import net.ulrice.sample.module.moviedb.Movie.Actor;

public class MovieData {
	
	protected static List<Movie> generateData() {
		Movie dieHard1 = new Movie();
		dieHard1.setName("Die Hard");
		dieHard1.setYear(1988);
		dieHard1.setDirector("John McTiernan");
		dieHard1.setActors(new ArrayList<Actor>());
		dieHard1.getActors().add(new Actor("Bruce", "Willis"));
		dieHard1.getActors().add(new Actor("Alan", "Rickman"));
		
		Movie dieHard2 = new Movie();
		dieHard2.setName("Die Hard 2");
		dieHard2.setYear(1990);
		dieHard2.setDirector("Renny Harlin");
		dieHard2.setActors(new ArrayList<Actor>());
		dieHard2.getActors().add(new Actor("Bruce", "Willis"));
		dieHard2.getActors().add(new Actor("William", "Atherton"));
		
		Movie dieHard3 = new Movie();
		dieHard3.setName("Die Hard: With a Vengeance");
		dieHard3.setYear(1995);
		dieHard3.setDirector("John McTiernan");
		dieHard3.setActors(new ArrayList<Actor>());
		dieHard3.getActors().add(new Actor("Bruce", "Willis"));
		dieHard3.getActors().add(new Actor("Jeremy", "Irons"));
		dieHard3.getActors().add(new Actor("Samuel L.", "Jackson"));

		Movie greenHornet = new Movie();
		greenHornet.setName("The Green Hornet");
		greenHornet.setYear(2011);
		greenHornet.setDirector("Michel Gondry");
		greenHornet.setActors(new ArrayList<Actor>());
		greenHornet.getActors().add(new Actor("Seth", "Rogen"));
		greenHornet.getActors().add(new Actor("Jay", "Chou"));
		greenHornet.getActors().add(new Actor("Christoph", "Waltz"));
		
		Movie inception = new Movie();
		inception.setName("Inception");
		inception.setYear(2010);
		inception.setDirector("Christopher Nolan");
		inception.setActors(new ArrayList<Actor>());
		inception.getActors().add(new Actor("Leonardo", "DiCaprio"));
		inception.getActors().add(new Actor("Joseph", "Gordon-Lewitt"));
		
		List<Movie> movieList = new ArrayList<Movie>();
		movieList.add(dieHard1);
		movieList.add(dieHard2);
		movieList.add(dieHard3);
		movieList.add(greenHornet);
		movieList.add(inception);
		
		return movieList;
	}
}