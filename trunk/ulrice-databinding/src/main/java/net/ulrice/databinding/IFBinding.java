package net.ulrice.databinding;

import java.util.List;

public interface IFBinding {

	String getId();
	
	Object getOriginalValue();
	
	Object getCurrentValue();
	
	DataState getState();
	
	List<String> getValidationFailures();
	
	boolean isReadOnly();
}
