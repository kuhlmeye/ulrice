package net.ulrice.message;

import java.util.Locale;


public interface TranslationProvider {
    String getTranslation (String module, TranslationUsage usage, String key, Object... params);
    String getTranslation (Locale locale, String module, TranslationUsage usage, String key, Object... params);
}
