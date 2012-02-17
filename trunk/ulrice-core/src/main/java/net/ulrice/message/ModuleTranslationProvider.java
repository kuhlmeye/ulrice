package net.ulrice.message;

import java.util.Locale;


/**
 * This is a <code>TranslationProvider</code> that lives inside a module and knows this module. 
 * 
 * @author arno
 */
public interface ModuleTranslationProvider {
    Translation getTranslation(TranslationUsage usage, String key, Object... params);
    Translation getTranslation(Locale locale, TranslationUsage usage, String key, Object... params);
    String getTranslationText(TranslationUsage usage, String key, Object... params);
    String getTranslationText(Locale locale, TranslationUsage usage, String key, Object... params);
    boolean isTranslationAvailable(TranslationUsage usage, String key);
    boolean isTranslationAvailable(Locale locale, TranslationUsage usage, String key);
}
