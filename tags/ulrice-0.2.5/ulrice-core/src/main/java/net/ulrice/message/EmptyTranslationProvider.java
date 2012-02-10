package net.ulrice.message;

import java.util.Locale;

public class EmptyTranslationProvider implements TranslationProvider {

    @Override
    public String getTranslation(String module, TranslationUsage usage, String key, Object... params) {
        return String.format(key, params);
    }

    @Override
    public String getTranslation(Locale locale, String module, TranslationUsage usage, String key, Object... params) {
        return String.format(key, params);
    }

    @Override
    public boolean isTranslationAvailable(String module, TranslationUsage usage, String key) {
        return false;
    }

    @Override
    public boolean isTranslationAvailable(Locale locale, String module, TranslationUsage usage, String key) {
        return false;
    }
}
