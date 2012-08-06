package net.ulrice.databinding.bufferedbinding.impl;


import static org.junit.Assert.assertEquals;
import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.bufferedbinding.impl.GenericAM;
import net.ulrice.databinding.modelaccess.impl.ReflectionMVA;
import net.ulrice.databinding.validation.impl.RegExValidator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the list attribute model. 
 * @author christof
 */
public class GenericAMTest {

	public String stringA = "StringA";
    public String stringB = "StringB";
    private GenericAM<String> stringBAM;
    private GenericAM<String> stringAAM;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
       IFAttributeInfo attributeInfo = new IFAttributeInfo() {};
	    
	   stringAAM = new GenericAM<String>(new ReflectionMVA(this, "stringA"), attributeInfo);
       stringBAM = new GenericAM<String>(new ReflectionMVA(this, "stringB"), attributeInfo);
       stringBAM.addValidator(
                new RegExValidator<String>("StringB", "String is not 'stringB'"));

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	

    /**
     * Tests, if the data is read into the attribute model by calling read.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void read() {
    	stringAAM.read();
    	stringBAM.read();
        assertEquals("StringA", stringAAM.getCurrentValue());
        assertEquals("StringB", stringBAM.getCurrentValue());
    }

    /**
     * Tests, if the state of the datagroup is set correctly.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void states() {
    	Assert.assertEquals(false, stringAAM.isInitialized());
    	Assert.assertEquals(false, stringAAM.isDirty());
    	Assert.assertEquals(true, stringAAM.isValid());
    	Assert.assertEquals(false, stringBAM.isInitialized());
    	Assert.assertEquals(false, stringBAM.isDirty());
    	Assert.assertEquals(true, stringBAM.isValid());

    	stringAAM.read();
    	Assert.assertEquals(true, stringAAM.isInitialized());
    	Assert.assertEquals(false, stringAAM.isDirty());
    	Assert.assertEquals(true, stringAAM.isValid());

    	stringAAM.setCurrentValue("Changed");
    	Assert.assertEquals(true, stringAAM.isInitialized());
    	Assert.assertEquals(true, stringAAM.isDirty());
    	Assert.assertEquals(true, stringAAM.isValid());

    	stringAAM.setCurrentValue("StringA");
    	Assert.assertEquals(true, stringAAM.isInitialized());
    	Assert.assertEquals(false, stringAAM.isDirty());
    	Assert.assertEquals(true, stringAAM.isValid());
    	
    	stringBAM.read();
    	Assert.assertEquals(true, stringBAM.isInitialized());
    	Assert.assertEquals(false, stringBAM.isDirty());
    	Assert.assertEquals(true, stringBAM.isValid());

    	stringBAM.setCurrentValue("Changed");
    	Assert.assertEquals(true, stringBAM.isInitialized());
    	Assert.assertEquals(true, stringBAM.isDirty());
    	Assert.assertEquals(false, stringBAM.isValid());

    	stringBAM.setCurrentValue("StringB");
    	Assert.assertEquals(true, stringBAM.isInitialized());
    	Assert.assertEquals(false, stringBAM.isDirty());
    	Assert.assertEquals(true, stringBAM.isValid());

    }
    
    @Test
    public void externalErrorMessage() {
        stringAAM.read();
        Assert.assertTrue(stringAAM.isInitialized());
        Assert.assertFalse(stringAAM.isDirty());
        Assert.assertTrue(stringAAM.isValid());

        stringAAM.addExternalValidationError("Test");
        Assert.assertFalse(stringAAM.isDirty());
        Assert.assertFalse(stringAAM.isValid());
        
        stringAAM.setCurrentValue("Test");
        Assert.assertTrue(stringAAM.isDirty());
        Assert.assertTrue(stringAAM.isValid());        
    }
}
