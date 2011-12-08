package net.ulrice.translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.process.AbstractProcess;
import net.ulrice.translator.service.DictionaryEntryDTO;
import net.ulrice.translator.service.TranslationDTO;
import net.ulrice.translator.service.UsageDTO;

public class PGenerateTranslations extends AbstractProcess<List<TranslationDTO>, TranslationDTO> {

	private MTranslator model;
	private Locale[] locales;
	private Map<String, DictionaryEntryDTO> dictionaryMap = new HashMap<String, DictionaryEntryDTO>();

	public PGenerateTranslations(IFController owner, String name, MTranslator model, Locale... locales) {
		super(owner);
		setProgressMessage(name);
		this.model = model;
		this.locales = locales;
		model.getDictionaryAM().write();
		model.getTranslationsAM().write();
	}

	@Override
	protected List<TranslationDTO> work() {
		List<TranslationDTO> result = new ArrayList<TranslationDTO>();
		List<DictionaryEntryDTO> dictionary = model.getDictionary();
		List<UsageDTO> usages = model.getUsages();

		int numRows = dictionary.size() + (usages.size() * locales.length);
		int cRows = 0;

		// Fill maps
		for (DictionaryEntryDTO entry : dictionary) {
			String key = concatString(entry.getLanguage(), entry.getApplication(), entry.getModule(), entry.getUsage(),
					entry.getAttribute());
			dictionaryMap.put(key, entry);
			updateProgress((int) (100.0f / (float) numRows * (float) (++cRows)));
		}

		for (UsageDTO usage : usages) {
			for (Locale locale : locales) {
				DictionaryEntryDTO entry = getEntry(usage, locale);
				updateProgress((int) (100.0f / (float) numRows * (float) (++cRows)));

				if (entry == null) {
					entry = getEntry(usage, new Locale("us"));
				}

				if (entry != null) {
					TranslationDTO translation = new TranslationDTO();
					translation.setApplication(usage.getApplication());
					translation.setModule(usage.getModule());
					translation.setUsage(usage.getUsage());
					translation.setAttribute(usage.getAttribute());
					translation.setLanguage(locale);
					translation.setTranslation(entry.getTranslation());
					result.add(translation);
				}
			}
		}

		return result;
	}

	private DictionaryEntryDTO getEntry(UsageDTO usage, Locale locale) {
		String key = concatString(locale, usage.getApplication(), usage.getModule(), usage.getUsage(), usage.getAttribute());
		if (dictionaryMap.containsKey(key)) {
			return dictionaryMap.get(key);
		}

		key = concatString(locale, usage.getApplication(), usage.getUsage(), usage.getAttribute());
		if (dictionaryMap.containsKey(key)) {
			return dictionaryMap.get(key);
		}

		key = concatString(locale, usage.getApplication(), usage.getAttribute());
		if (dictionaryMap.containsKey(key)) {
			return dictionaryMap.get(key);
		}

		key = concatString(locale, usage.getAttribute());
		if (dictionaryMap.containsKey(key)) {
			return dictionaryMap.get(key);
		}

		return null;
	}

	private String concatString(Locale locale, String... keys) {
		StringBuffer result = new StringBuffer();
		for (String key : keys) {
			if (key != null) {
				result.append(key.toLowerCase().trim()).append('.');
			}
		}
		result.append(locale.toString().toLowerCase().trim());
		return result.toString();
	}

	@Override
	protected void finished(List<TranslationDTO> result) {
		model.setTranslations(result);
		model.getTranslationsAM().read();
	}
	
    @Override
    protected void failed(Throwable t) {
        Ulrice.getMessageHandler().handleException(getOwningController(), t);
    }

    @Override
    public boolean hasProgressInformation() {
        return false;
    }
    
    @Override
    public boolean supportsCancel() {
        return true;
    }
    
    @Override
    public void cancelProcess() {
        cancel(true);
    }
}
