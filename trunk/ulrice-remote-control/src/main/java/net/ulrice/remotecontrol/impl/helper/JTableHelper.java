package net.ulrice.remotecontrol.impl.helper;

import java.awt.Component;
import java.awt.Robot;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import net.ulrice.remotecontrol.ComponentTableData;
import net.ulrice.remotecontrol.RemoteControlException;

public class JTableHelper extends AbstractJComponentHelper<JTable> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<JTable> getType() {
        return JTable.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getData(JTable component) {
        ComponentTableData data = new ComponentTableData();

        for (int column = 0; column < component.getColumnCount(); column += 1) {
            data.setHeader(column, component.getColumnName(column));
        }

        TableModel model = component.getModel();

        for (int row = 0; row < model.getRowCount(); row += 1) {
            for (int column = 0; column < model.getColumnCount(); column += 1) {
                int modelRow = component.convertRowIndexToModel(row);
                int modelColumn = component.convertColumnIndexToModel(column);

                data.setEntry(row, column, model.getValueAt(modelRow, modelColumn),
                    component.isCellSelected(row, column));
            }
        }

        return data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean click(Robot robot, JTable component, int index) throws RemoteControlException {
        return click(robot, component, index, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean click(Robot robot, JTable component, int row, int column) throws RemoteControlException {
        row = invertValue(row, component.getRowCount());
        column = invertValue(column, component.getColumnCount());

        return click(robot, component, component.getCellRect(row, column, false));
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.impl.helper.AbstractComponentHelper#enter(java.awt.Robot, java.awt.Component,
     *      java.lang.String, int, int)
     */
    @Override
    public boolean enter(Robot robot, JTable component, String text, int row, int column)
        throws RemoteControlException {
        row = invertValue(row, component.getRowCount());
        column = invertValue(column, component.getColumnCount());

        if (!component.editCellAt(row, column)) {
            return false;
        }

        Component editor = component.getComponent(component.getComponentCount() - 1);

        return ComponentHelperRegistry.get(editor.getClass()).enter(robot, editor, text);
    }
}
