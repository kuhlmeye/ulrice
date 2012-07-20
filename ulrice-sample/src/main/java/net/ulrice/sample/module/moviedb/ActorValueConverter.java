package net.ulrice.sample.module.moviedb;

import java.util.List;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.sample.module.moviedb.Movie.Actor;


@SuppressWarnings("rawtypes")
public class ActorValueConverter implements IFValueConverter <List, String> {
    @Override
    public Class<String> getViewType(Class<? extends List> modelType) {
        return String.class;
    }
    
    @Override
    public Class<List> getModelType(Class<? extends String> viewType) {
        return List.class;
    }

	@Override
	public List<?> viewToModel(String o, IFAttributeInfo attributeInfo) {
	    throw new UnsupportedOperationException(); // only for r/o columns
	}

	@SuppressWarnings("unchecked")
    @Override
	public String modelToView(List o, IFAttributeInfo attributeInfo) {
        List<Actor> actorList = o;
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

	@Override
	public boolean canHandle(Class<? extends Object> modelType,
			Class<? extends Object> viewType) {
		if (String.class.equals(viewType) && List.class.equals(modelType)) {
			return true;
		}
		return false;
	}

}
