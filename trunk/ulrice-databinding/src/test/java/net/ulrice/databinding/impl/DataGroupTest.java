package net.ulrice.databinding.impl;

import static org.junit.Assert.assertEquals;

import net.ulrice.databinding.DataState;
import net.ulrice.databinding.bufferedbinding.DataGroup;
import net.ulrice.databinding.bufferedbinding.GenericAM;
import net.ulrice.databinding.bufferedbinding.IFExtdAttributeModel;
import net.ulrice.databinding.modelaccess.impl.ReflectionMVA;
import net.ulrice.databinding.validation.impl.RegExValidator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the data group.
 * 
 * @author christof
 */
public class DataGroupTest {

    private DataGroup dataGroup;

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
        stringAAM = new GenericAM<String>("stringA", new ReflectionMVA(this, "stringA"));
        stringBAM = new GenericAM<String>("stringB", new ReflectionMVA(this, "stringB"));
        intAAM = new GenericAM<Integer>("intA", new ReflectionMVA(this, "intA"));
        intBAM = new GenericAM<Integer>("intB", new ReflectionMVA(this, "intB"));

        dataGroup = new DataGroup();
        dataGroup.addAM(stringAAM);
        dataGroup.addAM(stringBAM);
        dataGroup.addAM(intAAM);
        dataGroup.addAM(intBAM);

        dataGroup.getAttributeModel("stringB").setValidator(
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

        assertEquals("StringA", ((IFExtdAttributeModel<String>)dataGroup.getAttributeModel("stringA")).getCurrentValue());
        assertEquals("StringB", ((IFExtdAttributeModel<String>)dataGroup.getAttributeModel("stringB")).getCurrentValue());
        assertEquals((Integer)1, ((IFExtdAttributeModel<Integer>)dataGroup.getAttributeModel("intA")).getCurrentValue());
        assertEquals((Integer)2, ((IFExtdAttributeModel<Integer>)dataGroup.getAttributeModel("intB")).getCurrentValue());
    }

    /**
     * Tests the getter of the data group
     */
    @Test
    public void getter() {
        assertEquals(4, dataGroup.getAttributeModels().size());
        assertEquals(stringAAM, dataGroup.getAttributeModel("stringA"));
        assertEquals(stringBAM, dataGroup.getAttributeModel("stringB"));
        assertEquals(intAAM, dataGroup.getAttributeModel("intA"));
        assertEquals(intBAM, dataGroup.getAttributeModel("intB"));
    }

    /**
     * Tests, if the state of the datagroup is set correctly.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void states() {
        assertEquals(DataState.NotInitialized, dataGroup.getState());
        dataGroup.read();
        assertEquals(DataState.NotChanged, dataGroup.getState());
        ((IFExtdAttributeModel<String>)dataGroup.getAttributeModel("stringA")).setCurrentValue("Changed");
        assertEquals(DataState.Changed, dataGroup.getState());
        ((IFExtdAttributeModel<String>)dataGroup.getAttributeModel("stringA")).setCurrentValue("StringA");
        assertEquals(DataState.NotChanged, dataGroup.getState());
        ((IFExtdAttributeModel<String>)dataGroup.getAttributeModel("stringB")).setCurrentValue("Changed");
        assertEquals(DataState.Invalid, dataGroup.getState());
        ((IFExtdAttributeModel<String>)dataGroup.getAttributeModel("stringB")).setCurrentValue("StringB");
        assertEquals(DataState.NotChanged, dataGroup.getState());
    }
}
