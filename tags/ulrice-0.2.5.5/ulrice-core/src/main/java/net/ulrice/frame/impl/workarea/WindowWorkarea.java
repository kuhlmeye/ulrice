package net.ulrice.frame.impl.workarea;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.ulrice.Ulrice;
import net.ulrice.frame.IFWorkarea;
import net.ulrice.message.TranslationProvider;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModuleTitleProvider.Usage;
import net.ulrice.module.ModuleIconSize;

/**
 * Workarea displaying the modules as internal frames.
 * 
 * @author DL10KUH
 */
public class WindowWorkarea extends JDesktopPane implements IFWorkarea {
    
    private static final long serialVersionUID = 6510498627910475397L;

    private Map<IFController, ControllerFrame> ctrlFrameMap = new HashMap<IFController, ControllerFrame>();
    private JPopupMenu popup = new JPopupMenu("Arrange");
    
    
    public WindowWorkarea() {        
        setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        
        addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                
                if(getPopup().getComponentCount() > 0) {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }            
        });
    }
    
    public JPopupMenu getPopup() {
        return popup;
    }
    
    @Override
    public String getComponentId() {
        return "WindowWorkarea";
    }

    @Override
    public void openModule(IFController activeController) {
        ControllerFrame frame = new ControllerFrame();
        frame.controller = activeController;
        
        ctrlFrameMap.put(activeController, frame);
        
        frame.setLayout(new BorderLayout());
        frame.add(activeController.getView());
        frame.setTitle(Ulrice.getModuleManager().getModuleTitle(activeController, Usage.DetailedTitle));
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            
            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {
                ControllerFrame frame = (ControllerFrame) e.getInternalFrame();
                Ulrice.getModuleManager().activateModule(frame.controller);
            }
            
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                ControllerFrame frame = (ControllerFrame) e.getInternalFrame();
                Ulrice.getModuleManager().closeController(frame.controller, null);
            }
            
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                ControllerFrame frame = (ControllerFrame) e.getInternalFrame();
                Ulrice.getModuleManager().activateModule(frame.controller);
            }
        });

        frame.setLocation(0, 0);
        frame.setResizable(true);
        frame.setIconifiable(true);
        frame.setMaximizable(true);
        frame.setClosable(true);
        frame.setFrameIcon(Ulrice.getModuleManager().getModule(activeController).getIcon(ModuleIconSize.Size_16x16));
        frame.pack();
        frame.setVisible(true);

        add(frame);
        frame.toFront();
    }

    @Override
    public void activateModule(IFController activeController) {
        ControllerFrame controllerFrame = ctrlFrameMap.get(activeController);

        try {
            controllerFrame.setIcon(false);
        }
        catch (PropertyVetoException e1) {
        }
        controllerFrame.toFront();
    }

    @Override
    public void deactivateModule(IFController activeController) {
    }

    @Override
    public void closeController(IFController activeController) {
        ControllerFrame controllerFrame = ctrlFrameMap.remove(activeController);
        remove(controllerFrame);        
    }

    @Override
    public void moduleBlocked(IFController controller, Object blocker) {
        ControllerFrame controllerFrame = ctrlFrameMap.get(controller);
        if(controllerFrame != null) {
            controllerFrame.getGlassPane().setVisible(true);
        }
    }

    @Override
    public void moduleUnblocked(IFController controller, Object blocker) {
        ControllerFrame controllerFrame = ctrlFrameMap.get(controller);
        controllerFrame.getGlassPane().setVisible(false);
    }

    @Override
    public void moduleBlockerRemoved(IFController controller, Object blocker) {
        // Do nothing, Workarea doesnt care about the blockers, just if it is blocked or not
    }

    @Override
    public void nameChanged(IFController controller) {
        ControllerFrame controllerFrame = ctrlFrameMap.get(controller);
        controllerFrame.setTitle(Ulrice.getModuleManager().getModuleTitle(controller, Usage.DetailedTitle));
    }

    @Override
    public JComponent getView() {
        return this;
    }

    @Override
    public void onActivateWorkarea() {
        Ulrice.getModuleManager().addModuleEventListener(this);
        List<IFController> controllers = Ulrice.getModuleManager().getActiveControllers();
        if(controllers != null) {
            for(IFController controller : controllers) {
                openModule(controller);
            }

            IFController activeController = Ulrice.getModuleManager().getCurrentController();
            if(activeController != null) {
                activateModule(activeController);
            }
        }        
    }

    @Override
    public void onDeactivateWorkarea() {
        Ulrice.getModuleManager().removeModuleEventListener(this);
        ctrlFrameMap.clear();
        removeAll();
    }

    private static class ControllerFrame extends JInternalFrame {
        
        private static final long serialVersionUID = -1677737799839584216L;
        private IFController controller;                
    }
}
