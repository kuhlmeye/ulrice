package net.ulrice.simpledatabinding.modelaccess.impl;

import java.util.HashMap;
import java.util.Map;

import net.ulrice.simpledatabinding.modelaccess.Predicate;

import ognl.Ognl;
import ognl.OgnlException;
import net.ulrice.simpledatabinding.util.ErrorHandler;


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
