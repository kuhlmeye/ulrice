/**
 * 
 */
package net.ulrice.databinding.viewadapter.utable;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;
import net.ulrice.databinding.bufferedbinding.impl.Element;
import net.ulrice.databinding.bufferedbinding.impl.GenericAM;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;
import net.ulrice.databinding.ui.BindingUI;
import net.ulrice.databinding.viewadapter.IFStateMarker;
import net.ulrice.databinding.viewadapter.IFTooltipHandler;

/**
 * @author christof
 */
public class UTableVADefaultRenderer extends DefaultTableCellRenderer {

    private UTableViewAdapter tableVA;

    private IFStateMarker stateMarker;
    private IFTooltipHandler<Element> tooltipHandler;

    private Color evenNormalBackground = BindingUI.getColor(BindingUI.BACKGROUND_NORMAL_EVEN_TABLE_ROW, new Color(230, 230, 230));
    private Color oddNormalBackground = BindingUI.getColor(BindingUI.BACKGROUND_NORMAL_ODD_TABLE_ROW, new Color(200, 200, 200));
    private Color evenReadOnlyBackground = BindingUI.getColor(BindingUI.BACKGROUND_READONLY_EVEN_TABLE_ROW, new Color(200, 230, 200));
    private Color oddReadOnlyBackground = BindingUI.getColor(BindingUI.BACKGROUND_READONLY_ODD_TABLE_ROW, new Color(170, 200, 170));
    private Color selectedBackground = BindingUI.getColor(BindingUI.BACKGROUND_SELECTED_TABLE_ROW, new Color(200, 200, 255));

    public UTableVADefaultRenderer(UTableViewAdapter tableVA) {
        this.tableVA = tableVA;
    }

    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object,
     *      boolean, boolean, int, int)
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
        int row, int column) {

        JComponent component =
                (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        boolean readOnly = !getTableVA().isCellEditable(row, column);
        

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

        boolean dirty = false;
        boolean valid = true;

        Element element = getTableVA().getElementAt(row);
        if (stateMarker != null) {
            dirty |= element.isOriginalValueDirty();
            valid &= element.isOriginalValueValid();

            dirty |= element.isColumnDirty(columnId);
            valid &= element.isColumnValid(columnId);

            stateMarker.initialize(component);
            stateMarker.updateState(dirty, valid, component);
        }

        if (tooltipHandler != null) {
            tooltipHandler.updateTooltip(element, component);
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

    protected UTableViewAdapter getTableVA() {
        return tableVA;
    }
}
