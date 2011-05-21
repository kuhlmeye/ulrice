package net.ulrice.frame.impl.statusbar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import net.ulrice.Ulrice;
import net.ulrice.process.IFBackgroundProcess;
import net.ulrice.process.IFProcessListener;

public class ProcessList extends JList implements IFProcessListener {

	
	private ProcessListModel model;
	private ProcessComparator processComparator;

	public ProcessList() {
		setCellRenderer(new ProcessCellRenderer());
		model = new ProcessListModel();
		processComparator = new ProcessComparator();
		setModel(model);
		setCellRenderer(new ProcessCellRenderer());
		Ulrice.getProcessManager().addProcessListener(this);
	}

	@Override
	public void stateChanged(IFBackgroundProcess process) {
		switch(process.getProcessState()) {
		case Started:
			model.addProcess(process);
			break;
		case Initialized:
			break;
		case Done:
			model.delProcess(process);
			break;
		}
	}

	@Override
	public void progressChanged(IFBackgroundProcess process) {
		model.chgProcess(process);
	}
	
	class ProcessListModel extends AbstractListModel {

		ArrayList<IFBackgroundProcess> processList = new ArrayList<IFBackgroundProcess>();

		@Override
		public int getSize() {
			return processList.size();
		}

		@Override
		public Object getElementAt(int index) {
			return processList.get(index);
		}
		
		public void chgProcess(IFBackgroundProcess process) {
			int idx = processList.indexOf(process);
			if(idx > -1) {
				fireContentsChanged(this, idx, idx);
			}
		}
		
		public void addProcess(IFBackgroundProcess process) {
			if(!processList.contains(process)) {
				Collections.sort(processList, processComparator);
				processList.add(process);
				fireIntervalAdded(this, 0, 1);
			}
		}
		
		public void delProcess(IFBackgroundProcess process) {
			int idx = processList.indexOf(process);
			if(idx > -1) {
				processList.remove(process);
				fireIntervalRemoved(this, idx, idx);
			}
		}
				
		
	}

	class ProcessCellRenderer implements ListCellRenderer {
		
		private JPanel processPanel = new JPanel();
		private JPanel rightPanel = new JPanel();
		private JProgressBar progressBar = new JProgressBar();
		private JLabel stateLabel = new JLabel();
		private JLabel nameLabel = new JLabel();
		private JLabel messageLabel = new JLabel();
		
		public ProcessCellRenderer() {
			progressBar.setMinimum(0);
			progressBar.setMaximum(100);
			progressBar.setStringPainted(true);
			
			rightPanel.setLayout(new GridLayout(3, 0));
			rightPanel.add(nameLabel);
			rightPanel.add(progressBar);
			rightPanel.add(messageLabel);
			
			stateLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			nameLabel.setBorder(BorderFactory.createLineBorder(Color.black));
			nameLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			
			processPanel.setLayout(new BorderLayout());
			processPanel.add(stateLabel, BorderLayout.WEST);
			processPanel.add(rightPanel, BorderLayout.CENTER);
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			IFBackgroundProcess process = (IFBackgroundProcess) value;
			
			stateLabel.setText(process.getProcessState().toString());
			int progress = (int)(process.getProcessProgress() * 100.0);			
			progressBar.setValue(progress);
			messageLabel.setText(process.getProcessProgressMessage());
			progressBar.setString(String.format("%03d%%", progress));
			nameLabel.setText(process.getProcessName());
			return processPanel;
		}		
	}
	
	class ProcessComparator implements Comparator<IFBackgroundProcess> {

		@Override
		public int compare(IFBackgroundProcess o1, IFBackgroundProcess o2) {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
}
