package net.ulrice.simpledatabinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import net.ulrice.databinding.DataState;
import net.ulrice.databinding.converter.HeuristicConverterFactory;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.converter.ValueConverterException;
import net.ulrice.databinding.modelaccess.IFIndexedModelValueAccessor;
import net.ulrice.databinding.modelaccess.IndexedPredicate;
import net.ulrice.databinding.modelaccess.ModelChangeListener;
import net.ulrice.databinding.modelaccess.ModelNotificationAdapter;
import net.ulrice.databinding.modelaccess.IFModelValueAccessor;
import net.ulrice.databinding.modelaccess.Predicate;
import net.ulrice.databinding.modelaccess.PropertyChangeSupportModelNotificationAdapter;
import net.ulrice.databinding.modelaccess.impl.OnglMVA;
import net.ulrice.databinding.modelaccess.impl.OgnlPredicate;
import net.ulrice.databinding.modelaccess.impl.OgnlSingleListIndexedMVA;
import net.ulrice.databinding.validation.IFValidator;
import net.ulrice.databinding.validation.ValidationResult;
import net.ulrice.databinding.viewadapter.IFViewAdapter;
import net.ulrice.databinding.viewadapter.IFViewChangeListener;
import net.ulrice.databinding.viewadapter.impl.factory.HeuristicViewAdapterFactory;
import net.ulrice.simpledatabinding.util.EditableTableModel;
import net.ulrice.simpledatabinding.util.ErrorHandler;
import net.ulrice.simpledatabinding.viewaccess.IndexedViewAdapter;
import net.ulrice.simpledatabinding.viewaccess.table.DefaultTableModelColumnViewAdapter;
import net.ulrice.simpledatabinding.viewaccess.table.DefaultTableModelViewAdapter;
import net.ulrice.simpledatabinding.viewaccess.table.ExpressionColumnSpec;
import net.ulrice.simpledatabinding.viewaccess.table.TableViewAdapter;
import net.ulrice.simpledatabinding.viewaccess.table.WithTypesPerColumn;


/**
 * Dies ist die Einstiegsklasse - sie repräsentiert bzw. sammelt die Detail zu einem Binding von UI-Elementen
 *  an ein Model. 
 */
public class ModelBinding {
    private final Object _model;
    private final ModelNotificationAdapter _modelNotificationAdapter;

    private final List<Binding> _bindings = new ArrayList<Binding> ();
    private final List<TableBinding> _tableBindings = new ArrayList<TableBinding> ();

    private boolean _isUpdatingView = false;

    /**
     * Wenn das Modell PropertyChangeSupport unterstützt, muss man keinen expliziten Adapter angeben
     */
    public ModelBinding (Object model) {
        this (model, new PropertyChangeSupportModelNotificationAdapter (model));
    }

    public ModelBinding (Object model, ModelNotificationAdapter modelNotificationAdapter) {
        _model = model;
        _modelNotificationAdapter = modelNotificationAdapter;

        _modelNotificationAdapter.addModelChangeListener (new ModelChangeListener() {
            public void modelChanged () {
                updateViews ();
            }
        });
    }

    private void updateViews () {
        for (Binding b: _bindings) {
            updateView (b);
        }

        for (TableBinding b: _tableBindings)
            updateView (b);

    }

    private void updateView (final TableBinding b) {
        final int numRows = getNumRows (b);
        b.getTableViewAdapter ().setSize (numRows, b.getColumnBindings ().size ());

        for (int row=0; row < numRows; row++) {
            for (int col=0; col < b.getColumnBindings ().size (); col++) {
                final IndexedBinding cb = b.getColumnBindings ().get (col);

                final Object raw = cb.getModelValueAccessor ().getValue (row);
                final Object converted = cb.getConverter ().modelToView (raw);
                
                _isUpdatingView = true;
                try {
                    cb.getViewAdapter ().setValue (row, converted);
                }
                finally {
                    _isUpdatingView = false;
                }
            }
        }
    }

    private int getNumRows (TableBinding b) {
        int result = 0;
        for (IndexedBinding colBinding: b.getColumnBindings ()) {
            final int colSize = (Integer) colBinding.getNumEntriesAccessor ().getValue ();
            if (colSize > result)
                result = colSize;
        }
        return result;
    }

    private void updateView (final Binding b) {
        if (! b.hasDataBinding ())
            return;

        final Object converted = b.getCurrentValue();

        final Object oldValue = b.getViewAdapter ().getValue ();
        if (oldValue == null && converted == null)
            return;
        if (oldValue != null && oldValue.equals (converted))
            return;

        _isUpdatingView = true;
        try {        
            calculateState(b);        	        	
            b.getViewAdapter ().updateBinding(b);
        }
        finally {
            _isUpdatingView = false;
        }
    }

