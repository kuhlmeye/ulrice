package net.ulrice.databinding.bufferedbinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;

import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;
import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition.ColumnType;
import net.ulrice.databinding.bufferedbinding.impl.FilterMode;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.modelaccess.impl.DynamicReflectionMVA;
import net.ulrice.databinding.modelaccess.impl.IndexedReflectionMVA;
import net.ulrice.databinding.reflect.ReflectionUtils;
import net.ulrice.databinding.viewadapter.utable.UTableRenderer;
import net.ulrice.message.ModuleTranslationProvider;
import net.ulrice.message.TranslationUsage;

public class TableAMBuilder {

    private Object modelRoot;
    private String elementCollectionPath;
    private Class<?> modelRowClass;
    private boolean readOnly;

    protected final List<ColumnDefinition< ?>> columnDefs = new ArrayList<ColumnDefinition< ?>>();
    protected final Map<String, ColumnDefinition< ?>> columnDefsByPath = new HashMap<String, ColumnDefinition< ?>>();
    protected final Map<String, ColumnDefinition< ?>> derivedColumnDefsByPath = new HashMap<String, ColumnDefinition< ?>>();
    protected String[] uniqueKeyColumnPaths;

    protected List<SortKey> defaultSortKeys;
    protected List<SortKey> mandatorySortKeys;

    protected String pathToChildren;
    private IFTableAMBuilderCallback callback;


    public TableAMBuilder(Object modelRoot, String elementCollectionPath, Class<?> modelRowClass, ModuleTranslationProvider translationProvider, IFTableAMBuilderCallback callback) {        
        init(modelRoot, elementCollectionPath, modelRowClass, callback);
    }
    
    protected TableAMBuilder() {        
    }
    
    protected void init(Object modelRoot, String elementCollectionPath, Class<?> modelRowClass, IFTableAMBuilderCallback callback) {
        this.modelRoot = modelRoot;
        this.elementCollectionPath = elementCollectionPath;
        this.modelRowClass = modelRowClass;
        this.callback = callback;
    }

    public TableAMBuilder(Object modelRoot, String elementCollectionPath, Class<?> modelRowClass) {
        this(modelRoot, elementCollectionPath, modelRowClass, null, new DefaultTableAMBuilderCallback());
    }
   
    public TableAMBuilder(Object modelRoot, String elementCollectionPath, Class<?> modelRowClass, IFTableAMBuilderCallback callback) {
        this(modelRoot, elementCollectionPath, modelRowClass, null, callback);
    }

    public void addUniqueKey(String... columnPaths) {
        List<String> approvedPathList = new ArrayList<String>();
        for (String path : columnPaths) {
            if (columnDefsByPath.get(path) != null && !columnDefsByPath.get(path).getColumnType().equals(ColumnType.Hidden)) {
                approvedPathList.add(path);
            }
            if (derivedColumnDefsByPath.get(path) != null) {
                approvedPathList.add(path);
            }
        }

        this.uniqueKeyColumnPaths = new String[approvedPathList.size()];
        int index = 0;
        for (String approvedPath : approvedPathList) {
            this.uniqueKeyColumnPaths[index++] = approvedPath;
        }
    }

    public void addPostRendererToAllColumns(UTableRenderer renderer) {
        for (ColumnDefinition< ?> col : columnDefs) {
            col.addPostRenderer(renderer);
        }
    }

    public void addPreRendererToAllColumns(UTableRenderer renderer) {
        for (ColumnDefinition< ?> col : columnDefs) {
            col.addPreRenderer(renderer);
        }
    }

    /**
     * This is a convenience method that determines the type of the field / column via reflection on the model.
     */
    @SuppressWarnings("rawtypes")
    public <T> ColumnDefinition addColumn(String path, ColumnType columnType) {
        return addColumn(path, ReflectionUtils.getInstance().getDotSeparatedFieldType(modelRowClass, path), columnType);
    }

    @SuppressWarnings("rawtypes")
    public <T> ColumnDefinition addColumn(String path, ColumnType columnType, FilterMode filterMode) {
        return addColumn(path, ReflectionUtils.getInstance().getDotSeparatedFieldType(modelRowClass, path), columnType, filterMode);
    }

    @SuppressWarnings("rawtypes")
    public <T> ColumnDefinition addColumn(String columnName, String path, ColumnType columnType) {
        return addColumn(columnName, path, ReflectionUtils.getInstance().getDotSeparatedFieldType(modelRowClass, path), columnType, null, true);
    }

