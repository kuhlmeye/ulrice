package net.ulrice.databinding.viewadapter.utable;

/**
 * Conversion interface that is used to get values from the utable to the clipboard and vice versa.
 * 
 * @author DL10KUH
 */
public interface UTableCopyPasteCellConverter {

	/**
	 * Converts the cell value to a String presentation that is put into the clipboard.
	 */
	String cellToClipboard(Object value);

	/**
	 * Converts the clipboard string value into the object that is put into the cell.
	 */
	Object clipboardToCell(String value);

}
