package net.ulrice.databinding.viewadapter.utable;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.tree.TreePath;

import net.ulrice.Ulrice;
import net.ulrice.databinding.bufferedbinding.impl.Element;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;
import net.ulrice.module.impl.action.ModuleActionManager;

public class UTreeTableComponent extends UTableComponent {

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
                if (getRowSorter() != null)
                    getRowSorter().allRowsChanged();

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

                Set<String> selUniqueIds = new HashSet<String>();
                List<Element> selElements =
                        UTreeTableComponent.this.getSelectedElementsTreeIntern(getSelectedRowsModelIndex());
                for (Element elem : selElements) {

                    testForUniqueSelection(selUniqueIds, elem, e);
                }
                System.out.println(selUniqueIds.size());
            }

            private void testForUniqueSelection(Set<String> selUniqueIds, Element element, ListSelectionEvent e) {
                if (element.getChildCount() == 0) {

                    if (selUniqueIds.contains(element.getUniqueId())) {
                        // TODO: error handling
                        UTreeTableComponent.this.rowSelModel.removeSelectionInterval(e.getFirstIndex(),
                            e.getLastIndex());
                    }
                    else {
                        selUniqueIds.add(element.getUniqueId());
                    }
                }
                for (int i = 0; i < element.getChildCount(); i++) {
                    testForUniqueSelection(selUniqueIds, element.getChild(i), e);
                }

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
 
    public void rebuildTreeTableStructure() {
        TableAM tableAM = viewAdapter.getAttributeModel();
        treeTableModel.fireTreeStructureChanged(tableAM, new Object[] { tableAM }, null, null);
    }
    
    public void updateColumnModel() {
        super.updateColumnModel();
        scrollTable.getUTableHeader().removeAll();
        scrollTable.getUTableHeader().add(new ExpandColapsePanel(this), scrollTable.getUTableHeader().getColumnModel().getColumn(0).getIdentifier());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List getSelectedObjects() {
        checkAttributeModelSet();
        int[] rowsInModel = getSelectedRowsModelIndex();
        return getSelectedObjectsTreeIntern(rowsInModel);
    }

    protected List getSelectedObjectsTreeIntern(int[] rowsInModel) {
        List<Element> selectedElements = reduceDoubleElements(getSelectedElementsTreeIntern(rowsInModel));
        List result = new ArrayList(rowsInModel.length);
        for (Element element : selectedElements) {
            result.add(element != null ? element.getCurrentValue() : null);
        }
        return result;
    }

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
        HashMap<String, Element> elementsByUniquId = new HashMap<String, Element>();
        for (Element element : elements) {
            mapDoubleElement(elementsByUniquId, element);
        }
        return new ArrayList<Element>(elementsByUniquId.values());
    }

    private void mapDoubleElement(HashMap<String, Element> elementsByUniquId, Element element) {

        if (element.getChildCount() == 0) {
            elementsByUniquId.put(element.getUniqueId(), element);
        }
        for (int i = 0; i < element.getChildCount(); i++) {
            mapDoubleElement(elementsByUniquId, element.getChild(i));
        }
    }

    public Element getElementAtViewIndex(int viewIndex) {
        if (attributeModel == null) {
            return null;
        }
        int modelRow = viewIndex;
        if (getRowSorter() != null)
            modelRow = getRowSorter().convertRowIndexToModel(viewIndex);

        return getElementAtModelIndex(modelRow);
    }

    public Element getElementAtModelIndex(int modelIndex) {
        return viewAdapter.getElementAtUsingModelIndex(modelIndex);
    }

    public int getModelRowCount() {
        return viewAdapter.getRowCount();
    }

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
}
