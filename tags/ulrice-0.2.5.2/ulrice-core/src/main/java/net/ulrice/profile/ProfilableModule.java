package net.ulrice.profile;

import net.ulrice.module.IFController;
import net.ulrice.module.IFModule;

/**
 * Marker interface for modules that are supporting profiles.
 * 
 * @author christof
 */
public interface ProfilableModule<T extends IFController> extends IFModule, ProfileDataHandler<T> {

}
