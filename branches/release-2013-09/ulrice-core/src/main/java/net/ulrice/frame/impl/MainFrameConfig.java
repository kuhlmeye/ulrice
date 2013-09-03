package net.ulrice.frame.impl;

import net.ulrice.appprefs.IFAppPrefs;

public class MainFrameConfig {

	private static final String KEY_ACTION_ORDER = "ActionOrder";
	private static final String KEY_EXIT_ON_CLOSE = "ExitOnClose";
	private static final String KEY_SHOW_STATUSBAR = "ShowStatusbar";
	private static final String KEY_SHOW_INSTANCE_TREE = "ShowInstanceTree";
	private static final String KEY_SHOW_MODULE_TREE = "ShowModuleTree";
	private static final String KEY_SHOW_TOOLBAR = "ShowToolbar";
	private static final String KEY_TITLE = "Title";
	private static final String KEY_CENTER_FRAME = "CenterFrame";
	private static final String KEY_FRAME_DEFAULT_WIDTH = "FrameDefaultWidth";
	private static final String KEY_FRAME_DEFAULT_HEIGHT = "FrameDefaultHeight";
	private static final String KEY_DIVIDER_LOCATION = "DividerLocation";

	private boolean showToolbar = true;
	private boolean showModuleTree = true;
	private boolean showInstanceTree = true;
	private boolean showStatusbar = true;
	private boolean exitOnClose = true;
	private boolean centerFrame = true;
	private int dividerLocation = 200;
	private int defaultWidth = 1000;
	private int defaultHeight = 700;
	private String actionOrder = Toolbar.MODULE_ACTIONS;
	private String title = "";
	
	public int getDividerLocation() {
		return dividerLocation;
	}
	
	public void setDividerLocation(int dividerLocation) {
		this.dividerLocation = dividerLocation;
	}

	public boolean isCenterFrame() {
		return centerFrame;
	}

	public void setCenterFrame(boolean centerFrame) {
		this.centerFrame = centerFrame;
	}

	public int getDefaultWidth() {
		return defaultWidth;
	}

	public void setDefaultWidth(int defaultWidth) {
		this.defaultWidth = defaultWidth;
	}

	public int getDefaultHeight() {
		return defaultHeight;
	}

	public void setDefaultHeight(int defaultHeight) {
		this.defaultHeight = defaultHeight;
	}

	public boolean isShowToolbar() {
		return showToolbar;
	}

	public void setShowToolbar(boolean showToolbar) {
		this.showToolbar = showToolbar;
	}

	public boolean isShowModuleTree() {
		return showModuleTree;
	}

	public void setShowModuleTree(boolean showModuleTree) {
		this.showModuleTree = showModuleTree;
	}

	public boolean isShowInstanceTree() {
		return showInstanceTree;
	}

	public void setShowInstanceTree(boolean showInstanceTree) {
		this.showInstanceTree = showInstanceTree;
	}

	public boolean isShowStatusbar() {
		return showStatusbar;
	}

	public void setShowStatusbar(boolean showStatusbar) {
		this.showStatusbar = showStatusbar;
	}

	public boolean isExitOnClose() {
		return exitOnClose;
	}

	public void setExitOnClose(boolean exitOnClose) {
		this.exitOnClose = exitOnClose;
	}

	public String getToolbarActionOrder() {
		return actionOrder;
	}
	
	public void setToolbarActionOrder(String...actions) {
		StringBuilder builder = new StringBuilder();
		if(actions != null) {
			for(int i = 0; i < actions.length; i++) {
				if(i > 0) {
					builder.append(',');
				}
				builder.append(actions[i]);
			}
		}
		setToolbarActionOrder(builder.toString());
	}

	public void setToolbarActionOrder(String actionOrder) {
		this.actionOrder = actionOrder;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void activateConfiguration(IFAppPrefs appPrefs) {
		appPrefs.putConfiguration(MainFrame.class.getName(), KEY_ACTION_ORDER, actionOrder);
		appPrefs.putConfiguration(MainFrame.class.getName(), KEY_TITLE, title);
		appPrefs.putConfiguration(MainFrame.class.getName(), KEY_SHOW_TOOLBAR, Boolean.toString(showToolbar));
		appPrefs.putConfiguration(MainFrame.class.getName(), KEY_SHOW_MODULE_TREE, Boolean.toString(showModuleTree));
		appPrefs.putConfiguration(MainFrame.class.getName(), KEY_SHOW_INSTANCE_TREE, Boolean.toString(showInstanceTree));
		appPrefs.putConfiguration(MainFrame.class.getName(), KEY_SHOW_STATUSBAR, Boolean.toString(showStatusbar));
		appPrefs.putConfiguration(MainFrame.class.getName(), KEY_EXIT_ON_CLOSE, Boolean.toString(exitOnClose));
		appPrefs.putConfiguration(MainFrame.class.getName(), KEY_CENTER_FRAME, Boolean.toString(centerFrame));
		appPrefs.putConfiguration(MainFrame.class.getName(), KEY_FRAME_DEFAULT_HEIGHT, Integer.toString(defaultHeight));
		appPrefs.putConfiguration(MainFrame.class.getName(), KEY_FRAME_DEFAULT_WIDTH, Integer.toString(defaultWidth));
		appPrefs.putConfiguration(MainFrame.class.getName(), KEY_DIVIDER_LOCATION, Integer.toString(dividerLocation));
	}

	public void loadConfiguration(IFAppPrefs appPrefs) {
		actionOrder = appPrefs.getConfiguration(MainFrame.class.getName(), KEY_ACTION_ORDER, actionOrder);
		title = appPrefs.getConfiguration(MainFrame.class.getName(), KEY_TITLE, title);
		dividerLocation = Integer.parseInt(appPrefs.getConfiguration(MainFrame.class.getName(), KEY_DIVIDER_LOCATION, Integer.toString(dividerLocation)));
		defaultHeight = Integer.parseInt(appPrefs.getConfiguration(MainFrame.class.getName(), KEY_FRAME_DEFAULT_HEIGHT, Integer.toString(defaultHeight)));
		defaultWidth = Integer.parseInt(appPrefs.getConfiguration(MainFrame.class.getName(), KEY_FRAME_DEFAULT_WIDTH, Integer.toString(defaultWidth)));
		centerFrame = Boolean.parseBoolean(appPrefs.getConfiguration(MainFrame.class.getName(), KEY_CENTER_FRAME, Boolean.toString(centerFrame)));
		showToolbar = Boolean.parseBoolean(appPrefs.getConfiguration(MainFrame.class.getName(), KEY_SHOW_TOOLBAR, Boolean.toString(showToolbar)));
		showModuleTree = Boolean.parseBoolean(appPrefs.getConfiguration(MainFrame.class.getName(), KEY_SHOW_MODULE_TREE, Boolean.toString(showModuleTree)));
		showInstanceTree = Boolean.parseBoolean(appPrefs.getConfiguration(MainFrame.class.getName(), KEY_SHOW_INSTANCE_TREE, Boolean.toString(showInstanceTree)));
		showStatusbar = Boolean.parseBoolean(appPrefs.getConfiguration(MainFrame.class.getName(), KEY_SHOW_STATUSBAR, Boolean.toString(showStatusbar)));
		exitOnClose = Boolean.parseBoolean(appPrefs.getConfiguration(MainFrame.class.getName(), KEY_EXIT_ON_CLOSE, Boolean.toString(exitOnClose)));
	}
}
