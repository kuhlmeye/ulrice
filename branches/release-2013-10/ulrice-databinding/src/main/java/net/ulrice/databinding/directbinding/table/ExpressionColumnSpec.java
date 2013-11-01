package net.ulrice.databinding.directbinding.table;


public class ExpressionColumnSpec {
    private final String expression;
    private final Boolean isReadOnly;
    private final Class<?> type;
    
    public ExpressionColumnSpec (String expression, Class<?> type) {
        this (expression, null, type);
    }
    
    public ExpressionColumnSpec (String expression, Boolean readOnly, Class<?> type) {
    	this.expression = expression;
    	this.isReadOnly = readOnly;
    	this.type = type;
    }
    
    public String getExpression () {
        return expression;
    }
    
    public Class<?> getType () {
        return type;
    }

    public Boolean getIsReadOnly () {
        return isReadOnly;
    }
}
