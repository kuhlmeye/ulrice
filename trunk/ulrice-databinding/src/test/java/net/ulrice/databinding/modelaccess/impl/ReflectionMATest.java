package net.ulrice.databinding.modelaccess.impl;

import junit.framework.TestCase;

public class ReflectionMATest extends TestCase {

	protected String stringA = "Test";
	protected int intA = 5;
	
	private AbstractReflectionMVA stringAModelAccess;
	private AbstractReflectionMVA intAModelAccess;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		stringAModelAccess = new ReflectionMVA(this, "stringA");
		intAModelAccess = new ReflectionMVA(this, "intA");
	}
	
	public void testGetModelType() {
		assertEquals(String.class, stringAModelAccess.getModelType());
		assertEquals(Integer.TYPE, intAModelAccess.getModelType());
	}
	
	public void testGetValue() {
		assertEquals("Test", stringAModelAccess.getValue());
		assertEquals(5, intAModelAccess.getValue());
	}
	
	public void testSetValue() {
		stringAModelAccess.setValue("Modified");
		intAModelAccess.setValue(4);

		assertEquals("Modified", stringAModelAccess.getValue());
		assertEquals(4, intAModelAccess.getValue());
	}	
}