    private void updateModelFromTable (TableModel tableModel, List<IndexedBinding> columnBindings, TableModelEvent e) {
        try {
            if (_isUpdatingView)
                return;
            
            if (e.getType () != TableModelEvent.UPDATE)
                return;

            if (e.getColumn () == TableModelEvent.ALL_COLUMNS)
                return; //TODO anders behandeln?

            final IndexedBinding columnBinding = columnBindings.get (e.getColumn ());

            if (columnBinding.isReadOnly ())
                return;

            final Object raw = tableModel.getValueAt (e.getFirstRow (), e.getColumn ());
            final Object converted = columnBinding.getConverter ().viewToModel (raw);
            columnBinding.getModelValueAccessor ().setValue (e.getFirstRow (), converted);
        }
        finally {
            validateAll ();
        }
    }

    private void updateModel (Binding b) {
        try {
            if (_isUpdatingView) 
                return;

            if (b.isReadOnly ())
                return;

            try {
                final Object raw = b.getViewAdapter ().getValue ();
            	b.setCurrentValue(raw);
            }
            catch (ValueConverterException exc) {
            }
        }
        finally {
            validateAll ();
        }
    }

    private void calculateState(Binding b) {
    	
    	if(b.getValidationFailures() != null && !b.getValidationFailures().isEmpty()) {
    		b.setState(DataState.Invalid);
    		return;
    	}
    	
    	// Dirty handling
        if (b.getCurrentValue() != null && b.getOriginalValue() != null) {
        	b.setState(b.getCurrentValue().equals(b.getOriginalValue()) ? DataState.NotChanged : DataState.Changed);
        } else {
        	b.setState(DataState.Changed);
        }
	}

	private void validateAll () {
        final ValidationResult validationResult = new ValidationResult ();

        for (Binding b: _bindings)
            validate (b, validationResult);

        for (Binding b: _bindings) {
            final List<String> raw = validationResult.getMessagesByBinding(b);
            b.setValidationFailures(raw != null ? raw : new ArrayList<String> ());
            calculateState(b);
            b.getViewAdapter ().updateBinding(b);
            b.getViewAdapter ().setEnabled (b.isWidgetEnabled (validationResult.isValid (), _model));
        }
    }

    private void validate (Binding b, ValidationResult validationResult) {
        if (b.isReadOnly ())
            return;

        try {
            final Object raw = b.getViewAdapter ().getValue ();
            final Object converted = b.getConverter ().viewToModel (raw);

            for (IFValidator v: b.getValidators ()) {
				ValidationResult validationErrors = v.isValid(b, converted);
				if(validationErrors != null) {
					validationResult.addValidationErrors(validationErrors.getValidationErrors());
				}
			}

        }
        catch (ValueConverterException exc) {
            validationResult.addFailure (b, "Fehler bei der Konvertierung");
        }
    }

    private void ensureEventThread () {
        if (!SwingUtilities.isEventDispatchThread ())
            ErrorHandler.handle (new RuntimeException ("nicht im Event-Thread")); //TODO Fehlerbehandlung
    }

    public Binding registerWithoutData (Object viewElement, String enabledExpression) {
        ensureEventThread ();
        final IFViewAdapter va = HeuristicViewAdapterFactory.createAdapter (viewElement);
        final Predicate enabledPredicate = new OgnlPredicate (enabledExpression);
        Binding binding = new Binding (va, null, enabledPredicate, null, new ArrayList<IFValidator<?>> (), true);
		_bindings.add (binding);
        va.setBindWithoutValue(true);
        updateViews ();
        return binding;
    }

    //TODO soll auch ohne Type gehen
    public Binding register (Object viewElement, String modelPath, Class<?> modelType, IFValidator<?>... validators) {
        final IFModelValueAccessor mva = new OnglMVA (_model, modelPath, modelType);
        final IFViewAdapter va = HeuristicViewAdapterFactory.createAdapter (viewElement);
        final Predicate enabledPredicate = mva.isReadOnly () ? Predicate.FALSE : Predicate.TRUE;

        return register (va, mva, enabledPredicate, Arrays.asList (validators), mva.isReadOnly () || !va.isEnabled());
    }

    public Binding register (Object viewElement, String modelPath, Class<?> modelType, boolean enabled, IFValidator<?>... validators) { 
        final IFModelValueAccessor mva = new OnglMVA (_model, modelPath, modelType);
        final IFViewAdapter va = HeuristicViewAdapterFactory.createAdapter (viewElement);
        final Predicate enabledPredicate = enabled ? Predicate.FALSE : Predicate.TRUE;

        return register (va, mva, enabledPredicate, Arrays.asList (validators), mva.isReadOnly () || va.isEnabled());
    }

