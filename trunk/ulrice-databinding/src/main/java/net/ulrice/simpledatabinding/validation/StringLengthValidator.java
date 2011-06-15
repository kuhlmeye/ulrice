package net.ulrice.simpledatabinding.validation;


public class StringLengthValidator implements Validator {
    private final int _minLength;
    private final int _maxLength;
    
    public StringLengthValidator (int minLength, int maxLength) {
        _minLength = minLength;
        _maxLength = maxLength;
    }

    public boolean isValid (Object value) {
        final String s = (String) value;
        return s.length () >= _minLength && s.length () <= _maxLength;
    }

    public String getFailureMessage (Object value) {
        final String s = (String) value;
        if (s.length () < _minLength)
            return "min. Länge: " + _minLength;
        if (s.length () > _maxLength)
            return "max. Länge: " + _minLength;
        
        return null;
    }
}
