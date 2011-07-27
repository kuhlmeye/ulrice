package net.ulrice.translator;


import java.awt.event.ActionEvent;

import net.ulrice.module.IFModule;
import net.ulrice.module.impl.AbstractController;
import net.ulrice.module.impl.ModuleActionState;
import net.ulrice.module.impl.action.Action;
import net.ulrice.module.impl.action.ActionType;
import net.ulrice.translator.service.IFTranslationService;

public class CTranslator extends AbstractController<MTranslator, VTranslator> {

	public static final String SERVICE_IMPLEMENTATION = "Translator.Service.Implementation";
	
	private IFTranslationService translationService;
				
	
	@Override
	public void postCreationEvent(IFModule module) {
		super.postCreationEvent(module);
				
		Object value = module.getParameter(SERVICE_IMPLEMENTATION);
		if(value == null || !(value instanceof IFTranslationService)) {
			throw new RuntimeException("Translation Service Implementation is not set as parameter.");
		}
		
		translationService = (IFTranslationService) value;
		
		getModel().getDictionaryAM().addViewAdapter(getView().getDictionaryVA());
		getModel().getUsagesAM().addViewAdapter(getView().getUsagesVA());
		getModel().getTranslationsAM().addViewAdapter(getView().getTranslationsVA());
		
		// TODO Background thread..
		getModel().setDictionary(translationService.findAllDictionaryEntries());
		getModel().getDictionaryAM().read();
		
		getModel().setUsages(translationService.findAllUsages());
		getModel().getUsagesAM().read();
		
		getModel().setTranslations(translationService.findAllTranslations());
		getModel().getTranslationsAM().read();				
	}
	
	@Override
	protected ModuleActionState[] getHandledActions() {
		
		Action addDictionaryEntryAction = new Action("ADD_DICT_ENTRY", "Add Dict", true, ActionType.ModuleAction, null) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		};
		
		Action delDictionaryEntryAction = new Action("ADD_DICT_ENTRY", "Add Dict", true, ActionType.ModuleAction, null) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		};
		
		Action addUsageAction = new Action("ADD_DICT_ENTRY", "Add Dict", true, ActionType.ModuleAction, null) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		};
		
		Action delUsageAction = new Action("ADD_DICT_ENTRY", "Add Dict", true, ActionType.ModuleAction, null) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		};
		
		Action generateTranslationTableAction = new Action("ADD_DICT_ENTRY", "Add Dict", true, ActionType.ModuleAction, null) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		};
		
		Action savePropertyFilesAction = new Action("ADD_DICT_ENTRY", "Add Dict", true, ActionType.ModuleAction, null) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		};
				
		return new ModuleActionState[] {
				new ModuleActionState(true, this, addDictionaryEntryAction),
				new ModuleActionState(true, this, delDictionaryEntryAction),
				new ModuleActionState(true, this, addUsageAction),
				new ModuleActionState(true, this, delUsageAction),
				new ModuleActionState(true, this, generateTranslationTableAction),
				new ModuleActionState(true, this, savePropertyFilesAction),
		};
	}
	
	@Override
	protected MTranslator instanciateModel() {
		return new MTranslator();
	}

	@Override
	protected VTranslator instanciateView() {
		return new VTranslator();
	}

}
