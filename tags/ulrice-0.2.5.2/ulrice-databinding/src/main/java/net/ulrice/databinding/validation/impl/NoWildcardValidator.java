package net.ulrice.databinding.validation.impl;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.validation.AbstractValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;

public class NoWildcardValidator extends AbstractValidator<String> {

    private final int startIndex;
    private final int endIndex;
    private final String[] wildcards;
    
    public NoWildcardValidator(int startIndex, int endIndex, String[] wildcards) {
        super();
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.wildcards = wildcards;
    }

    public NoWildcardValidator(int startIndex, int endIndex) {
        this(startIndex, endIndex, new String[] { "*", "?" });
    }

    @Override
    protected ValidationResult validate(IFBinding bindingId, String attribute) {
        ValidationResult result = new ValidationResult();
        if (attribute != null && !attribute.isEmpty() && attribute.length() > startIndex) {
            String str =
                    attribute.substring(startIndex, attribute.length() > endIndex ? endIndex : attribute.length());
            for (String wildcard : wildcards) {
                if (str.contains(wildcard)) {
                    result.addValidationError(new ValidationError(bindingId, "No wildcards allowed from "
                        + startIndex + " to " + endIndex + " : \"" + attribute + "\"", null));
                }
            }

        }
        return result;
    }

}
