package net.ulrice.simpledatabinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class ValidationResult {
    private final List<ValidationFailure> _failures = new ArrayList<ValidationFailure> ();
    private final Map <Binding, List<String>> _byBinding = new HashMap<Binding, List<String>> ();

    public void addFailure (Binding b, String message) {
        _failures.add (new ValidationFailure (b, message));

        if (! _byBinding.containsKey (b))
            _byBinding.put (b, new ArrayList<String> ());
        _byBinding.get (b).add (message);
    }
    
    public boolean isValid () {
        return _failures.isEmpty ();
    }
    
    public List<ValidationFailure> getFailures () {
        return _failures;
    }

    public List<String> getFailuresByBinding (Binding b) {
        return _byBinding.get (b);
    }
}
