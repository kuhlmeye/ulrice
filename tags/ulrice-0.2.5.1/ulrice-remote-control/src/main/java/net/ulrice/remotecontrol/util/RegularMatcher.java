package net.ulrice.remotecontrol.util;

import java.io.Serializable;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegularMatcher implements Serializable {

    private static final long serialVersionUID = 2985303399762300662L;
    
    private final String regex;
    private final Pattern pattern;

    public RegularMatcher(String regex) {
        super();

        this.regex = regex;

        Pattern pattern;

        try {
            pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        }
        catch (PatternSyntaxException e) {
            pattern = null;
        }

        this.pattern = pattern;
    }

    public boolean matches(String value) {

        if ((pattern != null) && (pattern.matcher(value).matches())) {
            return true;
        }

        return regex.equalsIgnoreCase(value);
    }

}
