package net.ulrice.databinding.columnchooser;

import net.ulrice.Ulrice;
import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;
import net.ulrice.databinding.validation.impl.NotNullValidator;
import net.ulrice.databinding.viewadapter.utable.UTableComponent;
import net.ulrice.message.TranslationProvider;
import net.ulrice.message.TranslationUsage;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

/**
 * This tool is able to let the user select the columns that should be shown. Unique columns are always shown!
 * Use "addColumnChooser()" in tableAMbuilder
 * Right-click on table header to show column chooser!
 * Table data is saved in Local User Profile
 *
 * @author Stefan Huber
 */
public class ColumnChooser {

    private static final ImageIcon CHOOSE_IMAGE = new ImageIcon(ColumnChooser.class.getResource("choosecolumns.png"));

    private final TableAM tableAM;
    private final UTableComponent tableView;
    private final String columnChooserUniqueID;
    private final List<String> defaultInvisibleColumns;

    private static final String VERSION = "1";

    private Map<String, ColumnDefinition.ColumnType> initialColumnTypes = new HashMap<>();

    private ColumnTableModel model;
    private ColumnChooserView view;

    public ColumnChooser(TableAM tableAM, UTableComponent tableView, String uniqueId, List<String> defaultInvisibleColumns) {
        this.tableAM = tableAM;
        this.tableView = tableView;
        this.defaultInvisibleColumns = defaultInvisibleColumns;
        this.columnChooserUniqueID = VERSION + uniqueId.substring(0, uniqueId.length() > getPrefLength() ? getPrefLength() : uniqueId.length());

        for (int i = 0; i < tableAM.getColumnCount(); i++) {
            ColumnDefinition<?> column = tableAM.getColumnByIndex(i);
            initialColumnTypes.put(column.getId(), column.getColumnType());
        }

        addContextMenuToTableHeader();

        //        loadExistingColPosPrefs();
        loadExistingPrefs();
    }

    private int getPrefLength() {
        return Preferences.MAX_KEY_LENGTH - VERSION.length() - 1;
    }

    private void loadExistingPrefs() {
        List<String> columnsToHide = ColumnChooserSaver.loadColVisiblePrefs(columnChooserUniqueID);
        if (columnsToHide == null || columnsToHide.isEmpty()) {
            updateViewColumns(defaultInvisibleColumns);
            return;
        }
        updateViewColumns(columnsToHide);
    }

    private void savePrefs() {
        List<String> columnsToHide = model.getColumnsToHide();

        Map<String, Integer> newColOrder = getCurrentColumnOrder();
        //        ColumnChooserSaver.saveColPosPrefs(columnChooserUniqueID, newColOrder);
        ColumnChooserSaver.saveColVisiblePrefs(columnChooserUniqueID, columnsToHide);
        updateViewColumns(columnsToHide);
        //        updateColumnOrder(newColOrder);
    }

    private void resetPrefs() {
        //        ColumnChooserSaver.saveColPosPrefs(columnChooserUniqueID, null);
        ColumnChooserSaver.saveColVisiblePrefs(columnChooserUniqueID, defaultInvisibleColumns);
        if (view != null) {
            view.dispose();
        }
        updateViewColumns(defaultInvisibleColumns);
    }

