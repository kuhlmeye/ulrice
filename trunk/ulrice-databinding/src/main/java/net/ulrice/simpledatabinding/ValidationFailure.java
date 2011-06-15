package net.ulrice.simpledatabinding;


class ValidationFailure {
    private final Binding _binding;
    private final String _message;

    public ValidationFailure (Binding binding, String message) {
        _binding = binding;
        _message = message;
    }

    public Binding getBinding () {
        return _binding;
    }
    
    public String getMessage () {
        return _message;
    }

    @Override
    public String toString () {
        return "ValidationFailure [_binding=" + _binding + ", _message=" + _message + "]";
    }   
}
