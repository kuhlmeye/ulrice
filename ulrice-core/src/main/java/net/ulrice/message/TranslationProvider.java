package net.ulrice.message;

import java.util.Locale;


public interface TranslationProvider {
    String getTranslation (String component, TranslationUsage usage, String key, Object... params);
    String getTranslation (Locale locale, String component, TranslationUsage usage, String key, Object... params);
}
