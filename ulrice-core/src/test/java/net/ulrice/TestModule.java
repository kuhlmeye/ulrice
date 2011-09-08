package net.ulrice;

import javax.swing.JComponent;

import net.ulrice.module.impl.AbstractController;
import net.ulrice.module.impl.IFClosing;


public class TestModule extends AbstractController {

    @Override
    public JComponent getView() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void onClose(IFClosing closing) {
        
        closing.doClose();
    }
}