    public Binding register (Object viewElement, String modelPath, Class<?> modelType, String enabledExpression, IFValidator<?>... validators) {
        final IFModelValueAccessor mva = new OnglMVA (_model, modelPath, modelType);
        return register (HeuristicViewAdapterFactory.createAdapter (viewElement), mva, new OgnlPredicate (enabledExpression), Arrays.asList (validators), mva.isReadOnly ());
    }

    public Binding register (IFViewAdapter viewAdapter, IFModelValueAccessor modelValueAccessor, Predicate enabledPredicate, List<IFValidator<?>> validators, boolean isReadOnly) {
        return register (viewAdapter, HeuristicConverterFactory.createConverter (viewAdapter.getViewType (), modelValueAccessor.getModelType ()), enabledPredicate, modelValueAccessor, validators, isReadOnly);
    }

    public Binding register (IFViewAdapter viewAdapter, IFValueConverter viewConverter, Predicate enabledPredicate, IFModelValueAccessor modelValueAccessor, List<IFValidator<?>> validators, boolean isReadOnly) {
        ensureEventThread ();
        final Binding b = new Binding (viewAdapter, viewConverter, enabledPredicate, modelValueAccessor, validators, isReadOnly);
        _bindings.add (b);
        updateViews ();

        viewAdapter.addViewChangeListener (new IFViewChangeListener() {
			@Override
			public void viewValueChanged(IFViewAdapter viewAdapter) {
				updateModel (b);
			}
        });
        return b;
    }

    //TODO flexibleres Tabellen-Binding ohne BaseExpression, dafür mit #index
    public void registerSingleListTable (Object oTableModel, String baseExpression, String... columnExpressions) {
        final ExpressionColumnSpec[] columnSpecs = new ExpressionColumnSpec [columnExpressions.length];
        for (int i=0; i<columnExpressions.length; i++)
            columnSpecs[i] = new ExpressionColumnSpec (columnExpressions[i], String.class);

        registerSingleListTable (oTableModel, baseExpression, columnSpecs);
    }

    public void registerSingleListTable (Object oTableModel, String baseExpression, ExpressionColumnSpec... columnSpecs) {
        ensureEventThread ();

        final DefaultTableModel tableModel = (DefaultTableModel) oTableModel;
        final IFModelValueAccessor numRowsAccessor = new OnglMVA (_model, "(" + baseExpression + ").size()", Integer.class);

        final boolean canBeEditable = tableModel instanceof EditableTableModel;

        final List<IndexedBinding> columnBindings = new ArrayList<IndexedBinding> ();
        for (int col=0; col < columnSpecs.length; col++) {
            final String expr = columnSpecs [col].getExpression ();
            final Class<?> columnType = columnSpecs [col].getType ();
            final IFValueConverter converter = HeuristicConverterFactory.createConverter (columnType, columnType);

            final IndexedViewAdapter viewAdapter = new DefaultTableModelColumnViewAdapter (tableModel, columnType, col, ! canBeEditable); //TODO readOnly noch über Typen filtern?
            final IFIndexedModelValueAccessor modelValueAccessor = new OgnlSingleListIndexedMVA (columnType, null, _model, baseExpression, expr);
            columnBindings.add (new IndexedBinding (numRowsAccessor, viewAdapter, converter, IndexedPredicate.TRUE, modelValueAccessor, viewAdapter.isReadOnly () || modelValueAccessor.isReadOnly ())); //TODO: enabled
        }

        final TableViewAdapter tableViewAdapter = new DefaultTableModelViewAdapter (tableModel);
        _tableBindings.add (new TableBinding (tableViewAdapter, columnBindings));

        if (tableModel instanceof WithTypesPerColumn) {
            final List<Class<?>> columnTypes = new ArrayList<Class<?>> ();
            for (IndexedBinding b: columnBindings)
                columnTypes.add (b.getViewAdapter ().getViewType ());

            ((WithTypesPerColumn) tableModel).setColumnTypes (columnTypes);
        }

        if (canBeEditable) {
            final List<Boolean> columnsEditable = new ArrayList<Boolean> ();
            for (IndexedBinding b: columnBindings)
                columnsEditable.add (! b.isReadOnly ());

            ((EditableTableModel) tableModel).setEditable (columnsEditable);
        }

        updateViews ();

        tableModel.addTableModelListener (new TableModelListener () {
            public void tableChanged (TableModelEvent e) {
                System.out.println (e.getType () + ", " + e.getFirstRow () + ", " + e.getColumn ());
                updateModelFromTable (tableModel, columnBindings, e);
            }
        });
    }
    
    public void commit() {
        for (Binding b: _bindings) {
            b.setState(DataState.NotChanged);
            Object value = b.getModelValueAccessor().getValue();
            b.setCurrentValue(value);
            b.setOriginalValue(value);
        }
    }
    
    public void rollback() {
        for (Binding b: _bindings) {
            b.setState(DataState.NotChanged);
            b.setCurrentValue(b.getOriginalValue());
            updateModel(b);
        }
    }
}


















