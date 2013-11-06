package net.ulrice.databinding.validation;

import net.ulrice.databinding.IFBinding;

/**
 * A UniqueKeyConstraintError error. 
 * to differ between normal ValidationErrors
 * 
 * @author rad
 */
public class UniqueKeyConstraintError extends ValidationError {
    
    private String id;
    
    public UniqueKeyConstraintError(IFBinding bindingId, String id, String message, Throwable th) {
        super(bindingId, message, th);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
   
}
