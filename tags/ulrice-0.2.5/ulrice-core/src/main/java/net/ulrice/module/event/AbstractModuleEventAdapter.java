package net.ulrice.module.event;

import net.ulrice.module.IFController;

/**
 * Adapter class for the <code>IFModuleEventListener</code>
 * 
 * @author dv20jac
 *
 */
public abstract class AbstractModuleEventAdapter implements IFModuleEventListener {

    /**
     * 
     * {@inheritDoc}
     * @see net.ulrice.module.event.IFModuleEventListener#openModule(net.ulrice.module.IFController)
     */
    public void openModule(IFController activeController){
    }
    
    /**
     * 
     * {@inheritDoc}
     * @see net.ulrice.module.event.IFModuleEventListener#activateModule(net.ulrice.module.IFController)
     */
    public void activateModule(IFController activeController){
    }
    
    /**
     * 
     * {@inheritDoc}
     * @see net.ulrice.module.event.IFModuleEventListener#deactivateModule(net.ulrice.module.IFController)
     */
    public void deactivateModule(IFController activeController){        
    }

    /**
     * 
     * {@inheritDoc}
     * @see net.ulrice.module.event.IFModuleEventListener#closeController(net.ulrice.module.IFController)
     */
    public void closeController(IFController activeController){
    }

    @Override
    public void moduleBlocked(IFController controller, Object blocker) {
    }
    
    @Override
    public void moduleUnblocked(IFController controller, Object blocker) {
    }
    
    @Override
    public void nameChanged(IFController controller) {
    }
}
