package net.ulrice.translator;


import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import net.ulrice.databinding.viewadapter.utable.UTableViewAdapter;
import net.ulrice.module.impl.AbstractController;
import net.ulrice.module.impl.IFClosing;
import net.ulrice.module.impl.ModuleActionState;
import net.ulrice.module.impl.action.ActionType;
import net.ulrice.module.impl.action.UlriceAction;
import net.ulrice.process.CtrlProcessExecutor;
import net.ulrice.process.IncrementalDataProvider;
import net.ulrice.process.IncrementalLoader;
import net.ulrice.translator.service.DictionaryEntryDTO;
import net.ulrice.translator.service.IFTranslationService;
import net.ulrice.translator.service.TranslationDTO;
import net.ulrice.translator.service.UsageDTO;


public class CTranslator extends AbstractController {

	private CtrlProcessExecutor processExecutor;

	private final VTranslator view = new VTranslator();
	private final MTranslator model = new MTranslator();
	private final IFTranslationService translationService;
	

	public CTranslator(IFTranslationService translationService) {
	    this.translationService = translationService;
	    view.initialize(this);	    
	}

	public JComponent getView() {
	    return view.getView();
	}
	
	@Override
	public void postCreate() {
		super.postCreate();
		
		processExecutor = new CtrlProcessExecutor(1); 
		translationService.openTranslationService();

        model.getDictionaryAM().addViewAdapter(view.getDictionaryVA());
		model.getUsagesAM().addViewAdapter(view.getUsagesVA());
		model.getTranslationsAM().addViewAdapter(view.getTranslationsVA());

        loadData();
	}

    private void loadData() {
        processExecutor.executeProcess(getDictionaryEntryLoader());
        processExecutor.executeProcess(getUsageLoader());        
        processExecutor.executeProcess(getTranslationLoader());      
    }

    private IncrementalLoader<DictionaryEntryDTO> getDictionaryEntryLoader() {
        return new IncrementalLoader<DictionaryEntryDTO>(this, false, translationService.getDictionaryEntriesChunkSize(), Integer.MAX_VALUE, new IncrementalDataProvider<DictionaryEntryDTO>() {

            @Override
            public List<DictionaryEntryDTO> getData(int firstRow, int maxNumRows) {
                return translationService.getDictionaryEntries(firstRow, maxNumRows);
            }

            @Override
            public int getNumRows() {
                return translationService.getNumDictionaryEntries();
            }

            @Override
            public void onChunkLoaded(List<DictionaryEntryDTO> chunk, int firstRow) {
                model.getDictionaryAM().read(chunk, true);
            }

            @Override
            public void onFinished() {                
            }
            
        });
    }

    private IncrementalLoader<TranslationDTO> getTranslationLoader() {
        return new IncrementalLoader<TranslationDTO>(this, false, translationService.getTranslationsChunkSize(), Integer.MAX_VALUE, new IncrementalDataProvider<TranslationDTO>() {

            @Override
            public List<TranslationDTO> getData(int firstRow, int maxNumRows) {
                return translationService.getTranslations(firstRow, maxNumRows);
            }

            @Override
            public int getNumRows() {
                return translationService.getNumTranslations();
            }

            @Override
            public void onChunkLoaded(List<TranslationDTO> chunk, int firstRow) {
                model.getTranslationsAM().read(chunk, true);
            }

            @Override
            public void onFinished() {                
            }
            
        });
    }

    private IncrementalLoader<UsageDTO> getUsageLoader() {
        return new IncrementalLoader<UsageDTO>(this, false, translationService.getUsagesChunkSize(), Integer.MAX_VALUE, new IncrementalDataProvider<UsageDTO>() {

            @Override
            public List<UsageDTO> getData(int firstRow, int maxNumRows) {
                return translationService.getUsages(firstRow, maxNumRows);
            }

            @Override
            public int getNumRows() {
                return translationService.getNumUsages();
            }

            @Override
            public void onChunkLoaded(List<UsageDTO> chunk, int firstRow) {
                if(chunk != null) {
                    model.getUsagesAM().read(chunk, true);
                }
            }

            @Override
            public void onFinished() {                
            }
            
        });
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
					viewAdapter.delSelectedRows();
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
				    viewAdapter.delSelectedRows();
				}
			}
		};

		UlriceAction saveAction = new UlriceAction("SAVE", "Save", true, ActionType.ModuleAction, new ImageIcon(getClass().getResource("save.png"))) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Let user select locales in Dialog..
				PSaveData saveDataProcess = new PSaveData(CTranslator.this, "Save Data", model, translationService);
				
				processExecutor.executeProcess(saveDataProcess);
                processExecutor.executeProcess(getUsageLoader(), saveDataProcess);
                processExecutor.executeProcess(getDictionaryEntryLoader(), saveDataProcess);
                processExecutor.executeProcess(getTranslationLoader(), saveDataProcess);
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
				new ModuleActionState(true, addDictionaryEntryAction),
				new ModuleActionState(true, delDictionaryEntryAction),
				new ModuleActionState(true, addUsageAction),
				new ModuleActionState(true, delUsageAction),
				new ModuleActionState(true, saveAction),
				new ModuleActionState(true, generateTranslationTableAction),
				new ModuleActionState(true, generatePropertyFilesAction)
		);
	}
	
	@Override
    public void onClose(IFClosing closing) {
        
        closing.doClose();
    }
}
