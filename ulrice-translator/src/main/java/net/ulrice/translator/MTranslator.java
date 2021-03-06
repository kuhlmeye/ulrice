package net.ulrice.translator;

import java.util.List;
import java.util.Locale;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;
import net.ulrice.databinding.modelaccess.impl.DynamicReflectionMVA;
import net.ulrice.databinding.modelaccess.impl.IndexedReflectionMVA;
import net.ulrice.translator.service.DictionaryEntryDTO;
import net.ulrice.translator.service.TranslationDTO;
import net.ulrice.translator.service.UsageDTO;

public class MTranslator {

    private List<TranslationDTO> translations;
    private List<DictionaryEntryDTO> dictionary;
    private List<UsageDTO> usages;

    private TableAM translationsAM;
    private TableAM dictionaryAM;
    private TableAM usagesAM;

    public MTranslator() {
        IFAttributeInfo attributeInfo = new IFAttributeInfo() {
        };

        LocaleToStringConverter localeConverter = new LocaleToStringConverter();

        translationsAM = new TableAM(new IndexedReflectionMVA(this, "translations"), attributeInfo);
        translationsAM.setReadOnly(true);
        translationsAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(String.class, "application"),
            String.class));
        translationsAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(String.class, "module"),
            String.class));
        translationsAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(String.class, "usage"),
            String.class));
        translationsAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(String.class, "attribute"),
            String.class));
        ColumnDefinition<String> localeColumn =
                new ColumnDefinition<String>(new DynamicReflectionMVA(Locale.class, "language"), String.class);
        localeColumn.setValueConverter(localeConverter);
        translationsAM.addColumn(localeColumn);
        translationsAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(String.class, "translation"),
            String.class));

        dictionaryAM = new TableAM(new IndexedReflectionMVA(this, "dictionary"), attributeInfo);
        dictionaryAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(String.class, "application"),
            String.class));
        dictionaryAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(String.class, "module"),
            String.class));
        dictionaryAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(String.class, "usage"),
            String.class));
        dictionaryAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(String.class, "attribute"),
            String.class));
        localeColumn = new ColumnDefinition<String>(new DynamicReflectionMVA(Locale.class, "language"), String.class);
        localeColumn.setValueConverter(localeConverter);
        dictionaryAM.addColumn(localeColumn);

        dictionaryAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(String.class, "translation"),
            String.class));

        usagesAM = new TableAM(new IndexedReflectionMVA(this, "usages"), attributeInfo);
        usagesAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(String.class, "application"),
            String.class));
        usagesAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(String.class, "module"),
            String.class));
        usagesAM
            .addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(String.class, "usage"), String.class));
        usagesAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(String.class, "attribute"),
            String.class));
    }

    public List<TranslationDTO> getTranslations() {
        return translations;
    }

    public void setTranslations(List<TranslationDTO> translations) {
        this.translations = translations;
    }

    public void setDictionary(List<DictionaryEntryDTO> dictionary) {
        this.dictionary = dictionary;
    }

    public List<DictionaryEntryDTO> getDictionary() {
        return dictionary;
    }

    public void setUsages(List<UsageDTO> usages) {
        this.usages = usages;
    }

    public List<UsageDTO> getUsages() {
        return usages;
    }

    public TableAM getTranslationsAM() {
        return translationsAM;
    }

    public TableAM getUsagesAM() {
        return usagesAM;
    }

    public TableAM getDictionaryAM() {
        return dictionaryAM;
    }
}
