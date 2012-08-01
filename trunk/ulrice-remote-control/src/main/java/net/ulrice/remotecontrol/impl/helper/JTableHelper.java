package net.ulrice.remotecontrol.impl.helper;

import java.awt.Component;
import java.awt.Robot;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import net.ulrice.remotecontrol.ComponentTableData;
import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.RemoteControlUtils;
import net.ulrice.remotecontrol.util.Result;

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
    public Object getData(JTable component) throws RemoteControlException {
        ComponentTableData data = new ComponentTableData();

        for (int column = 0; column < component.getColumnCount(); column += 1) {
            data.setHeader(column, component.getColumnName(column));
        }

        TableModel model = component.getModel();

        for (int row = 0; row < model.getRowCount(); row += 1) {
            for (int column = 0; column < model.getColumnCount(); column += 1) {
                try {
                    int modelRow = component.convertRowIndexToModel(row);
                    int modelColumn = component.convertColumnIndexToModel(column);
                    int preferredWidth = component.getColumnModel().getColumn(modelColumn).getPreferredWidth();

                    data.setEntry(row, column, model.getValueAt(modelRow, modelColumn), component.isCellSelected(row, column), preferredWidth == 0);
                }
                catch (IndexOutOfBoundsException e) {
                    // concurrent problem
                }
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
    public boolean enter(final Robot robot, final JTable component, final String text, int row, int column) throws RemoteControlException {
        final int finalRow = invertValue(row, component.getRowCount());
        final int finalColumn = invertValue(column, component.getColumnCount());

        final Result<Boolean> result = new Result<Boolean>(2);

        try {
            RemoteControlUtils.invokeInSwing(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (!component.editCellAt(finalRow, finalColumn)) {
                            result.fireResult(false);
                            return;
                        }

                        Component editor = component.getComponent(component.getComponentCount() - 1);
                        result.fireResult(ComponentHelperRegistry.get(editor.getClass()).enter(robot, editor, text));
                    }
                    catch (Exception e) {
                        result.fireException(e);
                    }
                }

            });
        }
        catch (RemoteControlException e) {
            throw new RemoteControlException(String.format("Failed to enter %s into row/column %d/%d of the table", text, row, column), e);
        }

        boolean editResult = result.aquireResult();

        final TableCellEditor cellEditor = component.getCellEditor();

        if (cellEditor != null) {
            try {
                RemoteControlUtils.invokeInSwing(new Runnable() {

                    @Override
                    public void run() {
                        cellEditor.stopCellEditing();
                    }
                });
            }
            catch (RemoteControlException e) {
                throw new RemoteControlException(String.format("Failed to stop editing after entering %s into row/column %d/%d of the table", text, row, column), e);
            }
        }

        return editResult;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.remotecontrol.impl.helper.AbstractComponentHelper#select(java.awt.Robot, java.awt.Component,
     *      int, int)
     */
    @Override
    public boolean select(Robot robot, final JTable component, final int start, final int end) throws RemoteControlException {
        final Result<Boolean> result = new Result<Boolean>(2);

        try {
            RemoteControlUtils.invokeInSwing(new Runnable() {

                @Override
                public void run() {
                    try {
                        ListSelectionModel selectionModel = component.getSelectionModel();

                        selectionModel.addSelectionInterval(invertValue(start, component.getRowCount()), invertValue(end, component.getRowCount()));

                        result.fireResult(true);
                    }
                    catch (Exception e) {
                        result.fireException(e);
                    }
                }

            });

            return result.aquireResult();
        }
        catch (RemoteControlException e) {
            throw new RemoteControlException(String.format("Failed to select section %d to %d of the table", start, end), e);
        }

    }

}
