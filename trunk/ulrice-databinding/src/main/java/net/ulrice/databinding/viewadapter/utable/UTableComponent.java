package net.ulrice.databinding.viewadapter.utable;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter.SortKey;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;
import net.ulrice.databinding.bufferedbinding.impl.Element;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;
import net.ulrice.databinding.viewadapter.IFCellStateMarker;
import net.ulrice.databinding.viewadapter.IFCellTooltipHandler;

public class UTableComponent extends JPanel {

    private static final long serialVersionUID = 6533485227507042410L;
    private static final int RESIZE_MARGIN = 2;

    protected EventListenerList listenerList = new EventListenerList();

    protected UTable staticTable;
    protected UTable scrollTable;

    protected UTableModel staticTableModel;
    protected UTableModel scrollTableModel;

    protected UTableVAFilter filter;
    protected UTableRowSorter sorter;

    protected ListSelectionModel rowSelModel = new DefaultListSelectionModel();

    protected int fixedColumns;
    protected int originalFixedColumns;
    protected int selColumn = -1;

    protected IFCellTooltipHandler tooltipHandler;
    protected IFCellStateMarker stateMarker;

    protected TableAM attributeModel;

    protected List<UTableAction> popupMenuActions = new ArrayList<UTableAction>();

    public UTableComponent(final int fixedColumns) {
        this.fixedColumns = fixedColumns;
        this.originalFixedColumns = fixedColumns;

        staticTable = new UTable(this);
        scrollTable = new UTable(this);

        staticTable.setAssocTable(scrollTable);
        scrollTable.setAssocTable(staticTable);

        UTableViewport staticViewport = new UTableViewport();
        staticTable.setBackground(staticViewport.getBackground());
        staticViewport.setView(staticTable);

        UTableViewport scrollViewport = new UTableViewport();
        scrollTable.setBackground(scrollViewport.getBackground());
        scrollViewport.setView(scrollTable);

        staticViewport.addChangeListener(scrollViewport);
        scrollViewport.addChangeListener(staticViewport);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, staticTable.getTableHeader());
        scrollPane.setRowHeader(staticViewport);
        scrollPane.setViewport(scrollViewport);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        MouseListener mouseListener = new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                MouseListener[] listeners = listenerList.getListeners(MouseListener.class);
                if (listeners != null) {
                    for (MouseListener listener : listeners) {
                        listener.mouseReleased(adaptMouseEvent(e));
                    }
                }

