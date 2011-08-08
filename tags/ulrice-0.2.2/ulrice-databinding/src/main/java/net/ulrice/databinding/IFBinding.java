package net.ulrice.databinding;

import java.util.List;

public interface IFBinding {

	String getId();
	
	Object getOriginalValue();
	
	Object getCurrentValue();
	
	boolean isDirty();
	boolean isValid();
	
	List<String> getValidationFailures();
	
	boolean isReadOnly();
}
