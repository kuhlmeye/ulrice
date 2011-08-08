package net.ulrice.translator;


import java.awt.event.ActionEvent;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import net.ulrice.databinding.viewadapter.utable.UTableViewAdapter;
import net.ulrice.module.IFModule;
import net.ulrice.module.impl.AbstractController;
import net.ulrice.module.impl.ModuleActionState;
import net.ulrice.module.impl.action.Action;
import net.ulrice.module.impl.action.ActionType;
import net.ulrice.process.CtrlProcessExecutor;
import net.ulrice.translator.service.IFTranslationService;


public class CTranslator extends AbstractController<MTranslator> {

	public static final String SERVICE_IMPLEMENTATION = "Translator.Service.Implementation";
	private IFTranslationService translationService;
	private CtrlProcessExecutor processExecutor;

	private final VTranslator vTranslator = new VTranslator();
	private final IFModule module;
	
	public CTranslator(IFModule module) {
	    this.module = module;
	}
	
	@Override
	public void postCreate() {
		super.postCreate();
		
		processExecutor = new CtrlProcessExecutor(1); 
				
		Object value = module.getParameter(SERVICE_IMPLEMENTATION);
		if(value == null || !(value instanceof IFTranslationService)) {
			throw new RuntimeException("Translation Service Implementation is not set as parameter.");
		}
		
		translationService = (IFTranslationService) value;
		translationService.openTranslationService();
			
		getModel().getDictionaryAM().addViewAdapter(vTranslator.getDictionaryVA());
		getModel().getUsagesAM().addViewAdapter(vTranslator.getUsagesVA());
		getModel().getTranslationsAM().addViewAdapter(vTranslator.getTranslationsVA());
		
		processExecutor.executeProcess(new PLoadData(this, "Load Data", getModel(), translationService));
	}
	
	@Override
	protected ModuleActionState[] getHandledActions() {
		
		Action addDictionaryEntryAction = new Action("ADD_DICT_ENTRY", "Add Dict", true, ActionType.ModuleAction, new ImageIcon(getClass().getResource("dict_add.png"))) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				vTranslator.getDictionaryVA().addRow();
			}
		};
		
		
		Action delDictionaryEntryAction = new Action("DEL_DICT_ENTRY", "Add Dict", true, ActionType.ModuleAction, new ImageIcon(getClass().getResource("dict_del.png"))) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				UTableViewAdapter viewAdapter = vTranslator.getDictionaryVA();
				if(viewAdapter.getSelectedRowViewIndex() > -1) {
					viewAdapter.delRow(viewAdapter.getSelectedRowViewIndex());
				}
			}
		};
		
		Action addUsageAction = new Action("ADD_USAGE_ENTRY", "Add Usage", true, ActionType.ModuleAction, new ImageIcon(getClass().getResource("loc_add.png"))) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				vTranslator.getUsagesVA().addRow();
			}
		};
		
		Action delUsageAction = new Action("DEL_USAGE_ENTRY", "Del Usage", true, ActionType.ModuleAction, new ImageIcon(getClass().getResource("loc_del.png"))) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				UTableViewAdapter viewAdapter = vTranslator.getUsagesVA();
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
	protected MTranslator instantiateModel() {
		return new MTranslator();
	}

	@Override
	protected JComponent instantiateView() {
		return vTranslator.getView();
	}

}
