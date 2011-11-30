package net.ulrice.translator.service.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
	private Map<String, UsageDTO> usages = new HashMap<String, UsageDTO>();
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
	public List<DictionaryEntryDTO> getDictionaryEntries(int start, int length) {
		return new ArrayList<DictionaryEntryDTO>(dictionary.values());
	}
	
	@Override
	public int getNumDictionaryEntries() {
	    return dictionary.size();
	}
	
    @Override
	public int getDictionaryEntriesChunkSize() {
	    return 0;
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
		usages.put(buildUsageKey(usage), usage);
	}

	@Override
	public void saveUsage(UsageDTO usage) {
		usages.put(buildUsageKey(usage), usage);
	}

	@Override
	public void deleteUsage(UsageDTO usage) {
		usages.remove(buildUsageKey(usage));
	}


	private String buildUsageKey(UsageDTO entry) {
		StringBuffer key = new StringBuffer();
		key.append(entry.getApplication()).append('.');
		key.append(entry.getModule()).append('.');
		key.append(entry.getUsage()).append('.');
		key.append(entry.getAttribute()).append('.');
		return key.toString();
	}
	
	@Override
	public List<UsageDTO> getUsages(int start, int length) {
		return new ArrayList<UsageDTO>(usages.values());
	}
	
	public int getNumUsages() {
	    return usages.size();
	}
	
	public int getUsagesChunkSize() {
	    return 0;
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
	public List<TranslationDTO> getTranslations(int start, int length) {
		return translations;
	}
	
	public int getNumTranslations() {
	    return translations.size();
	}
	
	public int getTranslationsChunkSize() {
	    return 0;
	}

	@Override
	public void openTranslationService() {
		
		try {			
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(new DictionaryXMLHandler(this));
			xmlReader.parse(new InputSource(new FileInputStream("dictionary.xml")));
			
			xmlReader.setContentHandler(new UsagesXMLHandler(this));
			xmlReader.parse(new InputSource(new FileInputStream("usages.xml")));
			
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
				FileOutputStream fos = new FileOutputStream("dictionary.xml");
				PrintStream ps = new PrintStream(fos, true, "UTF-8");
				ps.println("<dictionary>");
				for(DictionaryEntryDTO entry : dictionary.values()) {
					ps.println("\t<dictEntry>");
					if(entry.getApplication() != null) {
						ps.println("\t\t<application>" + fix(entry.getApplication()) + "</application>");
					}
					if(entry.getModule() != null) {
						ps.println("\t\t<module>" + fix(entry.getModule()) + "</module>");
					}
					if(entry.getUsage() != null) {
						ps.println("\t\t<usage>" + fix(entry.getUsage()) + "</usage>");
					}
					ps.println("\t\t<attribute>" + fix(entry.getAttribute()) + "</attribute>");
					ps.println("\t\t<language>" + fix(entry.getLanguage().toString()) + "</language>");
					ps.println("\t\t<translation>" + fix(entry.getTranslation()) + "</translation>");
					ps.println("\t</dictEntry>");
				}
				ps.println("</dictionary>");
				ps.flush();
				ps.close();
				
				fos = new FileOutputStream("usages.xml");
				ps = new PrintStream(fos, true, "UTF-8");
				ps.println("<usages>");
				for(UsageDTO entry : usages.values()) {
					ps.println("\t<UsageEntry>");
					ps.println("\t\t<application>" + fix(entry.getApplication()) + "</application>");
					ps.println("\t\t<module>" + fix(entry.getModule()) + "</module>");
					ps.println("\t\t<usage>" + fix(entry.getUsage()) + "</usage>");
					ps.println("\t\t<attribute>" + fix(entry.getAttribute()) + "</attribute>");
					ps.println("\t</UsageEntry>");
				}
				ps.println("</usages>");
				ps.flush();
				ps.close();
				
				fos = new FileOutputStream("translations.xml");
				ps = new PrintStream(fos, true, "UTF-8");
				ps.println("<translations>");
				if(translations != null) {
					for(TranslationDTO entry : translations) {
						ps.println("\t<translation>");
						ps.println("\t\t<application>" + fix(entry.getApplication()) + "</application>");
						ps.println("\t\t<module>" + fix(entry.getModule()) + "</module>");
						ps.println("\t\t<usage>" + fix(entry.getUsage()) + "</usage>");
						ps.println("\t\t<attribute>" + fix(entry.getAttribute()) + "</attribute>");
						ps.println("\t\t<language>" + fix(entry.getLanguage().toString()) + "</language>");
						ps.println("\t\t<translation>" + fix(entry.getTranslation()) + "</translation>");
						ps.println("\t</translation>");
					}
				}
				ps.println("</translations>");
				ps.flush();
				ps.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	private String fix(String value) {
		String result = value;;

	    result = result.replace("&", "&amp;");
	    result = result.replace("#", "&#035;");
	    result = result.replace("<", "&lt;");
	    result = result.replace(">", "&gt;");
	    result = result.replace("\"", "&quot;");
	    result = result.replace("\t", "&#009;");
	    result = result.replace("!", "&#033;");
	    result = result.replace("$", "&#036;");
	    result = result.replace("%", "&#037;");
	    result = result.replace("'", "&#039;");
	    result = result.replace("(", "&#040;"); 
	    result = result.replace(")", "&#041;");
	    result = result.replace("*", "&#042;");
	    result = result.replace("+", "&#043;");
	    result = result.replace(",", "&#044;");
	    result = result.replace("-", "&#045;");
	    result = result.replace(".", "&#046;");
	    result = result.replace("/", "&#047;");
	    result = result.replace(":", "&#058;");
	    result = result.replace("=", "&#061;");
	    result = result.replace("?", "&#063;");
	    result = result.replace("@", "&#064;");
	    result = result.replace("[", "&#091;");
	    result = result.replace("\\", "&#092;");
	    result = result.replace("]", "&#093;");
	    result = result.replace("^", "&#094;");
	    result = result.replace("_", "&#095;");
	    result = result.replace("`", "&#096;");
	    result = result.replace("{", "&#123;");
	    result = result.replace("|", "&#124;");
	    result = result.replace("}", "&#125;");
	    result = result.replace("~", "&#126;");
		
		return result;
	}
}
