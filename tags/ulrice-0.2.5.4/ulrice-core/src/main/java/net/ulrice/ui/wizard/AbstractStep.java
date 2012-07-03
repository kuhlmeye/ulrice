package net.ulrice.ui.wizard;

import java.util.UUID;

import javax.swing.JComponent;

public abstract class AbstractStep implements Step {

    private String id;

    public AbstractStep() {
        this(UUID.randomUUID().toString());
    }
    
    public AbstractStep(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }        
    
    public abstract String getTitle();
    
    public abstract JComponent getView();
}
