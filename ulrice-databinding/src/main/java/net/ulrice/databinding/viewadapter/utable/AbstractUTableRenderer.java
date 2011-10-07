package net.ulrice.databinding.viewadapter.utable;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import net.ulrice.databinding.bufferedbinding.impl.Element;
import net.ulrice.databinding.ui.BindingUI;
import net.ulrice.databinding.viewadapter.IFStateMarker;
import net.ulrice.databinding.viewadapter.IFTooltipHandler;

public abstract class AbstractUTableRenderer extends DefaultTableCellRenderer {
    IFStateMarker stateMarker;
    IFTooltipHandler<Element> tooltipHandler;
    static IFStateMarker defaultStateMarker;
    static IFTooltipHandler<Element> defaultTooltipHandler;

    Color evenNormalBackground = BindingUI.getColor(BindingUI.BACKGROUND_NORMAL_EVEN_TABLE_ROW, new Color(230, 230, 230));
    Color oddNormalBackground = BindingUI.getColor(BindingUI.BACKGROUND_NORMAL_ODD_TABLE_ROW, new Color(200,200, 200));
    Color evenReadOnlyBackground = BindingUI.getColor(BindingUI.BACKGROUND_READONLY_EVEN_TABLE_ROW,new Color(200, 230, 200));
    Color oddReadOnlyBackground = BindingUI.getColor(BindingUI.BACKGROUND_READONLY_ODD_TABLE_ROW, new Color(170, 200, 170));
    Color selectedBackground = BindingUI.getColor(BindingUI.BACKGROUND_SELECTED_TABLE_ROW, new Color(200, 200, 255));
    
    public AbstractUTableRenderer() {        
        super();        
        stateMarker = defaultStateMarker;
        tooltipHandler = defaultTooltipHandler;
    }

    public JComponent adaptComponent(JTable table, boolean isSelected, int row, int column, JComponent component) {
        if (table instanceof UTable) {
    
            boolean dirty = false;
            boolean valid = true;
    
            UTable uTable = (UTable) table;
            UTableComponent tableComponent = uTable.getTableComponent();
            boolean readOnly = !uTable.isCellEditable(row, column);
    
            if (isSelected) {
                component.setBackground(selectedBackground);
            }
            else if (readOnly) {
                component.setBackground(row % 2 == 0 ? evenReadOnlyBackground : oddReadOnlyBackground);
            }
            else {
                component.setBackground(row % 2 == 0 ? evenNormalBackground : oddNormalBackground);
            }
    
            String columnId = table.getColumnModel().getColumn(column).getIdentifier().toString();
            // TODO SELECTION BACKGROUND
    
            Element element = tableComponent.getElementAtViewIndex(row);
            if (element != null) {
                IFStateMarker sm = stateMarker;
                if(sm == null) {
                    sm = AbstractUTableRenderer.defaultStateMarker;
                }
                IFTooltipHandler tth = tooltipHandler;
                if(tth == null) {
                    tth = AbstractUTableRenderer.defaultTooltipHandler;
                }
                if (sm != null) {
                    dirty |= element.isOriginalValueDirty();
                    valid &= element.isOriginalValueValid();
    
                    dirty |= element.isInsertedOrRemoved();
    
                    dirty |= element.isColumnDirty(columnId);
                    valid &= element.isColumnValid(columnId);
    
                    sm.initialize(component);
                    sm.updateState(dirty, valid, component);
                }
    
                if (tth != null) {
                    tth.updateTooltip(element, component);
                }
    
            }
        }
        return component;
    }

    public IFStateMarker getStateMarker() {
        return stateMarker;
    }

    public void setStateMarker(IFStateMarker stateMarker) {
        this.stateMarker = stateMarker;        
    }

    public IFTooltipHandler<Element> getTooltipHandler() {
        return tooltipHandler;
    }

    public void setTooltipHandler(IFTooltipHandler<Element> tooltipHandler) {
        this.tooltipHandler = tooltipHandler;
    }
    
    public static void setDefaultStateMarker(IFStateMarker defaultStateMarker) {
        AbstractUTableRenderer.defaultStateMarker = defaultStateMarker;
    }
    
    public static void setDefaultTooltipHandler(IFTooltipHandler<Element> defaultTooltipHandler) {
        AbstractUTableRenderer.defaultTooltipHandler = defaultTooltipHandler;
    }
    
    public static IFStateMarker getDefaultStateMarker() {
        return defaultStateMarker;
    }
    
    public static IFTooltipHandler<Element> getDefaultTooltipHandler() {
        return defaultTooltipHandler;
    }
}
