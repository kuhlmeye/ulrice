package net.ulrice.frame.impl;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import net.ulrice.ConfigurationListener;
import net.ulrice.Ulrice;
import net.ulrice.frame.IFMainFrame;
import net.ulrice.frame.IFWorkarea;
import net.ulrice.frame.impl.navigation.InstanceTree;
import net.ulrice.frame.impl.navigation.ModuleTree;
import net.ulrice.frame.impl.statusbar.Statusbar;
import net.ulrice.frame.impl.workarea.TabbedWorkarea;

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
	
	/**
	 * Creates a new main frame.
	 */
	public MainFrame() {
		setLayout(new BorderLayout());
		Ulrice.addConfigurationListener(new ConfigurationListener() {
			
			@Override
			public void initializationFinished() {
				setTitle(Ulrice.getAppPrefs().getConfiguration(MainFrame.this, "Title", ""));	
			}
		});
	}

	/**
	 * This method defines the layout of the main frame. It the layout of the mainframe should be adapted to application
	 * needs, this method can be overwritten.
	 */
	public void inializeLayout() {
		new ChangeoverDialog();
		
		activeWorkarea = new TabbedWorkarea();
		activeWorkarea.onActivateWorkarea();

		Menubar menubar = new Menubar();
		setJMenuBar(menubar);

		Toolbar toolbar = new Toolbar(Ulrice.getAppPrefs().getConfiguration(this, "ActionOrder", Toolbar.MODULE_ACTIONS));
		if(Boolean.getBoolean(Ulrice.getAppPrefs().getConfiguration(this, "ShowToolbar", "true"))) {
			add(toolbar, BorderLayout.NORTH);
		}

		if(Boolean.getBoolean(Ulrice.getAppPrefs().getConfiguration(this, "ShowModuleTree", "true"))) {
			ModuleTree moduleTree = new ModuleTree();
			add(new JScrollPane(moduleTree.getView()), BorderLayout.WEST);
		}

		if(Boolean.getBoolean(Ulrice.getAppPrefs().getConfiguration(this, "ShowInstanceTree", "true"))) {
			InstanceTree instanceTree = new InstanceTree();
			add(new JScrollPane(instanceTree.getView()), BorderLayout.EAST);			
		}
		
		add(activeWorkarea.getView(), BorderLayout.CENTER);

		if(Boolean.getBoolean(Ulrice.getAppPrefs().getConfiguration(this, "ShowStatusbar", "true"))) {
			Statusbar statusbar = new Statusbar();
			add(statusbar.getView(), BorderLayout.SOUTH);						
		}

		if(Boolean.getBoolean(Ulrice.getAppPrefs().getConfiguration(this, "ExitOnClose", "true"))) {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} else {
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
