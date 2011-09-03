package net.ulrice.ui.components;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Tube extends JPanel {

	private JPanel contentPanel = new JPanel();
	private JPanel tabPanel = new JPanel();

	private List<TubeTab> tabList = new ArrayList<TubeTab>();
	
	public Tube() {
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		tabPanel.setLayout(new BoxLayout(tabPanel, BoxLayout.Y_AXIS));
		
		setLayout(new BorderLayout());		
		add(new JScrollPane(tabPanel), BorderLayout.WEST);
		add(new JScrollPane(contentPanel), BorderLayout.CENTER);
	}
	
	public void addTab(String name, JComponent panel) {
		addTab(new DefaultTubeTabRenderer(name), panel);
	}
	
	public void addTab(TubeTabRenderer renderer, JComponent panel) {
		TubeTab tab = new TubeTab(renderer, panel);
		tabList.add(tab);
		tabPanel.add(tab);
		contentPanel.add(panel);
	}
	
	private class TubeTab extends JPanel {
		
						
		private JComponent contentPanel;
		
		
		public TubeTab(final TubeTabRenderer renderer, final JComponent contentPanel) {
			this.contentPanel = contentPanel;

			setLayout(new BorderLayout());
			add(renderer.getComponent(), BorderLayout.CENTER);
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					
					Tube.this.contentPanel.scrollRectToVisible(contentPanel.getBounds());
				}
			});
		}
				
	}
}
