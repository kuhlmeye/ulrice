package net.ulrice.databinding.converter.impl;

import java.util.Currency;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.converter.IFValueConverter;

public class StringToCurrencyConverter implements IFValueConverter<Currency, String> {

    private static final StringToCurrencyConverter instance = new StringToCurrencyConverter();

    public static StringToCurrencyConverter getInstance() {
        return instance;
    }

    private StringToCurrencyConverter() {
        super();
    }

    @Override
    public Class< ? extends String> getViewType(Class< ? extends Currency> modelType) {
        return String.class;
    }

    @Override
    public Class< ? extends Currency> getModelType(Class< ? extends String> viewType) {
        return Currency.class;
    }

    @Override
    public boolean canHandle(Class< ? extends Object> modelType, Class< ? extends Object> viewType) {
        return (Currency.class.equals(modelType) && String.class.equals(viewType));
    }

    @Override
    public Currency viewToModel(String currencyCode, IFAttributeInfo attributeInfo) {
        return Currency.getInstance(currencyCode);
    }

    @Override
    public String modelToView(Currency ccy, IFAttributeInfo attributeInfo) {
        return ccy.getCurrencyCode();
    }

}
