package net.ulrice.databinding.viewadapter.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.BadLocationException;

import net.ulrice.databinding.bufferedbinding.ColumnDefinition;
import net.ulrice.databinding.bufferedbinding.FilterMode;

/**
 * @author christof
 * 
 */
public class JTableVAFilter extends RowFilter<JTableViewAdapter, String> implements DocumentListener, TableColumnModelListener {

    /** The logger used by this class. */
    private static final Logger LOG = Logger.getLogger(JTableVAFilter.class.getName());

    /**
     * The constant of the property key holding the identifier of the column id.
     */
    private static final String DOCUMENT_PROPERTY_FIELD_ID = "FIELD_ID";

    /** The table header. */
    private JTableVAHeader tableHeader;

    /** The list of column identifiers. */
    private List<String> columnIdentifiers = new ArrayList<String>(0);

    /** The list of filter modes per column. */
    private Map<String, FilterMode> columnFilterModes = new HashMap<String, FilterMode>();

    /** The map holding the current filter expressions for all columns by id. */
    private Map<String, Pattern> regexExpressionMap = new HashMap<String, Pattern>();
    private Map<String, NumericPattern> numericPatternExpressionMap = new HashMap<String, NumericPattern>();

    private JTableVARowSorter rowSorter;

    /**
     * @param rowSorter
     * @param tableHeader2
     * @param columnModel
     */
    public JTableVAFilter(JTableVARowSorter rowSorter, JTableVAHeader tableHeader, TableColumnModel columnModel) {
        this.tableHeader = tableHeader;
        createFilterComponents(tableHeader);
        tableHeader.getColumnModel().addColumnModelListener(this);
        this.rowSorter = rowSorter;
    }

