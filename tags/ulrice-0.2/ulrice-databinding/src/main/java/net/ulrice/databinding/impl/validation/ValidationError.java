package net.ulrice.databinding.impl.validation;

/**
 * A validation error. 
 * 
 * @author christof
 */
public class ValidationError {

    /** The identifier of the attribute having the validation error. */
    private String attributeId;
    
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
    public ValidationError(String attributeId, String message, Throwable th) {
        this.attributeId = attributeId;
        this.message = message;
        this.th = th;
    }
    
    /**
     * @return the attributeId
     */
    public String getAttributeId() {
        return attributeId;
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
    
}
