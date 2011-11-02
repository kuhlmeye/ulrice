package net.ulrice.sample;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;
import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition.ColumnType;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.modelaccess.IFDynamicModelValueAccessor;
import net.ulrice.databinding.modelaccess.impl.DynamicReflectionMVA;
import net.ulrice.databinding.modelaccess.impl.IndexedReflectionMVA;
import net.ulrice.databinding.modelaccess.impl.UlriceReflectionUtils;


/**
 * This is a convenience class that helps in building TableAM instances. It hides some of the details, especially
 *  for simple cases. 
 * 
 * @author arno
 */
public class TableAMBuilder {
    private Object modelRoot;
    private String elementCollectionPath;
    private Class<?> modelRowClass;

    private final List<ColumnDefinition<?>> columnDefs = new ArrayList<ColumnDefinition<?>>();
    private final Map<String, ColumnDefinition<?>> columnDefsByPath = new HashMap<String, ColumnDefinition<?>>();
    
    public TableAMBuilder(Object modelRoot, String elementCollectionPath, Class<?> modelRowClass) {
        init (modelRoot, elementCollectionPath, modelRowClass);
    }
    
    protected TableAMBuilder() {
    }
    
    protected void init (Object pModelRoot, String pElementCollectionPath, Class<?> pModelRowClass) {
        this.modelRoot = pModelRoot;
        this.elementCollectionPath = pElementCollectionPath;
        this.modelRowClass = pModelRowClass; //TODO arno derive this via reflection
    }
    
    /**
     * This is a convenience method that determines the type of the field / column via reflection on the model.
     */
    public TableAMBuilder addColumn(String path) {
        return addColumn (path, UlriceReflectionUtils.getFieldType(modelRowClass, path));
    }

    /**
     * This is a convenience method that determines the type of the column based on the converter and reflection
     *  on the model. It is based on the assumption that if the view and model types differ, a converter is
     *  usually provided.
     */
    public TableAMBuilder addColumn(String path, IFValueConverter converter) {
        addColumn(path, converter.getViewType(UlriceReflectionUtils.getFieldType(modelRowClass, path)));
        getColumn(path).setValueConverter(converter);
        return this;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public TableAMBuilder addColumn(String path, Class<?> typeAfterConversion) {
        final ColumnDefinition<?> newColumn = new ColumnDefinition (new DynamicReflectionMVA (modelRowClass, path), typeAfterConversion);
        columnDefs.add (newColumn);
        columnDefsByPath.put(path, newColumn);        
        return this;
    }
    
    /**
     * This is a convenience method that adds a readonly 'derived' column that can reference one or more model values
     */
    public TableAMBuilder addDerivedColumn (String columnName, String pattern, String... paths) {
        final ColumnDefinition<String> newColumn = new ColumnDefinition<String> (new CompositeMVA (modelRowClass, pattern, paths), String.class, ColumnType.ReadOnly);
        newColumn.setColumnName (columnName);
        columnDefs.add (newColumn);
        
        return this;
    }

    @SuppressWarnings("rawtypes")
    public ColumnDefinition getColumn(int index) {
        return columnDefs.get(index);
    }

    @SuppressWarnings("rawtypes")
    public ColumnDefinition getColumn(String path) {
        return columnDefsByPath.get(path);
    }

    public TableAM build() {
        IFAttributeInfo attributeInfo = new IFAttributeInfo() {
        };
        
        
        final TableAM result = new TableAM(new IndexedReflectionMVA(modelRoot, elementCollectionPath), attributeInfo);
        for (ColumnDefinition<?> colDef: columnDefs) {
            result.addColumn (colDef);
        }
        return result;
    }
    
    private static class CompositeMVA implements IFDynamicModelValueAccessor {
        private final String pattern;
        private final String[] paths;
        private final String id;
        
        public CompositeMVA (Class<?> modelRowClass, String pattern, String... paths) {
            this.pattern = pattern;
            this.paths = paths;
            this.id = modelRowClass.getName() + "." + pattern;
        }

        @Override
        public Object getValue (Object root) {
            final List<Object> arguments = new ArrayList<Object>();
            
            for (String path: paths) {
                arguments.add (UlriceReflectionUtils.getValueByReflection (root, path));
            }
            
            return MessageFormat.format (pattern, arguments.toArray());
        }

        @Override
        public void setValue (Object root, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getAttributeId () {
            return id;
        }

        @Override
        public Class<?> getModelType (Class<?> rootType) {
            return String.class;
        }
    }  
}
