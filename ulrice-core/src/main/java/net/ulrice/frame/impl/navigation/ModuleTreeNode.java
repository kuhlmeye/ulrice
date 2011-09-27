package net.ulrice.frame.impl.navigation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;

import net.ulrice.module.IFModule;
import net.ulrice.module.IFModuleGroup;

public class ModuleTreeNode {

    private IFModuleGroup moduleGroup;
    private IFModule module;
    private Comparator<ModuleTreeNode> comparator;
    private ModuleTreeNodeFilter filter;

    private List<ModuleTreeNode> childs = new ArrayList<ModuleTreeNode>();    
    private List<ModuleTreeNode> filteredChilds = new ArrayList<ModuleTreeNode>();
    
    public ModuleTreeNode(IFModuleGroup moduleGroup, Comparator<ModuleTreeNode> comparator, ModuleTreeNodeFilter filter) {
        this.moduleGroup = moduleGroup;
        this.comparator = comparator;
        this.filter = filter;
        
        this.childs = new ArrayList<ModuleTreeNode>(moduleGroup.getModuleGroups().size());
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
    }
    
    
    public void applyFilter() {
        if(moduleGroup != null) {
            this.filteredChilds = new ArrayList<ModuleTreeNode>(moduleGroup.getModuleGroups().size());

            for(ModuleTreeNode child : childs) {
                if(child.isGroup()) {
                    child.applyFilter();
                    if(child.getChildCount() > 0) {
                        filteredChilds.add(child);
                    }
                } else {
                    if(filter == null || filter.accept(child.getModule())) {
                        filteredChilds.add(child);
                    }
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
    
    public boolean isGroup() {
        return moduleGroup != null;
    }
    
    public IFModule getModule() {
        return module;
    }
    
    public IFModuleGroup getModuleGroup() {
        return moduleGroup;
    }

    public Enumeration<ModuleTreeNode> getChildren() {
        return Collections.enumeration(childs);
    }
}
