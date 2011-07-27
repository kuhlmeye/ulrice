package net.ulrice.translator.service;

import java.util.List;

public interface IFTranslationService {

	void createDictionaryEntry(DictionaryEntryDTO entry);
	
	void saveDictionaryEntry(DictionaryEntryDTO entry);
	
	void deleteDictionaryEntry(DictionaryEntryDTO entry);
	
	List<DictionaryEntryDTO> findAllDictionaryEntries();
	
	
	void createUsage(UsageDTO usage);
	
	void saveUsage(UsageDTO usage);
	
	void deleteUsage(UsageDTO usage);
	
	List<UsageDTO> findAllUsages();
	
	
	void createTranslation(TranslationDTO translation);
	
	void saveTranslation(TranslationDTO translation);
	
	void deleteTranslation(TranslationDTO translation);
	
	List<TranslationDTO> findAllTranslations();
	
	void openTranslationService();
	
	void closeTranslationService();
}
