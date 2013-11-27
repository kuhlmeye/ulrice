/**
 * 
 */
package net.ulrice.sample.module.laflist;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

/**
 * Data model of the look and feel module
 * 
 * @author christof
 */
public class LafListModel extends DefaultTableModel {

	/** Default generated serial version uid. */
	private static final long serialVersionUID = 1731283174024412217L;

	public LafListModel() {
		addColumn("Key");
		addColumn("Value");
	}

	/**
	 * Add a new look and feel value.
	 * @param key The look and feel key.
	 * @param value The look and feel value.
	 */
	public void addValue(String key, String value) {
		addRow(new String[]{key, value});
	}
	

	/***
	 * Comparator used to sort the look and feel constants.
	 * 
	 * @author christof
	 */
	class StringComparator implements Comparator<Object> {

		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Object o1, Object o2) {
			return o1.toString().compareTo(o2.toString());
		}
	}


	public void refresh() {
		SortedSet<Object> lafKeySet = new TreeSet<Object>(new StringComparator());

		lafKeySet.addAll(UIManager.getLookAndFeel().getDefaults().keySet());
		lafKeySet.addAll(UIManager.getDefaults().keySet());
		
		for (Object key : lafKeySet) {
			Object value = UIManager.get(key);
			addValue(key == null ? "" : key.toString(), value == null ? "" : value.toString());
		}
		fireTableDataChanged();
	}
}
