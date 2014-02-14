package net.ulrice.databinding.viewadapter.utable;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
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

    public UTableComboBoxCellEditor(final JComboBox comboBox) {
        this.comboBox = comboBox;
        this.comboBox.setBorder(null);        
        
        this.comboBox.addPopupMenuListener(new PopupMenuListener() {
            
            private final static int ADDITIONAL_WIDTH = 30;

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                JComboBox box = (JComboBox) e.getSource();
                Object selectedItem = box.getSelectedItem();
                box.getEditor().setItem(selectedItem);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                JComboBox box = (JComboBox) e.getSource();
                Object selectedItem = box.getSelectedItem();
                box.getEditor().setItem(selectedItem);
            }

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

                final JComboBox box = (JComboBox) e.getSource();
                final JPopupMenu popupMenu = getPopupMenuFromComboBox(box);
                if (popupMenu == null) {
                    return;
                }

                final JScrollPane scrollPane = getScrollPaneFromPopupMenu(popupMenu);
                if (scrollPane == null) {
                    return;
                }
                
                popupMenu.remove(scrollPane);
                popupMenu.setLayout(new BorderLayout());
                popupMenu.add(scrollPane, BorderLayout.CENTER);
                
                final int widestElementWidth = detectWidthOfWidestElement(box) + ADDITIONAL_WIDTH;
                final int comboBoxWidth = comboBox.getWidth();
                
                final int popupMenuWidth = Math.max(widestElementWidth, comboBoxWidth);
                final int popupMenuHeight = scrollPane.getPreferredSize().height;
                final Dimension size = new Dimension(popupMenuWidth, popupMenuHeight);
                
                popupMenu.setPreferredSize(size);
            }
            
            private JPopupMenu getPopupMenuFromComboBox(final JComboBox box) {
                Object comp = box.getUI().getAccessibleChild(box, 0);
                if (!(comp instanceof JPopupMenu)) {
                    return null;
                }
                return (JPopupMenu) comp;
            }
            
            private JScrollPane getScrollPaneFromPopupMenu(final JPopupMenu popupMenu) {
                if (!(popupMenu.getComponent(0) instanceof JScrollPane)) {
                    return null;
                }
                return (JScrollPane) popupMenu.getComponent(0);
            }
            
            private int detectWidthOfWidestElement(final JComboBox box) {
                int maxWidth = 0;
                final FontMetrics metrics = box.getGraphics().getFontMetrics();
                
                final int elementCount = box.getItemCount();
                for (int i = 0; i < elementCount; i++) {
                    final Object element = box.getItemAt(i);
                    
                    final String textToMeasure = element == null ? "" : element.toString();
                    final int width = metrics.stringWidth(textToMeasure);
                    maxWidth = (width > maxWidth ? width : maxWidth);
                }
                return maxWidth;
            }
        });
    }

    public UTableComboBoxCellEditor(List< ?> valueRange) {
        this(new JComboBox(valueRange.toArray()));
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    	
    	for(int i = 0; i < comboBox.getModel().getSize(); i++) {
    		Object element = comboBox.getModel().getElementAt(i);
    		if(element == null && value == null) {
    			comboBox.setSelectedIndex(i);
    			return comboBox;
    		} else if(element instanceof ObjectWithPresentation<?>) {
    			ObjectWithPresentation<?> owp = (ObjectWithPresentation<?>) element;
    			if((value == null && owp.getValue() == null) || (owp.getValue() != null && owp.getValue().equals(value))) {
    				comboBox.setSelectedIndex(i);
    				return comboBox;
    			}
    		} else if(element != null && element.equals(value)) {
				comboBox.setSelectedIndex(i);
				return comboBox;
    		}
    	}
    	
        return comboBox;
    }

    @Override
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
