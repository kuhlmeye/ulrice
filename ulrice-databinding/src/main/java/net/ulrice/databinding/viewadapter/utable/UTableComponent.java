package net.ulrice.databinding.viewadapter.utable;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;
import net.ulrice.databinding.bufferedbinding.impl.Element;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;
import net.ulrice.databinding.viewadapter.IFStateMarker;
import net.ulrice.databinding.viewadapter.IFTooltipHandler;


public class UTableComponent extends JPanel {



    private EventListenerList listenerList = new EventListenerList();
    
	private static final long serialVersionUID = 6533485227507042410L;

    private static final int RESIZE_MARGIN = 2;
	private UTable staticTable;
	private UTable scrollTable;

	private UTableVAFilter filter;
	
	private ListSelectionModel rowSelModel;

	private int fixedColumns;
	
	private int originalFixedColumns;

	private int selColumn = -1;

	private UTableRowSorter sorter;

	private UTableVADefaultRenderer staticTableRenderer;

	private UTableVADefaultRenderer scrollTableRenderer;

	private IFTooltipHandler<Element> tooltipHandler;

	private IFStateMarker stateMarker;

    private UTableModel staticTableModel;

    private UTableModel scrollTableModel;

    private TableAM attributeModel;

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
        
        scrollTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
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
        

        MouseListener mouseListener = new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                MouseListener[] listeners = listenerList.getListeners(MouseListener.class);
                if (listeners != null) {
                    for (MouseListener listener : listeners) {
                        listener.mouseReleased(adaptMouseEvent(e));
                    }
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
            }

