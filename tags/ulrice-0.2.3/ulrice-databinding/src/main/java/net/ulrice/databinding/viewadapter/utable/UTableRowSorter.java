package net.ulrice.databinding.viewadapter.utable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultRowSorter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;

public class UTableRowSorter extends DefaultRowSorter<UTableViewAdapter, String> {

	private UTableModelRowSorter scrollTableRS;
	private UTableModelRowSorter staticTableRS;
	private List<? extends RowSorter.SortKey> staticSortKeys;
	private List<? extends RowSorter.SortKey> scrollSortKeys;
	private int fixedColumns;
	private UTableViewAdapter model;

	public UTableRowSorter(final UTableViewAdapter model, int fixedColumns, UTableModel staticTableModel, UTableModel scrollTableModel) {
		this.staticSortKeys = Collections.emptyList();
		this.scrollSortKeys = Collections.emptyList();
		this.staticTableRS = new UTableModelRowSorter(false, staticTableModel);
		this.scrollTableRS = new UTableModelRowSorter(true, scrollTableModel);
		this.fixedColumns = fixedColumns;
		this.model = model;

		setModelWrapper(new ModelWrapper<UTableViewAdapter, String>() {

			@Override
			public UTableViewAdapter getModel() {
				return model;
			}

			@Override
			public int getColumnCount() {
				return model.getColumnCount();
			}

			@Override
			public int getRowCount() {
				return model.getRowCount();
			}

			@Override
			public Object getValueAt(int row, int column) {
				return model.getValueAt(row, column);
			}

			@Override
			public String getIdentifier(int row) {
				return model.getAttributeModel().getElementAt(row).getUniqueId();
			}
		});
	}

	public RowSorter<UTableModel> getStaticTableRowSorter() {
		return staticTableRS;
	}

	public RowSorter<UTableModel> getScrollTableRowSorter() {
		return scrollTableRS;
	}

	public void updateGlobalSortKeys() {
		List<SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		for (SortKey key : staticSortKeys) {
			sortKeys.add(new SortKey(key.getColumn(), key.getSortOrder()));
		}
		for (SortKey key : scrollSortKeys) {
			sortKeys.add(new SortKey(key.getColumn() + fixedColumns, key.getSortOrder()));
		}
		UTableRowSorter.this.setSortKeys(sortKeys);		
		UTableRowSorter.this.model.getComponent().invalidate();
		UTableRowSorter.this.model.getComponent().repaint();
	}		
	
	protected class UTableModelRowSorter extends RowSorter<UTableModel> {
		private UTableModel model;
		private boolean scrollable;

		public UTableModelRowSorter(boolean scrollable, UTableModel model) {
			this.model = model;
			this.scrollable = scrollable;
		}

		@Override
		public UTableModel getModel() {
			return model;
		}

		@Override
		public void toggleSortOrder(int column) {
			List<? extends SortKey> sortKeys = getSortKeys();
			SortKey sortKey = null;
			for (SortKey key : sortKeys) {
				if (key.getColumn() == column) {
					sortKey = key;
				}
			}

			if (sortKey != null) {
				switch (sortKey.getSortOrder()) {
					case ASCENDING:
						sortKey = new RowSorter.SortKey(column, SortOrder.DESCENDING);
						break;
					case DESCENDING:
						sortKey = new RowSorter.SortKey(column, SortOrder.UNSORTED);
						break;
					case UNSORTED:
						sortKey = new RowSorter.SortKey(column, SortOrder.ASCENDING);
						break;
				}
			} else {
				sortKey = new RowSorter.SortKey(column, SortOrder.ASCENDING);
			}
			List<RowSorter.SortKey> newSortKeys = new ArrayList<RowSorter.SortKey>();
			newSortKeys.add(sortKey);
			setSortKeys(newSortKeys);
		}

		@Override
		public List<? extends SortKey> getSortKeys() {
			return scrollable ? scrollSortKeys : staticSortKeys;
		}

		@Override
		public void setSortKeys(List<? extends SortKey> keys) {
			if (scrollable) {
				staticSortKeys.clear();
				scrollSortKeys = keys;
				updateGlobalSortKeys();
				UTableRowSorter.this.sort();
				fireSortOrderChanged();
				staticTableRS.fireSortOrderChanged();
			} else {
				staticSortKeys = keys;
				scrollSortKeys.clear();
				updateGlobalSortKeys();
				UTableRowSorter.this.sort();
				fireSortOrderChanged();
				scrollTableRS.fireSortOrderChanged();
			}
		}



		@Override
		public int convertRowIndexToModel(int index) {
			return UTableRowSorter.this.convertRowIndexToModel(index);
		}

		@Override
		public int convertRowIndexToView(int index) {
			return UTableRowSorter.this.convertRowIndexToView(index);
		}

		@Override
		public int getViewRowCount() {
			return UTableRowSorter.this.getViewRowCount();
		}

		@Override
		public int getModelRowCount() {
			return UTableRowSorter.this.getModelRowCount();
		}

		@Override
		public void modelStructureChanged() {
			UTableRowSorter.this.modelStructureChanged();
		}

		@Override
		public void allRowsChanged() {
			UTableRowSorter.this.allRowsChanged();
		}

		@Override
		public void rowsInserted(int firstRow, int endRow) {
			UTableRowSorter.this.rowsInserted(firstRow, endRow);
		}

		@Override
		public void rowsDeleted(int firstRow, int endRow) {
			UTableRowSorter.this.rowsDeleted(firstRow, endRow);
		}

		@Override
		public void rowsUpdated(int firstRow, int endRow) {
			UTableRowSorter.this.rowsUpdated(firstRow, endRow);
		}

		@Override
		public void rowsUpdated(int firstRow, int endRow, int column) {
			UTableRowSorter.this.rowsUpdated(firstRow, endRow, column);
		}
	}
}
