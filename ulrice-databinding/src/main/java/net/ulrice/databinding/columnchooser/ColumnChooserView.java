package net.ulrice.databinding.columnchooser;

import net.ulrice.Ulrice;
import net.ulrice.message.TranslationProvider;
import net.ulrice.message.TranslationUsage;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * View for choosing visible table columns
 *
 * @author EXSTHUB
 */
public class ColumnChooserView extends JDialog {
    private static final long serialVersionUID = 1L;

    public ColumnChooserView(ColumnTableModel tableModel) {
        super(Ulrice.getMainFrame().getFrame());
        this.tableModel = tableModel;
        initGUI();
    }

    private ColumnTableModel tableModel;
    private JButton saveButton;
    private JButton cancleButton;
    private ColumnChooserTable table;

    private void initGUI() {
        TranslationProvider tp = Ulrice.getTranslationProvider();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        String title = tp.getUlriceTranslation(TranslationUsage.Title, "ColumnChooser").getText();
        String importButtonText = tp.getUlriceTranslation(TranslationUsage.Button, "OK").getText();
        String cancelButtonText = tp.getUlriceTranslation(TranslationUsage.Button, "Cancel").getText();
        setTitle(title);

        setSize(520, 550);
        setMinimumSize(new Dimension(520, 550));

        table = new ColumnChooserTable();
        table.setModel(tableModel);

        JScrollPane tableScrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());

        saveButton = new JButton(importButtonText);
        cancleButton = new JButton(cancelButtonText);
        cancleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        TableColumn tableCol0 = table.getColumnModel().getColumn(0);
        tableCol0.setMaxWidth(75);
        tableCol0.setCellRenderer(new CheckboxCellRenderer(tableModel, true));

        TableColumn tableCol2 = table.getColumnModel().getColumn(2);
        tableCol2.setMaxWidth(75);
        tableCol2.setMinWidth(75);
        tableCol2.setCellRenderer(new CheckboxCellRenderer(tableModel, false));

        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancleButton);

        add(tableScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public void setSaveButton(JButton saveButton) {
        this.saveButton = saveButton;
    }

    public JButton getCancleButton() {
        return cancleButton;
    }

    public void setCancleButton(JButton cancleButton) {
        this.cancleButton = cancleButton;
    }

    public JTable getTable() {
        return table;
    }

    public TableModel getTableModel() {
        return tableModel;
    }

}
