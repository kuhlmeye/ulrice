package net.ulrice.databinding.bufferedbinding.impl;


import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;
import net.ulrice.databinding.modelaccess.impl.DynamicReflectionMVA;
import net.ulrice.databinding.modelaccess.impl.IndexedReflectionMVA;
import net.ulrice.databinding.modelaccess.impl.ReflectionMVA;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the list attribute model. 
 * @author christof
 */
public class TableAMTest {

	private TableAM tableAM;
	
	public List<Person> list;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		tableAM = new TableAM(new IndexedReflectionMVA(this, "list"));
		tableAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(Person.class, "name"), String.class));
		tableAM.addColumn(new ColumnDefinition<Integer>(new DynamicReflectionMVA(Person.class, "age"), Integer.class));
		list = new LinkedList<Person>();

		Person a = new Person();
		a.name = "Max Mustermann";
		a.age = 18;
		
		Person b = new Person();
		b.name = "Petra Musterfrau";
		b.age = 20;
		
		list.clear();
		list.add(a);
		list.add(b);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Tests, if data could be read into the data-model.
	 */
	@Test
	public void read() {
		tableAM.read();				
		assertEquals(2, tableAM.getRowCount());
		assertEquals("Max Mustermann", ((Person)tableAM.getElementAt(0).writeObject()).name);
		assertEquals("Petra Musterfrau", ((Person)tableAM.getElementAt(1).writeObject()).name);
	}
	
	/**
	 * Tests, if the values are read into the internal element objects.
	 */
	@Test
	public void readCellValues() {
    	Assert.assertEquals(false, tableAM.isInitialized());
    	Assert.assertEquals(false, tableAM.isDirty());
    	Assert.assertEquals(true, tableAM.isValid());
		
		tableAM.read();
    	Assert.assertEquals(true, tableAM.isInitialized());
    	Assert.assertEquals(false, tableAM.isDirty());
    	Assert.assertEquals(true, tableAM.isValid());
    	
		assertEquals("Max Mustermann", tableAM.getElementAt(0).getValueAt(0));
		assertEquals(18, tableAM.getElementAt(0).getValueAt(1));
		assertEquals("Petra Musterfrau", tableAM.getElementAt(1).getValueAt(0));
		assertEquals(20, tableAM.getElementAt(1).getValueAt(1));
	}
	
	/**
	 * Tests, if the data state mechanism of an element works.
	 */
	@Test
	public void updateCellValues() {
		tableAM.read();
        tableAM.getElementAt(0).setValueAt(0, "Otto Normal");
        assertEquals("Otto Normal", tableAM.getElementAt(0).getValueAt(0));
    	Assert.assertEquals(true, tableAM.getElementAt(0).isDirty());
    	Assert.assertEquals(true, tableAM.getElementAt(0).isValid());
    	Assert.assertEquals(true, tableAM.isInitialized());
    	Assert.assertEquals(true, tableAM.isDirty());
    	Assert.assertEquals(true, tableAM.isValid());

        // Change back.
        tableAM.getElementAt(0).setValueAt(0, "Max Mustermann");
        assertEquals("Max Mustermann", tableAM.getElementAt(0).getValueAt(0));
    	Assert.assertEquals(false, tableAM.getElementAt(0).isDirty());
    	Assert.assertEquals(true, tableAM.getElementAt(0).isValid());
    	Assert.assertEquals(true, tableAM.isInitialized());
    	Assert.assertEquals(false, tableAM.isDirty());
    	Assert.assertEquals(true, tableAM.isValid());
        
        tableAM.getElementAt(0).setValueAt("Person.name", "Otto Normal");
        assertEquals("Otto Normal", tableAM.getElementAt(0).getValueAt("Person.name"));
    	Assert.assertEquals(true, tableAM.getElementAt(0).isDirty());
    	Assert.assertEquals(true, tableAM.getElementAt(0).isValid());
    	Assert.assertEquals(true, tableAM.isInitialized());
    	Assert.assertEquals(true, tableAM.isDirty());
    	Assert.assertEquals(true, tableAM.isValid());
        
        // Change back.
        tableAM.getElementAt(0).setValueAt("Person.name", "Max Mustermann");
        assertEquals("Max Mustermann", tableAM.getElementAt(0).getValueAt("Person.name"));
    	Assert.assertEquals(false, tableAM.getElementAt(0).isDirty());
    	Assert.assertEquals(true, tableAM.getElementAt(0).isValid());
    	Assert.assertEquals(true, tableAM.isInitialized());
    	Assert.assertEquals(false, tableAM.isDirty());
    	Assert.assertEquals(true, tableAM.isValid());
	}

	/**
	 * Tests writing of changed element values-
	 */
	@Test
	public void writeChangedValues() {
	    tableAM.read();
        tableAM.getElementAt(0).setValueAt(0, "Otto Normal");
	    tableAM.write();
	    assertEquals("Otto Normal", list.get(0).name);
	}
	
	public static class Person {
		public String name;
		public int age;
	}
}