            private MouseEvent adaptMouseEvent(MouseEvent e) {
                return new MouseEvent(UTableComponent.this, e.getID(), e.getWhen(), e.getModifiers(), e.getX(), e.getY(),
                    e.getXOnScreen(), e.getYOnScreen(), e.getClickCount(), e.isPopupTrigger(), e.getButton());
            }
        };
        staticTable.addMouseListener(mouseListener);
        scrollTable.addMouseListener(mouseListener);
    }
	
	public void init(final UTableViewAdapter viewAdapter) {
	    rowSelModel = new DefaultListSelectionModel();
	    
	    staticTableModel = new UTableModel(false, UTableComponent.this.fixedColumns, viewAdapter);
	    staticTable.setModel(staticTableModel);
	    staticTable.setSelectionModel(rowSelModel);
	    
	    scrollTableModel = new UTableModel(true, UTableComponent.this.fixedColumns, viewAdapter);
	    scrollTable.setModel(scrollTableModel);
	    scrollTable.setSelectionModel(rowSelModel);
	    
        sorter = new UTableRowSorter(viewAdapter, UTableComponent.this.fixedColumns, staticTableModel, scrollTableModel);
        staticTable.setRowSorter(sorter.getStaticTableRowSorter());
        scrollTable.setRowSorter(sorter.getScrollTableRowSorter());
        
        staticTableRenderer = new UTableVADefaultRenderer();
        staticTable.setDefaultRenderer(Object.class, staticTableRenderer);
        scrollTableRenderer = new UTableVADefaultRenderer();
        scrollTable.setDefaultRenderer(Object.class, scrollTableRenderer);
        
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

	public void setCellTooltipHandler(IFTooltipHandler<Element> tooltipHandler) {
		this.tooltipHandler = tooltipHandler;
		staticTableRenderer.setTooltipHandler(tooltipHandler);
		scrollTableRenderer.setTooltipHandler(tooltipHandler);
	}


	public void setCellStateMarker(IFStateMarker stateMarker) {
		this.stateMarker = stateMarker;
		staticTableRenderer.setStateMarker(stateMarker);
		scrollTableRenderer.setStateMarker(stateMarker);
	}

	public IFTooltipHandler<Element> getCellTooltipHandler() {
		return tooltipHandler;
	}

	public IFStateMarker getCellStateMarker() {
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
		} else {
			getScrollTable().setColumnSelectionInterval(selColumn - fixedColumns, selColumn - fixedColumns);
		}
	}

	public int getSelectedColumn() {
		return selColumn;
	}

	/**
	 */
	protected void updateColumnModel() {
	    
	    if(originalFixedColumns >= attributeModel.getColumnCount()) {
	        setFixedColumns(attributeModel.getColumnCount() > 0 ? attributeModel.getColumnCount() - 1 : 0);
	    }
	    
		TableColumnModel columnModel = null;
		List<ColumnDefinition<? extends Object>> columnDefinitions = attributeModel.getColumns();

		columnModel = getStaticTable().getColumnModel();
		for (int i = columnModel.getColumnCount() - 1; i >= 0; i--) {
			columnModel.removeColumn(columnModel.getColumn(i));
		}
		
		
		
		if (columnDefinitions != null) {
			for (int i = 0; i < fixedColumns; i++) {
				ColumnDefinition<?> columnDefinition = columnDefinitions.get(i);
                addColumn(columnModel, i, columnDefinition);
			}
		}

		columnModel = getScrollTable().getColumnModel();
		for (int i = columnModel.getColumnCount() - 1; i >= 0; i--) {
			columnModel.removeColumn(columnModel.getColumn(i));
		}

		if (columnDefinitions != null) {
			for (int i = fixedColumns; i < columnDefinitions.size(); i++) {
				ColumnDefinition<?> columnDefinition = columnDefinitions.get(i);
				addColumn(columnModel, i - fixedColumns, columnDefinition);
			}
		}
	}

    private void addColumn(TableColumnModel columnModel, int columnIndex, ColumnDefinition< ?> columnDefinition) {
        TableColumn column = new TableColumn();
        column.setIdentifier(columnDefinition.getId());
        column.setHeaderValue(columnDefinition);
        column.setModelIndex(columnIndex);
        if(columnDefinition.getCellEditor() != null) {
            column.setCellEditor(columnDefinition.getCellEditor());
        }
        if(columnDefinition.getCellRenderer() != null) {
            column.setCellRenderer(columnDefinition.getCellRenderer());
        }

        columnModel.addColumn(column);
        
        if(columnDefinition.isUseValueRange()) {				    
            if(columnDefinition.getValueRange() != null) {
                column.setCellEditor(new UTableComboBoxCellEditor(columnDefinition.getValueRange()));
            } else {
                column.setCellEditor(new UTableComboBoxCellEditor(Collections.EMPTY_LIST));
            }
        }
    }
	
	private void setFixedColumns(int fixedColumns) {
        this.fixedColumns = fixedColumns;
        this.scrollTableModel.setOffset(fixedColumns);
        this.staticTableModel.setOffset(fixedColumns);
    }

    public int convertColumnIndexToModel(int col) {
	    int modelCol = col;
	    if(col < fixedColumns) {
	        modelCol = staticTable.convertColumnIndexToModel(col);
	    } else {
	        modelCol = scrollTable.convertColumnIndexToModel(col) + fixedColumns;
	    }
	    return modelCol;
	}

    public boolean stopEditing() {
        return staticTable.getCellEditor().stopCellEditing() || scrollTable.getCellEditor().stopCellEditing();
    }

    public void setDefaultCellRenderer(Class<?> clazz, TableCellRenderer renderer) {
        scrollTable.setDefaultRenderer(clazz, renderer);
        staticTable.setDefaultRenderer(clazz, renderer);
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
    

    private int calcMaxSize(JTable table, int vColIndex, boolean includeHeader, TableColumn col) {
        int maxWidth = 0;

        if (includeHeader) {
            TableCellRenderer renderer = col.getHeaderRenderer();
            if (renderer == null) {
                renderer = table.getTableHeader().getDefaultRenderer();
            }
            Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
            maxWidth = comp.getPreferredSize().width;
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

    public int[] getSelectedRowsModelIndex() {
        int min = getSelectionModel().getMinSelectionIndex();
        int max = getSelectionModel().getMaxSelectionIndex();

        int[] tmpRows = new int[max - min + 1];
        int idx = 0;
        for (int i = min; i <= max; i++) {
            if (getSelectionModel().isSelectedIndex(i)) {
                tmpRows[idx++] = getRowSorter().convertRowIndexToModel(i);
            }
        }

        int[] result = new int[idx];
        System.arraycopy(tmpRows, 0, result, 0, idx);
        return result;
    }

    public void setAttributeModel(TableAM attributeModel) {
        this.attributeModel = attributeModel;
    }


    public void addRow() {
        checkAttributeModelSet();
        attributeModel.addElement(null);
    }

    public void delRowWithModelIndex(int modelIndex) {
        checkAttributeModelSet();
        attributeModel.delElement(modelIndex);
    }

    public void delRowWithViewIndex(int viewIndex) {
        checkAttributeModelSet();
        attributeModel.delElement(getRowSorter().convertRowIndexToModel(viewIndex));
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
        checkAttributeModelSet();
        int[] rowsInModel = getSelectedRowsModelIndex();
        List<Element> result = new ArrayList<Element>(rowsInModel.length);
        for (int row : rowsInModel) {
            result.add(attributeModel.getElementAt(row));
        }
        return result;
    }

    public Object getSelectedObject() {
        checkAttributeModelSet();
        int rowInModel = getSelectedRowModelIndex();
        return attributeModel.getCurrentValueAt(rowInModel);
    }
    
    private void checkAttributeModelSet() {
        if(attributeModel == null) {
            throw new IllegalStateException("Component is not bound to an attribute model.");
        }        
    }

    public Element getElementAtViewIndex(int viewIndex) {
        if(attributeModel == null) {
            return null;
        }
        int modelRow = getRowSorter().convertRowIndexToModel(viewIndex);
        return attributeModel.getElementAt(modelRow);
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

    public ColumnDefinition< ?> getColumnById(String columnId) {
        if(attributeModel != null) {
            return attributeModel.getColumnById(columnId);
        }
        return null;
    }
}
