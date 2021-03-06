package net.ulrice.databinding.bufferedbinding.impl;

import static org.junit.Assert.assertEquals;
import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.modelaccess.impl.ReflectionMVA;
import net.ulrice.databinding.validation.impl.NotNullValidator;
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
    
    private String stringZ = null;
    private String stringX = null;

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
        IFAttributeInfo attributeInfo = new IFAttributeInfo() {
        };
        
        stringAAM = new GenericAM<String>(new ReflectionMVA(this, "stringA"), attributeInfo);
        stringBAM = new GenericAM<String>(new ReflectionMVA(this, "stringB"), attributeInfo);
        intAAM = new GenericAM<Integer>(new ReflectionMVA(this, "intA"), attributeInfo);
        intBAM = new GenericAM<Integer>(new ReflectionMVA(this, "intB"), attributeInfo);

        dataGroup = new BindingGroup();
        dataGroup.addAttributeModel(stringAAM);
        dataGroup.addAttributeModel(stringBAM);
        dataGroup.addAttributeModel(intAAM);
        dataGroup.addAttributeModel(intBAM);

        dataGroup.getAttributeModel("DataGroupTest.stringB").addValidator(
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
    
    /**
     * Test if two read of the Binding Group deliver the same result 
     *
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testDoubleRead() {
        IFAttributeInfo attributeInfo = new IFAttributeInfo() {
        };
        
        BindingGroup bg = new BindingGroup();
        GenericAM<String> stringZAM = new GenericAM<String>(new ReflectionMVA(this, "stringZ"), attributeInfo);
        stringZAM.addValidator(new NotNullValidator());
        bg.addAttributeModel(stringZAM);
        
        bg.read();
        Assert.assertFalse("False because, stringZ is null", bg.isValid());
        
        bg.read();
        Assert.assertFalse("False because, stringZ is still null", bg.isValid());
        
    }
    
    
    /**
     * consequential error from setting valid true in GenericAM.read
     *  
     * 
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testDoubleReadGetValid() {
        IFAttributeInfo attributeInfo = new IFAttributeInfo() {
        };
        
        BindingGroup bg = new BindingGroup();
        
        GenericAM<String> stringZAM = new GenericAM<String>(new ReflectionMVA(this, "stringZ"), attributeInfo);
        GenericAM<String> stringXAM = new GenericAM<String>(new ReflectionMVA(this, "stringX"), attributeInfo);
        
        
        stringZAM.addValidator(new NotNullValidator());
        bg.addAttributeModel(stringZAM);
        
        bg.addAttributeModel(stringXAM);
        
        bg.read();
        Assert.assertFalse("False because, stringZ is null", bg.isValid());
        
        
        stringZ = "iAmStringZ";
        stringX = "iAmStringX"; //
        bg.read();
        
        Assert.assertTrue("True, because stringZ is not null any more", bg.isValid());
        
        stringXAM.setCurrentValue("IAmANewStringX");
        
        //falls dieser fehler passiert, liegts evtl daran das das dirty set in der bg nicht leer ist und das ändern von X die bg aktualisiert
        Assert.assertTrue("True, because stringZ is not null any more, and StringX has no validator", bg.isValid());
        
    }
}
