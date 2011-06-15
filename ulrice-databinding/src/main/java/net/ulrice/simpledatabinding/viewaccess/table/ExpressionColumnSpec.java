package net.ulrice.simpledatabinding.viewaccess.table;


public class ExpressionColumnSpec {
    private final String _expression;
    private final Boolean _isReadOnly;
    private final Class<?> _type;
    
    public ExpressionColumnSpec (String expression, Class<?> type) {
        this (expression, null, type);
    }
    
    public ExpressionColumnSpec (String expression, Boolean readOnly, Class<?> type) {
        _expression = expression;
        _isReadOnly = readOnly;
        _type = type;
    }
    
    public String getExpression () {
        return _expression;
    }
    
    public Class<?> getType () {
        return _type;
    }

    public Boolean getIsReadOnly () {
        return _isReadOnly;
    }
}
