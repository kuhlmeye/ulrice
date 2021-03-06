package net.ulrice.databinding.viewadapter.utable;

import java.awt.Cursor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.RowSorter.SortKey;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.BadLocationException;

import net.ulrice.ui.components.ClearableTextComponent;

import net.ulrice.Ulrice;
import net.ulrice.databinding.ObjectWithPresentation;
import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;
import net.ulrice.databinding.bufferedbinding.impl.Element;
import net.ulrice.databinding.bufferedbinding.impl.FilterMode;
import net.ulrice.databinding.viewadapter.impl.UBorder;
import net.ulrice.message.TranslationConstants;
import net.ulrice.message.TranslationUsage;
import net.ulrice.ui.components.ContextMenuMouseListener;

/**
 * @author christof
 */
public class UTableVAFilter extends RowFilter<UTableViewAdapter, Long> implements DocumentListener,
    TableColumnModelListener, ListDataListener {

    /** The logger used by this class. */
    private static final Logger LOG = Logger.getLogger(UTableVAFilter.class.getName());

    /** Locale for search for formatted numbers.. */
    private Locale formatLocale = Locale.getDefault();

    /**
     * The constant of the property key holding the identifier of the column id.
     */
    private static final String DOCUMENT_PROPERTY_FIELD_ID = "FIELD_ID";

    /** The list of column identifiers. */
    private List<String> columnIdentifiers = new ArrayList<String>(0);

    /** The list of filter modes per column. */
    private Map<String, FilterMode> columnFilterModes = new HashMap<String, FilterMode>();

    /** The map holding the current filter expressions for all columns by id. */
    private Map<String, Pattern> regexExpressionMap = new HashMap<String, Pattern>();
    private Map<String, Boolean> emptyOrFilledMap = new HashMap<String, Boolean>();
    private Map<String, NumericPattern> numericPatternExpressionMap = new HashMap<String, NumericPattern>();
    private Map<String, Object> comboBoxExpressionMap = new HashMap<String, Object>();
    private Map<String, List<String>> collapsedRowFilterMap = new HashMap<String, List<String>>();

    private UTableRowSorter rowSorter;

    private UTableVAHeader staticTableHeader;

    private UTableVAHeader scrollTableHeader;
    
    private boolean filterActive = false; 

	private boolean showDirtyAndInvalidElements = true;
	private boolean rebuildOnColumnChanges = true;

    
    private class BooleanFilterElement {
    	private String text;
		private String id;
    	
    	public BooleanFilterElement(String id, String text) {
    		this.id = id;
    		this.text = text;
    	}
    	
    	public String getId() {
			return id;
		}
    	
		@Override
    	public String toString() {
    		return text;
    	}
    }
     
    private static final String ALL = "All";
    private static final String YES = "Yes";
    private static final String NO = "No";
    
    

    /**
     * @param rowSorter
     * @param tableHeader2
     * @param columnModel
     */
    public UTableVAFilter(UTableRowSorter rowSorter, UTableVAHeader staticTableHeader,
        UTableVAHeader scrollTableHeader) {
        this.staticTableHeader = staticTableHeader;
        this.scrollTableHeader = scrollTableHeader;

        createFilterComponents(staticTableHeader);
        createFilterComponents(scrollTableHeader);
        staticTableHeader.getColumnModel().addColumnModelListener(this);
        scrollTableHeader.getColumnModel().addColumnModelListener(this);
        this.rowSorter = rowSorter;
    }

    /**
     * @param columnModel
     */
    private void createFilterComponents(UTableVAHeader tableHeader, int toIndex) {
        TableColumnModel columnModel = tableHeader.getColumnModel();

        if (rowSorter != null) {
            final UTableViewAdapter model = rowSorter.getModel();
            int colCount = columnIdentifiers.size();
            if (toIndex < model.getColumnCount()) {
                final ColumnDefinition< ?> columnDefinition = model.getAttributeModel().getColumns().get(toIndex);
                columnIdentifiers.add(columnDefinition.getId());
            }
            if (colCount == columnIdentifiers.size()) {
                this.columnIdentifiers = new ArrayList<String>(model.getColumnCount());
                for (int i = 0; i < model.getColumnCount(); i++) {
                    final ColumnDefinition< ?> columnDefinition = model.getAttributeModel().getColumns().get(i);
                    columnIdentifiers.add(columnDefinition.getId());
                }
            }

       }
        
        if (toIndex < columnModel.getColumnCount()) {
            createFilterComponentForColumn(tableHeader, columnModel, toIndex);
        }
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            createFilterComponentForColumn(tableHeader, columnModel, i);
        }
    }

    private void createFilterComponentForColumn(UTableVAHeader tableHeader, TableColumnModel columnModel, int i) {
        final TableColumn column = columnModel.getColumn(i);
        
        final ColumnDefinition< ?> columnDefinition = (ColumnDefinition< ?>) column.getHeaderValue();
        
        
        final FilterMode filterMode = columnDefinition.getFilterMode();
        columnFilterModes.put(columnDefinition.getId(), filterMode);
        if (!FilterMode.NoFilter.equals(filterMode)) {
            final JComponent component;

            switch (filterMode) {
                case RegEx:
                case Numeric:
                case Percent: {
                    JTextField field = new JTextField();
                    field.setName(columnDefinition.getId());
                    field.getDocument().putProperty(DOCUMENT_PROPERTY_FIELD_ID, columnDefinition.getId());
                    field.getDocument().addDocumentListener(this);
                    field.addMouseListener(new ContextMenuMouseListener());
                    field.setText(columnDefinition.getPreFilledFilterValue());
                    
                    ClearableTextComponent<JTextField> clearableField = new ClearableTextComponent<JTextField>(field);
                    clearableField.setBorder(new UBorder(true, false, false));
                    
                    component = clearableField;
                    break;
                }
                case Boolean: {
                    FilterComboBoxModel cbm = new FilterComboBoxModel(columnDefinition.getId());
                    
                    BooleanFilterElement allElement = new BooleanFilterElement(ALL, Ulrice.getTranslationProvider().getUlriceTranslation(TranslationUsage.Label, TranslationConstants.ALL).toString());
					cbm.addElement(allElement);
                    cbm.addElement(new BooleanFilterElement(YES, Ulrice.getTranslationProvider().getUlriceTranslation(TranslationUsage.Label, TranslationConstants.YES).toString()));
                    cbm.addElement(new BooleanFilterElement(NO, Ulrice.getTranslationProvider().getUlriceTranslation(TranslationUsage.Label, TranslationConstants.NO).toString()));
                    cbm.setSelectedItem(allElement);
                    cbm.addListDataListener(this);

                    JComboBox comboBox = new JComboBox(cbm);
                    component = comboBox;
                    break;
            	}
                case ComboBox: {
                    FilterComboBoxModel enumCbm = new FilterComboBoxModel(columnDefinition.getId());
                    BooleanFilterElement allElement = new BooleanFilterElement(ALL, Ulrice.getTranslationProvider().getUlriceTranslation(TranslationUsage.Label, TranslationConstants.ALL).toString());
					enumCbm.addElement(allElement);
                    if (columnDefinition.isUseValueRange() && columnDefinition.getValueRange() != null) {
                        for (Object value : columnDefinition.getValueRange()) {
                            enumCbm.addElement(value);
                        }
                    }
                    else if (columnDefinition.getColumnClass().isEnum()) {
                        for (Object enumValue : columnDefinition.getColumnClass().getEnumConstants()) {
                            enumCbm.addElement(enumValue);
                        }
                    }

                    enumCbm.addListDataListener(this);
                    JComboBox enumComboBox = new JComboBox(enumCbm);
                    component = enumComboBox;

                    break;
                }
                default:
                    component = null;
                    break;
            }

            if (component != null) {
                tableHeader.add(component, column.getIdentifier());
            }
        }
    }

    /**
     * @param columnModel
     */
    private void createFilterComponents(UTableVAHeader tableHeader) {
        TableColumnModel columnModel = tableHeader.getColumnModel();
        // TODO Totally inefficient
        if (rowSorter != null) {
            final UTableViewAdapter model = rowSorter.getModel();
            this.columnIdentifiers = new ArrayList<String>(model.getColumnCount());
            for (int i = 0; i < model.getColumnCount(); i++) {
                final ColumnDefinition< ?> columnDefinition =
                        model.getAttributeModel().getColumns().get(i);
                columnIdentifiers.add(columnDefinition.getId());
            }
        }

        tableHeader.removeAll();

        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            final TableColumn column = columnModel.getColumn(i);
            final ColumnDefinition< ?> columnDefinition = (ColumnDefinition< ?>) column.getHeaderValue();
            final FilterMode filterMode = columnDefinition.getFilterMode();
            columnFilterModes.put(columnDefinition.getId(), filterMode);
            if (!FilterMode.NoFilter.equals(filterMode)) {
                final JComponent component;

                switch (filterMode) {
                    case RegEx:
                    case Numeric:
                    case Percent: {
                        JTextField field = new JTextField();
                        field.setName(columnDefinition.getId());
                        field.getDocument().putProperty(DOCUMENT_PROPERTY_FIELD_ID, columnDefinition.getId());
                        field.getDocument().addDocumentListener(this);
                        field.setBorder(new UBorder(true, false, false));
                        field.setText(columnDefinition.getPreFilledFilterValue());
                        
                        ClearableTextComponent<JTextField> clearableField = new ClearableTextComponent<JTextField>(field);
                        clearableField.setBorder(new UBorder(true, false, false));
                        
                        component = clearableField;
                        break;
                    }
                    case Boolean: {
                        FilterComboBoxModel cbm = new FilterComboBoxModel(columnDefinition.getId());
                        BooleanFilterElement allElement = new BooleanFilterElement(ALL, Ulrice.getTranslationProvider().getUlriceTranslation(TranslationUsage.Label, TranslationConstants.ALL).toString());
    					cbm.addElement(allElement);
                        cbm.addElement(new BooleanFilterElement(YES, Ulrice.getTranslationProvider().getUlriceTranslation(TranslationUsage.Label, TranslationConstants.YES).toString()));
                        cbm.addElement(new BooleanFilterElement(NO, Ulrice.getTranslationProvider().getUlriceTranslation(TranslationUsage.Label, TranslationConstants.NO).toString()));
                        cbm.setSelectedItem(allElement);
                        cbm.addListDataListener(this);
                        
                        JComboBox comboBox = new JComboBox(cbm);
                        comboBox.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        component = comboBox;
                        break;
                    }
                    case ComboBox: {
                        FilterComboBoxModel enumCbm = new FilterComboBoxModel(columnDefinition.getId());
                        BooleanFilterElement allElement = new BooleanFilterElement(ALL, Ulrice.getTranslationProvider().getUlriceTranslation(TranslationUsage.Label, TranslationConstants.ALL).toString());
    					enumCbm.addElement(allElement);
                        if (columnDefinition.isUseValueRange() && columnDefinition.getValueRange() != null) {
                            for (Object value : columnDefinition.getValueRange()) {
                                enumCbm.addElement(value);
                            }
                        }
                        else if (columnDefinition.getColumnClass().isEnum()) {
                            for (Object enumValue : columnDefinition.getColumnClass().getEnumConstants()) {
                                enumCbm.addElement(enumValue);
                            }
                        }

                        enumCbm.addListDataListener(this);
                        JComboBox enumComboBox = new JComboBox(enumCbm);
                        enumComboBox.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        component = enumComboBox;
                        break;
                    }
                    default:
                        component = null;
                        break;
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
    public boolean include(javax.swing.RowFilter.Entry< ? extends UTableViewAdapter, ? extends Long> entry) {
        
        if (!filterActive) {
            return true;
        }
        
        boolean include = true;
        Long id = entry.getIdentifier();
        Element element = entry.getModel().getComponent().getElementById(id);
        if (showDirtyAndInvalidElements && element != null && (element.isDirty() || !element.isValid())) {
            return true;
        }
        int count = entry.getValueCount();
        for (int i = 0; i < count && include; i++) {

            String columnId = columnIdentifiers.get(i);

            if (collapsedRowFilterMap.containsKey(columnId) && (element.getCurrentValue() instanceof HeaderCapable)) {
                HeaderCapable item = (HeaderCapable) element.getCurrentValue();
                if (!item.isHeader() && (collapsedRowFilterMap.get(columnId).contains(entry.getStringValue(i)))) {
                    return false;
                }
            }

            if (element != null && element.getOriginalValue() instanceof HeaderCapable) {
                HeaderCapable item = (HeaderCapable) element.getOriginalValue();
                if (item.isHeader()) {
                    return true;
                }
            }

            UTableComponent uTableComponent = entry.getModel().getComponent();
            @SuppressWarnings("rawtypes")
            ColumnDefinition colDef = uTableComponent.getColumnById(columnId);
            UTable table = null;
            if (colDef.isFixedColumn()) {
                table = (UTable) uTableComponent.getStaticTable();
            }
            else {
                table = (UTable) uTableComponent.getScrollTable();
            }

            TableCellRenderer tableCellRenderer = uTableComponent.getColumnById(columnId).getCellRenderer();
            if (tableCellRenderer == null) {
                tableCellRenderer = table.getDefaultRenderer(colDef.getColumnClass());
            }
            if (tableCellRenderer != null
                && StringBasedTableCellRenderer.class.isAssignableFrom(tableCellRenderer.getClass())) {
                StringBasedTableCellRenderer c = (StringBasedTableCellRenderer) tableCellRenderer;
                include &=
                        includeValue(columnId, id, c.getString(entry.getValue(i), table, colDef));
            }
            else {
                include &= includeValue(columnId, id, entry.getValue(i));
            }
        }
        return include;
    }

    /**
     * @param columnId
     * @param identifier
     * @param value
     * @return
     */
    private boolean includeValue(String columnId, Long identifier, Object value) {
        String strValue = value == null ? "" : value.toString();
        boolean isPercentMode = false;
        if (columnFilterModes != null && columnFilterModes.containsKey(columnId)) {
            switch (columnFilterModes.get(columnId)) {
                case Boolean:
                case ComboBox:
                    if(comboBoxExpressionMap.containsKey(columnId)){
                        Object reference = comboBoxExpressionMap.get(columnId);
                        if(reference != null){
                            // if (reference.getClass().isEnum()) {
                            // if (reference.toString().equals(value)) {
                            // return true;
                            // }
                            // else {
                            // return false;
                            // }
                            // }
                            if(reference.equals(value)){
                                return true;
                            }else{
                                return false;
                            }
                        }else if(value == null){
                            return true;
                        }else{
                            return false;
                        }
                    }
                    break;
                case RegEx:
                    Pattern pattern = regexExpressionMap.get(columnId);
                    if(pattern == null) {
                        Boolean shouldBeEmpty = emptyOrFilledMap.get(columnId);
                        if(shouldBeEmpty != null && shouldBeEmpty) {
                            return value == null;
                        } else if(shouldBeEmpty != null && !shouldBeEmpty) {
                            return value != null;
                        }
                    } else {                                                
                        LOG.finest("ColumnId: " + columnId + ", Value: " + strValue + ", Pattern: "
                            + pattern.pattern());
                        // If the value is a map from I18nTextField or I18nTextArea, it should be search in all
                        // languages for the text
                        if (value instanceof Map) {
                            for (String mapValue : ((Map<String, String>) value).values()) {
                                if (pattern.matcher(mapValue).matches()) {
                                    return true;
                                }
                            }
                            return false;
                        }
                        else {
                            return pattern.matcher(strValue).matches();
                        }
                    }
                    break;
                case Percent:
                    isPercentMode = true;
                case Numeric:
                    NumericPattern numericPattern = numericPatternExpressionMap.get(columnId);
                    if (numericPattern != null) {
                        // if (numericPattern.pattern().startsWith("+")) {
                        // return value != null;
                        // }
                        // else if (numericPattern.pattern().startsWith("-")) {
                        // return value == null;
                        // }
                        // else {
                        LOG.finest("ColumnId: " + columnId + ", Value: " + strValue + ", Pattern: "
                            + numericPattern.toString());
                        if (isPercentMode) {
                            try {
                                value = new BigDecimal(strValue).multiply(new BigDecimal(100)).toString();
                            }
                            catch (NumberFormatException e) {
                                // no problem
                            }
                        }
                        return value == null ? false : numericPattern.matches(value);
                        // }
                    }
                default:
                    break;
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
        }
        catch (BadLocationException e1) {
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
        }
        catch (BadLocationException e1) {
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
        }
        catch (BadLocationException e1) {
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

        if(text == null || text.isEmpty()){
            regexExpressionMap.remove(columnId);
            emptyOrFilledMap.remove(columnId);
            numericPatternExpressionMap.remove(columnId);
        }
        else if (columnFilterModes.containsKey(columnId)) {

            switch (columnFilterModes.get(columnId)) {
                case NoFilter:
                    break;                    
                case RegEx: {
                    if (text.equals("+")) {
                        regexExpressionMap.remove(columnId);
                        emptyOrFilledMap.put(columnId, Boolean.FALSE);
                    } else if (text.equals("-")) {
                        regexExpressionMap.remove(columnId);
                        emptyOrFilledMap.put(columnId, Boolean.TRUE);
                    } else {
                        emptyOrFilledMap.remove(columnId);
                        String regex = correctRegEx(text);                    
    
                        try {
                            final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
                            regexExpressionMap.put(columnId, pattern);
                        }
                        catch (PatternSyntaxException e) {
                            LOG.log(Level.FINER, "Could not compile regex: " + regex, e);
                        }
                    }
                    break;
                }
                case Percent:
                case Numeric: {

                    // Allowed:
                    //  > -0.9
                    //  < -0.9
                    //  <> -0.9
                    // [0.8 1.0]

                    String trimmedText = text.trim();
                    if (trimmedText.startsWith(">") && trimmedText.length() > 1) {
                        trimmedText = trimmedText.substring(1);

                        Scanner numericScanner = new Scanner(trimmedText);
                        numericScanner.useLocale(formatLocale);
                        if(numericScanner.hasNextDouble()) {
                            try {
                                final Double valueA = numericScanner.nextDouble();
                                numericPatternExpressionMap.put(columnId, new NumericPattern(NumericPattern.Operator.Greater, valueA, null));
                            }
                            catch (InputMismatchException e) {
                                LOG.log(Level.FINER, "Could not get double value from " + text, e);
                            }
                        }
                    } else if (trimmedText.startsWith("<>") && trimmedText.length() > 2) {
                        trimmedText = trimmedText.substring(2);

                        Scanner numericScanner = new Scanner(trimmedText);
                        numericScanner.useLocale(formatLocale);

                        if(numericScanner.hasNextDouble()) {
                            try {
                                final Double valueA = numericScanner.nextDouble();
                                numericPatternExpressionMap.put(columnId, new NumericPattern(NumericPattern.Operator.NotEqual, valueA, null));
                            }
                            catch (InputMismatchException e) {
                                LOG.log(Level.FINER, "Could not get double value from " + text, e);
                            }
                        }
                    } else if (trimmedText.startsWith("<") && trimmedText.length() > 1) {
                        trimmedText = trimmedText.substring(1);

                        Scanner numericScanner = new Scanner(trimmedText);
                        numericScanner.useLocale(formatLocale);

                        if(numericScanner.hasNextDouble()) {
                            try {
                                final Double valueA = numericScanner.nextDouble();
                                numericPatternExpressionMap.put(columnId, new NumericPattern(NumericPattern.Operator.Smaller, valueA, null));
                            }
                            catch (InputMismatchException e) {
                                LOG.log(Level.FINER, "Could not get double value from " + text, e);
                            }
                        }
                    } else  if (trimmedText.startsWith("[") && trimmedText.length() > 1) {
                        trimmedText = trimmedText.substring(1);
                        String[] interval = trimmedText.split(";");
                        if(interval.length == 2) {
                            try {
                                Double valueA = Double.parseDouble(interval[0]);
                                Double valueB = Double.parseDouble(interval[1]);

                                numericPatternExpressionMap.put(columnId, new NumericPattern(NumericPattern.Operator.Interval, valueA, valueB));
                            } catch(NumberFormatException e) {
                                LOG.log(Level.FINER, "Could not get double value from " + text, e);
                            }
                        }
                    } else {

                        Scanner numericScanner = new Scanner(trimmedText);
                        numericScanner.useLocale(formatLocale);

                        if(numericScanner.hasNextDouble()) {
                            try {
                                final Double valueA = numericScanner.nextDouble();
                                numericPatternExpressionMap.put(columnId, new NumericPattern(NumericPattern.Operator.Exact, valueA, null));
                            }
                            catch (InputMismatchException e) {
                                LOG.log(Level.FINER, "Could not get double value from " + text, e);
                            }

                        } else {

                            String regex = correctRegEx(text);
                            try {
                                final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                                regexExpressionMap.put(columnId, pattern);
                            }
                            catch (PatternSyntaxException e) {
                                LOG.log(Level.FINER, "Could not compile regex: " + regex, e);
                            }
                        }
                    break;
                    }
                }
                default:
                    break;
            }
        }
        
        determineFilterActive();
        rowSorter.getModel().fireTableDataChanged();
    }

    private void determineFilterActive() {
        filterActive = !numericPatternExpressionMap.isEmpty() || !regexExpressionMap.isEmpty() || !emptyOrFilledMap.isEmpty() || !comboBoxExpressionMap.isEmpty() || !collapsedRowFilterMap.isEmpty();
    }

    private String correctRegEx(String regex) {
        if(regex.startsWith("\\+") || regex.startsWith("\\-")) {
            regex = regex.substring(1);
        } 
        
        if (regex.contains("(")) {
            regex = regex.replace("(", "\\(");
        }
        if (regex.contains(")")) {
            regex = regex.replace(")", "\\)");
        }
        regex = regex.replace("\\", "\\\\");
        regex = regex.replace("+", "\\+");
        regex = regex.replace(".", "\\.");
        regex = regex.replace("?", ".");
        regex = regex.replace("*", ".*");

        regex += ".*";
        return regex;
    }

    /**
     * @see javax.swing.event.TableColumnModelListener#columnAdded(javax.swing.event.TableColumnModelEvent)
     */
    @Override
    public void columnAdded(TableColumnModelEvent e) {
        TableColumnModel colModel = (TableColumnModel) e.getSource();
        int toIndex = e.getToIndex();
        if (colModel.equals(staticTableHeader.getColumnModel()) && rebuildOnColumnChanges) {
            createFilterComponents(staticTableHeader);
            // createFilterComponents(staticTableHeader, toIndex);
        }
        if (colModel.equals(scrollTableHeader.getColumnModel()) && rebuildOnColumnChanges) {
            createFilterComponents(scrollTableHeader);
            // createFilterComponents(scrollTableHeader, toIndex);
        }
    }

    public void rebuildFilter() {
        numericPatternExpressionMap.clear();
        regexExpressionMap.clear();
        createFilterComponents(staticTableHeader);
        createFilterComponents(scrollTableHeader);
    }

    /**
     * @see javax.swing.event.TableColumnModelListener#columnRemoved(javax.swing.event.TableColumnModelEvent)
     */
    @Override
    public void columnRemoved(TableColumnModelEvent e) {
        TableColumnModel colModel = (TableColumnModel) e.getSource();
        if (colModel.equals(staticTableHeader.getColumnModel()) && rebuildOnColumnChanges) {
            createFilterComponents(staticTableHeader);
        }
        if (colModel.equals(scrollTableHeader.getColumnModel()) && rebuildOnColumnChanges) {
            createFilterComponents(scrollTableHeader);
        }
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

    @Override
    public void intervalAdded(ListDataEvent e) {
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
    }

    @Override
    public void contentsChanged(ListDataEvent e) {

        FilterComboBoxModel cbModel = (FilterComboBoxModel) e.getSource();
        Object value = cbModel.getSelectedItem();

        if(value instanceof ObjectWithPresentation< ?> && ((ObjectWithPresentation< ?>) value).getValue() == null){
            //Stefan Huber: With this we can also filter all cells with value "null"
            filterComboChanged(cbModel.columndId, null, true);
        }
        else if(value instanceof BooleanFilterElement) {
        	BooleanFilterElement filterElement = (BooleanFilterElement) value;
        	if (ALL.equals(filterElement.id)) {
	            filterComboChanged(cbModel.columndId, null);
	        }
	        else if (YES.equals(filterElement.id)) {
	            filterComboChanged(cbModel.columndId, Boolean.TRUE);
	        }
	        else if (NO.equals(filterElement.id)) {
	            filterComboChanged(cbModel.columndId, Boolean.FALSE);
	        }
        }
        else if (value instanceof ObjectWithPresentation< ?>) {
            filterComboChanged(cbModel.columndId, ((ObjectWithPresentation< ?>) value).getValue());
        }
        else if (value != null) {
            filterComboChanged(cbModel.columndId, value);
        }
        else {
            filterComboChanged(cbModel.columndId, null);
        }
    }

    private void filterComboChanged(String columndId, Object object){
        filterComboChanged(columndId, object, false);
    }

    private void filterComboChanged(String columnId, Object value, boolean isEmptySelected) {
        if(isEmptySelected){
            comboBoxExpressionMap.put(columnId, null);
        }else if (value == null){
            comboBoxExpressionMap.remove(columnId);
        }else{
        	Object filterValue = value;
        	UTableComponent uTableComponent = null;
        	if(rowSorter != null && rowSorter.getModel() != null  && rowSorter.getModel().getComponent() != null) {
        		uTableComponent = rowSorter.getModel().getComponent();
        	}
        	
        	// Try to convert to string using cell renderer, if available
        	if(uTableComponent != null) {
    	        @SuppressWarnings("rawtypes")
    	        ColumnDefinition colDef = uTableComponent.getColumnById(columnId);
    	        UTable table = null;
    	        if (colDef.isFixedColumn()) {
    	            table = (UTable) uTableComponent.getStaticTable();
    	        }
    	        else {
    	            table = (UTable) uTableComponent.getScrollTable();
    	        }
    	
    	        TableCellRenderer tableCellRenderer = uTableComponent.getColumnById(columnId).getCellRenderer();
    	        if (tableCellRenderer == null) {
    	            tableCellRenderer = table.getDefaultRenderer(colDef.getColumnClass());
    	        }
    	        if (tableCellRenderer != null
    	            && StringBasedTableCellRenderer.class.isAssignableFrom(tableCellRenderer.getClass())) {
    	            StringBasedTableCellRenderer c = (StringBasedTableCellRenderer) tableCellRenderer;
    	            
    	            filterValue = c.getString(value, table, colDef);
    	        }
        	}
        	
            comboBoxExpressionMap.put(columnId, filterValue);
        }
        determineFilterActive();
        rowSorter.getModel().fireTableDataChanged();
    }

    private static class NumericPattern {

        static enum Operator {
            Greater, Smaller, Interval, Exact, NotEqual;
        }

        protected Operator op = null;
        protected Number valueA;
        protected Number valueB;

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
            try {
                Double.parseDouble(value.toString());
            }
            catch (NumberFormatException nfe) {
                return false;
            }
            
            switch (op) {
                case Greater:
                    return valueA.doubleValue() < Double.valueOf(value.toString());
                case Smaller:
                    return valueA.doubleValue() > Double.valueOf(value.toString());
                case NotEqual:
                    return valueA.doubleValue() != Double.valueOf(value.toString());
                case Interval:
                    return valueA.doubleValue() < Double.valueOf(value.toString())
                        && valueB.doubleValue() > Double.valueOf(value.toString());
                case Exact:
                    return valueA.equals(Double.valueOf(value.toString()));
                default:
                    return false;
            }
        }

        @Override
        public String toString() {
            StringBuffer buffer = new StringBuffer();
            switch (op) {
                case Greater:
                    buffer.append('>').append(valueA);
                    break;
                case Smaller:
                    buffer.append('<').append(valueA);
                    break;
                case NotEqual:
                    buffer.append("<>").append(valueA);
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

    @SuppressWarnings("serial")
    private class FilterComboBoxModel extends DefaultComboBoxModel {
        private String columndId;

        public FilterComboBoxModel(String columndId) {
            super();
            this.columndId = columndId;
        }
    }

    public void collapseRow(String columnId, String value) {
        if (collapsedRowFilterMap.containsKey(columnId)) {
            if (collapsedRowFilterMap.get(columnId).contains(value)) {
                collapsedRowFilterMap.get(columnId).remove(value);
            }
            else {
                collapsedRowFilterMap.get(columnId).add(value);
            }
        }
        else {
            collapsedRowFilterMap.put(columnId, new ArrayList<String>(Arrays.asList(value)));
        }
        List< ? extends SortKey> sortKeys = rowSorter.getSortKeys();
        Comparator[] comparators = new Comparator[rowSorter.getModel().getColumnCount()];
        for (int i = 0; i < rowSorter.getModel().getColumnCount(); i++) {
            comparators[i] = rowSorter.getComparator(i);
        }
        rowSorter.sort();
        rowSorter.getModel().fireTableStructureChanged();
        for (int i = 0; i < rowSorter.getModel().getColumnCount(); i++) {
            rowSorter.setComparator(i, comparators[i]);
        }
        rowSorter.setSortKeys(sortKeys);
        determineFilterActive();
    }

    public void collapseRow(String columnId, String value, boolean collapse) {
        if (collapse) {
            if (collapsedRowFilterMap.containsKey(columnId)) {
                collapsedRowFilterMap.get(columnId).add(value);
            }
            else {
                collapsedRowFilterMap.put(columnId, new ArrayList<String>(Arrays.asList(value)));
            }
        }
        else {
            List<String> list = collapsedRowFilterMap.get(columnId);
            if (list != null) {
                list.remove(value);
            }
        }
        List< ? extends SortKey> sortKeys = rowSorter.getSortKeys();
        Comparator[] comparators = new Comparator[rowSorter.getModel().getColumnCount()];
        for (int i = 0; i < rowSorter.getModel().getColumnCount(); i++) {
            comparators[i] = rowSorter.getComparator(i);
        }
        rowSorter.sort();
        rowSorter.getModel().fireTableStructureChanged();
        for (int i = 0; i < rowSorter.getModel().getColumnCount(); i++) {
            rowSorter.setComparator(i, comparators[i]);
        }
        rowSorter.setSortKeys(sortKeys);
        determineFilterActive();
    }

    public void useFormatLocale(Locale locale) {
        this.formatLocale = locale;
    }

    public boolean isShowDirtyAndInvalidElements() {
		return showDirtyAndInvalidElements;
	}
    
    public void setShowDirtyAndInvalidElements(boolean showDirtyAndInvalidElements) {
		this.showDirtyAndInvalidElements = showDirtyAndInvalidElements;
	}
    
    public boolean isRebuildOnColumnChanges() {
        return rebuildOnColumnChanges;
    }

    /**
     * With this trick you can temporarily disable the recalculation of the filters for every add/remove column.
     * Recommended if a lot of columns should be added/removed
     */
    public void setRebuildOnColumnChanges(boolean rebuildOnColumnChanges) {
        this.rebuildOnColumnChanges = rebuildOnColumnChanges;

        if (rebuildOnColumnChanges) {
            rebuildFilter();
        }
    }
}
