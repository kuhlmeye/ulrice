package net.ulrice.module.impl;


import net.ulrice.AbstractUlriceTest;
import net.ulrice.module.IFController;
import net.ulrice.module.exception.ModuleInstantiationException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("Not yet ready")
public class ModuleManagerTest extends AbstractUlriceTest {

	@Test
	public void testOpenCloseSingleModules() throws ModuleInstantiationException {		
	    Assert.fail ("todo"); //TODO
//		IFController firstSingleModule = moduleManager.openModule(SINGLE_MODULE_ID);
//		IFController secondSingleModule = firstSingleModule;
//		
//		firstSingleModule = moduleManager.openModule(SINGLE_MODULE_ID);
//		Assert.assertSame(secondSingleModule, firstSingleModule);			
//		
//		moduleManager.closeController(firstSingleModule);
//		
//		firstSingleModule = moduleManager.openModule(SINGLE_MODULE_ID);
//		Assert.assertNotSame(secondSingleModule, firstSingleModule);		
//
//		moduleManager.closeController(firstSingleModule);
//		moduleManager.closeController(secondSingleModule);
	}
	
	public void testOpenCloseNormalModules() throws ModuleInstantiationException {
	    Assert.fail ("todo"); //TODO
//		IFController firstNormalModule = moduleManager.openModule(NORMAL_MODULE_ID);
//		IFController secondNormalModule = firstNormalModule;
//		
//		firstNormalModule = moduleManager.openModule(NORMAL_MODULE_ID);
//		Assert.assertNotSame(secondNormalModule, firstNormalModule);
//
//		moduleManager.closeController(firstNormalModule);
//		moduleManager.closeController(secondNormalModule);
	}
	
}
