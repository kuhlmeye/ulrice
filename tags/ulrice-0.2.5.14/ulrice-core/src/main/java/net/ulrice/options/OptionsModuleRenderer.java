package net.ulrice.options;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import net.ulrice.options.modules.IFOptionModule;

/**
 * Renderer for the option modules rendered in the list of the application options dialog.
 * 
 * @author DL10KUH
 */
public class OptionsModuleRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 7776352438476396826L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Object renderValue = value;
        if(value instanceof IFOptionModule) {
            renderValue = ((IFOptionModule)value).getName();
        }
        return super.getListCellRendererComponent(list, renderValue, index, isSelected, cellHasFocus);                
    }
}
