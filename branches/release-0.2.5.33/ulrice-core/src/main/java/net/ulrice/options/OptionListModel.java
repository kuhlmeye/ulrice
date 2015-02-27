package net.ulrice.options;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import net.ulrice.options.modules.IFOptionModule;

public class OptionListModel extends AbstractListModel {

    private static final long serialVersionUID = 3915721291724623463L;
    private List<IFOptionModule> modelList = new ArrayList<IFOptionModule>();
    private IFOptionModule activeModule = null;
    
    public void addAllModules(List<IFOptionModule> moduleList) {
        modelList.clear();
        modelList.addAll(moduleList);        
        fireIntervalAdded(this, 0, moduleList.size());
    }
    
    @Override
    public int getSize() {
        return modelList.size();
    }

    @Override
    public IFOptionModule getElementAt(int index) {
        return modelList.get(index);
    }
    
    public void setActiveModule(IFOptionModule activeModule) {
        this.activeModule = activeModule;
    }
    
    public IFOptionModule getActiveModule() {
        return activeModule;
    }
}
