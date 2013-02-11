package net.ulrice.message;

import java.util.Locale;

/**
 * Represents a translation result.
 * 
 * @author DL10KUH
 */
public class Translation {

    /**
     * The translation key.
     */
    private String key;
    
    /**
     * The translated text
     */
    private String text;
    
    /**
     * The locale of the translated text.
     */
    private Locale locale;
    
    /**
     * The mnemonic for buttons, if available;
     */
    private int mnemonic;
    
    /**
     * Flag, if the translation is available
     */
    private boolean available;
    
    public Translation(String key, boolean available, Locale locale, String text, int mnemonic) {
        this.key = key;
        this.available = available;
        this.locale = locale;
        this.text = text;
        this.mnemonic = mnemonic;
    }
    
    public String getKey() {
        return key;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public String getText() {
        return text;
    }
    
    public Locale getLocale() {
        return locale;
    }
    
    public int getMnemonic() {
        return mnemonic;
    }
    
    @Override
    public String toString() {
        return isAvailable() ? getText() : "!" + getKey() + "!";
    }
}
