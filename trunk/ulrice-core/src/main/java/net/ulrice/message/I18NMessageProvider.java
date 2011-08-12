package net.ulrice.message;

import java.util.Locale;

import net.ulrice.module.exception.NoSuchMessageException;

/**
 * Interface for resolving messages which supports parameterization
 * and internationalization of messages.
 * 
 * @author dritonshoshi
 */
@Deprecated
public interface I18NMessageProvider {

    /**
     * Retrieve the message for the given code and the default Locale.
     * @param code code of the message
     * @return the message
     * @throws NoSuchMessageException - when a message could not be be found.
     */
    public String getMessage(String code) throws NoSuchMessageException;
    /**
     * Retrieve the message for the given code and the given Locale.
     * @param code - message code
     * @param locale - Locale of the messages
     * @return the message as String.
     * @throws NoSuchMessageException - when a message could not be be found.
     */
    public String getMessage(String code, Locale locale) throws NoSuchMessageException;
    
    /**
     * Retrieve the message for the given code and the given Locale.
     * @param code code of the message
     * @param args arguments for the message, or <code>null</code> if none
     * @param locale Locale in which to do lookup
     * @return the message
     * @throws NoSuchMessageException - when a message could not be be found.
     */
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException;
}
