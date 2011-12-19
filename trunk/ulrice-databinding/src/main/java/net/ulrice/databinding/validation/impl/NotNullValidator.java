package net.ulrice.databinding.validation.impl;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.validation.AbstractValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;

/**
 * 
 * @author apunahassaphemapetilon@hotmail.com
 *
 * Rawtypes is ok as we just check if the object is null.
 */

@SuppressWarnings("rawtypes")
public class NotNullValidator extends AbstractValidator {

	@Override
	protected ValidationResult validate(IFBinding bindingId, Object attribute) {
		ValidationResult result = new ValidationResult();
				
		if (attribute == null) {
		    
		    if(bindingId.getOriginalValue() instanceof Number){
	            result.addValidationError(new ValidationError(bindingId, "attribute is not a number", null));
		    }else{
		          // TODO Tobias Internationalize
	            result.addValidationError(new ValidationError(bindingId, "attribute must not be null!", null));

		    }

		}
		return result;
	}

}
