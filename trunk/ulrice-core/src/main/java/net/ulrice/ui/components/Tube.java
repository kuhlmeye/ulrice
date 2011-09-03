package net.ulrice.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Tube extends JPanel {

	/** Default generated serial version uid. */
	private static final long serialVersionUID = -8457230025465216510L;
	
	private Box contentBox = Box.createVerticalBox();
	private Box tabBox = Box.createVerticalBox();

	private List<TubeTab> tabList = new ArrayList<TubeTab>();
	protected TubeTab selectedTab;
	private boolean opaquePanels;
	private Border border;
	private Insets tabPanelInsets;
	private Insets contentPanelInsets;
	private Color tabPanelBackground;
	private Color contentPanelBackground;

	public Tube() {		
		
		JPanel tabPanel = new JPanel();
		tabPanel.setLayout(new BorderLayout());
		tabPanel.setOpaque(false);
		tabPanel.add(tabBox, BorderLayout.NORTH);

		JPanel contentPanel = new JPanel();
		contentPanel.setOpaque(false);
		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(contentBox, BorderLayout.NORTH);
		
		int hGap = UIManager.getInt("Tube.mainHoriztonalGap");
		
		setLayout(new BorderLayout(hGap, 0));

		JScrollPane tabPanelScroller = new JScrollPane(tabPanel);
		tabPanelScroller.setOpaque(opaquePanels);
		tabPanelScroller.getViewport().setOpaque(opaquePanels);
		tabPanelScroller.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(tabPanelInsets)));
		if(tabPanelBackground != null) {
			tabPanelScroller.getViewport().setBackground(tabPanelBackground);
			tabPanelScroller.setBackground(tabPanelBackground);
		}
		
		final JScrollPane contentPanelScroller = new JScrollPane(contentPanel);
		contentPanelScroller.setOpaque(opaquePanels);
		contentPanelScroller.getViewport().setOpaque(opaquePanels);
		contentPanelScroller.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(contentPanelInsets)));
		contentPanelScroller.getVerticalScrollBar().setUnitIncrement(16);
		contentPanelScroller.getViewport().addChangeListener(
				new ChangeListener() {

					@Override
					public void stateChanged(ChangeEvent e) {
						 
						Rectangle r = contentPanelScroller.getViewport().getViewRect();
						Rectangle v = contentPanelScroller.getViewport().getView().getBounds();

						int y = (int)(100.0 / (v.height - r.height) * (double)r.y);
						
						int ry = (int)(v.height * (y / 100.0));
						
						
						for(TubeTab tab : tabList) {
							Rectangle b = tab.getContentRect();
							if(b.y <= ry && b.y + b.height >= ry) {
								if(!tab.equals(selectedTab)) {
									if(selectedTab != null) {
										selectedTab.setSelected(false);
									}
									selectedTab = tab;
									tab.setSelected(true);
									return;
								}
							}
						}
					}
				});
		if(contentPanelBackground != null) {
			contentPanelScroller.getViewport().setBackground(contentPanelBackground);
			contentPanelScroller.setBackground(contentPanelBackground);
		}
		
		add(tabPanelScroller, BorderLayout.WEST);
		add(contentPanelScroller, BorderLayout.CENTER);
	}

	public void addTab(String name, JComponent panel) {
		addTab(new DefaultTubeTabRenderer(name), panel);
	}

	public void addTab(TubeTabRenderer renderer, JComponent panel) {
		TubeTab tab = new TubeTab(renderer, panel);
		tabList.add(tab);
		tabBox.add(tab);
		contentBox.add(panel);
	}
	
	@Override
	public void updateUI() {
		super.updateUI();
		opaquePanels = UIManager.get("Tube.opaquePanels") != null ? UIManager.getBoolean("Tube.opaquePanels") : true;
		tabPanelInsets = UIManager.get("Tube.tabPanelInsets") != null ? UIManager.getInsets("Tube.tabPanelInsets") : new Insets(0, 0, 0, 0);
		contentPanelInsets = UIManager.get("Tube.contentPanelInsets") != null ? UIManager.getInsets("Tube.contentPanelInsets") : new Insets(0, 0, 0, 0);
		border = UIManager.get("Tube.panelBorder") != null ? UIManager.getBorder("Tube.panelBorder") : new EmptyBorder(0, 0, 0, 0);
		tabPanelBackground = UIManager.get("Tube.tabPanelBackground") != null ? UIManager.getColor("Tube.tabPanelBackground") : UIManager.getColor("Panel.background");
		contentPanelBackground = UIManager.get("Tube.contentPanelBackground") != null ? UIManager.getColor("Tube.contentPanelBackground") : UIManager.getColor("Panel.background");
	}

	private class TubeTab extends JComponent {

		/** Default generated serial version uid. */
		private static final long serialVersionUID = 9037531749862649102L;
		private boolean selected;
		private TubeTabRenderer renderer;
		private JComponent contentPanel;

		public TubeTab(final TubeTabRenderer renderer, final JComponent contentPanel) {
			this.contentPanel = contentPanel;
			this.renderer = renderer;

			setLayout(new BorderLayout());
			setSelected(false);

			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					Tube.this.contentBox.scrollRectToVisible(contentPanel.getBounds());
					if (selectedTab != null) {
						selectedTab.setSelected(false);
					}
					Tube.this.selectedTab = TubeTab.this;
					setSelected(true);
				}
			});
		}

		public Rectangle getContentRect() {
			return contentPanel.getBounds();
		}
		
		public void setSelected(boolean selected) {
			this.selected = selected;

			removeAll();
			add(renderer.getComponent(selected), BorderLayout.CENTER);
			invalidate();
			repaint();
		}

		@Override
		public Dimension getPreferredSize() {
			return renderer.getComponent(selected).getPreferredSize();
		}
	}
}
