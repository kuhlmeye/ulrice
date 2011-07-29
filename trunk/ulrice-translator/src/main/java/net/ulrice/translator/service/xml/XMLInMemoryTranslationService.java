package net.ulrice.translator.service.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ulrice.translator.service.DictionaryEntryDTO;
import net.ulrice.translator.service.IFTranslationService;
import net.ulrice.translator.service.TranslationDTO;
import net.ulrice.translator.service.UsageDTO;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XMLInMemoryTranslationService implements IFTranslationService {

	private Map<String, DictionaryEntryDTO> dictionary = new HashMap<String, DictionaryEntryDTO>();
	private List<UsageDTO> usages = new ArrayList<UsageDTO>();
	private List<TranslationDTO> translations = new ArrayList<TranslationDTO>();
	

	@Override
	public void createDictionaryEntry(DictionaryEntryDTO entry) {
		dictionary.put(buildDictionaryKey(entry), entry);
	}


	@Override
	public void saveDictionaryEntry(DictionaryEntryDTO entry) {
		dictionary.put(buildDictionaryKey(entry), entry);
	}

	@Override
	public void deleteDictionaryEntry(DictionaryEntryDTO entry) {
		dictionary.remove(buildDictionaryKey(entry));
	}

	@Override
	public List<DictionaryEntryDTO> findAllDictionaryEntries() {
		return new ArrayList<DictionaryEntryDTO>(dictionary.values());
	}


	private String buildDictionaryKey(DictionaryEntryDTO entry) {
		StringBuffer key = new StringBuffer();
		key.append(entry.getApplication()).append('.');
		key.append(entry.getModule()).append('.');
		key.append(entry.getUsage()).append('.');
		key.append(entry.getAttribute()).append('.');
		return key.toString();
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
		
		try {
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(new DictionaryXMLHandler(this));
			xmlReader.parse(new InputSource(new FileInputStream("dictionary.xml")));
			
			xmlReader.setContentHandler(new UsagesXMLHandler(this));
			xmlReader.parse(new InputSource(new FileInputStream("usage.xml")));
			
			xmlReader.setContentHandler(new TranslationsXMLHandler(this));
			xmlReader.parse(new InputSource(new FileInputStream("translations.xml")));
			
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void closeTranslationService() {
		if(dictionary != null) {
			try {
				PrintWriter pw = new PrintWriter(new File("dictionary.xml"));
				pw.println("<dictionary>");
				for(DictionaryEntryDTO entry : dictionary.values()) {
					pw.println("\t<dictEntry>");
					pw.println("\t\t<application>" + entry.getApplication() + "</application>");
					pw.println("\t\t<module>" + entry.getModule() + "</module>");
					pw.println("\t\t<usage>" + entry.getUsage() + "</usage>");
					pw.println("\t\t<attribute>" + entry.getAttribute() + "</attribute>");
					pw.println("\t\t<language>" + entry.getLanguage().getLanguage() + "</language>");
					pw.println("\t\t<translation>" + entry.getTranslation() + "</translation>");
					pw.println("\t</dictEntry>");
				}
				pw.println("</dictionary>");
				pw.flush();
				pw.close();
				
				pw = new PrintWriter(new File("usages.xml"));
				pw.println("<usages>");
				for(UsageDTO entry : usages) {
					pw.println("\t<usage>");
					pw.println("\t\t<application>" + entry.getApplication() + "</application>");
					pw.println("\t\t<module>" + entry.getModule() + "</module>");
					pw.println("\t\t<usage>" + entry.getUsage() + "</usage>");
					pw.println("\t\t<attribute>" + entry.getAttribute() + "</attribute>");
					pw.println("\t</usage>");
				}
				pw.println("</usages>");
				pw.flush();
				pw.close();
				
				pw = new PrintWriter(new File("translations.xml"));
				pw.println("<translations>");
				if(translations != null) {
					for(TranslationDTO entry : translations) {
						pw.println("\t<translation>");
						pw.println("\t\t<application>" + entry.getApplication() + "</application>");
						pw.println("\t\t<module>" + entry.getModule() + "</module>");
						pw.println("\t\t<usage>" + entry.getUsage() + "</usage>");
						pw.println("\t\t<attribute>" + entry.getAttribute() + "</attribute>");
						pw.println("\t\t<language>" + entry.getLanguage().getLanguage() + "</language>");
						pw.println("\t\t<translation>" + entry.getTranslation() + "</translation>");
						pw.println("\t</translation>");
					}
				}
				pw.println("</translations>");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
