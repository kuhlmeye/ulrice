/**
 * 
 */
package net.ulrice.sample.module.laflist;

import javax.swing.table.DefaultTableModel;

import net.ulrice.module.IFModel;

/**
 * Data model of the look and feel module
 * 
 * @author christof
 */
public class MLafList extends DefaultTableModel implements IFModel {

	/** Default generated serial version uid. */
	private static final long serialVersionUID = 1731283174024412217L;

	/**
	 * Initialize the table.
	 * 
	 * @see net.ulrice.module.IFModel#initialize()
	 */
	@Override
	public void initialize() {
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
