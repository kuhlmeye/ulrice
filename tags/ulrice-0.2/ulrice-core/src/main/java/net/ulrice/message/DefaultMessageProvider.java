/**
 * 
 */
package net.ulrice.message;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.ulrice.module.exception.NoSuchMessageException;

/**
 * TODO: Implement the local ResourceBundle reading and filling
 * @author dritonshoshi
 */
public class DefaultMessageProvider implements I18NMessageProvider {
    
private String bundlePath;
    
    private Locale locale;


    public String getString(String key) {
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(bundlePath, locale,
                DefaultMessageProvider.class.getClassLoader());
            return resourceBundle.getString(key);
        }
        catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    public DefaultMessageProvider(String bundleName, Locale locale) {
        this.bundlePath = bundleName;
        this.locale = locale;
    }
    
    @Override
    public String getMessage(String code) throws NoSuchMessageException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMessage(String code, Locale locale) throws NoSuchMessageException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        // TODO Auto-generated method stub
        return null;
    }

}
