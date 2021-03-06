package net.ulrice.databinding.validation;

import net.ulrice.databinding.IFBinding;

/**
 * A validation error. 
 * 
 * @author christof
 */
public class ValidationError {

    /** The identifier of the attribute having the validation error. */
    private IFBinding bindingId;
    
    /** The message of the validation error. */
    private String message;
    
    /** The throwable */
    private Throwable th;
        
    /**
     * Creates a new validation error.
     * 
     * @param attributeId The identifier of the attribute
     * @param message The message
     * @param th The throwable.
     */
    public ValidationError(IFBinding bindingId, String message, Throwable th) {
        this.bindingId = bindingId;
        this.message = message;
        this.th = th;
    }
    
    /**
     * @return the attributeId
     */
    public IFBinding getBindingId() {
        return bindingId;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the th
     */
    public Throwable getTh() {
        return th;
    }
    
    
    @Override
    public String toString() {
    	return "ValidationFailure [Id=" + bindingId.getId() + ", Message=" + message + "]";
    }
}
