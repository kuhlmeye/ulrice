package net.ulrice.translator;

import net.ulrice.module.IFController;
import net.ulrice.process.AbstractProcess;
import net.ulrice.translator.service.IFTranslationService;

public class PLoadData extends AbstractProcess<Void, Void> {

	private MTranslator model;
	private IFTranslationService service;

	public PLoadData(IFController owner, String name, MTranslator model, IFTranslationService service) {
		super(owner, name, true);
		this.model = model;
		this.service = service;
	}

	@Override
	protected Void work() {
		model.setDictionary(service.findAllDictionaryEntries());
		model.setUsages(service.findAllUsages());
		model.setTranslations(service.findAllTranslations());

		return null;
	}

	@Override
	protected void finished(Void result) {
		model.getDictionaryAM().read();
		model.getUsagesAM().read();
		model.getTranslationsAM().read();						
	}
}
