package net.ulrice.databinding.viewadapter.utable;

import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.tree.TreePath;

import net.ulrice.Ulrice;
import net.ulrice.databinding.bufferedbinding.impl.Element;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;
import net.ulrice.message.Message;
import net.ulrice.message.MessageSeverity;

public class UTreeTableComponent extends UTableComponent implements ExpandColapseListener{

    private static final long serialVersionUID = 1L;
    protected UTableViewAdapter viewAdapter;
    protected UTreeTableModel treeTableModel;

    protected TreeTableCellRenderer tree;

    public UTreeTableComponent() {
        super(0);
    }

    /**
     * TODO: doppelten Code aus UTableComponent zusammen fassen
     */
    @Override
    public void init(final UTableViewAdapter viewAdapter) {
        this.viewAdapter = viewAdapter;
        staticTableModel = null;
        scrollTableModel = null;
        sorter = null;
        filter = null;

        staticTable.setDefaultRenderer(Object.class, new UTableVADefaultRenderer());
        scrollTable.setDefaultRenderer(Object.class, new UTableVADefaultRenderer());

        treeTableModel = new UTreeTableModel(viewAdapter.getAttributeModel());
        tree = new TreeTableCellRenderer(scrollTable, treeTableModel);
        tree.setRootVisible(false);
        viewAdapter.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {

                TableAM tableAM = viewAdapter.getAttributeModel();
                if (tableAM.consumeTreeStayOpen()) {
                    return;
                }
                if (e.getType() == TableModelEvent.ALL_COLUMNS) {
                    treeTableModel.fireTreeStructureChanged(tableAM, new Object[] { tableAM }, null, null);
                }
                else if (e.getType() == TableModelEvent.INSERT) {
                    TreePath firstRowPath = tree.getPathForRow(e.getFirstRow());
                    TreePath lastRowPath = tree.getPathForRow(e.getFirstRow());
                    treeTableModel.fireTreeNodesInserted(tableAM, new Object[] { firstRowPath, lastRowPath }, null,
                        null);
                }
                else if (e.getType() == TableModelEvent.DELETE) {
                    TreePath firstRowPath = tree.getPathForRow(e.getFirstRow());
                    TreePath lastRowPath = tree.getPathForRow(e.getFirstRow());
                    treeTableModel.fireTreeNodesRemoved(tableAM, new Object[] { firstRowPath, lastRowPath }, null,
                        null);
                }
                else {
                    treeTableModel.fireTreeStructureChanged(tableAM, new Object[] { tableAM }, null, null);
                }
            }
        });

        TreeTableModelAdapter modelAdapter = new TreeTableModelAdapter(treeTableModel, tree);
        viewAdapter.setTreeTableModelAdapter(modelAdapter);
        modelAdapter.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (getRowSorter() != null) {
                    getRowSorter().allRowsChanged();
                }

                scrollTable.revalidate();
                staticTable.revalidate();
                scrollTable.repaint();
                staticTable.repaint();
            }
        });

        // Modell setzen.
        scrollTableModel = new UTableModel(true, UTreeTableComponent.this.fixedColumns, viewAdapter);
        scrollTable.setModel(scrollTableModel);

        // Gleichzeitiges Selektieren fuer Tree und Table.
        TreeTableSelectionModel selectionModel = new TreeTableSelectionModel();

        tree.setSelectionModel(selectionModel); // For the tree
        scrollTable.setSelectionModel(selectionModel.getListSelectionModel()); // For the table

        rowSelModel = selectionModel.getListSelectionModel();
        selectionModel.getListSelectionModel().addListSelectionListener(new ListSelectionListener() {
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

                TLongSet selUniqueIds = new TLongHashSet();
                List<Element> selElements =
                        UTreeTableComponent.this.getSelectedElementsTreeIntern(getSelectedRowsModelIndex());

                boolean ambiguousSelection = false;
                for (Element elem : selElements) {
                    if (viewAdapter.getAttributeModel().isVirtualTreeNodes()) {
                        ambiguousSelection |= testForUniqueSelection(selUniqueIds, elem, e);                        
                    }
                    else {
                        ambiguousSelection |= processSelection(selUniqueIds, elem, e);
                    }
                }
                if(ambiguousSelection){
                    final String messageText = "Selection is not definite";
                    
                    Ulrice.getMessageHandler().handleMessage(new Message(MessageSeverity.Warning, messageText));
                }
            }

            private boolean testForUniqueSelection(TLongSet selUniqueIds, Element element, ListSelectionEvent e) {
                if (element.getChildCount() == 0) {
                    return processSelection(selUniqueIds, element, e);
                }
                boolean ambiguousSelection = false;
                for (int i = 0; i < element.getChildCount(); i++) {
                    ambiguousSelection |= testForUniqueSelection(selUniqueIds, element.getChild(i), e);
                }
                return ambiguousSelection;
            }

            private boolean processSelection(TLongSet selUniqueIds, Element element, final ListSelectionEvent e) {
                if (selUniqueIds.contains(element.getUniqueId())) {
                    nested = true;
                    try {
                        UTreeTableComponent.this.rowSelModel.removeSelectionInterval(e.getFirstIndex(), e.getLastIndex());
                    }
                    finally {
                        nested = false;
                    }                   
                    
                    return true;
                }
                else {
                    selUniqueIds.add(element.getUniqueId());
                }
                return false;
            }
        });

        // Renderer fuer den Tree.
        scrollTable.setDefaultRenderer(TreeTableModel.class, tree);
        // Editor fuer die TreeTable
        scrollTable.setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor(tree, scrollTable));

        // sorter = new UTableRowSorter(viewAdapter, 0, staticTableModel, scrollTableModel);
        // staticTable.setRowSorter(sorter.getStaticTableRowSorter());
        // scrollTable.setRowSorter(sorter.getScrollTableRowSorter());

        // filter = new UTableVAFilter(sorter, staticTable.getUTableHeader(), scrollTable.getUTableHeader());
        // sorter.setRowFilter(filter);

        staticTable.getUTableHeader().setExtendInHeight(false);
        scrollTable.getUTableHeader().setExtendInHeight(false);
        staticTableModel = new UTableModel(false, UTreeTableComponent.this.fixedColumns, viewAdapter);
        staticTable.setModel(staticTableModel);
        staticTable.setSelectionModel(rowSelModel);

    }

    /**
     * rebuids the table structure for a tree table
     *
     * @param virtualTreeNodes true if a parent node is just a container for the childs false if the parent node
     *        represent own objectes
     */
    public void rebuildTreeTableStructure(boolean virtualTreeNodes) {
        TableAM tableAM = viewAdapter.getAttributeModel();
        if (virtualTreeNodes) {
            tableAM.setVirtualTreeNodes(virtualTreeNodes);
        }
        treeTableModel.fireTreeStructureChanged(tableAM, new Object[] { tableAM }, null, null);
    }
    
    @Override
    public void updateColumnModel() {
        super.updateColumnModel();
        scrollTable.getUTableHeader().removeAll();
        scrollTable.getUTableHeader().add(new ExpandColapsePanel(this), scrollTable.getUTableHeader().getColumnModel().getColumn(0).getIdentifier());
    }

    @Override
    @SuppressWarnings({ "rawtypes" })
    public List getSelectedObjects() {
        checkAttributeModelSet();
        int[] rowsInModel = getSelectedRowsModelIndex();
        return getSelectedObjectsTreeIntern(rowsInModel);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected List getSelectedObjectsTreeIntern(int[] rowsInModel) {
        List<Element> selectedElements = reduceDoubleElements(getSelectedElementsTreeIntern(rowsInModel));
        List result = new ArrayList(rowsInModel.length);
        for (Element element : selectedElements) {
            result.add(element != null ? element.getCurrentValue() : null);
        }
        return result;
    }

    @Override
    public List<Element> getSelectedElements() {

        if (attributeModel == null) {
            return new ArrayList<Element>();
        }
        int[] rowsInModel = getSelectedRowsModelIndex();
        return reduceDoubleElements(getSelectedElementsTreeIntern(rowsInModel));
    }

    public List<Element> getSelectedElementsTreeIntern(int[] rowsInModel) {
        List<Element> result = new ArrayList<Element>(rowsInModel.length);
        for (int row : rowsInModel) {
            result.add(viewAdapter.getElementAtUsingModelIndex(row));
        }
        return result;
    }

    private List<Element> reduceDoubleElements(List<Element> elements) {
        TLongObjectMap<Element> elementsByUniquId = new TLongObjectHashMap<Element>();
        for (Element element : elements) {
            mapDoubleElement(elementsByUniquId, element);
        }
        return new ArrayList<Element>(elementsByUniquId.valueCollection());
    }

    private void mapDoubleElement(TLongObjectMap<Element> elementsByUniquId, Element element) {
        if (viewAdapter.getAttributeModel().isVirtualTreeNodes()) {
            
            if (element.getChildCount() == 0) {
                elementsByUniquId.put(element.getUniqueId(), element);
            }
            for (int i = 0; i < element.getChildCount(); i++) {
                mapDoubleElement(elementsByUniquId, element.getChild(i));
            }
        }
        else {
            elementsByUniquId.put(element.getUniqueId(), element);            
        }
    }

    @Override
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

    @Override
    public Element getElementAtModelIndex(int modelIndex) {
        return viewAdapter.getElementAtUsingModelIndex(modelIndex);
    }

    @Override
    public int getModelRowCount() {
        return viewAdapter.getRowCount();
    }

    @Override
    public int getViewRowCount() {
        return viewAdapter.getRowCount();
    }

    public void expandAll() {
        int row = 0;
        while (row < tree.getRowCount()) {
            tree.expandRow(row);
            row++;
        }
    }

    public void collapseAll() {
        int row = tree.getRowCount() - 1;
        while (row >= 0) {
            tree.collapseRow(row);
            row--;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delSelectedRows() {
        if (viewAdapter.getAttributeModel().isVirtualTreeNodes()) {
            checkAttributeModelSet();
            List<Element> elements = new ArrayList<Element>();
            TLongList uniqueIds = new TLongArrayList();
            for (Element elem : getSelectedElements()) {
                getElementsAndChilds(elem, elements, uniqueIds);
                if (elem.getParent() != null) {
                    elem.removeChild(viewAdapter.getAttributeModel().getPathToChildren());
                }
            }
            delRows(elements);
        }
        else {
            super.delSelectedRows();
        }

    }

    private void getElementsAndChilds(Element elem, List<Element> elements, TLongList uniqueIds) {
        if (!uniqueIds.contains(elem.getUniqueId())) {
            elements.add(elem);
            uniqueIds.add(elem.getUniqueId());
            if (elem.getChildCount() > 0) {
                for (int index = 0; index < elem.getChildCount(); index++) {
                    getElementsAndChilds(elem.getChild(index), elements, uniqueIds);
                }
            }
        }
    }
}
