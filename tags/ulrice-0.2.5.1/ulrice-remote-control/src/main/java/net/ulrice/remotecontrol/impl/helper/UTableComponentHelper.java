package net.ulrice.remotecontrol.impl.helper;

import java.awt.Robot;

import javax.swing.JTable;

import net.ulrice.databinding.viewadapter.utable.UTableComponent;
import net.ulrice.remotecontrol.ComponentTableData;
import net.ulrice.remotecontrol.RemoteControlException;

public class UTableComponentHelper extends AbstractJComponentHelper<UTableComponent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<UTableComponent> getType() {
        return UTableComponent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getData(UTableComponent component) {
        ComponentTableData data = new ComponentTableData();

        for (int column = 0; column < component.getColumnCount(); column += 1) {
            try {
                data.setHeader(column, component.getColumnByViewIndex(column).getColumnName());
            }
            catch (ArrayIndexOutOfBoundsException e) {
                //ignore
            }
        }

        for (int row = 0; row < component.getViewRowCount(); row += 1) {
            for (int column = 0; column < component.getColumnCount(); column += 1) {
                data.setEntry(row, column,
                    component.getElementAtViewIndex(row).getValueAt(component.convertColumnIndexToModel(column)),
                    component.getSelectionModel().isSelectedIndex(row));
            }
        }

        return data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean click(Robot robot, UTableComponent component, int index) throws RemoteControlException {
        return click(robot, component, index, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean click(Robot robot, UTableComponent component, int row, int column) throws RemoteControlException {
        row = invertValue(row, component.getModelRowCount());
        column = invertValue(column, component.getColumnCount());

        if (column < component.getFixedColumns()) {
            JTable staticTable = component.getStaticTable();

            return ComponentHelperRegistry.get(staticTable.getClass()).click(robot, staticTable, row, column);
        }

        JTable scrollTable = component.getScrollTable();

        return ComponentHelperRegistry.get(scrollTable.getClass()).click(robot, scrollTable, row,
            column - component.getFixedColumns());
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.impl.helper.AbstractComponentHelper#enter(java.awt.Robot, java.awt.Component,
     *      java.lang.String, int, int)
     */
    @Override
    public boolean enter(Robot robot, UTableComponent component, String text, int row, int column)
        throws RemoteControlException {

        row = invertValue(row, component.getModelRowCount());
        column = invertValue(column, component.getColumnCount());

        if (column < component.getFixedColumns()) {
            JTable staticTable = component.getStaticTable();

            return ComponentHelperRegistry.get(staticTable.getClass()).enter(robot, staticTable, text, row, column);
        }

        JTable scrollTable = component.getScrollTable();

        return ComponentHelperRegistry.get(scrollTable.getClass()).enter(robot, scrollTable, text, row,
            column - component.getFixedColumns());
    }

}
