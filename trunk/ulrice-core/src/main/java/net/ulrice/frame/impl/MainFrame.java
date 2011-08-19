package net.ulrice.frame.impl;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import net.ulrice.Ulrice;
import net.ulrice.frame.IFMainFrame;
import net.ulrice.frame.IFWorkarea;
import net.ulrice.frame.impl.navigation.InstanceTree;
import net.ulrice.frame.impl.navigation.ModuleTree;
import net.ulrice.frame.impl.statusbar.Statusbar;
import net.ulrice.frame.impl.workarea.SingleWorkarea;
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
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle(Ulrice.getConfiguration(this, "Title", ""));	
		setLayout(new BorderLayout());
	}

	/**
	 * This method defines the layout of the main frame. It the layout of the mainframe should be adapted to application
	 * needs, this method can be overwritten.
	 */
	public void inializeLayout() {
		new ChangeoverDialog();
		
		activeWorkarea = new TabbedWorkarea();
		activeWorkarea.onActivateWorkarea();

		ModuleTree moduleTree = new ModuleTree();
		InstanceTree instanceTree = new InstanceTree();
		Statusbar statusbar = new Statusbar();
		Toolbar toolbar = new Toolbar(Ulrice.getConfiguration(this, "ActionOrder", Toolbar.MODULE_ACTIONS));
		Menubar menubar = new Menubar();
		
		setJMenuBar(menubar);
		add(toolbar, BorderLayout.NORTH);
		add(new JScrollPane(moduleTree.getView()), BorderLayout.WEST);
		add(new JScrollPane(instanceTree.getView()), BorderLayout.EAST);
		add(activeWorkarea.getView(), BorderLayout.CENTER);
		add(statusbar.getView(), BorderLayout.SOUTH);		
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
	public IFWorkarea getActiveWorkarea() {
		return activeWorkarea;
	}
}
