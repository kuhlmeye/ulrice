package net.ulrice.frame.impl;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import net.ulrice.ConfigurationListener;
import net.ulrice.Ulrice;
import net.ulrice.appprefs.IFAppPrefs;
import net.ulrice.configuration.UlriceConfigurationCallback;
import net.ulrice.frame.IFMainFrame;
import net.ulrice.frame.IFWorkarea;
import net.ulrice.frame.impl.navigation.InstanceTree;
import net.ulrice.frame.impl.navigation.ModuleTree;
import net.ulrice.frame.impl.statusbar.Statusbar;
import net.ulrice.frame.impl.workarea.TabbedWorkarea;
import net.ulrice.message.TranslationUsage;

/**
 * The default main frame of ulrice.
 * 
 * @author ckuhlmeyer
 */
public class MainFrame extends JFrame implements IFMainFrame {

	/** Default generated serial version uid. */
	private static final long serialVersionUID = 3237338164470389941L;
	
	/** The currently active workarea. */
	private IFWorkarea activeWorkarea;
	
	private JSplitPane mainSplitter;

	private MainFrameConfig config = new MainFrameConfig();

	private ModuleTree moduleTree;

	private InstanceTree instanceTree;

	private Statusbar statusbar;
	
	/**
	 * Creates a new main frame.
	 */
	public MainFrame() {
		setLayout(new BorderLayout());
		Ulrice.addConfigurationListener(new ConfigurationListener() {
			
			@Override
			public void initializationFinished() {
				config.loadConfiguration(Ulrice.getAppPrefs());
				setTitle(config.getTitle());	
			}
		});
	}

	/**
	 * This method defines the layout of the main frame. It the layout of the mainframe should be adapted to application
	 * needs, this method can be overwritten.
	 */
	public void inializeLayout(final IFAppPrefs appPrefs, UlriceConfigurationCallback configurationCallback) {
		new ChangeoverDialog();
		
		config.loadConfiguration(appPrefs);		

		Menubar menubar = new Menubar(configurationCallback);
		setJMenuBar(menubar);

		if(config.isShowToolbar()) {
			Toolbar toolbar = new Toolbar(config.getToolbarActionOrder());
			toolbar.setHideUnusedModuleActions(config.isToolbarHideUnusedModuleActions());
			add(toolbar, BorderLayout.NORTH);
		}

		if(config.isShowStatusbar()) {
			statusbar = new Statusbar();
			add(statusbar.getView(), BorderLayout.SOUTH);						
		}

		if(config.isExitOnClose()) {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} else {
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
		
		
		resizeWindow(appPrefs);
		
		activeWorkarea = new TabbedWorkarea();
		activeWorkarea.onActivateWorkarea();
				
		if(!config.isShowModuleTree() && !config.isShowInstanceTree()) {
			// If no trees are shown, directly display the workarea as center component
			add(activeWorkarea.getView(), BorderLayout.CENTER);			
		} else {
			// If one or more trees are shown => work with a splitter.

			if(config.isShowModuleTree()) {
				moduleTree = new ModuleTree();
			}

			if(config.isShowInstanceTree()) {
				instanceTree = new InstanceTree();
			}	
			
			mainSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			if(config.isShowModuleTree() && !config.isShowInstanceTree()) {
				mainSplitter.setLeftComponent(new JScrollPane(moduleTree));
			} else if(!config.isShowModuleTree() && config.isShowInstanceTree()) {
				mainSplitter.setLeftComponent(new JScrollPane(instanceTree));
			} else if(config.isShowModuleTree() && config.isShowInstanceTree()) {
				JTabbedPane treeArea = new JTabbedPane();
				treeArea.addTab(Ulrice.getTranslationProvider().getUlriceTranslation(TranslationUsage.Title, "Modules").getText(), new JScrollPane(moduleTree));
				treeArea.addTab(Ulrice.getTranslationProvider().getUlriceTranslation(TranslationUsage.Title, "Instances").getText(), new JScrollPane(instanceTree));
				mainSplitter.setLeftComponent(treeArea);
			}	
			mainSplitter.setRightComponent(activeWorkarea.getView());
			add(mainSplitter, BorderLayout.CENTER);
			mainSplitter.setDividerLocation(config.getDividerLocation());
		}		
		
		
		addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				storeWindowPositions();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				storeWindowPositions();
			}
			
			private void storeWindowPositions() {
				appPrefs.putConfiguration(MainFrame.this, "frameX", Integer.toString(MainFrame.this.getX()));
				appPrefs.putConfiguration(MainFrame.this, "frameY", Integer.toString(MainFrame.this.getY()));
				appPrefs.putConfiguration(MainFrame.this, "frameWidth", Integer.toString(MainFrame.this.getWidth()));
				appPrefs.putConfiguration(MainFrame.this, "frameHeight", Integer.toString(MainFrame.this.getHeight()));			
			}
		});
	}

	private void resizeWindow(IFAppPrefs appPrefs) {
		String xStr = appPrefs.getConfiguration(this, "frameX", null);
		String yStr = appPrefs.getConfiguration(this, "frameY", null);
		int width = Integer.parseInt(appPrefs.getConfiguration(this, "frameWidth", Integer.toString(config.getDefaultWidth())));
		int height = Integer.parseInt(appPrefs.getConfiguration(this, "frameHeight", Integer.toString(config.getDefaultHeight())));
		
		if(xStr != null && yStr != null) {
			try {
				int x = Integer.parseInt(xStr);
				int y = Integer.parseInt(yStr);
			
				setBounds(x, y, width, height);
			} catch(NumberFormatException e) {
				resizeToDefaultSizes(width, height);
			}
		} else {
			resizeToDefaultSizes(width, height);
		}
	}

	private void resizeToDefaultSizes(int width, int height) {
		setSize(width, height);
		
		if(config.isCenterFrame()) {
			setLocationRelativeTo(null);
		}
	}

	/**
	 * @see net.ulrice.frame.IFMainFrame#getFrame()
	 */
	public JFrame getFrame() {
		return this;
	}

	/**
	 * @return the activeWorkarea
	 */
	public IFWorkarea getWorkarea() {
		return activeWorkarea;
	}
}
