package net.ulrice.translator;


import java.awt.event.ActionEvent;
import java.util.Locale;

import javax.swing.ImageIcon;

import net.ulrice.databinding.viewadapter.utable.UTableViewAdapter;
import net.ulrice.module.IFModule;
import net.ulrice.module.impl.AbstractController;
import net.ulrice.module.impl.ModuleActionState;
import net.ulrice.module.impl.action.Action;
import net.ulrice.module.impl.action.ActionType;
import net.ulrice.process.CtrlProcessExecutor;
import net.ulrice.translator.service.IFTranslationService;

public class CTranslator extends AbstractController<MTranslator, VTranslator> {

	public static final String SERVICE_IMPLEMENTATION = "Translator.Service.Implementation";
	
	private IFTranslationService translationService;

	private CtrlProcessExecutor processExecutor;
				
	
	@Override
	public void postCreationEvent(IFModule module) {
		super.postCreationEvent(module);
		
		processExecutor = new CtrlProcessExecutor(1); 
				
		Object value = module.getParameter(SERVICE_IMPLEMENTATION);
		if(value == null || !(value instanceof IFTranslationService)) {
			throw new RuntimeException("Translation Service Implementation is not set as parameter.");
		}
		
		translationService = (IFTranslationService) value;
		translationService.openTranslationService();
			
		getModel().getDictionaryAM().addViewAdapter(getView().getDictionaryVA());
		getModel().getUsagesAM().addViewAdapter(getView().getUsagesVA());
		getModel().getTranslationsAM().addViewAdapter(getView().getTranslationsVA());
		
		processExecutor.executeProcess(new PLoadData(this, "Load Data", getModel(), translationService));
	}
	
	@Override
	protected ModuleActionState[] getHandledActions() {
		
		Action addDictionaryEntryAction = new Action("ADD_DICT_ENTRY", "Add Dict", true, ActionType.ModuleAction, new ImageIcon(getClass().getResource("dict_add.png"))) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getView().getDictionaryVA().addRow();
			}
		};
		
		
		Action delDictionaryEntryAction = new Action("DEL_DICT_ENTRY", "Add Dict", true, ActionType.ModuleAction, new ImageIcon(getClass().getResource("dict_del.png"))) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				UTableViewAdapter viewAdapter = getView().getDictionaryVA();
				if(viewAdapter.getSelectedRowViewIndex() > -1) {
					viewAdapter.delRow(viewAdapter.getSelectedRowViewIndex());
				}
			}
		};
		
		Action addUsageAction = new Action("ADD_USAGE_ENTRY", "Add Usage", true, ActionType.ModuleAction, new ImageIcon(getClass().getResource("loc_add.png"))) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getView().getUsagesVA().addRow();
			}
		};
		
		Action delUsageAction = new Action("DEL_USAGE_ENTRY", "Del Usage", true, ActionType.ModuleAction, new ImageIcon(getClass().getResource("loc_del.png"))) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				UTableViewAdapter viewAdapter = getView().getUsagesVA();
				if(viewAdapter.getSelectedRowViewIndex() > -1) {
					viewAdapter.delRow(viewAdapter.getSelectedRowViewIndex());
				}
			}
		};

		Action saveAction = new Action("SAVE", "Save", true, ActionType.ModuleAction, new ImageIcon(getClass().getResource("save.png"))) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Let user select locales in Dialog..
				PSaveData saveDataProcess = new PSaveData(CTranslator.this, "Save Data", getModel(), translationService);
				PLoadData loadDataProcess = new PLoadData(CTranslator.this, "Load Data", getModel(), translationService);				
				processExecutor.executeProcess(saveDataProcess);
				processExecutor.executeProcess(loadDataProcess, saveDataProcess);
			}
		};
		
		Action generateTranslationTableAction = new Action("GENERATE_TRANSLATION", "Generate", true, ActionType.ModuleAction, new ImageIcon(getClass().getResource("generate.png"))) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Let user select locales in Dialog..
				processExecutor.executeProcess(new PGenerateTranslations(CTranslator.this, "Generate Translations", getModel(), new Locale("en", "US"), new Locale("de", "DE")));
			}
		};
		
		Action generatePropertyFilesAction = new Action("GENERATE_PROPERTIES", "Generate Properties", true, ActionType.ModuleAction, new ImageIcon(getClass().getResource("save_properties.png"))) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Let user select locales in Dialog..
				processExecutor.executeProcess(new PGenerateProperties(CTranslator.this, "Generate Properties", getModel(), new Locale("en", "US"), new Locale("de", "DE")));
			}
		};
				
		return new ModuleActionState[] {
				new ModuleActionState(true, this, addDictionaryEntryAction),
				new ModuleActionState(true, this, delDictionaryEntryAction),
				new ModuleActionState(true, this, addUsageAction),
				new ModuleActionState(true, this, delUsageAction),
				new ModuleActionState(true, this, saveAction),
				new ModuleActionState(true, this, generateTranslationTableAction),
				new ModuleActionState(true, this, generatePropertyFilesAction),
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
