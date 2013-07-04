package net.ulrice.databinding.validation;

import net.ulrice.databinding.IFBinding;

/**
 * A UniqueKeyConstraintError error. 
 * to differ between normal ValidationErrors
 * 
 * @author rad
 */
public class UniqueKeyConstraintError extends ValidationError {
    public UniqueKeyConstraintError(IFBinding bindingId, String message, Throwable th) {
        super(bindingId, message, th);
    }
   
}
