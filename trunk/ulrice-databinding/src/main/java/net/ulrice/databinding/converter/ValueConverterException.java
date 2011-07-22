package net.ulrice.databinding.converter;


public class ValueConverterException extends RuntimeException {

	public ValueConverterException(NumberFormatException ex) {
		super(ex);
	}
	
	public ValueConverterException() {
		super();
	}
	
	
}
