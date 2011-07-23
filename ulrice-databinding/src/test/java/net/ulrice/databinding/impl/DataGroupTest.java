package net.ulrice.databinding.impl;

import static org.junit.Assert.assertEquals;
import net.ulrice.databinding.bufferedbinding.impl.BindingGroup;
import net.ulrice.databinding.bufferedbinding.impl.GenericAM;
import net.ulrice.databinding.modelaccess.impl.ReflectionMVA;
import net.ulrice.databinding.validation.impl.RegExValidator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the data group.
 * 
 * @author christof
 */
public class DataGroupTest {

    private BindingGroup dataGroup;

    public String stringA = "StringA";
    public String stringB = "StringB";
    public Integer intA = 1;
    public Integer intB = 2;

    private GenericAM<Integer> intBAM;

    private GenericAM<Integer> intAAM;

    private GenericAM<String> stringBAM;

    private GenericAM<String> stringAAM;

    /**
     * @throws java.lang.Exception
     */
    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        stringAAM = new GenericAM<String>(new ReflectionMVA(this, "stringA"));
        stringBAM = new GenericAM<String>(new ReflectionMVA(this, "stringB"));
        intAAM = new GenericAM<Integer>(new ReflectionMVA(this, "intA"));
        intBAM = new GenericAM<Integer>(new ReflectionMVA(this, "intB"));

        dataGroup = new BindingGroup();
        dataGroup.addAttributeModel(stringAAM);
        dataGroup.addAttributeModel(stringBAM);
        dataGroup.addAttributeModel(intAAM);
        dataGroup.addAttributeModel(intBAM);

        dataGroup.getAttributeModel("DataGroupTest.stringB").setValidator(
                new RegExValidator<Object>("StringB", "String is not 'stringB'"));
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
        dataGroup.read();

        assertEquals("StringA", ((GenericAM<String>)dataGroup.getAttributeModel("DataGroupTest.stringA")).getCurrentValue());
        assertEquals("StringB", ((GenericAM<String>)dataGroup.getAttributeModel("DataGroupTest.stringB")).getCurrentValue());
        assertEquals((Integer)1, ((GenericAM<Integer>)dataGroup.getAttributeModel("DataGroupTest.intA")).getCurrentValue());
        assertEquals((Integer)2, ((GenericAM<Integer>)dataGroup.getAttributeModel("DataGroupTest.intB")).getCurrentValue());
    }

    /**
     * Tests the getter of the data group
     */
    @Test
    public void getter() {
        assertEquals(4, dataGroup.getAttributeModels().size());
        assertEquals(stringAAM, dataGroup.getAttributeModel("DataGroupTest.stringA"));
        assertEquals(stringBAM, dataGroup.getAttributeModel("DataGroupTest.stringB"));
        assertEquals(intAAM, dataGroup.getAttributeModel("DataGroupTest.intA"));
        assertEquals(intBAM, dataGroup.getAttributeModel("DataGroupTest.intB"));
    }

    /**
     * Tests, if the state of the datagroup is set correctly.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void states() {
    	Assert.assertEquals(false, dataGroup.isInitialized());
    	Assert.assertEquals(false, dataGroup.isDirty());
    	Assert.assertEquals(true, dataGroup.isValid());
        
    	dataGroup.read();        
    	Assert.assertEquals(true, dataGroup.isInitialized());
    	Assert.assertEquals(false, dataGroup.isDirty());
    	Assert.assertEquals(true, dataGroup.isValid());
    	
        ((GenericAM<String>)dataGroup.getAttributeModel("DataGroupTest.stringA")).setCurrentValue("Changed");
    	Assert.assertEquals(true, dataGroup.isInitialized());
    	Assert.assertEquals(true, dataGroup.isDirty());
    	Assert.assertEquals(true, dataGroup.isValid());

        ((GenericAM<String>)dataGroup.getAttributeModel("DataGroupTest.stringA")).setCurrentValue("StringA");
    	Assert.assertEquals(true, dataGroup.isInitialized());
    	Assert.assertEquals(false, dataGroup.isDirty());
    	Assert.assertEquals(true, dataGroup.isValid());

    	((GenericAM<String>)dataGroup.getAttributeModel("DataGroupTest.stringB")).setCurrentValue("Changed");
    	Assert.assertEquals(true, dataGroup.isInitialized());
    	Assert.assertEquals(true, dataGroup.isDirty());
    	Assert.assertEquals(false, dataGroup.isValid());

    	((GenericAM<String>)dataGroup.getAttributeModel("DataGroupTest.stringB")).setCurrentValue("StringB");
    	Assert.assertEquals(true, dataGroup.isInitialized());
    	Assert.assertEquals(false, dataGroup.isDirty());
    	Assert.assertEquals(true, dataGroup.isValid());

    }
}
