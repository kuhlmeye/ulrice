package net.ulrice.databinding.impl.ga;

import javax.swing.DefaultRowSorter;

/**
 * Default implementation of the row sorter of the list gui accessor.
 * 
 * @author christof
 */
public class TableGARowSorter extends DefaultRowSorter<TableGA, String> {

    /**
     * Constructs a new row sorter for the list gui accessor.
     * 
     * @param listGA The list gui accessor.
     */
    public TableGARowSorter(final TableGA listGA) {
        setModelWrapper(new ModelWrapper<TableGA, String>() {

            /**
             * @see javax.swing.DefaultRowSorter.ModelWrapper#getColumnCount()
             */
            @Override
            public int getColumnCount() {
                return listGA.getColumnCount();
            }

            /**
             * @see javax.swing.DefaultRowSorter.ModelWrapper#getIdentifier(int)
             */
            @Override
            public String getIdentifier(int row) {
                return listGA.getAttributeModel().getElementAt(row).getUniqueId();
            }

            /**
             * @see javax.swing.DefaultRowSorter.ModelWrapper#getModel()
             */
            @Override
            public TableGA getModel() {
                return listGA;
            }

            /**
             * @see javax.swing.DefaultRowSorter.ModelWrapper#getRowCount()
             */
            @Override
            public int getRowCount() {
                return listGA.getRowCount();
            }

            /**
             * @see javax.swing.DefaultRowSorter.ModelWrapper#getValueAt(int, int)
             */
            @Override
            public Object getValueAt(int row, int column) {
                return listGA.getValueAt(row, column);
            }
        });
    }
}
