package net.ulrice.sample.module.moviedb;

import java.util.List;

import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.sample.module.moviedb.Movie.Actor;

public class ActorValueConverter implements IFValueConverter {

	@Override
	public Object viewToModel(Object o) {
		// Don't need this. It is a readonly column
		return null;
	}

	@Override
	public Object modelToView(Object o) {

		List<Actor> actorList = (List<Actor>)o;
		StringBuffer buffer = new StringBuffer();
		
		for(int i = 0; i < actorList.size(); i++) {
			if(i > 0) {
				buffer.append(", ");
			}
			Actor actor = actorList.get(i);
			buffer.append(actor.getFirstname());
			buffer.append(' ');
			buffer.append(actor.getLastname());
		}

		return buffer.toString();
	}

}
