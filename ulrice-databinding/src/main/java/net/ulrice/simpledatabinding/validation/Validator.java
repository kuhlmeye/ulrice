package net.ulrice.simpledatabinding.validation;


public interface Validator {
    boolean isValid (Object value);
    String getFailureMessage (Object value);
}
