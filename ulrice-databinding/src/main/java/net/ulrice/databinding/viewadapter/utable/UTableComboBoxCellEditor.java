package net.ulrice.databinding.viewadapter.utable;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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

    public UTableComboBoxCellEditor(JComboBox comboBox) {
        this.comboBox = comboBox;
        this.comboBox.setBorder(null);        
        
        this.comboBox.addPopupMenuListener(new PopupMenuListener() {

            /**
             * {@inheritDoc}
             */
            public void popupMenuCanceled(PopupMenuEvent e) {
                // Synchronize the selected list item with the text field
                JComboBox box = (JComboBox) e.getSource();
                Object selectedItem = box.getSelectedItem();
                box.getEditor().setItem(selectedItem);
            }

            /**
             * {@inheritDoc}
             */
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                // Synchronize the selected list item with the text field
                JComboBox box = (JComboBox) e.getSource();
                Object selectedItem = box.getSelectedItem();
                box.getEditor().setItem(selectedItem);
            }

            /**
             * {@inheritDoc}
             */
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

                JComboBox box = (JComboBox) e.getSource();

                // Get the popup menu.
                Object comp = box.getUI().getAccessibleChild(box, 0);
                if (!(comp instanceof JPopupMenu)) {
                    return;
                }
                JPopupMenu popupMenu = (JPopupMenu) comp;

                // Get the scrollpane in the scroller.
                if (!(popupMenu.getComponent(0) instanceof JScrollPane)) {
                    return;
                }
                JScrollPane scrollPane = (JScrollPane) ((JPopupMenu) comp).getComponent(0);
                popupMenu.remove(scrollPane);
                popupMenu.setLayout(new BorderLayout());
                popupMenu.add(scrollPane, BorderLayout.CENTER);
                // Get the component from the scrollpane.
                if (!(scrollPane.getViewport().getView() instanceof JComponent)) {
                    return;
                }
                JComponent comboBoxPopup = (JComponent) scrollPane.getViewport().getView();

                // Check the size of the popup and set it to preferred size, if
                // the size
                // is greater than the size of the combo box.
                Dimension size = new Dimension();
                size.width =
                        comboBoxPopup.getPreferredSize().width < scrollPane.getPreferredSize().width ? scrollPane
                            .getPreferredSize().width : comboBoxPopup.getPreferredSize().width;
                size.height = scrollPane.getPreferredSize().height;
                scrollPane.setPreferredSize(size);
                scrollPane.setMinimumSize(size);
                scrollPane.setMaximumSize(size);

                comboBoxPopup.setPreferredSize(size);
                comboBoxPopup.setMinimumSize(size);
                comboBoxPopup.setMaximumSize(size);
                
                popupMenu.setPreferredSize(size);
                popupMenu.setMinimumSize(size);
                popupMenu.setMaximumSize(size);
            }
        });
    }

    public UTableComboBoxCellEditor(List< ?> valueRange) {
        this(new JComboBox(valueRange.toArray()));
    }

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
