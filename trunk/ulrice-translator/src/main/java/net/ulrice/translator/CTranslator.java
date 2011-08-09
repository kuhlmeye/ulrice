package net.ulrice.translator;


import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import net.ulrice.databinding.viewadapter.utable.UTableViewAdapter;
import net.ulrice.module.IFModule;
import net.ulrice.module.impl.AbstractController;
import net.ulrice.module.impl.ModuleActionState;
import net.ulrice.module.impl.action.UlriceAction;
import net.ulrice.module.impl.action.ActionType;
import net.ulrice.process.CtrlProcessExecutor;
import net.ulrice.translator.service.IFTranslationService;


public class CTranslator extends AbstractController {

	public static final String SERVICE_IMPLEMENTATION = "Translator.Service.Implementation";
	
	private IFTranslationService translationService;
	private CtrlProcessExecutor processExecutor;

	private final VTranslator view = new VTranslator();
	private final MTranslator model = new MTranslator();
	private final IFModule module;
	

	public CTranslator(IFModule module) {
	    this.module = module;
	}

	public JComponent getView() {
	    return view.getView();
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
			
		model.getDictionaryAM().addViewAdapter(view.getDictionaryVA());
		model.getUsagesAM().addViewAdapter(view.getUsagesVA());
		model.getTranslationsAM().addViewAdapter(view.getTranslationsVA());
		
		processExecutor.executeProcess(new PLoadData(this, "Load Data", model, translationService));
	}
	
	@Override
	public List<ModuleActionState> getHandledActions() {
		
		UlriceAction addDictionaryEntryAction = new UlriceAction("ADD_DICT_ENTRY", "Add Dict", true, ActionType.ModuleAction, new ImageIcon(getClass().getResource("dict_add.png"))) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				view.getDictionaryVA().addRow();
			}
		};
		
		
		UlriceAction delDictionaryEntryAction = new UlriceAction("DEL_DICT_ENTRY", "Add Dict", true, ActionType.ModuleAction, new ImageIcon(getClass().getResource("dict_del.png"))) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				UTableViewAdapter viewAdapter = view.getDictionaryVA();
				if(viewAdapter.getSelectedRowViewIndex() > -1) {
					viewAdapter.delRow(viewAdapter.getSelectedRowViewIndex());
				}
			}
		};
		
		UlriceAction addUsageAction = new UlriceAction("ADD_USAGE_ENTRY", "Add Usage", true, ActionType.ModuleAction, new ImageIcon(getClass().getResource("loc_add.png"))) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				view.getUsagesVA().addRow();
			}
		};
		
		UlriceAction delUsageAction = new UlriceAction("DEL_USAGE_ENTRY", "Del Usage", true, ActionType.ModuleAction, new ImageIcon(getClass().getResource("loc_del.png"))) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				UTableViewAdapter viewAdapter = view.getUsagesVA();
				if(viewAdapter.getSelectedRowViewIndex() > -1) {
					viewAdapter.delRow(viewAdapter.getSelectedRowViewIndex());
				}
			}
		};

		UlriceAction saveAction = new UlriceAction("SAVE", "Save", true, ActionType.ModuleAction, new ImageIcon(getClass().getResource("save.png"))) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Let user select locales in Dialog..
				PSaveData saveDataProcess = new PSaveData(CTranslator.this, "Save Data", model, translationService);
				PLoadData loadDataProcess = new PLoadData(CTranslator.this, "Load Data", model, translationService);				
				processExecutor.executeProcess(saveDataProcess);
				processExecutor.executeProcess(loadDataProcess, saveDataProcess);
			}
		};
		
		UlriceAction generateTranslationTableAction = new UlriceAction("GENERATE_TRANSLATION", "Generate", true, ActionType.ModuleAction, new ImageIcon(getClass().getResource("generate.png"))) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Let user select locales in Dialog..
				processExecutor.executeProcess(new PGenerateTranslations(CTranslator.this, "Generate Translations", model, new Locale("en", "US"), new Locale("de", "DE")));
			}
		};
		
		UlriceAction generatePropertyFilesAction = new UlriceAction("GENERATE_PROPERTIES", "Generate Properties", true, ActionType.ModuleAction, new ImageIcon(getClass().getResource("save_properties.png"))) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Let user select locales in Dialog..
				processExecutor.executeProcess(new PGenerateProperties(CTranslator.this, "Generate Properties", model, new Locale("en", "US"), new Locale("de", "DE")));
			}
		};
				
		return Arrays.asList (
				new ModuleActionState(true, this, addDictionaryEntryAction),
				new ModuleActionState(true, this, delDictionaryEntryAction),
				new ModuleActionState(true, this, addUsageAction),
				new ModuleActionState(true, this, delUsageAction),
				new ModuleActionState(true, this, saveAction),
				new ModuleActionState(true, this, generateTranslationTableAction),
				new ModuleActionState(true, this, generatePropertyFilesAction)
		);
	}
}
