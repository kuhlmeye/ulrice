package net.ulrice.translator;

import java.util.List;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.process.AbstractProcess;
import net.ulrice.translator.service.DictionaryEntryDTO;
import net.ulrice.translator.service.IFTranslationService;
import net.ulrice.translator.service.UsageDTO;

public class PSaveData extends AbstractProcess<Void, Void> {

	private MTranslator model;
	private IFTranslationService service;

	public PSaveData(IFController owner, String name, MTranslator model, IFTranslationService service) {
        super(owner);
        setProgressMessage(name);
		this.model = model;
		this.service = service;
	}

	@Override
	protected Void work() {
		
		List<DictionaryEntryDTO> createdDictEntries = model.getDictionaryAM().getCreatedObjects();
		if(createdDictEntries != null) {
			for(DictionaryEntryDTO entry : createdDictEntries) {
				service.createDictionaryEntry(entry);
			}
		}
		List<DictionaryEntryDTO> modifiedDictEntries = model.getDictionaryAM().getModifiedObjects();
		if(modifiedDictEntries != null) {
			for(DictionaryEntryDTO entry : modifiedDictEntries) {
				service.saveDictionaryEntry(entry);
			}
		}
		List<DictionaryEntryDTO> deletedDictEntries = model.getDictionaryAM().getDeletedObjects();
		if(deletedDictEntries != null) {
			for(DictionaryEntryDTO entry : deletedDictEntries) {
				service.deleteDictionaryEntry(entry);
			}
		}
		
		List<UsageDTO> createdUsages = model.getUsagesAM().getCreatedObjects();
		if(createdUsages != null) {
			for(UsageDTO usage : createdUsages) {
				service.createUsage(usage);
			}			
		}
		List<UsageDTO> modifiedUsages = model.getUsagesAM().getModifiedObjects();
		if(modifiedUsages != null) {
			for(UsageDTO usage : modifiedUsages) {
				service.saveUsage(usage);
			}			
		}
		List<UsageDTO> deletedUsages = model.getUsagesAM().getDeletedObjects();
		if(deletedUsages != null) {
			for(UsageDTO usage : deletedUsages) {
				service.deleteUsage(usage);
			}
		}
		
		return null;
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
