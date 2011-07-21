package net.ulrice.databinding.modelaccess.impl;

import java.util.HashMap;
import java.util.Map;

import net.ulrice.databinding.ErrorHandler;
import net.ulrice.databinding.modelaccess.Predicate;
import ognl.Ognl;
import ognl.OgnlException;


public class OgnlPredicate implements Predicate {
    private final Object _ognlTree;
    
    public OgnlPredicate (String expression) {
        try {
            _ognlTree = Ognl.parseExpression (expression);
        } catch (OgnlException exc) {
            ErrorHandler.handle (exc);
            throw new RuntimeException (); // für den Compiler
        }
    }

    public boolean getValue (boolean isValid, Object model) {
        final Map <String, Object> context = new HashMap<String, Object> ();
        context.put ("isValid", isValid);
        
        try {
            return Boolean.TRUE.equals (Ognl.getValue (_ognlTree, context, model));
        } catch (OgnlException exc) {
            ErrorHandler.handle (exc);
            return false; // für den Compiler
        }
    }
}
