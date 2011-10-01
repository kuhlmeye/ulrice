package net.ulrice.databinding.viewadapter.utable;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import net.ulrice.databinding.bufferedbinding.impl.Element;
import net.ulrice.databinding.ui.BindingUI;
import net.ulrice.databinding.viewadapter.IFStateMarker;
import net.ulrice.databinding.viewadapter.IFTooltipHandler;

public class AbstractUTableRenderer extends DefaultTableCellRenderer {
    IFStateMarker stateMarker;
    IFTooltipHandler<Element> tooltipHandler;

    Color evenNormalBackground = BindingUI.getColor(BindingUI.BACKGROUND_NORMAL_EVEN_TABLE_ROW, new Color(230, 230, 230));
    Color oddNormalBackground = BindingUI.getColor(BindingUI.BACKGROUND_NORMAL_ODD_TABLE_ROW, new Color(200,200, 200));
    Color evenReadOnlyBackground = BindingUI.getColor(BindingUI.BACKGROUND_READONLY_EVEN_TABLE_ROW,new Color(200, 230, 200));
    Color oddReadOnlyBackground = BindingUI.getColor(BindingUI.BACKGROUND_READONLY_ODD_TABLE_ROW, new Color(170, 200, 170));
    Color selectedBackground = BindingUI.getColor(BindingUI.BACKGROUND_SELECTED_TABLE_ROW, new Color(200, 200, 255));
    
    public AbstractUTableRenderer() {
        super();
    }

    public JComponent adaptComponent(JTable table, boolean isSelected, int row, int column, JComponent component) {
        if (table instanceof UTable) {
    
            boolean dirty = false;
            boolean valid = true;
    
            UTableComponent tableComponent = ((UTable) table).getTableComponent();
            boolean readOnly = !tableComponent.isCellEditable(row, column);
    
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
                if (stateMarker != null) {
                    dirty |= element.isOriginalValueDirty();
                    valid &= element.isOriginalValueValid();
    
                    dirty |= element.isInsertedOrRemoved();
    
                    dirty |= element.isColumnDirty(columnId);
                    valid &= element.isColumnValid(columnId);
    
                    stateMarker.initialize(component);
                    stateMarker.updateState(dirty, valid, component);
                }
    
                if (tooltipHandler != null) {
                    tooltipHandler.updateTooltip(element, component);
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
}
