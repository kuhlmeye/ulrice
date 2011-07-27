package net.ulrice.translator.servicexml;

import java.util.ArrayList;
import java.util.List;

import net.ulrice.translator.service.DictionaryEntryDTO;
import net.ulrice.translator.service.IFTranslationService;
import net.ulrice.translator.service.TranslationDTO;
import net.ulrice.translator.service.UsageDTO;

public class XMLInMemoryTranslationService implements IFTranslationService {

	private List<DictionaryEntryDTO> dictionary = new ArrayList<DictionaryEntryDTO>();
	private List<UsageDTO> usages = new ArrayList<UsageDTO>();
	private List<TranslationDTO> translations = new ArrayList<TranslationDTO>();
	

	
	@Override
	public void createDictionaryEntry(DictionaryEntryDTO entry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveDictionaryEntry(DictionaryEntryDTO entry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteDictionaryEntry(DictionaryEntryDTO entry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<DictionaryEntryDTO> findAllDictionaryEntries() {
		return dictionary;
	}

	@Override
	public void createUsage(UsageDTO usage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveUsage(UsageDTO usage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteUsage(UsageDTO usage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<UsageDTO> findAllUsages() {
		return usages;
	}

	@Override
	public void createTranslation(TranslationDTO translation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveTranslation(TranslationDTO translation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteTranslation(TranslationDTO translation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<TranslationDTO> findAllTranslations() {
		return translations;
	}

	@Override
	public void openTranslationService() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void closeTranslationService() {
		// TODO Auto-generated method stub
		
	}
}
