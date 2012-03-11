package net.ulrice.options.modules;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 
 * @author DL10KUH
 */
public class HotkeyOptionModule extends JPanel implements IFOptionModule {

    private static final long serialVersionUID = 4603342680941008166L;

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "HotkeyOptionModule";
    }

    @Override
    public JComponent getView() {
        // TODO Auto-generated method stub
        return new JLabel("Hallo!");
    }

    @Override
    public void onInitialize() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onShow() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onHide() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSave() {
        // TODO Auto-generated method stub
        
    }
}