    /**
     * @param columnModel
     */
    private void createFilterComponents(JTableVAHeader tableHeader) {
        TableColumnModel columnModel = tableHeader.getColumnModel();
        columnIdentifiers = new ArrayList<String>(columnModel.getColumnCount());
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            ColumnDefinition<?> columnDefinition = (ColumnDefinition<?>) column.getHeaderValue();
            FilterMode filterMode = columnDefinition.getFilterMode();
            columnIdentifiers.add(columnDefinition.getId());
            columnFilterModes.put(columnDefinition.getId(), filterMode);
            if (!FilterMode.NoFilter.equals(filterMode)) {
                Class<?> columnClass = columnDefinition.getColumnClass();
                JComponent component = null;
                if (String.class.isAssignableFrom(columnClass)) {
                    JTextField field = new JTextField();
                    field.setName(columnDefinition.getId());
                    field.getDocument().putProperty(DOCUMENT_PROPERTY_FIELD_ID,columnDefinition.getId());
                    field.getDocument().addDocumentListener(this);
                    component = field;
                } else if (Number.class.isAssignableFrom(columnClass)) {
                    JTextField field = new JTextField();
                    field.setName(columnDefinition.getId());
                    field.getDocument().putProperty(DOCUMENT_PROPERTY_FIELD_ID, columnDefinition.getId());
                    field.getDocument().addDocumentListener(this);
                    component = field;
                }
                if (component != null) {
                    tableHeader.add(component, column.getIdentifier());
                }
            }
        }
    }

    /**
     * @see javax.swing.RowFilter#include(javax.swing.RowFilter.Entry)
     */
    @Override
    public boolean include(javax.swing.RowFilter.Entry<? extends JTableViewAdapter, ? extends String> entry) {
        boolean include = true;
        for (int i = 0; i < entry.getValueCount() && include; i++) {
            String columnId = columnIdentifiers.get(i);
            include &= includeValue(columnId, entry.getIdentifier(), entry.getValue(i));
        }
        return include;
    }

    /**
     * @param columnId
     * @param identifier
     * @param value
     * @return
     */
    private boolean includeValue(String columnId, String identifier, Object value) {
        String strValue = value == null ? "" : value.toString();
        if (columnFilterModes != null && columnFilterModes.containsKey(columnId)) {
            switch (columnFilterModes.get(columnId)) {
                case RegEx:
                    Pattern pattern = regexExpressionMap.get(columnId);
                    if (pattern != null) {
                        LOG.finest("ColumnId: " + columnId + ", Value: " + strValue + ", Pattern: " + pattern.pattern());
                        return pattern.matcher(strValue).matches();
                    }
                case Numeric:
                    NumericPattern numericPattern = numericPatternExpressionMap.get(columnId);
                    if (numericPattern != null) {
                        LOG.finest("ColumnId: " + columnId + ", Value: " + strValue + ", Pattern: "
                                + numericPattern.toString());
                        return numericPattern.matches(value);
                    }
            }
        }

        return true;
    }

    /**
     * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
     */
    @Override
    public void changedUpdate(DocumentEvent e) {
        String columnId = e.getDocument().getProperty(DOCUMENT_PROPERTY_FIELD_ID).toString();
        try {
            filterChanged(columnId, e.getDocument().getText(0, e.getDocument().getLength()));
        } catch (BadLocationException e1) {
            LOG.log(Level.WARNING, "Could not get the filtertext for column '" + columnId + "'.", e1);
        }
    }

    /**
     * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
     */
    @Override
    public void insertUpdate(DocumentEvent e) {
        String columnId = e.getDocument().getProperty(DOCUMENT_PROPERTY_FIELD_ID).toString();
        try {
            filterChanged(columnId, e.getDocument().getText(0, e.getDocument().getLength()));
        } catch (BadLocationException e1) {
            LOG.log(Level.WARNING, "Could not get the filtertext for column '" + columnId + "'.", e1);
        }
    }

    /**
     * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
     */
    @Override
    public void removeUpdate(DocumentEvent e) {
        String columnId = e.getDocument().getProperty(DOCUMENT_PROPERTY_FIELD_ID).toString();
        try {
            filterChanged(columnId, e.getDocument().getText(0, e.getDocument().getLength()));
        } catch (BadLocationException e1) {
            LOG.log(Level.WARNING, "Could not get the filtertext for column '" + columnId + "'.", e1);
        }
    }
    
    /**
     * Set the filter value for a column.
     * 
     * @param columnId The identifier of the column
     * @param text The filter text.
     */
    public void setFilterValue(String columnId, String text) {
        filterChanged(columnId, text);
    }

    /**
     * @param property
     * @param text
     */
    private void filterChanged(String columnId, String text) {
        LOG.finer("Filter changed for column-id '" + columnId + "'. Text is: " + text);
        if (text == null || text.isEmpty()) {
            regexExpressionMap.remove(columnId);
            numericPatternExpressionMap.remove(columnId);

            rowSorter.sort();
        } else if(columnFilterModes.containsKey(columnId)){
            
            switch (columnFilterModes.get(columnId)) {
                case NoFilter:
                    break;
                case RegEx: {
                    String regex = text;
                    regex = regex.replace("?", ".?");
                    regex = regex.replace("*", ".*");
                    regex += ".*";

                    try {
                        Pattern pattern = Pattern.compile(regex);
                        regexExpressionMap.put(columnId, pattern);
                    } catch (PatternSyntaxException e) {
                        LOG.log(Level.FINER, "Could not compile regex: " + regex, e);
                    }
                    break;
                }
                case Numeric: {
                    String regex = text;
                    if (regex.matches("\\s*>\\s*[\\-\\+]?[0-9]+\\s*")) {
                        regex = regex.trim();
                        Double valueA = Double.valueOf(regex.substring(1));
                        numericPatternExpressionMap.put(columnId, new NumericPattern(NumericPattern.Operator.Greater,
                                valueA, null));
                    } else if (regex.matches("\\s*\\<\\s*[\\-\\+]?[0-9]+\\s*")) {
                        regex = regex.trim();
                        Double valueA = Double.valueOf(regex.substring(1));
                        numericPatternExpressionMap.put(columnId, new NumericPattern(NumericPattern.Operator.Smaller,
                                valueA, null));
                    } else if (regex.matches("\\s*\\[\\s*[\\-\\+]?[0-9]+\\s*,\\s*[\\-\\+]?[0-9]+\\s*\\]\\s*")) {
                        regex = regex.trim();
                        String strValueA = regex.substring(1, regex.indexOf(','));
                        String strValueB = regex.substring(regex.indexOf(',') + 1, regex.length() - 1);
                        Double valueA = Double.valueOf(strValueA);
                        Double valueB = Double.valueOf(strValueB);
                        numericPatternExpressionMap.put(columnId, new NumericPattern(NumericPattern.Operator.Interval,
                                valueA, valueB));
                    } else if (regex.matches("\\s*[\\-\\+]?[0-9]+\\s*")) {
                        regex = regex.trim();
                        Double valueA = Double.valueOf(regex.substring(0));
                        numericPatternExpressionMap.put(columnId, new NumericPattern(NumericPattern.Operator.Exact,
                                valueA, null));
                    } else {
                        numericPatternExpressionMap.remove(columnId);
                    }
                    break;
                }
            }
            rowSorter.sort();
        }
    }

    /**
     * @see javax.swing.event.TableColumnModelListener#columnAdded(javax.swing.event.TableColumnModelEvent)
     */
    @Override
    public void columnAdded(TableColumnModelEvent e) {
        createFilterComponents(tableHeader);
    }

    /**
     * @see javax.swing.event.TableColumnModelListener#columnRemoved(javax.swing.event.TableColumnModelEvent)
     */
    @Override
    public void columnRemoved(TableColumnModelEvent e) {
        createFilterComponents(tableHeader);
    }

    /**
     * @see javax.swing.event.TableColumnModelListener#columnSelectionChanged(javax.swing.event.ListSelectionEvent)
     */
    @Override
    public void columnSelectionChanged(ListSelectionEvent e) {
    }

    /**
     * @see javax.swing.event.TableColumnModelListener#columnMarginChanged(javax.swing.event.ChangeEvent)
     */
    @Override
    public void columnMarginChanged(ChangeEvent e) {
    }

    /**
     * @see javax.swing.event.TableColumnModelListener#columnMoved(javax.swing.event.TableColumnModelEvent)
     */
    @Override
    public void columnMoved(TableColumnModelEvent e) {
    }

    private static class NumericPattern {

        static enum Operator {
            Greater, Smaller, Interval, Exact;
        };

        Operator op = null;
        Number valueA;
        Number valueB;

        public NumericPattern(Operator op, Number valueA, Number valueB) {
            this.op = op;
            this.valueA = valueA;
            this.valueB = valueB;
        }

        /**
         * @param value
         * @return
         */
        public boolean matches(Object value) {
            switch (op) {

                case Greater:
                    return valueA.doubleValue() < Double.valueOf(value.toString());
                case Smaller:
                    return valueA.doubleValue() > Double.valueOf(value.toString());
                case Interval:
                    return valueA.doubleValue() < Double.valueOf(value.toString())
                            && valueB.doubleValue() > Double.valueOf(value.toString());
                case Exact:
                    return valueA.equals(Double.valueOf(value.toString()));
                default:
                    return false;
            }
        }

        public String toString() {
            StringBuffer buffer = new StringBuffer();
            switch (op) {
                case Greater:
                    buffer.append('>').append(valueA);
                    break;
                case Smaller:
                    buffer.append('<').append(valueA);
                    break;
                case Interval:
                    buffer.append('[').append(valueA).append(',').append(valueB).append(']');
                    break;
                case Exact:
                    buffer.append('=').append(valueA);
                    break;
                default:
                    buffer.append("<empty>");
            }
            return buffer.toString();
        }

    }
}
