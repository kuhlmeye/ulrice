package net.ulrice.translator.service.xml;

import java.util.Locale;

import net.ulrice.translator.service.DictionaryEntryDTO;
import net.ulrice.translator.service.IFTranslationService;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class DictionaryXMLHandler implements ContentHandler {

	private IFTranslationService service;
	
	private DictionaryEntryDTO currentEntry;	
	
	private String currentText;

	public DictionaryXMLHandler(IFTranslationService service) {
		this.service = service;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if("DictEntry".equalsIgnoreCase(localName)) {
			currentEntry = new DictionaryEntryDTO();
		} 
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if("DictEntry".equalsIgnoreCase(localName)) {
			service.createDictionaryEntry(currentEntry);
			currentEntry = null;
		} else if("Application".equalsIgnoreCase(localName)){
			currentEntry.setApplication(currentText);
		} else if("Module".equalsIgnoreCase(localName)) {
			currentEntry.setModule(currentText);
		} else if("Usage".equalsIgnoreCase(localName)) {
			currentEntry.setUsage(currentText);
		} else if("Attribute".equalsIgnoreCase(localName)) {
			currentEntry.setAttribute(currentText);
		} else if("Language".equalsIgnoreCase(localName)) {
			currentEntry.setLanguage(new Locale(currentText));
		} else if("Translation".equalsIgnoreCase(localName)) {
			currentEntry.setTranslation(currentText);
		}
	}	

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		currentText = new String(ch, start, length);
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		// TODO Auto-generated method stub

	}


	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void processingInstruction(String target, String data) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void skippedEntity(String name) throws SAXException {
		// TODO Auto-generated method stub

	}

}
