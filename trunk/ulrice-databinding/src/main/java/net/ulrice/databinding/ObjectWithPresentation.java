package net.ulrice.databinding;


public class ObjectWithPresentation {
	private final Object value;
	private final String presentation;
	
	public ObjectWithPresentation(Object value, String presentation) {
		this.value = value;
		this.presentation = presentation;
	}

	public Object getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return presentation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObjectWithPresentation other = (ObjectWithPresentation) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
