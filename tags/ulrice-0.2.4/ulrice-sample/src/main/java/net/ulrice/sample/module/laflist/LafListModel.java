/**
 * 
 */
package net.ulrice.sample.module.laflist;

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
}
