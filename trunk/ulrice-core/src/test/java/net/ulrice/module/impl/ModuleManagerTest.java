package net.ulrice.module.impl;


import net.ulrice.AbstractUlriceTest;
import net.ulrice.module.IFController;
import net.ulrice.module.exception.ModuleInstanciationException;

import org.junit.Assert;
import org.junit.Test;


public class ModuleManagerTest extends AbstractUlriceTest {

	@Test
	public void testOpenCloseSingleModules() throws ModuleInstanciationException {		
		IFController firstSingleModule = moduleManager.openModule(SINGLE_MODULE_ID);
		IFController secondSingleModule = firstSingleModule;
		
		firstSingleModule = moduleManager.openModule(SINGLE_MODULE_ID);
		Assert.assertSame(secondSingleModule, firstSingleModule);			
		
		moduleManager.closeController(firstSingleModule);
		
		firstSingleModule = moduleManager.openModule(SINGLE_MODULE_ID);
		Assert.assertNotSame(secondSingleModule, firstSingleModule);		

		moduleManager.closeController(firstSingleModule);
		moduleManager.closeController(secondSingleModule);
	}
	
	public void testOpenCloseNormalModules() throws ModuleInstanciationException {
		IFController firstNormalModule = moduleManager.openModule(NORMAL_MODULE_ID);
		IFController secondNormalModule = firstNormalModule;
		
		firstNormalModule = moduleManager.openModule(NORMAL_MODULE_ID);
		Assert.assertNotSame(secondNormalModule, firstNormalModule);

		moduleManager.closeController(firstNormalModule);
		moduleManager.closeController(secondNormalModule);
	}
	
}