    @SuppressWarnings("rawtypes")
    public <T> ColumnDefinition addColumn(String columnName, String path, ColumnType columnType, FilterMode filterMode) {
        return addColumn(columnName, path, ReflectionUtils.getInstance().getDotSeparatedFieldType(modelRowClass, path), columnType, filterMode, true);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> ColumnDefinition addColumn(String path, IFValueConverter converter, ColumnType columnType) {
        ColumnDefinition columnDefinition = addColumn(path, converter.getViewType(ReflectionUtils.getInstance().getDotSeparatedFieldType(modelRowClass, path)), columnType);
        columnDefinition.setValueConverter(converter);
        return columnDefinition;
    }

    @SuppressWarnings("rawtypes")
    public <T> ColumnDefinition addColumn(String path, Class< ?> typeAfterConversion, ColumnType columnType) {
        return addColumn(getAttributeFromPath(path), path, typeAfterConversion, columnType, null, true);
    }

    @SuppressWarnings("rawtypes")
    public <T> ColumnDefinition addColumn(String path, Class< ?> typeAfterConversion, ColumnType columnType, FilterMode filterMode) {
        return addColumn(getAttributeFromPath(path), path, typeAfterConversion, columnType, filterMode, true);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> ColumnDefinition addColumn(final String columnName, final String path, final Class<T> typeAfterConversion, final ColumnType columnType, final FilterMode filterMode, boolean useValidation) {
        final ColumnDefinition<T> newColumn = new ColumnDefinition(columnName, new DynamicReflectionMVA(path, path), typeAfterConversion);
        newColumn.setComparator((Comparator<T>) createComparator(typeAfterConversion));
        newColumn.setColumnType(columnType);

        if (filterMode != null) {
            newColumn.setFilterMode(filterMode);
        }
        
        ModuleTranslationProvider tp = callback.getTranslationProvider();
        if(tp != null) {
            newColumn.setColumnName(tp.getTranslationText(TranslationUsage.TableColumn, columnName));            
        } else {
            newColumn.setColumnName(columnName);
        }
        
        if(tp != null && tp.isTranslationAvailable(TranslationUsage.Tooltip, columnName)) {
            newColumn.setColumnTooltip(tp.getTranslationText(TranslationUsage.Tooltip, columnName));
        }

        return addColumn(path, newColumn);
    }
    
    /**
     * sets the path to a collection of children to build a tree table
     */
    public <T> TableAMBuilder setPathToChildren(String pathToChildren) {
        this.pathToChildren = pathToChildren;
        return this;
    }

    @SuppressWarnings("rawtypes")
    protected Comparator< ?> createComparator(final Class< ?> typeAfterConversion) {

        if (Comparable.class.isAssignableFrom(typeAfterConversion)) {
            return new Comparator<Comparable>() {

                @SuppressWarnings("unchecked")
                @Override
                public int compare(final Comparable o1, final Comparable o2) {
                    return o1.compareTo(o2);
                }
            };
        }

        return null;
    }


    public <T extends ColumnDefinition<?>> T addColumn(T columnDefinition) {
        columnDefs.add(columnDefinition);
        
        return columnDefinition;
    }

    public <T extends ColumnDefinition<?>> T addColumn(String path, T columnDefinition) {
        addColumn(columnDefinition);
        
        columnDefsByPath.put(path, columnDefinition);
        
        return columnDefinition;
    }

    protected String getAttributeFromPath(String path) {
        String[] split = path.split("\\.");
        String attributeName = path;
        if (split.length > 0) {
            attributeName = split[split.length - 1];
        }

        if (attributeName.length() == 1) {
            return String.valueOf(Character.toUpperCase(attributeName.charAt(0)));
        }
        else if (attributeName.length() > 1) {
            return Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);
        }

        return attributeName;
    }

    public void setPreferredColumnWidth(String path, int width) {
        final ColumnDefinition< ?> colDef = getColumn(path);
        if (colDef == null) {
            return;
        }
        colDef.setPreferredWidth(width);
    }

    public void setPreferredColumnWidth(int index, int width) {
        final ColumnDefinition< ?> colDef = getColumn(index);
        if (colDef == null) {
            return;
        }
        colDef.setPreferredWidth(width);
    }

    public void addDefaultSortKey(String path) {
        addDefaultSortKey(path, SortOrder.ASCENDING);
    }

    public void addDefaultSortKey(String path, SortOrder sortOrder) {
        addDefaultSortKey(getIndexForColumn(getColumn(path)), sortOrder);
    }

    public void addDefaultSortKey(int index) {
        addDefaultSortKey(index, SortOrder.ASCENDING);
    }

    public void addDefaultSortKey(int index, SortOrder sortOrder) {
        if (index == -1) {
            return;
        }
        if (defaultSortKeys == null) {
            defaultSortKeys = new ArrayList<SortKey>();
        }
        defaultSortKeys.add(new SortKey(index, sortOrder));
    }

    public void addMandatorySortKey(String path) {
        addMandatorySortKey(path, SortOrder.ASCENDING);
    }

    public void addMandatorySortKey(String path, SortOrder sortOrder) {
        addMandatorySortKey(getIndexForColumn(getColumn(path)), sortOrder);
    }

    public void addMandatorySortKey(int index) {
        addMandatorySortKey(index, SortOrder.ASCENDING);
    }

    public void addMandatorySortKey(int index, SortOrder sortOrder) {
        if (index == -1) {
            return;
        }
        if (mandatorySortKeys == null) {
            mandatorySortKeys = new ArrayList<SortKey>();
        }
        mandatorySortKeys.add(new SortKey(index, sortOrder));
    }

    @SuppressWarnings("rawtypes")
    public ColumnDefinition getColumn(int index) {
        return columnDefs.get(index);
    }

    public int getColumnCount() {
        return columnDefs.size();
    }

    @SuppressWarnings("rawtypes")
    public ColumnDefinition getColumn(String path) {
        return columnDefsByPath.get(path);
    }

    @SuppressWarnings("rawtypes")
    public int getIndexForColumn(ColumnDefinition def) {
        for (int i = 0; i < columnDefs.size(); i++) {
            if (def == columnDefs.get(i)) {
                return i;
            }
        }
        return -1;
    }

    public TableAM build() {
        final TableAM result = new TableAM(new IndexedReflectionMVA(modelRoot, elementCollectionPath, modelRowClass), null);
        setColumnsAndKeys(result);
        return result;
    }

    private void setColumnsAndKeys(TableAM result) {
        result.setPathToChildren(pathToChildren);

        if (uniqueKeyColumnPaths != null) {
            String[] uniqueColumnIds = new String[uniqueKeyColumnPaths.length];
            for (int i = 0; i < uniqueKeyColumnPaths.length; i++) {
                if (columnDefsByPath.get(uniqueKeyColumnPaths[i]) != null) {
                    uniqueColumnIds[i] = columnDefsByPath.get(uniqueKeyColumnPaths[i]).getId();
                }
                else if (derivedColumnDefsByPath.get(uniqueKeyColumnPaths[i]) != null) {
                    uniqueColumnIds[i] = derivedColumnDefsByPath.get(uniqueKeyColumnPaths[i]).getId();
                }
                else {
                    throw new IllegalArgumentException(String.format("Unique column id: '%s' is not found in column definitions!", uniqueKeyColumnPaths[i]));
                }
            }
            result.setUniqueConstraint(uniqueColumnIds);
        }
        for (ColumnDefinition< ?> colDef : columnDefs) {
            result.addColumn(colDef);
        }
        result.setReadOnly(isReadOnly());
        result.setMandatorySortKeys(getMandatorySortKeys());

        LinkedHashMap<Integer, SortKey> sortKeyMap = new LinkedHashMap<Integer, SortKey>();
        if (getDefaultSortKeys() != null) {
            for (SortKey key : getDefaultSortKeys()) {
                sortKeyMap.put(key.getColumn(), key);
            }
        }
        result.setDefaultSortKeys(new ArrayList<SortKey>(sortKeyMap.values()));
    }

    public List<SortKey> getDefaultSortKeys() {
        return defaultSortKeys;
    }

    public void setDefaultSortKeys(List<SortKey> defaultSortKeys) {
        this.defaultSortKeys = defaultSortKeys;
    }

    public List<SortKey> getMandatorySortKeys() {
        return mandatorySortKeys;
    }

    public void setMandatorySortKeys(List<SortKey> mandatorySortKeys) {
        this.mandatorySortKeys = mandatorySortKeys;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isReadOnly() {
        return readOnly;
    }
}
