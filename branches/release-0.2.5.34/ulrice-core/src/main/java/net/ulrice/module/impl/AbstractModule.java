package net.ulrice.module.impl;

import java.net.URL;

import javax.swing.ImageIcon;

import net.ulrice.module.IFController;
import net.ulrice.module.IFModule;
import net.ulrice.module.ModuleIconSize;
import net.ulrice.module.ModuleType;

public abstract class AbstractModule<T extends IFController> implements IFModule<T> {
	
	private static final String ICON_EXT = ".png";
	private static final String ICON_NAME = "moduleicon";

	private String moduleId;
	private String moduleTitle;
	private ModuleType moduleType;
	private ImageIcon smallIcon;
	private ImageIcon bigIcon;
	
	public AbstractModule(String moduleTitle) {
		this(null, moduleTitle, ModuleType.NormalModule);
	}

	public AbstractModule(String moduleId, String moduleTitle, ModuleType moduleType) {
		this.moduleId = moduleId;
		this.moduleTitle = moduleTitle;
		this.moduleType = moduleType;
		this.smallIcon = findIcon(ModuleIconSize.Size_16x16);
		this.bigIcon = findIcon(ModuleIconSize.Size_32x32);
	}

	@Override
	public String getModuleTitle(Usage usage) {
		return moduleTitle;
	}

	@Override
	public String getUniqueId() {
		return moduleId == null ? getClass().getName() : moduleId;
	}


	@Override
	public ModuleType getModuleInstanceType() {
		return moduleType;
	}
	
	@Override
	public ImageIcon getIcon(ModuleIconSize size) {
		if (size != null) {
			switch (size) {
			case Size_16x16:
				return smallIcon;
			case Size_32x32:
				return bigIcon;
			}
		}
		return smallIcon;
	}
	
	public ImageIcon findIcon(ModuleIconSize size) {
		StringBuilder builder = new StringBuilder();
		builder.append(ICON_NAME);
		if (size != null) {
			switch (size) {
			case Size_16x16:
				builder.append("16");
				break;
			case Size_32x32:
				builder.append("32");
				break;
			}
		}
		builder.append(ICON_EXT);

		String iconPath = builder.toString();
		URL location = getClass().getResource(iconPath);
		if (location == null) {
			location = getClass().getResource(ICON_NAME + ICON_EXT);
			if(location == null) {
				return null;
			}
		}
		return new ImageIcon(location);
	}
}
