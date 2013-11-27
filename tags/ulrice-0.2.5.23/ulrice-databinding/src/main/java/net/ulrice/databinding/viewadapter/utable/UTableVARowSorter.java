package net.ulrice.databinding.viewadapter.utable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.RowSorter;
import javax.swing.SortOrder;


/**
 * Default implementation of the row sorter of the list gui accessor.
 * 
 * @author christof
 */
public class UTableVARowSorter extends RowSorter<UTableModel> {

	private UTableModel model;
	private List<? extends SortKey> sortKeys;

	public UTableVARowSorter(UTableModel model) {
		this.model = model;
		this.sortKeys = Collections.emptyList();
	}

	@Override
	public UTableModel getModel() {
		return model;
	}

	@Override
	public void toggleSortOrder(int column) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public List<? extends SortKey> getSortKeys() {
		return sortKeys;
	}

	@Override
	public void setSortKeys(List<? extends SortKey> keys) {
		this.sortKeys = keys;
	}

	@Override
	public int convertRowIndexToModel(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int convertRowIndexToView(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getViewRowCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getModelRowCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void modelStructureChanged() {
		List<SortKey> sortKeys = new ArrayList<SortKey>();
		for(int i = 0; i < model.getColumnCount(); i++) {
			sortKeys.add(new SortKey(i, SortOrder.UNSORTED));
		}
		setSortKeys(sortKeys);
	}

	@Override
	public void allRowsChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rowsInserted(int firstRow, int endRow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rowsDeleted(int firstRow, int endRow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rowsUpdated(int firstRow, int endRow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rowsUpdated(int firstRow, int endRow, int column) {
		// TODO Auto-generated method stub
		
	}

	public void sort() {
		// TODO Auto-generated method stub		
	}
}
