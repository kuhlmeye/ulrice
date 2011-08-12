package net.ulrice.message;

import java.util.Locale;


/**
 * This is a <code>TranslationProvider</code> that lives inside a module and knows this module. 
 * 
 * @author arno
 */
public interface ModuleTranslationProvider {
    String getTranslation (TranslationUsage usage, String key, Object... params);
    String getTranslation (Locale locale, TranslationUsage usage, String key, Object... params);
}
