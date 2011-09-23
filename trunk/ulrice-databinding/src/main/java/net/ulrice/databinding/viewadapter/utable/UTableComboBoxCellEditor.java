package net.ulrice.databinding.viewadapter.utable;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.AbstractListModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import net.ulrice.databinding.ObjectWithPresentation;

/**
 * The cell renderer for combo boxes.
 * 
 * @author christof
 */
public class UTableComboBoxCellEditor extends AbstractCellEditor implements TableCellEditor {


    private static final long serialVersionUID = 1073376082082392538L;
    private JComboBox comboBox;

    public UTableComboBoxCellEditor(JComboBox comboBox) {
        this.comboBox = comboBox;
        this.comboBox.setBorder(null);        
    }

    public UTableComboBoxCellEditor(List< ?> valueRange) {
        this(new JComboBox(valueRange.toArray()));
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        comboBox.setSelectedItem(value);
        return comboBox;
    }

    public Object getCellEditorValue() {
        
        
        Object selectedItem = comboBox.getSelectedItem();
        if(selectedItem instanceof ObjectWithPresentation<?>) {
            return ((ObjectWithPresentation<?>)selectedItem).getValue();
        }
        
        return selectedItem;
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        if (e instanceof MouseEvent) {
            return ((MouseEvent) e).getClickCount() >= 2;
        }
        return super.isCellEditable(e);
    }         
}
