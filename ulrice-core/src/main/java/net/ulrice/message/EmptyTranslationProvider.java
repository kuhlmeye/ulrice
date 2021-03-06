package net.ulrice.message;

import java.util.Locale;

public class EmptyTranslationProvider implements TranslationProvider, ModuleTranslationProvider {

    @Override
    public Translation getTranslation(String module, TranslationUsage usage, String key, Object... params) {
    	return getTranslation(usage, key, params);
    }

    @Override
    public Translation getTranslation(Locale locale, String module, TranslationUsage usage, String key, Object... params) {
    	return getTranslation(locale, usage, key, params);
    }

    @Override
    public boolean isTranslationAvailable(String module, TranslationUsage usage, String key) {
        return false;
    }

    @Override
    public boolean isTranslationAvailable(Locale locale, String module, TranslationUsage usage, String key) {
        return false;
    }

    @Override
    public String getTranslationText(String module, TranslationUsage usage, String key, Object... params) {
        return getTranslation(module, usage, key, params).getText();
    }

    @Override
    public String getTranslationText(Locale locale, String module, TranslationUsage usage, String key, Object... params) {
        return getTranslation(locale, module, usage, key, params).getText();
    }

    @Override
    public Translation getUlriceTranslation(TranslationUsage usage, String key, Object... params) {
        return getTranslation(ULRICE_COMPONENT, usage, key, params);
    }

	@Override
	public Translation getTranslation(TranslationUsage usage, String key, Object... params) {
        return new Translation(key, true, Locale.getDefault(), String.format(key, params), 0);
	}

	@Override
	public Translation getTranslation(Locale locale, TranslationUsage usage, String key, Object... params) {
        return new Translation(key, true, locale, String.format(key, params), 0);
	}

	@Override
	public String getTranslationText(TranslationUsage usage, String key, Object... params) {
		return getTranslation(usage, key, params).getText();
	}

	@Override
	public String getTranslationText(Locale locale, TranslationUsage usage, String key, Object... params) {
		return getTranslation(locale, usage, key, params).getText();
	}

	@Override
	public boolean isTranslationAvailable(TranslationUsage usage, String key) {
		return false;
	}

	@Override
	public boolean isTranslationAvailable(Locale locale, TranslationUsage usage, String key) {
		return false;
	}
}
