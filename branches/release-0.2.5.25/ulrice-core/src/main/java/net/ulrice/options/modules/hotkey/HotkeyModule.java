package net.ulrice.options.modules.hotkey;


public class HotkeyModule {

	private String moduleId;
	private String moduleTitle;
	private String functionKey;
	
	public HotkeyModule(String moduleId, String moduleTitle, String functionKey) {
		this.moduleId = moduleId;
		this.moduleTitle = moduleTitle;
		this.functionKey = functionKey;
	}
	
	public String getModuleId() {
		return moduleId;
	}
	
	public String getFunctionKey() {
		return functionKey;
	}	
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Ctrl+").append(functionKey);
		buffer.append(" ");
		buffer.append(moduleTitle);
		
		return buffer.toString();
	}
}
