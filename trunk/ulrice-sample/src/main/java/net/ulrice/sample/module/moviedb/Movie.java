package net.ulrice.sample.module.moviedb;

import java.util.List;

public class Movie {

	private String name;
	private String director;
	private Integer year;
	
	private List<Actor> actors;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public List<Actor> getActors() {
		return actors;
	}

	public void setActors(List<Actor> actors) {
		this.actors = actors;
	}
	
	
	public static class Actor {
		private String lastname;
		private String firstname;
		
		public Actor() {
			
		}
		
		public Actor(String firstname, String lastname) {
			this.firstname = firstname;
			this.lastname = lastname;
		}
		public String getLastname() {
			return lastname;
		}
		public void setLastname(String lastname) {
			this.lastname = lastname;
		}
		public String getFirstname() {
			return firstname;
		}
		public void setFirstname(String firstname) {
			this.firstname = firstname;
		}
	}
}