                if (e.isPopupTrigger()) {
                    showPopupMenu(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                MouseListener[] listeners = listenerList.getListeners(MouseListener.class);
                if (listeners != null) {
                    for (MouseListener listener : listeners) {
                        listener.mousePressed(adaptMouseEvent(e));
                    }
                }

                if (e.isPopupTrigger()) {
                    showPopupMenu(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                MouseListener[] listeners = listenerList.getListeners(MouseListener.class);
                if (listeners != null) {
                    for (MouseListener listener : listeners) {
                        listener.mouseExited(adaptMouseEvent(e));
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                MouseListener[] listeners = listenerList.getListeners(MouseListener.class);
                if (listeners != null) {
                    for (MouseListener listener : listeners) {
                        listener.mouseEntered(adaptMouseEvent(e));
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                MouseListener[] listeners = listenerList.getListeners(MouseListener.class);
                if (listeners != null) {
                    for (MouseListener listener : listeners) {
                        listener.mouseClicked(adaptMouseEvent(e));
                    }
                }
                if (e.isPopupTrigger()) {
                    showPopupMenu(e.getComponent(), e.getX(), e.getY());
                }
            }

            private MouseEvent adaptMouseEvent(MouseEvent e) {
                return new MouseEvent(UTableComponent.this, e.getID(), e.getWhen(), e.getModifiers(), e.getX(),
                    e.getY(), e.getXOnScreen(), e.getYOnScreen(), e.getClickCount(), e.isPopupTrigger(),
                    e.getButton());
            }
        };
        staticTable.addMouseListener(mouseListener);
        scrollTable.addMouseListener(mouseListener);
        
    }

    public void init(final UTableViewAdapter viewAdapter) {
        
        
        staticTable.setDefaultRenderer(Object.class, new UTableVADefaultRenderer());
        scrollTable.setDefaultRenderer(Object.class, new UTableVADefaultRenderer());
        
        rowSelModel.addListSelectionListener(new ListSelectionListener() {
            private boolean nested = false;

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }

                if (nested) {
                    return;
                }

                nested = true;
                try {
                    for (ListSelectionListener l : listenerList.getListeners(ListSelectionListener.class)) {
                        l.valueChanged(e);
                    }
                }
                finally {
                    nested = false;
                }
            }
        });
        
            
        staticTableModel = new UTableModel(false, UTableComponent.this.fixedColumns, viewAdapter);
        staticTable.setModel(staticTableModel);
        staticTable.setSelectionModel(rowSelModel);
        
        scrollTableModel = new UTableModel(true, UTableComponent.this.fixedColumns, viewAdapter);
        scrollTable.setModel(scrollTableModel);
        scrollTable.setSelectionModel(rowSelModel);
        
        sorter = new UTableRowSorter(viewAdapter, UTableComponent.this.fixedColumns, staticTableModel, scrollTableModel);
        if (viewAdapter.getAttributeModel().getMandatorySortKeys() != null) {
            sorter.setMandatorySortKeys(viewAdapter.getAttributeModel().getMandatorySortKeys());
        }
        if(viewAdapter.getAttributeModel().getDefaultSortKeys() != null){
            sorter.setGlobalSortKeys(viewAdapter.getAttributeModel().getDefaultSortKeys());
        }
        
        staticTable.setRowSorter(sorter.getStaticTableRowSorter());
        scrollTable.setRowSorter(sorter.getScrollTableRowSorter());
        
        staticTable.setDefaultRenderer(Object.class, new UTableVADefaultRenderer());
        scrollTable.setDefaultRenderer(Object.class, new UTableVADefaultRenderer());
        
        filter = new UTableVAFilter(sorter, staticTable.getUTableHeader(), scrollTable.getUTableHeader());
        sorter.setRowFilter(filter);
        
        staticTable.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(staticTable.getSelectedColumn() >= 0) {
                    selColumn = staticTable.getSelectedColumn();
                } 
                else if(scrollTable.getSelectedColumn() >= 0) {
                    selColumn = scrollTable.getSelectedColumn() + UTableComponent.this.fixedColumns;
                }
                else {
                    selColumn = -1;
                }
            }
        });
        scrollTable.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(scrollTable.getSelectedColumn() >= 0) {
                    selColumn = scrollTable.getSelectedColumn() + UTableComponent.this.fixedColumns;
                } 
                else if(staticTable.getSelectedColumn() >= 0) {
                    selColumn = staticTable.getSelectedColumn();
                } 
                else {
                    selColumn = -1;
                }
            }
        });
        
        //for multi column sorting
        setAlteredTableHeaderListener(staticTable);
        setAlteredTableHeaderListener(scrollTable);    
    }

    protected void setAlteredTableHeaderListener(JTable table) {
        JTableHeader header = table.getTableHeader();
        if (header == null) {
            return;
        }
        for (MouseListener ml : header.getMouseListeners()) {
            if (ml instanceof BasicTableHeaderUI.MouseInputHandler) {
                MouseListener altered =
                        new UTableHeaderListener((BasicTableHeaderUI.MouseInputHandler) ml, table, this);
                header.removeMouseListener(ml);
                header.addMouseListener(altered);
            }
        }
    }

    public void addMouseListener(MouseListener l) {
        listenerList.add(MouseListener.class, l);
    }

    public void removeMouseListener(MouseListener l) {
        listenerList.remove(MouseListener.class, l);
    }

    public void addListSelectionListener(ListSelectionListener l) {
        listenerList.add(ListSelectionListener.class, l);
    }

    public void removeListSelectionListener(ListSelectionListener l) {
        listenerList.remove(ListSelectionListener.class, l);
    }

    public JTable getStaticTable() {
        return staticTable;
    }

    public JTable getScrollTable() {
        return scrollTable;
    }

    public void setUpperInfoArea(JComponent component) {
        add(component, BorderLayout.NORTH);
    }

    public void setLowerInfoArea(JComponent component) {
        add(component, BorderLayout.SOUTH);
    }

    public void setLeftInfoArea(JComponent component) {
        add(component, BorderLayout.EAST);
    }

    public void setRightInfoArea(JComponent component) {
        add(component, BorderLayout.WEST);
    }

    public void setCellTooltipHandler(IFCellTooltipHandler tooltipHandler) {
        this.tooltipHandler = tooltipHandler;
    }

    public void setCellStateMarker(IFCellStateMarker stateMarker) {
        this.stateMarker = stateMarker;
    }

    public IFCellTooltipHandler getCellTooltipHandler() {
        return tooltipHandler;
    }

    public IFCellStateMarker getCellStateMarker() {
        return stateMarker;
    }

    public UTableRowSorter getRowSorter() {
        return sorter;
    }

    public UTableVAFilter getFilter() {
        return filter;
    }

    public ListSelectionModel getSelectionModel() {
        return rowSelModel;
    }

    public void setSelectedColumn(int selColumn) {
        this.selColumn = selColumn;
        if (selColumn < fixedColumns) {
            getStaticTable().setColumnSelectionInterval(selColumn, selColumn);
        }
        else {
            getScrollTable().setColumnSelectionInterval(selColumn - fixedColumns, selColumn - fixedColumns);
        }
    }

    public int getSelectedColumn() {
        return selColumn;
    }

    /**
	 */
    public void updateColumnModel() {

        if (fixedColumns < originalFixedColumns) {
            setFixedColumns(originalFixedColumns);
        }

        if (originalFixedColumns >= attributeModel.getColumnCount()) {
            setFixedColumns(attributeModel.getColumnCount() > 0 ? attributeModel.getColumnCount() - 1 : 0);
        }

        TableColumnModel columnModel = null;
        List<ColumnDefinition< ? extends Object>> columnDefinitions = attributeModel.getColumns();

        columnModel = getStaticTable().getColumnModel();
        for (int i = columnModel.getColumnCount() - 1; i >= 0; i--) {
            columnModel.removeColumn(columnModel.getColumn(i));
        }

        if (columnDefinitions != null) {
            for (int i = 0; i < fixedColumns; i++) {
                ColumnDefinition< ?> columnDefinition = columnDefinitions.get(i);
                columnDefinition.setFixedColumn(true);
                TableColumn col = addColumn(columnModel, i, columnDefinition);
                col.addPropertyChangeListener(columnDefinition);
            }
        }

        columnModel = getScrollTable().getColumnModel();

        for (int i = columnModel.getColumnCount() - 1; i >= 0; i--) {
            columnModel.removeColumn(columnModel.getColumn(i));
        }

        if (columnDefinitions != null) {
            if (sorter != null) {
                List<SortKey> sortKeys = sorter.getGlobalSortKeys();
                sorter.modelStructureChanged();
                sorter.setGlobalSortKeys(sortKeys);
            }
            for (int i = fixedColumns; i < columnDefinitions.size(); i++) {
                ColumnDefinition< ?> columnDefinition = columnDefinitions.get(i);
                columnDefinition.setFixedColumn(false);
                TableColumn col = addColumn(columnModel, i - fixedColumns, columnDefinition);
                col.addPropertyChangeListener(columnDefinition);
            }
        }
    }

    protected TableColumn addColumn(TableColumnModel columnModel, int columnIndex, ColumnDefinition< ?> columnDefinition) {
        TableColumn column = new TableColumn();
        column.setIdentifier(columnDefinition.getId());
        column.setHeaderValue(columnDefinition);
        column.setModelIndex(columnIndex);
        if (columnDefinition.getCellEditor() != null) {
            column.setCellEditor(columnDefinition.getCellEditor());
        }
        if (columnDefinition.getCellRenderer() != null) {
            column.setCellRenderer(columnDefinition.getCellRenderer());
        }

        Comparator< ?> comparator = columnDefinition.getComparator();
        if (comparator != null && sorter != null) {

            sorter.setComparator(columnIndex, comparator);
            if (columnDefinition.isFixedColumn()) {
                sorter.setComparator(columnIndex, comparator);
            }
            else {
                sorter.setComparator(columnIndex + fixedColumns, comparator);
            }
        }

        columnModel.addColumn(column);

        if (columnDefinition.isUseValueRange()) {
            if (columnDefinition.getValueRange() != null) {
                column.setCellEditor(new UTableComboBoxCellEditor(columnDefinition.getValueRange()));
            }
            else {
                column.setCellEditor(new UTableComboBoxCellEditor(Collections.EMPTY_LIST));
            }
        }

        if (columnDefinition.getPreferredWidth() != null) {
            column.setPreferredWidth(columnDefinition.getPreferredWidth());
        }

        return column;
    }

    public int getFixedColumns() {
        return fixedColumns;
    }

    protected void setFixedColumns(int fixedColumns) {
        this.fixedColumns = fixedColumns;
        if (scrollTableModel != null && staticTableModel != null) { // FIXME Quick fix to open all modules
            this.scrollTableModel.setOffset(fixedColumns);
            this.staticTableModel.setOffset(fixedColumns);
        }
    }

    public int convertColumnIndexToModel(int col) {
        int modelCol = col;
        if (col < fixedColumns) {
            modelCol = staticTable.convertColumnIndexToModel(col);
        }
        else {
            modelCol = scrollTable.convertColumnIndexToModel(col - fixedColumns) + fixedColumns;
        }
        return modelCol;
    }

    public boolean stopEditing() {
        return staticTable.getCellEditor().stopCellEditing() || scrollTable.getCellEditor().stopCellEditing();
    }

    public void setDefaultCellRenderer(Class< ?> clazz, TableCellRenderer renderer) {
        scrollTable.setDefaultRenderer(clazz, renderer);
        staticTable.setDefaultRenderer(clazz, renderer);
    }

    public void setDefaultCellEditor(Class< ?> clazz, TableCellEditor editor) {
        scrollTable.setDefaultEditor(clazz, editor);
        staticTable.setDefaultEditor(clazz, editor);
    }

    public void sizeColumns(boolean includeHeader) {
        for (int c = 0; c < staticTable.getColumnCount(); c++) {
            sizeColumn(staticTable, c, RESIZE_MARGIN, includeHeader);
        }
        for (int c = 0; c < scrollTable.getColumnCount(); c++) {
            sizeColumn(scrollTable, c, RESIZE_MARGIN, includeHeader);
        }
    }

    public void sizeColumn(JTable table, int colIndex, int margin, boolean includeHeader) {
        TableColumn col = table.getColumnModel().getColumn(colIndex);
        int maxWidth = calcMaxSize(table, colIndex, includeHeader, col);
        col.setPreferredWidth(maxWidth + 2 * margin);
    }

    protected int calcMaxSize(JTable table, int vColIndex, boolean includeHeader, TableColumn col) {
        int maxWidth = 0;

        if (includeHeader) {
            TableCellRenderer renderer = col.getHeaderRenderer();
            if (renderer == null) {
                renderer = table.getTableHeader() != null ? table.getTableHeader().getDefaultRenderer() : null;
            }
            
            if (renderer != null) {
                Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
                // TODO find a clever way and place to calculate the a real value instead of setting just +15
                maxWidth = comp.getPreferredSize().width + 15;
            }
        }

        for (int r = 0; r < table.getRowCount(); r++) {
            TableCellRenderer renderer = table.getCellRenderer(r, vColIndex);
            Component comp =
                    renderer.getTableCellRendererComponent(table, table.getValueAt(r, vColIndex), false, false, r,
                        vColIndex);
            maxWidth = Math.max(maxWidth, comp.getPreferredSize().width);
        }
        return maxWidth;
    }

    public int getSelectedRowViewIndex() {
        return getSelectionModel().getMinSelectionIndex();
    }

    public int getSelectedRowModelIndex() {
        int viewIndex = getSelectedRowViewIndex();
        if (viewIndex >= 0) {
            return getRowSorter().convertRowIndexToModel(viewIndex);
        }
        return -1;
    }

    public int[] getSelectedRowsViewIndex() {
        int min = getSelectionModel().getMinSelectionIndex();
        int max = getSelectionModel().getMaxSelectionIndex();

        int[] tmpRows = new int[max - min + 1];
        int idx = 0;
        for (int i = min; i <= max; i++) {
            if (getSelectionModel().isSelectedIndex(i)) {
                tmpRows[idx++] = i;
            }
        }

        int[] result = new int[idx];
        System.arraycopy(tmpRows, 0, result, 0, idx);
        return result;
    }

    public void selectElement(int index) {
        getSelectionModel().addSelectionInterval(index, index);
    }

    public boolean isSingleRowSelected() {
        ListSelectionModel sm = getSelectionModel();
        return !sm.isSelectionEmpty() && sm.getMinSelectionIndex() == sm.getMaxSelectionIndex();
    }

    public boolean areRowsSelected() {
        ListSelectionModel sm = getSelectionModel();
        return !sm.isSelectionEmpty();
    }

    public int[] getSelectedRowsModelIndex() {
        int min = getSelectionModel().getMinSelectionIndex();
        int max = getSelectionModel().getMaxSelectionIndex();

        try {
            int[] tmpRows = new int[max - min + 1];
            int idx = 0;
            for (int i = min; i <= max; i++) {
                if (getSelectionModel().isSelectedIndex(i)) {
                    if (getRowSorter() != null) {
                        tmpRows[idx++] = getRowSorter().convertRowIndexToModel(i);
                    }
                    else {
                        tmpRows[idx++] = i;
                    }
                }
            }
            int[] result = new int[idx];
            System.arraycopy(tmpRows, 0, result, 0, idx);
            return result;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return new int[0]; //if filtered and the last is deleted and selected elements are called by ListSelectionListener
            //TODO: find a better solution
        }
        
    }

    public void setAttributeModel(TableAM attributeModel) {
        this.attributeModel = attributeModel;
    }

    public void addRow() {
        checkAttributeModelSet();
        int modelIdx = getSelectedRowModelIndex();
        if (modelIdx >= 0) {
            attributeModel.addElement(modelIdx, null);
        } else {
            attributeModel.addElement(null);
        }
    }

    public void delRowWithModelIndex(int modelIndex) {
        checkAttributeModelSet();
        attributeModel.delElement(modelIndex);
    }

    public void delRowWithViewIndex(int viewIndex) {
        checkAttributeModelSet();
        attributeModel.delElement(getRowSorter().convertRowIndexToModel(viewIndex));
    }

    public void copySelectedRows() {
        checkAttributeModelSet();
        List<Object> values = getSelectedObjects();
        if (values != null) {
            for (Object value : values) {
                attributeModel.addElement(value);
            }
        }
    }

    public Element copySelectedRow() {
        checkAttributeModelSet();

        if (isSingleRowSelected()) {
            Object object = getSelectedObject();
            return attributeModel.addElement(object);
        } else {
            return null;
        }
    }

    public void delSelectedRows() {
        checkAttributeModelSet();
        List<Element> elements = getSelectedElements();
        if (elements != null) {
            for (Element element : elements) {
                attributeModel.delElement(element);
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List getSelectedObjects() {
        checkAttributeModelSet();
        int[] rowsInModel = getSelectedRowsModelIndex();

        List result = new ArrayList(rowsInModel.length);
        for (int row : rowsInModel) {
            result.add(attributeModel.getCurrentValueAt(row));
        }
        return result;
    }
    public List<Element> getSelectedElements() {
        //checkAttributeModelSet();
        if(attributeModel == null)
            return new ArrayList<Element>();
        int[] rowsInModel = getSelectedRowsModelIndex();

        List<Element> result = new ArrayList<Element>(rowsInModel.length);
        for (int row : rowsInModel) {
            result.add(attributeModel.getElementAt(row));
        }
        return result;
    }

    public Element getSelectedElement() {
        checkAttributeModelSet();
        int rowInModel = getSelectedRowModelIndex();
        return attributeModel.getElementAt(rowInModel);
    }

    public Object getSelectedObject() {
        checkAttributeModelSet();
        int rowInModel = getSelectedRowModelIndex();
        return attributeModel.getCurrentValueAt(rowInModel);
    }

    protected void checkAttributeModelSet() {
        if (attributeModel == null) {
            throw new IllegalStateException("Component is not bound to an attribute model.");
        }
    }

    public Element getElementById(String id) {
        if (attributeModel == null) {
            return null;
        }
        return attributeModel.getElementById(id);
    }

    public Element getElementAtViewIndex(int viewIndex) {
        if (attributeModel == null) {
            return null;
        }
        int modelRow = viewIndex;
        if (getRowSorter() != null) {
            modelRow = getRowSorter().convertRowIndexToModel(viewIndex);
        }

        return getElementAtModelIndex(modelRow);
    }

    public Element getElementAtModelIndex(int modelIndex) {
        return attributeModel.getElementAt(modelIndex);
    }

    public boolean isCellDirty(int row, int col) {
        int modelRow = getRowSorter().convertRowIndexToModel(row);
        int modelCol = convertColumnIndexToModel(col);
        return attributeModel != null ? attributeModel.isCellDirty(modelRow, modelCol) : false;
    }

    public boolean isCellValid(int row, int col) {
        int modelRow = getRowSorter().convertRowIndexToModel(row);
        int modelCol = convertColumnIndexToModel(col);
        return attributeModel != null ? attributeModel.isCellValid(modelRow, modelCol) : true;
    }

    public boolean isCellEditable(int row, int col) {
        int modelRow = getRowSorter().convertRowIndexToModel(row);
        int modelCol = convertColumnIndexToModel(col);

        if (attributeModel != null) {
            return attributeModel.isCellEditable(modelRow, modelCol);
        }
        return false;
    }

    public int getColumnCount() {
        return attributeModel != null ? attributeModel.getColumnCount() : 0;
    }

    public int getModelRowCount() {
        return getRowSorter().getModelRowCount();
    }

    public int getViewRowCount() {
        return getRowSorter().getViewRowCount();
    }

    public ColumnDefinition<?> getColumnById(String columnId) {
        if (attributeModel != null) {
            return attributeModel.getColumnById(columnId);
        }
        return null;
    }

    public ColumnDefinition<?> getColumnByViewIndex(int viewIndex) {
        if (attributeModel != null) {
            int modelIndex = convertColumnIndexToModel(viewIndex);
            return attributeModel.getColumnByIndex(modelIndex);
        }
        return null;
    }

    public TableColumn getColumn(int column) {
        if (column < fixedColumns) {
            return staticTable.getColumnModel().getColumn(column);
        }
        return scrollTable.getColumnModel().getColumn(column - fixedColumns);
    }

    public void addTableAction(UTableAction action) {
        popupMenuActions.add(action);
    }

    private void showPopupMenu(Component component, int x, int y) {

        if (!(areRowsSelected() && !isSingleRowSelected())) {
            int rowAtPoint = -1;
            if (scrollTable != null && rowAtPoint == -1) {
                rowAtPoint = scrollTable.rowAtPoint(new Point(x, y));
            }

            if (staticTable != null && rowAtPoint == -1) {
                rowAtPoint = staticTable.rowAtPoint(new Point(x, y));
            }

            if (rowAtPoint > -1) {
                getSelectionModel().setSelectionInterval(rowAtPoint, rowAtPoint);
            }
        }

        JPopupMenu popupMenu = new JPopupMenu();
        for (UTableAction action : popupMenuActions) {
            action.initialize(this);
            popupMenu.add(action);
        }
        popupMenu.show(component, x, y);
    }
    
    public int convertRowIndexToModel(int index) {
        if (getRowSorter() != null) {
            return getRowSorter().convertRowIndexToModel(index);
        }
        return index;
    }

    public int convertRowIndexToView(int index) {
        if (getRowSorter() != null) {
            return getRowSorter().convertRowIndexToView(index);
        }
        return index;
    }

    public void scrollToElement(Element element) {
        int index = attributeModel.getIndexOfElement(element);
        int viewIndex = convertRowIndexToView(index);
        scrollToRow(viewIndex);
    }
    
    public void scrollToRow(int row) {
        scrollTable.scrollRectToVisible(new Rectangle(scrollTable.getCellRect(row, 0, true)));
    }

    public void undoChangesOfSelectedRow() {
        if (isSingleRowSelected() && attributeModel != null) {
            attributeModel.rollbackElement(getSelectedElement());
        }
    }
}
