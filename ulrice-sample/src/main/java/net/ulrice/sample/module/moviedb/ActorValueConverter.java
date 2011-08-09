package net.ulrice.sample.module.moviedb;

import java.util.List;

import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.sample.module.moviedb.Movie.Actor;


public class ActorValueConverter implements IFValueConverter {
    @Override
    public Class<?> getViewType(Class<?> modelType) {
        return String.class;
    }
    
    @Override
    public Class<?> getModelType(Class<?> viewType) {
        return List.class;
    }

	@Override
	public Object viewToModel(Object o) {
	    throw new UnsupportedOperationException(); // only for r/o columns
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object modelToView(Object o) {
        List<Actor> actorList = (List<Actor>) o;
		StringBuffer buffer = new StringBuffer();

		if (actorList != null) {
			for (int i = 0; i < actorList.size(); i++) {
				if (i > 0) {
					buffer.append(", ");
				}
				Actor actor = actorList.get(i);
				buffer.append(actor.getFirstname());
				buffer.append(' ');
				buffer.append(actor.getLastname());
			}
		}

		return buffer.toString();
	}

}
