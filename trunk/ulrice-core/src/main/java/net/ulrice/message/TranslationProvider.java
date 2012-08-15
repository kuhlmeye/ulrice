package net.ulrice.message;

import java.util.Locale;


public interface TranslationProvider {
    
    final String ULRICE_COMPONENT = "ULRICE";

    Translation getUlriceTranslation(TranslationUsage usage, String key, Object... params);
    
    Translation getTranslation(String module, TranslationUsage usage, String key, Object... params);
    Translation getTranslation(Locale locale, String module, TranslationUsage usage, String key, Object... params);
    
    String getTranslationText(String module, TranslationUsage usage, String key, Object... params);
    String getTranslationText(Locale locale, String module, TranslationUsage usage, String key, Object... params);

    boolean isTranslationAvailable(String module, TranslationUsage usage, String key);
    boolean isTranslationAvailable(Locale locale, String module, TranslationUsage usage, String key);
}