    private void addContextMenuToTableHeader() {
        final JPopupMenu popupMenu = new JPopupMenu();

        TranslationProvider tp = Ulrice.getTranslationProvider();
        String selectTitle = tp.getUlriceTranslation(TranslationUsage.Menu, "SelectColumns").getText();
        JMenuItem menuItem = new JMenuItem(selectTitle, CHOOSE_IMAGE);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDialog();
            }
        });
        popupMenu.add(menuItem);

        MouseListener popupListener = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }

            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        };
        tableView.getStaticTable().getTableHeader().addMouseListener(popupListener);
        tableView.getScrollTable().getTableHeader().addMouseListener(popupListener);
    }

    public void showDialog() {
        initDialog();
        view.setLocationRelativeTo(Ulrice.getMainFrame().getFrame());
        view.setVisible(true);
    }

    private void initDialog() {
        model = new ColumnTableModel();

        for (int i = 0; i < tableAM.getColumnCount(); i++) {
            ColumnDefinition<?> column = tableAM.getColumnByIndex(i);
            boolean requiredColumn = tableAM.isUniquePathColumn(column.getId());

            if (!column.getValidators().isEmpty()) {
                for (Object v : column.getValidators()) {
                    if (v instanceof NotNullValidator) {
                        // NotNull+Editable columns must be shown
                        requiredColumn |= column.getColumnType() == ColumnDefinition.ColumnType.Editable || column.getColumnType() == ColumnDefinition.ColumnType.NewEditable;
                        break;
                    }
                }
            }

            if (initialColumnTypes.containsKey(column.getId()) && initialColumnTypes.get(column.getId()) == ColumnDefinition.ColumnType.Hidden) {
                // columns that are hidden per default must NOT be shown
                continue;
            }

            model.addRow(column.getId(), column.getColumnName(), requiredColumn, (column.getColumnType() != ColumnDefinition.ColumnType.Hidden) || requiredColumn);
        }
        view = new ColumnChooserView(model);

        view.setIconImage(CHOOSE_IMAGE.getImage());
        InputMap inpMap = new ComponentInputMap(view.getRootPane());
        inpMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Esc");
        ActionMap actionMap = new JRootPane().getActionMap();
        actionMap.put("Esc", new AbstractAction() {
            private static final long serialVersionUID = 3333446670902001597L;

            @Override
            public void actionPerformed(ActionEvent e) {
                view.dispose();
            }
        });

        view.getSaveButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePrefs();
                view.dispose();
            }
        });

        view.getCancleButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.dispose();
            }
        });
        view.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inpMap);
        view.getRootPane().setActionMap(actionMap);

        view.getResetButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetPrefs();
            }
        });

        view.getUnselectAllButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean unselect = isUnselectAll();
                for (int i = 0; i < model.getRowCount(); i++) {
                    if (((Boolean) model.getValueAt(i, 0))) {
                        // required value, always set
                        continue;
                    }
                    model.setValueAt(unselect, i, 2);
                }
            }
        });
    }

    private void updateViewColumns(List<String> columnsToHide) {
        tableView.disableUserSorting();

        for (String columnId : initialColumnTypes.keySet()) {
            ColumnDefinition<?> column = tableAM.getColumnById(columnId);
            if (column != null) {
                ColumnDefinition.ColumnType typeToSet = initialColumnTypes.get(columnId);

                if (columnsToHide.contains(columnId) && !tableAM.isUniquePathColumn(columnId)) {

                    ColumnDefinition.ColumnType initialType = initialColumnTypes.get(columnId);

                    if (initialType == ColumnDefinition.ColumnType.Hidden_ReadOnly) {
                        typeToSet = ColumnDefinition.ColumnType.Hidden_ReadOnly;
                    }
                    else {
                        typeToSet = ColumnDefinition.ColumnType.Hidden;
                    }
                }
                column.setColumnType(typeToSet);
            }
        }

        tableView.updateColumnModel();
        tableView.enableUserSorting();

    }

    boolean unselectAll = true;

    public boolean isUnselectAll() {
        unselectAll = !unselectAll;
        return unselectAll;
    }

    public void loadExistingColPosPrefs() {
        final Map<String, Integer> colMap = ColumnChooserSaver.loadColPosPrefs(columnChooserUniqueID);

        // TODO FIXME how to find out if finished?
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        updateColumnOrder(colMap);
                    }
                });
            }
        }, 150, TimeUnit.MILLISECONDS);
    }

    public Map<String, Integer> getCurrentColumnOrder() {
        Map<String, Integer> columnMap = new HashMap<>();
        int idx = 0;
        Enumeration<TableColumn> columns = tableView.getScrollTable().getTableHeader().getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn tableColumn = columns.nextElement();
            columnMap.put(tableColumn.getIdentifier().toString(), idx);
            idx++;
        }
        return columnMap;
    }

    public void updateColumnOrder(Map<String, Integer> columnMap) {
        Enumeration<TableColumn> columns = tableView.getScrollTable().getColumnModel().getColumns();
        int idx = 0;

        if (columnMap == null || columnMap.isEmpty()) {
            tableView.getFilter().rebuildFilter();
            return;
        }

        while (columns.hasMoreElements()) {
            TableColumn tableColumn = columns.nextElement();
            tableView.getScrollTable().getColumnModel().moveColumn(idx, columnMap.get(tableColumn.getIdentifier().toString()));
            idx++;
        }
        if (tableView.getFilter() != null) {
            tableView.getFilter().rebuildFilter();
        }
    }

}
