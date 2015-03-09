package net.ulrice.databinding.columnchooser;

import net.ulrice.Ulrice;
import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;
import net.ulrice.databinding.validation.impl.NotNullValidator;
import net.ulrice.databinding.viewadapter.utable.UTableComponent;
import net.ulrice.message.TranslationProvider;
import net.ulrice.message.TranslationUsage;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * This tool is able to let the user select the columns that should be shown. Unique columns are always shown!
 * Use "addColumnChooser()" in tableAMbuilder
 * Right-click on table header to show column chooser!
 * Table data is saved in Local User Profile
 *
 * @author EXSTHUB
 */
public class ColumnChooser {

    private static final ImageIcon CHOOSE_IMAGE = new ImageIcon(ColumnChooser.class.getResource("choosecolumns.png"));

    private final TableAM tableAM;
    private final UTableComponent tableView;
    private final String columnChooserUniqueID;

    private Map<String, ColumnDefinition.ColumnType> initialColumnTypes = new HashMap<>();

    private ColumnTableModel model;
    private ColumnChooserView view;

    public ColumnChooser(TableAM tableAM, UTableComponent tableView, String uniqueId) {
        this.tableAM = tableAM;
        this.tableView = tableView;
        this.columnChooserUniqueID = uniqueId.substring(0, uniqueId.length() > Preferences.MAX_KEY_LENGTH ? Preferences.MAX_KEY_LENGTH : uniqueId.length());

        for (int i = 0; i < tableAM.getColumnCount(); i++) {
            ColumnDefinition column = tableAM.getColumnByIndex(i);
            initialColumnTypes.put(column.getId(), column.getColumnType());
        }

        addContextMenuToTableHeader();

        loadExistingPrefs();
    }

    private void loadExistingPrefs() {
        List<String> columnsToHide = ColumnChooserSaver.loadPrefs(columnChooserUniqueID);
        if (columnsToHide == null || columnsToHide.isEmpty()) {
            return;
        }
        updateViewColumns(columnsToHide);
    }

    private void savePrefs() {
        List<String> columnsToHide = model.getColumnsToHide();

        ColumnChooserSaver.savePrefs(columnChooserUniqueID, columnsToHide);
        updateViewColumns(columnsToHide);
    }

    private void resetPrefs() {
        ColumnChooserSaver.savePrefs(columnChooserUniqueID, new ArrayList<String>());
        view.dispose();
        updateViewColumns(new ArrayList<String>());
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
        init();
        view.setVisible(true);
    }

    private void init() {
        model = new ColumnTableModel();

        for (int i = 0; i < tableAM.getColumnCount(); i++) {
            ColumnDefinition column = tableAM.getColumnByIndex(i);
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
    }

    private void updateViewColumns(List<String> columnsToHide) {
        tableView.disableUserSorting();

        for (String columnId : initialColumnTypes.keySet()) {
            ColumnDefinition column = tableAM.getColumnById(columnId);
            if (column != null) {
                ColumnDefinition.ColumnType typeToSet = initialColumnTypes.get(columnId);

                if (columnsToHide.contains(columnId) && !tableAM.isUniquePathColumn(columnId)) {
                    typeToSet = ColumnDefinition.ColumnType.Hidden;
                }
                column.setColumnType(typeToSet);
            }
        }

        tableView.updateColumnModel();
        tableView.enableUserSorting();
    }

}
