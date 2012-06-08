package net.ulrice.databinding.viewadapter.utable;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import net.ulrice.databinding.bufferedbinding.impl.ColumnColorOverride;
import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;
import net.ulrice.databinding.bufferedbinding.impl.Element;
import net.ulrice.databinding.viewadapter.IFCellStateMarker;
import net.ulrice.databinding.viewadapter.IFCellTooltipHandler;

public abstract class AbstractUTableRenderer extends DefaultTableCellRenderer implements UTableRenderer {
    IFCellStateMarker stateMarker;
    IFCellTooltipHandler tooltipHandler;
    static IFCellStateMarker defaultStateMarker;
    static IFCellTooltipHandler defaultTooltipHandler;

    public AbstractUTableRenderer() {
        super();
        stateMarker = defaultStateMarker;
        tooltipHandler = defaultTooltipHandler;
        setForeground(UIManager.getColor("Label.Foreground"));
    }

    public JComponent adaptComponent(JTable table, boolean isSelected, int row, int column, JComponent component) {
        if (table instanceof UTable) {
            
            boolean dirty = false;
            boolean valid = true;
            

            UTable uTable = (UTable) table;
            UTableComponent tableComponent = uTable.getTableComponent();
            boolean readOnly = !uTable.isCellEditable(row, column);
            
            String columnId = table.getColumnModel().getColumn(column).getIdentifier().toString();
            
            ColumnDefinition< ?> colDef = tableComponent.getColumnById(columnId);
            
            if(colDef.getPreRendererList() != null){
                component = adapt(table, isSelected, row,column, component, colDef.getPreRendererList());
            }            
            
            if (colDef.getBorder() != null) {
                component.setBorder(colDef.getBorder());
            }

            Element element = tableComponent.getElementAtViewIndex(row);
            if (element != null) {
                IFCellStateMarker sm = getStateMarker(tableComponent);
                if (sm != null) {
                    dirty |= element.isOriginalValueDirty();
                    valid &= element.isOriginalValueValid();

                    dirty |= element.isInsertedOrRemoved();

                    dirty |= element.isColumnDirty(columnId);
                    valid &= element.isColumnValid(columnId);

                    sm.initialize(component);
                    sm.updateState(element, row, isSelected, !readOnly, dirty, valid, component);
                }

                IFCellTooltipHandler tth = getTooltipHandler(tableComponent);
                if (tth != null) {
                    tth.updateTooltip(element, columnId, component);
                }
            }

            ColumnColorOverride colorOverride = colDef.getColumnColorOverride();
            if (colorOverride != null && !dirty && valid) {
                if (readOnly && !isSelected) {
                    component.setBackground(row % 2 == 0 ? colorOverride.getEvenReadOnlyColor() : colorOverride
                        .getOddReadOnlyColor());
                }
                else if (!readOnly && !isSelected) {
                    component.setBackground(row % 2 == 0 ? colorOverride.getEvenNormalColor() : colorOverride
                        .getOddNormalColor());
                }
            }
            
            if(colDef.getPostRendererList() != null){
                component = adapt(table, isSelected, row,column, component, colDef.getPostRendererList());
            }
        }
       
        return component;
    }
    
    private JComponent adapt(JTable table, boolean isSelected, int row, int column, JComponent component,
        List<UTableRenderer> list) {
        for(UTableRenderer renderer : list){
            component = renderer.adaptComponent(table, isSelected, row, column, component);
        }
        return component;
    }
  

    public IFCellStateMarker getStateMarker(UTableComponent tableComponent) {
        if (stateMarker != null) {
            return stateMarker;
        }
        else if (tableComponent.getCellStateMarker() != null) {
            return tableComponent.getCellStateMarker();
        }
        return AbstractUTableRenderer.getDefaultStateMarker();
    }

    public void setStateMarker(IFCellStateMarker stateMarker) {
        this.stateMarker = stateMarker;
    }

    public IFCellTooltipHandler getTooltipHandler(UTableComponent tableComponent) {
        if (tooltipHandler != null) {
            return tooltipHandler;
        }
        else if (tableComponent.getCellTooltipHandler() != null) {
            return tableComponent.getCellTooltipHandler();
        }
        return AbstractUTableRenderer.getDefaultTooltipHandler();
    }

    public void setTooltipHandler(IFCellTooltipHandler tooltipHandler) {
        this.tooltipHandler = tooltipHandler;
    }

    public static void setDefaultStateMarker(IFCellStateMarker defaultStateMarker) {
        AbstractUTableRenderer.defaultStateMarker = defaultStateMarker;
    }

    public static void setDefaultTooltipHandler(IFCellTooltipHandler defaultTooltipHandler) {
        AbstractUTableRenderer.defaultTooltipHandler = defaultTooltipHandler;
    }

    public static IFCellStateMarker getDefaultStateMarker() {
        return defaultStateMarker;
    }

    public static IFCellTooltipHandler getDefaultTooltipHandler() {
        return defaultTooltipHandler;
    }
}
