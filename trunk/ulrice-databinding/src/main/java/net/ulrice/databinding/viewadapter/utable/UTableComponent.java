package net.ulrice.databinding.viewadapter.utable;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;
import net.ulrice.databinding.bufferedbinding.impl.Element;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;
import net.ulrice.databinding.viewadapter.IFStateMarker;
import net.ulrice.databinding.viewadapter.IFTooltipHandler;


public class UTableComponent extends JPanel {

	private static final long serialVersionUID = 6533485227507042410L;

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

	public UTableComponent(final UTableViewAdapter viewAdapter, final int fixedColumns) {

		rowSelModel = new DefaultListSelectionModel();

		this.fixedColumns = fixedColumns;
		this.originalFixedColumns = fixedColumns;
		
		staticTableModel = new UTableModel(false, UTableComponent.this.fixedColumns, viewAdapter);
		staticTable = new UTable(viewAdapter, staticTableModel, rowSelModel);
		
		scrollTableModel = new UTableModel(true, UTableComponent.this.fixedColumns, viewAdapter);
		scrollTable = new UTable(viewAdapter, scrollTableModel, rowSelModel);

		sorter = new UTableRowSorter(viewAdapter, UTableComponent.this.fixedColumns, staticTableModel, scrollTableModel);
		staticTable.setRowSorter(sorter.getStaticTableRowSorter());
		scrollTable.setRowSorter(sorter.getScrollTableRowSorter());
		

		staticTableRenderer = new UTableVADefaultRenderer(viewAdapter);
		staticTable.setDefaultRenderer(Object.class, staticTableRenderer);
		scrollTableRenderer = new UTableVADefaultRenderer(viewAdapter);
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
	 * @param attributeModel
	 */
	protected void updateColumnModel(final TableAM attributeModel) {
	    
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
}
