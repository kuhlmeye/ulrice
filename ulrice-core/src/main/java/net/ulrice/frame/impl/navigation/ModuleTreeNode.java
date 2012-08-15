package net.ulrice.frame.impl.navigation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.ulrice.Ulrice;
import net.ulrice.module.IFModule;
import net.ulrice.module.IFModuleGroup;
import net.ulrice.profile.ProfilableModule;
import net.ulrice.profile.ProfiledModule;

public class ModuleTreeNode {

    private IFModuleGroup moduleGroup;
    private IFModule module;
    private ProfiledModule profiledModule;
    private Comparator<ModuleTreeNode> comparator;
    private ModuleTreeNodeFilter filter;
    
    public enum NodeType {
    	ModuleGroup, 
    	Module,
    	ProfiledModule
    }

    private List<ModuleTreeNode> childs = new ArrayList<ModuleTreeNode>();    
    private List<ModuleTreeNode> filteredChilds = new ArrayList<ModuleTreeNode>();
	private NodeType nodeType;
    
    public ModuleTreeNode(IFModuleGroup moduleGroup, Comparator<ModuleTreeNode> comparator, ModuleTreeNodeFilter filter) {
        this.moduleGroup = moduleGroup;
        this.nodeType = NodeType.ModuleGroup;
        
        this.comparator = comparator;
        this.filter = filter;
        
        int numGroups = (moduleGroup != null && moduleGroup.getModuleGroups() != null) ? moduleGroup.getModuleGroups().size() : 0;
        this.childs = new ArrayList<ModuleTreeNode>(numGroups);
        for(IFModuleGroup subGroup : moduleGroup.getModuleGroups()) {
            childs.add(new ModuleTreeNode(subGroup, comparator, filter));
        }
        for(IFModule subModule : moduleGroup.getModules()) {
            childs.add(new ModuleTreeNode(subModule));
        }
        
        if(comparator != null) {
            Collections.sort(childs, comparator);
        }
        
        
        applyFilter();
    }
    
    public ModuleTreeNode(IFModule module) {
        this.module = module;
        this.nodeType = NodeType.Module;
        
        if(module instanceof ProfilableModule) {
	        List<ProfiledModule> profiledModules = Ulrice.getProfileManager().loadProfiledModules((ProfilableModule)module);
	        if(profiledModules != null) {
	        	for(ProfiledModule profiledModule : profiledModules) {
	        		childs.add(new ModuleTreeNode(profiledModule));
	        	}
	        }        
        }
    }
    
    public ModuleTreeNode(ProfiledModule profiledModule) {
    	this.profiledModule = profiledModule;
    	this.nodeType = NodeType.ProfiledModule;
    }
    
    
    public void applyFilter() {
        this.filteredChilds = new ArrayList<ModuleTreeNode>(childs.size());

        for(ModuleTreeNode child : childs) {
            child.applyFilter();
            if(child.getChildCount() > 0) {
                    filteredChilds.add(child);
            }
            else {
                if(filter == null || filter.accept(child.getModule())) {
                    filteredChilds.add(child);
                }
            }
        }
    }
    
    public int getChildCount() {
        return filteredChilds.size();
    }
    
    public ModuleTreeNode getChild(int idx) {
        return filteredChilds.get(idx);
    }
    
    public int getIndex(ModuleTreeNode child) {
        return filteredChilds.indexOf(child);
    }
    
    public NodeType getNodeType() {
        return nodeType;
    }
    
    
    public IFModule getModule() {
        return module;
    }
    
    public IFModuleGroup getModuleGroup() {
        return moduleGroup;
    }

	public boolean hasChilds() {
		return filteredChilds.size() > 0;
	}

	public ProfiledModule getProfiledModule() {
		return profiledModule;
	}
}
