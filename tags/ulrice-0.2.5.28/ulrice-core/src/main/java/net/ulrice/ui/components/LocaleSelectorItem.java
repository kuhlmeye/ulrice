package net.ulrice.ui.components;

import java.util.Locale;

import javax.swing.Icon;

public class LocaleSelectorItem {

	private Locale locale;
	private String text;
	private Icon icon;

	public LocaleSelectorItem(Locale locale) {
		this(locale, locale.toString(), null);
	}
	
	public LocaleSelectorItem(Locale locale, String text) {
		this(locale, text, null);
	}
	
	public LocaleSelectorItem(Locale locale, String text, Icon icon) {
		this.locale = locale;
		this.text = text;
		this.icon = icon;
	}
	
	public Locale getLocale() {
		return locale;
	}
	
	public String getText() {
		return text;
	}
	
	public Icon getIcon() {
		return icon;
	}
}
