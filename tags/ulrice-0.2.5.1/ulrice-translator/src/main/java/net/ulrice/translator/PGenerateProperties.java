package net.ulrice.translator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.process.AbstractProcess;
import net.ulrice.translator.service.TranslationDTO;

public class PGenerateProperties extends AbstractProcess<Void, Void> {

	private MTranslator model;
	private Locale[] locales;
	private Map<Locale, Properties> propertiesMap = new HashMap<Locale, Properties>();

	public PGenerateProperties(IFController owner, String name, MTranslator model, Locale... locales) {		
		super(owner);
		setProgressMessage(name);
		this.model = model;
		this.locales = locales;
		model.getTranslationsAM().write();
	}

	@Override
	protected Void work() {
		for(Locale locale : locales) {
			propertiesMap.put(locale, new Properties());
		}
		
		
		List<TranslationDTO> translations = model.getTranslations();
		for(TranslationDTO translation : translations) {
			if(propertiesMap.containsKey(translation.getLanguage())) {
				Properties properties = propertiesMap.get(translation);
				if(properties != null) {
					properties.setProperty(buildKey(translation), translation.getTranslation());
				}
			}
		}
		
		for(Locale locale : locales) {
			Properties properties = propertiesMap.get(locale);
			try {
				properties.store(new FileOutputStream("Test_" + locale), "");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}

	private String buildKey(TranslationDTO translation) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(translation.getApplication()).append('.');
		buffer.append(translation.getModule()).append('.');
		buffer.append(translation.getUsage()).append('.');
		buffer.append(translation.getAttribute());
		return buffer.toString();
	}

	@Override
	protected void finished(Void result) {
		
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
