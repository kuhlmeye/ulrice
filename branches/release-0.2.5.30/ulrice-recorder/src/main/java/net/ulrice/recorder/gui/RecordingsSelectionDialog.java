package net.ulrice.recorder.gui;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.ulrice.recorder.domain.RecordingInfo;

public class RecordingsSelectionDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private List<RecordingInfo> recordings;
	private List<RecordingInfo> selectedRecordings = new ArrayList<RecordingInfo>();

	public RecordingsSelectionDialog(Window parent, final List<RecordingInfo> recordings, final boolean selectMultiple) {
		super(parent);
		setModal(true);
		this.recordings = recordings;

		final JTable table = new JTable(new RecordingsTableModel());

		JButton okButton = new JButton(new AbstractAction("OK") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				selectedRecordings.clear();
				if(selectMultiple) {
					for(int row : table.getSelectedRows()) {
						selectedRecordings.add(recordings.get(row));
					}
				} else {
					if(table.getSelectedRow() >= 0) {
						selectedRecordings.add(recordings.get(table.getSelectedRow()));
					}
				}
				dispose();
			}
		});

		JButton cancelButton = new JButton(new AbstractAction("Cancel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				selectedRecordings.clear();
				dispose();
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	}
	
	public List<RecordingInfo> getSelectedRecordings() {
		return selectedRecordings;
	}

	private class RecordingsTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public int getRowCount() {
			return recordings.size();
		}

		@Override
		public Object getValueAt(int row, int column) {
			RecordingInfo recording = recordings.get(row);
			switch (column) {
			case 0:
				return recording.getCategory();
			case 1:
				return recording.getTitle();
			}

			return null;
		}
	}
}
