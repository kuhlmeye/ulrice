package net.ulrice.module.exception;

import java.util.Locale;

/**
 * Throws an exception when a message could not be be found.
 * 
 * @author dritonshoshi
 */
public class NoSuchMessageException extends RuntimeException {

    /** Generated serialVersionUID */
    private static final long serialVersionUID = 7548343350459952888L;

    /**
     * Create a new exception.
     * 
     * @param code code that could not be resolved for given locale
     * @param locale locale that was used to search for the code within
     */
    public NoSuchMessageException(String code, Locale locale) {
        super("No message found under code '" + code + "' for locale '" + locale + "'.");
    }

    /**
     * Create a new exception.
     * 
     * @param code code that could not be resolved for given locale
     */
    public NoSuchMessageException(String code) {
        super("No message found under code '" + code + "' for locale '" + Locale.getDefault() + "'.");
    }
}
