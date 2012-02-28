package net.ulrice.databinding.viewadapter.utable;

import javax.swing.JComponent;
import javax.swing.JTable;

public interface UTableRenderer {
    
    JComponent adaptComponent(JTable table, boolean isSelected, int row, int column, JComponent component);

}
