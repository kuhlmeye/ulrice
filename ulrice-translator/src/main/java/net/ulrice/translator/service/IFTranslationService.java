package net.ulrice.translator.service;

import java.util.List;

public interface IFTranslationService {

	void createDictionaryEntry(DictionaryEntryDTO entry);
	
	void saveDictionaryEntry(DictionaryEntryDTO entry);
	
	void deleteDictionaryEntry(DictionaryEntryDTO entry);
	
	List<DictionaryEntryDTO> getDictionaryEntries(int start, int size);
	
	int getNumDictionaryEntries();
	
	int getDictionaryEntriesChunkSize();
	
	
	void createUsage(UsageDTO usage);
	
	void saveUsage(UsageDTO usage);
	
	void deleteUsage(UsageDTO usage);
	
	List<UsageDTO> getUsages(int start, int size);
	
	int getNumUsages();
	
	int getUsagesChunkSize();
	
	
	void createTranslation(TranslationDTO translation);
	
	void saveTranslation(TranslationDTO translation);
	
	void deleteTranslation(TranslationDTO translation);
	
	List<TranslationDTO> getTranslations(int start, int size);
	
	int getNumTranslations();
	
	int getTranslationsChunkSize();
	
	void openTranslationService();
	
	void closeTranslationService();
}
